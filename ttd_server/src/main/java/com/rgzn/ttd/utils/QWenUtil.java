package com.rgzn.ttd.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class QWenUtil {

    private static final Logger log = LoggerFactory.getLogger(QWenUtil.class);

    @Value("${dashscope.qwen.host}")
    private String qWenHost;

    @Value("${dashscope.qwen.apiKey}")
    private String apiKey;

    @Value("${ignore.https.certificate}")
    private boolean ignoreCertificate;

    @Value("${http.timeout}")
    private int timeout;

    public String requestQWen(String prompt) {
        prompt = prompt.replace("[","").replace("]","")
                .replace("{","").replace("}","").replace("\"","'");
        String result = "";
        //封装请求体
        String jsonStr = "{\n" +
                "    \"model\": \"qwen-max\",\n" +
                "    \"input\":{\n" +
                "        \"messages\":[      \n" +
                "            {\n" +
                "                \"role\": \"system\",\n" +
                "                \"content\": \"You are a helpful assistant.\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"role\": \"user\",\n" +
                "                \"content\": \""+prompt+"\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"parameters\": {\n" +
                "        \"result_format\": \"message\"\n" +
                "    }\n" +
                "}";
        jsonStr = JSONObject.parseObject(jsonStr).toJSONString();
        OkHttpClient client;
        //开启忽略SSL证书
        if (ignoreCertificate){
            log.warn("关闭SSL证书安全校验......");
            client = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
                    .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                    .build();
        }else {
            client = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .build();
        }

        // 创建MediaType对象，指定发送数据的格式
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        // 创建RequestBody对象，发送json数据
        RequestBody requestBody = RequestBody.create(JSON,jsonStr);
        //封装请求头
        Headers headers = Headers.of(
                "Content-Type", "application/json;charset=utf-8",
                "Accept-Encoding", "utf-8",
                "Authorization","Bearer "+apiKey
        );
        // 创建Request对象，设置url和RequestBody
        Request request = new Request.Builder()
                .url(qWenHost)
                .post(requestBody)
                .headers(headers)
                .build();

        // 创建Call对象
        Call call = client.newCall(request);

        try {
            // 同步执行请求
            long startTime = System.currentTimeMillis();
            Response response = call.execute();
            long endTime = System.currentTimeMillis();

            // 处理响应
            if (response.isSuccessful()) {
                result = response.body().string();
                //处理大模型返回值
                JSONObject jsonObject = JSONObject.parseObject(result);
                JSONObject output = jsonObject.getJSONObject("output");
                JSONArray choices = output.getJSONArray("choices");
                JSONObject choicesData = choices.getJSONObject(0);
                JSONObject message = choicesData.getJSONObject("message");
                result = message.getString("content").replace("#","");
                log.info("千问通义大模型返回:{}",result);
                log.info("千问通义大模型接口耗时:{}秒",(endTime-startTime)/1000);
            }else {
                log.error("千问通义大模型接口:{},请求头:{},请求体:{},异常返回:{}",qWenHost,headers,jsonStr,response.message());
                throw new RuntimeException("千问通义大模型接口调用返回异常,"+response.message());
            }
            return result;
        } catch (Exception e) {
            log.error("千问通义大模型接口:{},请求头:{},请求体:{},调用异常:{}",qWenHost,headers,jsonStr,e.getMessage());
            throw new RuntimeException("千问通义大模型接口调用异常,"+e.getMessage());
        }
    }

}
