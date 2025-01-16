package com.rgzn.ttd.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rgzn.ttd.core.Result;
import com.rgzn.ttd.core.ResultCode;
import com.rgzn.ttd.dto.Echart;
import com.rgzn.ttd.dto.EchartSerie;
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
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.rgzn.ttd.enums.PromptEnum.*;


/**
* Created by CodeGenerator on 2024/05/22.
 * 前端相关控制器
*/
@RestController
public class FrontendController {

    private static final Logger log = LoggerFactory.getLogger(FrontendController.class);

    @Autowired
    private LargeModelClient largeModelClient;
    /**
     * 登录相关请求
     * @return
     */
    @PostMapping("/login")
    public JSONObject login(@RequestParam String username, @RequestParam String password){
        System.out.println("登录:"+username+",密码:"+password);


        JSONObject data = new JSONObject();
        data.put("token_type","2");
        data.put("access_token","test_!2232");
        return data;
    }

    /**
     * 验证登录相关请求
     * @return
     */
    @GetMapping("/login")
    public JSONObject checkIsLogin(){
        System.out.println("检查登录......");

        //判断是否已经登录
        JSONObject data = new JSONObject();
        data.put("is_login",true);
        return data;
    }

    /**
     * 获取应用列表(临时)
     * @return
     */
    @GetMapping("/appset")
    public JSONArray appset(){
        System.out.println("获取应用列表......");

        JSONArray appset = new JSONArray();
        JSONObject data = new JSONObject();
        data.put("description","这是一个测试电力交易问数场景的应用");
        data.put("name","电力交易智能问数(测试)");
        data.put("privacy","公开");
        data.put("id",1);
        appset.add(data);
        return appset;
    }

    /**
     * 获取应用详情(临时)
     * @return
     */
    @GetMapping("/appset/1")
    public JSONObject appset1(){

        JSONObject data = new JSONObject();
        data.put("description","这是一个测试电力交易问数场景的应用");
        data.put("name","电力交易智能问数(测试)");
        data.put("privacy","公开");
        data.put("id",1);
        return data;
    }

    private String getEchartsType(List<LinkedHashMap<String,Object>> contentList){
        //如果有多个字段为数字(2个),则返回折线图
        int count = 0;
        if (!contentList.isEmpty()){
            for (Object value : contentList.get(0).values()) {
                //判断值是不是数字
                if (isNumeric(value)){
                    count++;
                }
            }
        }
        if (count > 1){
            return "line";
        }else {
            return "bar";
        }
    }

    public static boolean isNumeric(Object object) {
        if (object == null){
            return false;
        }else {
            //判断是不是数字
            if (object.toString().matches("-?\\d+(\\.\\d+)?")){
                boolean isIntNumber = true;
                try {
                    //判断数字是否在int的取值范围
                    if (!object.toString().contains(".")){
                        Integer.parseInt(object.toString());
                    }
                }catch (NumberFormatException e){
                    isIntNumber = false;
                }
                return isIntNumber;
            }else {
                return false;
            }
        }

    }

    /**
     * 获取聊天历史(临时)
     * @param context
     * @return
     */
    @PostMapping("/chat/1/chat_history")
    public JSONObject chatHistory(@RequestBody JSONObject context){
        System.out.println("接收到问题context:"+context);


        JSONObject data = new JSONObject();
        data.put("question",context.getString("query"));
        data.put("answer","test_!2232");
        return data;
    }

    /**
     * 获取聊天历史(临时)
     * @param chat_id
     * @return
     */
    @GetMapping("/chatset/1/chat_history")
    public JSONArray chatsetChatHistory(String chat_id){
        System.out.println("接收到问题chatset/chat_history:"+chat_id);
        JSONArray result = new JSONArray();

        JSONObject data = new JSONObject();
        data.put("id","1");
        data.put("answer","历史问题");
        data.put("question","历史答案");
        data.put("citeDocument",null);
        data.put("contextHistory",null);
        //result.add(data);
        return result;
    }

    /**
     * 清空聊天历史(临时)
     * @param context
     * @return
     */
    @DeleteMapping("/chatset/1/chat_history")
    public JSONObject deleteChatsetChatHistory(@RequestBody JSONObject context){
        System.out.println("接收到Delete请求 chatset/chat_history:"+context);

        JSONObject data = new JSONObject();
        return data;
    }

    @PostMapping("/chat/deleteChatCache")
    public JSONObject deleteChatCache(@RequestBody JSONObject context){
        String query = context.getString("query");
        boolean simpleAnswer = context.getBoolean("simpleAnswer");
        if (query.contains("(详细报告)")){
            simpleAnswer = false;
            query = query.replace("(详细报告)","");
        }
        String key = query + simpleAnswer + DateUtil.today();
        CacheManagerHelper.getInstance().remove("oneDay",key);
        JSONObject data = new JSONObject();
        data.put("code",200);
        return data;
    }

    @Autowired
    private SqlTemplateService sqlTemplateService;
    @Autowired
    private DynamicsService dynamicsService;

    @PostMapping("/chat/createEcharts")
    public JSONObject createEcharts(@RequestBody JSONObject context){
        String question = context.getString("query");
        log.info("createEcharts接收到问题:{}",context);
        JSONObject data = new JSONObject();
        question = question.replace("(详细报告)","");
        //获取业务数据
        List<LinkedHashMap<String, Object>> daoDataByQuery = getDaoDataByQuery(question);
        if (daoDataByQuery.isEmpty()){
            return data;
        }
        //根据业务数据生成图表
        String echartsType = getEchartsType(daoDataByQuery);
        log.info("生成echarts数据,数据类型:{}",echartsType);
        //key, x坐标
        ArrayList<String> xAxisData = new ArrayList<>();
        ArrayList<String> legendData = new ArrayList<>();
        ArrayList<EchartSerie> echartSeries = new ArrayList<>();
        if (echartsType.equals("bar")){
            //组织柱状图数据
            String echartSerieName = "数量";
            ArrayList<String> serieData = new ArrayList<>();
            for (LinkedHashMap<String,Object> map : daoDataByQuery) {

                //标记每行数据的第一个字符串值
                int xFlag = 0;
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (isNumeric(entry.getValue())){
                        echartSerieName = entry.getKey();
                        serieData.add(entry.getValue().toString());
                    }else {
                        //取第一个字符串值作为x轴名称
                        if (xFlag == 0){
                            xAxisData.add(entry.getValue().toString());
                        }
                        xFlag++;
                    }
                }
            }
            //封装y轴的数据
            EchartSerie echartSerie = new EchartSerie();
            echartSerie.setType("bar");
            echartSerie.setName(echartSerieName);
            echartSerie.setData(serieData);
            echartSeries.add(echartSerie);
        }else {
            int index = 0;
            for (LinkedHashMap<String,Object> map : daoDataByQuery) {
                //value,y轴值
                ArrayList<String> serieData = new ArrayList<>();

                EchartSerie echartSerie = new EchartSerie();
                echartSerie.setType("line");
                String name = "";
                echartSerie.setData(serieData);
                echartSeries.add(echartSerie);

                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    //把值是数值的字段添加到x轴
                    if (isNumeric(entry.getValue())){
                        if (index == 0){
                            xAxisData.add(entry.getKey());
                        }
                        //x轴字段对应的值
                        serieData.add(entry.getValue().toString());
                    }else {
                        //统计维度的名称
                        if (StringUtils.isEmpty(name)){
                            name += entry.getValue().toString();
                        }else {
                            name += "-"+entry.getValue().toString();
                        }
                    }

                }
                echartSerie.setName(name);
                legendData.add(name);
                index += 1;
            }
        }

        Echart echart = new Echart();
        echart.setTitle(question);
        echart.setLegendData(legendData);
        echart.setXAxisData(xAxisData);
        echart.setSeries(echartSeries);
        //返回的echarts节点,前端取法data.echarts
        data.put("echarts",echart);


        return data;
    }


    @Autowired
    private TableMeteService tableMeteService;
    @Autowired
    private PromptService promptService;

    @PostMapping("/chat")
    public ResponseEntity<StreamingResponseBody> chat(@RequestBody JSONObject context){
        boolean echartsDataStatus = true;
        String question = context.getString("query");
        String simpleAnswerStr = context.getString("simpleAnswer");
        boolean simpleAnswer = true;
        if (StringUtils.isNotEmpty(simpleAnswerStr)){
            simpleAnswer = Boolean.parseBoolean(simpleAnswerStr);
        }
        if (question.contains("(详细报告)")){
            simpleAnswer = false;
            question = question.replace("(详细报告)","");
        }
        log.info("chat接收到问题:{}",context);
        String content = "";
        //1.4从缓存分析说明结果key=问题+答案标识+数据更新日期(当天)
        String key = question + simpleAnswer + DateUtil.today();
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
                    PromptEnum prompt;
                    if (simpleAnswer){
                        prompt = SIMPLE_DESCRIBE_PROMPT;
                    }else {
                        prompt = DESCRIBE_PROMPT;
                    }
                    String describePrompt = prompt.getInstruction()+"\n表信息:"+"\n业务数据:"+dbData+"\n用户问题:"+question+"\n"+prompt.getOutputIndicator();
                    content = largeModelClient.requestLargeMode(describePrompt);
                    CacheManagerHelper.getInstance().put("oneDay",key,content);
                }
            }catch (Exception e){
                content = e.getMessage();
                echartsDataStatus = false;
            }
        }
        context.put("content",content);
        boolean finalEchartsDataStatus = echartsDataStatus;
        StreamingResponseBody responseBody = outputStream -> {
            sendEvents(outputStream,context, finalEchartsDataStatus);
        };
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(responseBody);
    }

    private void sendEvents(OutputStream outputStream,JSONObject context,boolean echartsDataStatus) {
        String content = context.getString("content");
        JSONObject data = new JSONObject();
        try {
            //业务操作
            log.info("输出回答内容:{}",content);
            // 可以在这里添加更多的事件发送逻辑
            for (int i = 0; i < content.length(); i++) {
                data.put("content",content.charAt(i));
                // 发送事件的格式：event:type\n data:message\n\n
                String resultData = "data:"+ JSONObject.toJSONString(data)+"\n\n";
                outputStream.write("event: message\n".getBytes(StandardCharsets.UTF_8));
                outputStream.write(resultData.getBytes(StandardCharsets.UTF_8));

                outputStream.flush(); // 确保消息已经发送
                Thread.sleep(5);
            }

            if (echartsDataStatus){
                //返回图表数据
                JSONObject echarts = createEcharts(context);
                echarts.put("content","\n");
                // 发送事件的格式：event:type\n data:message\n\n
                String resultData = "data:"+ JSONObject.toJSONString(echarts)+"\n\n";
                outputStream.write("event: message\n".getBytes(StandardCharsets.UTF_8));
                outputStream.write(resultData.getBytes(StandardCharsets.UTF_8));
                outputStream.flush(); // 确保消息已经发送
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<LinkedHashMap<String, Object>> getDaoDataByQuery(String query){
        List<LinkedHashMap<String, Object>> result;
        //从缓存分析说明结果key=问题+数据更新日期(当天)
        String key = query + DateUtil.today();
        result = (List<LinkedHashMap<String, Object>>)CacheManagerHelper.getInstance().get("oneDay",key);
        if (result != null){
            log.info("从缓存中获取问题的业务数据,问题:{}",query);
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
                    throw e;
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

    /**
     * 测试使用
     * @param context
     * @return
     */
    @PostMapping("/chat/test")
    public ResponseEntity<StreamingResponseBody> chatTest(@RequestBody JSONObject context){
        String question = context.getString("query");
        question.replace("test","");
        log.info("chat接收到问题:{}",context);
        String content = largeModelClient.requestLargeMode(question);

        context.put("content",content);
        StreamingResponseBody responseBody = outputStream -> {
            sendEvents(outputStream,context,false);
        };
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(responseBody);
    }
}
