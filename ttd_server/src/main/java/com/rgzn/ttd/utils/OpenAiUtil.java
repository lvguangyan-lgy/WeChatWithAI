/*
package com.rgzn.ttd.utils;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.*;
import com.unfbx.chatgpt.entity.models.Model;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class OpenAiUtil {

    private static final Logger log = LoggerFactory.getLogger(OpenAiUtil.class);

    private OpenAiClient openAiClient3;
    private OpenAiClient openAiClient4;

    @Autowired
    public OpenAiUtil(@Qualifier("openAiClient3") OpenAiClient openAiClient3,@Qualifier("openAiClient4") OpenAiClient openAiClient4) {
        this.openAiClient3 = openAiClient3;
        this.openAiClient4 = openAiClient4;
    }


    public String requestOpenAi(String prompt) {
        return requestOpenAi(prompt,"");
    }

    */
/**
     *
     * @param prompt
     * @param modelName
     * @return
     *//*

    public String requestOpenAi(String prompt, String modelName) {
        String result = "";
        OpenAiClient openAiClient;
        if (StringUtils.isEmpty(modelName)){
            //聊天模型：gpt-3.5
            modelName = ChatCompletion.Model.GPT_3_5_TURBO.getName();
            openAiClient = openAiClient3;
            //聊天模型：gpt-4
            //modelName = ChatCompletion.Model.GPT_4.getName();
        }else {
            //聊天模型：gpt-4
            // openAiClient = openAiClient4;
            //账户没钱了, 换成聊天模型：gpt-3.5
            modelName = ChatCompletion.Model.GPT_3_5_TURBO.getName();
            openAiClient = openAiClient3;
        }
        Message message = Message.builder().role(Message.Role.USER).content(prompt).build();
        ChatCompletion chatCompletion = ChatCompletion
                .builder()
                .model(modelName)
                //GPT3.5的温度范围为0-0.7，而GPT4.0的温度范围为0-1。当温度为0时，结果稳定；当温度为0.7或1时，结果发散且具有创力。数值越大，回答越发散；数值越小，回答越稳定‌
                .temperature(0.2)
                .maxTokens(2048)
                .messages(Collections.singletonList(message))
                .build();
        //ChatCompletion chatCompletion = ChatCompletion.builder().messages(Arrays.asList(message)).build();

        long startTime = System.currentTimeMillis();
        ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion);
        long endTime = System.currentTimeMillis();
        */
/*chatCompletionResponse.getChoices().forEach(e -> {
            String content = e.getMessage().getContent();
        });*//*

        List<ChatChoice> choices = chatCompletionResponse.getChoices();
        for (ChatChoice choice : choices) {
            result= choice.getMessage().getContent();
        }
        log.info("OpenAi返回:{}",result);
        log.info("OpenAi接口耗时:{}秒",(endTime-startTime)/1000);
        return result.replace("#","");
    }

}
*/
