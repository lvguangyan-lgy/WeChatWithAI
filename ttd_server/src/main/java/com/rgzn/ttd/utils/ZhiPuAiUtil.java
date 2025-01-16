/*
package com.rgzn.ttd.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ZhiPuAiUtil {

    private static final Logger log = LoggerFactory.getLogger(ZhiPuAiUtil.class);

    @Autowired
    private ClientV4 client;

    */
/**
     * 同步调用
     *//*

    public String requestZhiPuAi(String prompt) {
        String result = "";

        ObjectMapper mapper = defaultObjectMapper();
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), prompt);
        messages.add(chatMessage);
        String requestId = "人工智能-"+System.currentTimeMillis();

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .maxTokens(4095)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .build();
        try {
            long startTime = System.currentTimeMillis();
            ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
            long endTime = System.currentTimeMillis();
            JSONObject response = JSONObject.parseObject(mapper.writeValueAsString(invokeModelApiResp));
            if (response.getInteger("code") == 200){
                String content = response.getJSONObject("data").getJSONArray("choices")
                        .getJSONObject(0).getJSONObject("message").getString("content");
                result = content.replace("#","");
            }else {
                result = "智普model大模型返回报错信息: " + response.getJSONObject("error").getString("message");
            }
            log.info("智普model大模型返回:{}",result);
            log.info("智普model大模型接口耗时:{}秒",(endTime-startTime)/1000);
        } catch (Exception e) {
            throw new RuntimeException("智普model大模型调用异常"+e.getMessage());
        }
        return result;
    }

    private static ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return mapper;
    }

}
*/
