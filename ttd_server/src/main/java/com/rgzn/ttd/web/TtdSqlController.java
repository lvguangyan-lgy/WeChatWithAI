package com.rgzn.ttd.web;

import com.alibaba.fastjson.JSONObject;
import com.rgzn.ttd.core.Result;
import com.rgzn.ttd.core.ResultCode;
import com.rgzn.ttd.core.ResultGenerator;
import com.rgzn.ttd.dto.PromptDto;
import com.rgzn.ttd.enums.PromptEnum;
import com.rgzn.ttd.service.DynamicsService;
import com.rgzn.ttd.service.PromptService;
import com.rgzn.ttd.service.SqlTemplateService;
import com.rgzn.ttd.service.TableMeteService;
import com.rgzn.ttd.utils.CacheManagerHelper;
import com.rgzn.ttd.utils.DateUtil;
import com.rgzn.ttd.utils.LargeModelClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.rgzn.ttd.enums.PromptEnum.*;

/**
* Created by lgy on 2024/11/01.
*/
@RestController
@RequestMapping("/v1")
public class TtdSqlController {

    private static final Logger log = LoggerFactory.getLogger(TtdSqlController.class);

    @Autowired
    private PromptService promptService;

    @Autowired
    private TableMeteService tableMeteService;

    @Autowired
    private DynamicsService dynamicsService;

    @Autowired
    private LargeModelClient largeModelClient;

    @Autowired
    private SqlTemplateService sqlTemplateService;

    /**
     * 电力交易数据分析
     * @param context
     * @return
     */
    @PostMapping("/query")
    public Result query(@RequestBody JSONObject context) {
        String question = context.getString("query");
        String answer = "answer";
        log.info("query接收到问题:{}",question);
        String content = "";
        Result result = ResultGenerator.genSuccessResult();
        //1.4从缓存分析说明结果key=问题+答案标识+数据更新日期(当天)
        String key = question + answer + DateUtil.today();
        String cacheContent = (String) CacheManagerHelper.getInstance().get("oneDay",key);
        if (StringUtils.isNotEmpty(cacheContent)){
            log.info("从缓存中获取分析内容:{}",cacheContent);
            content = cacheContent;
        }else {
            try {
                List<LinkedHashMap<String, Object>> daoDataByQuery = getDaoDataByQuery(question);
                if (daoDataByQuery.isEmpty()){
                    content = "未查到相关的业务数据";
                }else {
                    String dbData = JSONObject.toJSONString(daoDataByQuery);
                    //1.3调用大模型接口生成简要分析说明
                    //对数据进行分析说明
                    PromptEnum prompt = DESCRIBE_PROMPT;
                    String describePrompt = prompt.getInstruction()+"\n表信息:"+"\n业务数据:"+dbData+"\n用户问题:"+question+"\n"+prompt.getOutputIndicator();
                    content = largeModelClient.requestLargeMode(describePrompt);
                    CacheManagerHelper.getInstance().put("oneDay",key,content);
                }
            }catch (Exception e){
                result.put("message",e.getMessage());
                result.put("code",ResultCode.FAIL.code());
                return result;
            }
        }
        result.put("code",ResultCode.SUCCESS.code());
        result.put("data",content);

        return result;
    }

    /**
     * 电力交易数据
     * @param context
     * @return
     */
    @PostMapping("/queryData")
    public Result queryData(@RequestBody JSONObject context) {
        String question = context.getString("query");
        log.info("queryData接收到问题:{}",question);
        Result result = ResultGenerator.genSuccessResult();
        List<LinkedHashMap<String, Object>> daoDataByQuery = getDaoDataByQuery(question);

        result.put("code",ResultCode.SUCCESS.code());
        result.put("data",daoDataByQuery);

        return result;
    }

    private List<LinkedHashMap<String, Object>> getDaoDataByQuery(String query){
        log.info("从缓存中获取问题的业务数据,问题:{}",query);
        List<LinkedHashMap<String, Object>> result;
        //从缓存分析说明结果key=问题+数据更新日期(当天)
        String key = query + DateUtil.today();
        result = (List<LinkedHashMap<String, Object>>) CacheManagerHelper.getInstance().get("oneDay",key);
        if (result != null){
            return result;
        }else {
            JSONObject sqlTemplate = sqlTemplateService.getTemplate(query);
            if (sqlTemplate != null){
                //执行模板sql,获取业务数据
                String sql = sqlTemplate.getString("sql");
                //替换模板sql的时间字符
                sql = replaceDateStr(sql);
                //判断是单条sql,还是多条sql
                String[] sqlArr = sql.split(";");
                if (sqlArr.length > 1){
                    for (String s : sqlArr) {
                        if (result == null){
                            result = dynamicsService.dynamicsSql(s.trim());
                        }else {
                            List<LinkedHashMap<String, Object>> list = dynamicsService.dynamicsSql(s.trim());
                            result.addAll(list);
                        }
                    }
                }else {
                    result = dynamicsService.dynamicsSql(sql);
                }
            }else {
                try {
                    //通过大模型进行语义识别, 从模板获取
                    sqlTemplate = sqlTemplateService.getTemplateByLargeModel(query);
                    if (sqlTemplate != null){
                        String sql = sqlTemplate.getString("sql");
                        //替换模板sql的时间字符
                        sql = replaceDateStr(sql);
                        //使用模板的sql，进行日期转换
                        String datePrompt = DATE_PROMPT.getInstruction()+"\n"+"当前时间:"+ DateUtil.now()+"\n"+"用户的问题:"+query+"\n"+DATE_PROMPT.getOutputIndicator();
                        //调用大模型获取转换日期后的问题
                        String input = largeModelClient.requestLargeMode(datePrompt);

                        //sql填槽,补充条件信息
                        Object tableMeteByQuery = tableMeteService.findTableMeteByQuery(query);
                        String sqlFillPrompt = SQL_FILL_PROMPT.getInstruction()+"\n模板问题:"+sqlTemplate.getString("query")+"\n模板sql:"+sql+"\n表的信息:"+JSONObject.toJSONString(tableMeteByQuery)+"\n用户的需求:"+input+"\n"+SQL_FILL_PROMPT.getOutputIndicator();
                        sql = largeModelClient.requestLargeMode(sqlFillPrompt);

                        result = dynamicsService.dynamicsSql(sql);

                    }else {
                        //获取提示词
                        PromptDto prompt = promptService.getPrompt(query);
                        //调用大模型生成sql
                        String promptStr = prompt.getInstruction()+"\n"+"表的信息:"+prompt.getContext()+"\n"+"用户的需求:"+prompt.getInputData()+"\n"+prompt.getOutputIndicator();
                        String sql = largeModelClient.requestLargeMode(promptStr);
                        try {
                            //执行sql
                            result = dynamicsService.dynamicsSql(sql);
                        }catch (Exception e){
                            log.error(e.getMessage());
                            //根据报错信息去修正sql
                            String correctPromptStr = SQL_CORRECT_PROMPT.getInstruction()+"\n"+"表信息:"+prompt.getContext()+"\n"+"报错信息:"+e.getMessage()+"\n"+"SQL:"+sql+"\n"+SQL_CORRECT_PROMPT.getOutputIndicator();
                            sql = largeModelClient.requestLargeMode(correctPromptStr);
                            //执行修正的sql
                            result = dynamicsService.dynamicsSql(sql);
                        }
                    }
                }catch (Exception e){
                    throw new RuntimeException("大模型接口调用异常,获取业务数据失败,"+e.getMessage());
                }

            }
            if (!result.isEmpty()){
                CacheManagerHelper.getInstance().put("oneDay",key,result);
            }
        }
        return result;
    }

    private String replaceDateStr(String sql){
        return sql.replace("今天",DateUtil.pushDay(0))
                .replace("昨天",DateUtil.pushDay(-1))
                .replace("前三天",DateUtil.pushDay(-2))
                .replace("下一周",DateUtil.pushDay(6));
    }

}
