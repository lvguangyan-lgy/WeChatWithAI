package com.rgzn.ttd.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rgzn.ttd.configurer.QueryTempConfigurer;
import com.rgzn.ttd.service.SqlTemplateService;
import com.rgzn.ttd.utils.LargeModelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

import static com.rgzn.ttd.enums.PromptEnum.SIMILARITY_PROMPT;

/**
 * Created by lgy on 2024/11/01.
 */
@Service
public class SqlTemplateServiceImpl implements SqlTemplateService {

    private static final Logger log = LoggerFactory.getLogger(SqlTemplateServiceImpl.class);
    @Autowired
    private LargeModelClient largeModelClient;
    @Value("${query.temp.correlation}")
    private double correlation;
    @Autowired
    private QueryTempConfigurer queryTempConfigurer;
    @Override
    public JSONObject getTemplateByLargeModel(String query) {
        JSONObject result = null;
        try {
            //读取模板内容
            final Map<String, JSONObject> queryTempMap = queryTempConfigurer.getQueryTempMap();
            ArrayList<String> tmpList = new ArrayList<>();
            for (String queryTemp : queryTempMap.keySet()) {
                tmpList.add(queryTemp);
            }
            //匹配相关度
            if (tmpList.size() > 0){
                String prompt = SIMILARITY_PROMPT.getInstruction()+"\n"+"模板列表:"+ tmpList+"\n"+"用户问题:"+query+"\n"+SIMILARITY_PROMPT.getOutputIndicator();
                //调用大模型获取模板问题
                log.info("开始匹配sql模板...");
                String tmpQuery = largeModelClient.requestLargeMode(prompt);
                String[] tmpQueryArr = tmpQuery.split("\\|");
                if (tmpQueryArr.length == 2 && Double.parseDouble(tmpQueryArr[1]) >= correlation){
                    log.info("匹配sql模板成功...");
                    result = queryTempConfigurer.getValueByKey(tmpQueryArr[0]);
                }else {
                    log.info("未匹配到sql模板...");
                }
            }
            return result;
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public JSONObject getTemplate(String query) {
        return queryTempConfigurer.getValueByKey(query);
    }
}
