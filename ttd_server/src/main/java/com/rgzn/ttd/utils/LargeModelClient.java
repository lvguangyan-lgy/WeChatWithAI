package com.rgzn.ttd.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 大模型接口客户端,总入口
 */
@Component
public class LargeModelClient {

    private static final Logger log = LoggerFactory.getLogger(LargeModelClient.class);
    @Autowired
    private MegawattUtil megawattUtil;

    /*@Autowired
    private OpenAiUtil openAiUtil;*/

    /*@Autowired
    private ZhiPuAiUtil zhiPuAiUtil;*/

    @Autowired
    private QWenUtil qWenUtil;

    @Value("${large.model.type}")
    private String largeModelType;

    /**
     * 大模型接口入口
     * @param prompt
     * @return
     */
    public String requestLargeMode(String prompt) {
        log.info("{} model大模型请求提示词:{}",largeModelType,prompt);
        try {
            //判断使用大模型接口的类型
            if (largeModelType.equals("megawatt")){
                log.info("大模型接口类型为:megawatt,调用大瓦特大模型接口");
                return megawattUtil.requestMegawatt(prompt);
            }/*else if (largeModelType.equals("gpt")){
                log.info("大模型接口类型为:gpt,调用OpenAi大模型接口");
                return openAiUtil.requestOpenAi(prompt);
            }else if (largeModelType.equals("zhipu")){
                log.info("大模型接口类型为:zhipu,调用智普Ai大模型接口");
                //智普model大模型返回的sql语句包含了其他文字说明
                String result = zhiPuAiUtil.requestZhiPuAi(prompt);
                if (prompt.contains("要求输出的SQL")){
                    result =result.substring(result.indexOf("```")+3, result.lastIndexOf("```")).trim();
                }

                return result;
            }*/else if (largeModelType.equals("qwen")){
                log.info("大模型接口类型为:qwen,调用千问通义大模型接口");
                return qWenUtil.requestQWen(prompt);
            } else {
                log.info("未知的大模型接口类型为:{}",largeModelType);
                return "未知的大模型接口类型为:"+largeModelType;
            }
        }catch (Exception e){
            throw e;
        }

    }
}
