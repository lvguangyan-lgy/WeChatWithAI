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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static com.rgzn.ttd.enums.PromptEnum.*;

/**
 * 问数插件服务
* Created by lgy on 2024/11/01.
*/
@RestController
@RequestMapping("/v1")
public class PluginController {

    private static final Logger log = LoggerFactory.getLogger(PluginController.class);

    @Autowired
    private DynamicsService dynamicsService;

    @Autowired
    private SqlTemplateService sqlTemplateService;

    @Autowired
    private LargeModelClient largeModelClient;

    /**
     * 获取新能源总出力数据
     * @param context
     * @return
     */
    @PostMapping("/data/getEnergyData")
    public Result getEnergyData(@RequestBody JSONObject context) {
        String startDate = context.getString("startDate");
        String endDate = context.getString("endDate");
        log.info("接收getEnergyData请求,查询条件:{}-{}",startDate,endDate);
        Result result = ResultGenerator.genSuccessResult();
        try {
            String sql = "SELECT * FROM energy_view WHERE data_type='新能源总出力' AND z_name = '海南' AND a_data BETWEEN '"+startDate+"' AND '"+endDate+"'";
            List<LinkedHashMap<String,Object>> dataMao = dynamicsService.dynamicsSql(sql);

            result.put("code",ResultCode.SUCCESS.code());
            result.put("data",dataMao);
        }catch (Exception e){
            result.put("code",ResultCode.FAIL.code());
            result.put("message",e.getMessage());
        }
        return result;
    }

    /**
     * 获取重要通道数据
     * @param context
     * @return
     */
    @PostMapping("/data/getChannelData")
    public Result getChannelData(@RequestBody JSONObject context) {
        String startDate = context.getString("startDate");
        String endDate = context.getString("endDate");
        log.info("接收getChannelData请求,查询条件:{}-{}",startDate,endDate);
        Result result = ResultGenerator.genSuccessResult();
        try {
            String sql = "SELECT * FROM transmission_capability_view WHERE a_data BETWEEN '"+startDate+"' AND '"+endDate+"'";
            List<LinkedHashMap<String,Object>> dataMao = dynamicsService.dynamicsSql(sql);

            result.put("code",ResultCode.SUCCESS.code());
            result.put("data",dataMao);
        }catch (Exception e){
            result.put("code",ResultCode.FAIL.code());
            result.put("message",e.getMessage());
        }
        return result;
    }

    /**
     * 获取电网负载数据
     * @param context
     * @return
     */
    @PostMapping("/data/getPowerLoadData")
    public Result getPowerLoadData(@RequestBody JSONObject context) {
        String startDate = context.getString("startDate");
        String endDate = context.getString("endDate");
        log.info("接收getPowerLoadData请求,查询条件:{}-{}",startDate,endDate);
        Result result = ResultGenerator.genSuccessResult();
        try {
            String sql = "SELECT a_data,max_load,min_load,avg_load FROM actual_power_load WHERE a_data BETWEEN '"+startDate+"' AND '"+endDate+"'";
            List<LinkedHashMap<String,Object>> dataMao = dynamicsService.dynamicsSql(sql);

            result.put("code",ResultCode.SUCCESS.code());
            result.put("data",dataMao);
        }catch (Exception e){
            result.put("code",ResultCode.FAIL.code());
            result.put("message",e.getMessage());
        }
        return result;
    }

    /**
     * 获取报价数据
     * @param context
     * @return
     */
    @PostMapping("/data/getQuotationData")
    public Result getQuotationData(@RequestBody JSONObject context) {
        String startDate = context.getString("startDate");
        String endDate = context.getString("endDate");
        log.info("接收getQuotationData请求,查询条件:{}-{}",startDate,endDate);
        Result result = ResultGenerator.genSuccessResult();
        try {
            String sql1 = "SELECT * FROM energy_view WHERE data_type='新能源总出力' AND z_name = '海南' AND a_data BETWEEN '"+startDate+"' AND '"+endDate+"'";
            List<LinkedHashMap<String,Object>> dataMao1 = dynamicsService.dynamicsSql(sql1);

            String sql2 = "SELECT * FROM transmission_capability_view WHERE a_data BETWEEN '"+startDate+"' AND '"+endDate+"'";
            List<LinkedHashMap<String,Object>> dataMao2 = dynamicsService.dynamicsSql(sql2);

            String sql3 = "SELECT a_data,max_load,min_load,avg_load FROM actual_power_load WHERE a_data BETWEEN '"+startDate+"' AND '"+endDate+"'";
            List<LinkedHashMap<String,Object>> dataMao3 = dynamicsService.dynamicsSql(sql3);

            JSONObject data = new JSONObject();
            data.put("energy",dataMao1);
            data.put("channel",dataMao2);
            data.put("powerLoad",dataMao3);


            result.put("code",ResultCode.SUCCESS.code());
            result.put("data",data);
        }catch (Exception e){
            result.put("code",ResultCode.FAIL.code());
            result.put("message",e.getMessage());
        }
        return result;
    }

    /**
     * 从问题模板中获取业务数据
     * @param context
     * @return
     */
    @PostMapping("/data/getDataByTemp")
    public Result getDataByTemp(@RequestBody JSONObject context) {
        String question = context.getString("query");
        log.info("接收getData请求,查询问题:{}",question);

        Result result = ResultGenerator.genSuccessResult();
        try {
            JSONObject sqlTemplate = sqlTemplateService.getTemplate(question);
            if (sqlTemplate != null){
                log.info("匹配到问题模板......");
                List<LinkedHashMap<String,Object>> daoList = null;
                //执行模板sql,获取业务数据
                String sql = sqlTemplate.getString("sql");
                //替换模板sql的时间字符
                sql = replaceDateStr(sql);
                //判断是单条sql,还是多条sql
                String[] sqlArr = sql.split(";");
                if (sqlArr.length > 1){
                    for (String s : sqlArr) {
                        if (daoList == null){
                            daoList = dynamicsService.dynamicsSql(s.trim());
                        }else {
                            List<LinkedHashMap<String, Object>> list = dynamicsService.dynamicsSql(s.trim());
                            daoList.addAll(list);
                        }
                    }
                }else {
                    daoList = dynamicsService.dynamicsSql(sql);
                }
                result.put("code",ResultCode.SUCCESS.code());
                result.put("data",daoList);
            }else {
                //未能匹配到模板
                result.put("code",201);
                result.put("data",null);
            }
        }catch (Exception e){
            result.put("code",ResultCode.FAIL.code());
            result.put("message",e.getMessage());
        }
        return result;
    }

    @Autowired
    private TableMeteService tableMeteService;
    @PostMapping("/data/getTableList")
    public Result getTableList() {
        Result result = ResultGenerator.genSuccessResult();
        try {
            Object allTableMete = tableMeteService.findAllTableMete();
            result.put("code",ResultCode.SUCCESS.code());
            result.put("data",allTableMete);
        }catch (Exception e){
            result.put("code",ResultCode.FAIL.code());
            result.put("message",e.getMessage());
        }
        return result;
    }

    @PostMapping("/data/getColumnMetes")
    public Result getColumnMetes(@RequestBody JSONObject context) {
        String tableNames = context.getString("tableNames");
        Result result = ResultGenerator.genSuccessResult();
        //获取关联的业务表元信息
        String[] tableArr = tableNames.split("\\|");
        if (tableArr.length == 0){
            result.put("code",202);
            result.put("message","没有相关的业务数据");
        }else {
            try {
                List<String> tableList = new ArrayList<>(Arrays.asList(tableArr));
                Object tableMeteByName = tableMeteService.findTableMeteByName(tableList);
                result.put("code",ResultCode.SUCCESS.code());
                result.put("data",tableMeteByName);
            }catch (Exception e){
                result.put("code",ResultCode.FAIL.code());
                result.put("message",e.getMessage());
            }
        }
        return result;
    }

    @PostMapping("/data/executeQuerySql")
    public Result executeQuerySql(@RequestBody JSONObject context) {
        String sql = context.getString("sql");
        Result result = ResultGenerator.genSuccessResult();
        try {
            List<LinkedHashMap<String,Object>> dataMao = dynamicsService.dynamicsSql(sql);

            result.put("code",ResultCode.SUCCESS.code());
            result.put("data",dataMao);
        }catch (Exception e){
            result.put("code",ResultCode.FAIL.code());
            result.put("message","无效的查询SQL,获取业务数据异常");
        }
        return result;
    }

    @PostMapping("/data/executeUpdateSql")
    public Result executeUpdateSql(@RequestBody JSONObject context) {
        String sql = context.getString("sql");
        Result result = ResultGenerator.genSuccessResult();
        try {
            int count = dynamicsService.dynamicsUpdateSql(sql);

            result.put("code",ResultCode.SUCCESS.code());
            result.put("data",count);
        }catch (Exception e){
            result.put("code",ResultCode.FAIL.code());
            result.put("message","SQL执行异常");
        }
        return result;
    }

    @PostMapping("/data/megawatt")
    public Result megawatt(@RequestBody JSONObject context) {
        String query = context.getString("query");
        Result result = ResultGenerator.genSuccessResult();
        try {
            String content = largeModelClient.requestLargeMode(query);

            result.put("code",ResultCode.SUCCESS.code());
            result.put("data",content);
        }catch (Exception e){
            result.put("code",ResultCode.FAIL.code());
            result.put("message",e.getMessage());
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
