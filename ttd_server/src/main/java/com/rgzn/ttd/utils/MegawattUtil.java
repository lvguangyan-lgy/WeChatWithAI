package com.rgzn.ttd.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MegawattUtil {

    private static final Logger log = LoggerFactory.getLogger(MegawattUtil.class);

    @Value("${megawatt.host}")
    private String megawattHost;

    @Value("${megawatt.appId}")
    private String megawattAppId;

    @Value("${megawatt.reAppId}")
    private String megawattReAppId;

    @Value("${megawatt.appSecret}")
    private String megawattAppSecret;

    @Value("${megawatt.frequencyPenalty}")
    private double frequencyPenalty;

    @Value("${ignore.https.certificate}")
    private boolean ignoreCertificate;

    @Value("${http.timeout}")
    private int timeout;

    public String requestMegawatt(String prompt) {
        //获取大模型接口调用的密钥
        String appKey = getAppKey();
        if (StringUtils.isEmpty(appKey)){
            return "";
        }
        prompt = prompt.replace("[","").replace("]","")
                .replace("{","").replace("}","").replace("\"","'");
        String result = "";
        //封装请求体
        String jsonStr = "{\n" +
                "\t\"relAppId\": \""+megawattReAppId+"\",\n"+
                "\t\"frequency_penalty\": "+frequencyPenalty+",\n"+
                "\t\"model\": \"Megawatt_13b\",\n" +
                "\t\"messages\": [{\n" +
                "\t\t\"role\": \"user\",\n" +
                "\t\t\"content\": \""+prompt+"\"\n" +
                "\t}],\n" +
                "\t\"max_tokens\": 2048,\n" +
                "\t\"stream\": false,\n" +
                "\t\"generate_config\": {\n" +
                "\t\t\"temperature\": 0.5\n" +
                "\t}\n" +
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
                "appId",megawattAppId,
                "appKey",appKey
        );
        // 创建Request对象，设置url和RequestBody
        String url = megawattHost+"/knowledgeService/extChatApi/chat";
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .headers(headers)
                .build();

        // 创建Call对象
        okhttp3.Call call = client.newCall(request);

        try {
            // 同步执行请求
            long startTime = System.currentTimeMillis();
            Response response = call.execute();
            long endTime = System.currentTimeMillis();

            // 处理响应
            if (response.isSuccessful()) {
                result = response.body().string();
                log.info("Megawatt大模型成功返回:{}",result);
                log.info("Megawatt大模型接口耗时:{}秒",(endTime-startTime)/1000);

                //解析大模型返回值,提取回答内容信息
                try {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    JSONObject choices = jsonObject.getJSONObject("choices");
                    JSONArray responseJson = choices.getJSONArray("response");
                    JSONObject choicesData = responseJson.getJSONObject(0);
                    result = choicesData.getString("content").replace("#","");
                }catch (Exception e){
                    log.warn("解析Megawatt大模型返回值异常,返回原始值");
                }
                return result;
            }else {
                result = response.body() == null ? "" : response.body().string();
                log.error("Megawatt大模型接口:{},请求头:{},请求体:{},异常body返回:{},异常信息返回:{}",url,headers,jsonStr,result,response.message());
                throw new RuntimeException("Megawatt大模型接口异常响应,异常信息:"+response.message());
            }
        } catch (Exception e) {
            log.error("Megawatt大模型接口:{},请求头:{},请求体:{},调用异常:{}",url,headers,jsonStr,e.getMessage());
            throw new RuntimeException("Megawatt大模型接口的调用失败,报错信息:"+e.getMessage());
        }
    }

    private String getAppKey() {
        //1.从缓存中获取密钥
        String appkey = (String) CacheManagerHelper.getInstance().get("appKey");
        if (StringUtils.isNotEmpty(appkey)){
            return appkey;
        }
        //2.调用接口获取密钥
        String result = "";
        String jsonStr = "{\n" +
                "\t\"appId\": \""+megawattAppId+"\",\n" +
                "\t\"appSecret\": \""+megawattAppSecret+"\"\n" +
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
        //请求头
        Headers headers = Headers.of(
                "Content-Type", "application/json;charset=utf-8",
                "Accept-Encoding", "utf-8"
        );
        // 创建Request对象，设置url和RequestBody
        String url = megawattHost+"/knowledgeService/extSecret/generateAppKey";
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .headers(headers)
                .build();

        // 创建Call对象
        okhttp3.Call call = client.newCall(request);

        try {
            // 同步执行请求
            long startTime = System.currentTimeMillis();
            Response response = call.execute();
            long endTime = System.currentTimeMillis();

            // 处理响应
            if (response.isSuccessful()) {
                result = response.body().string();
                log.info("智能体平台返回appKey返回:{}",result);
                log.info("智能体平台接口耗时:{}秒",(endTime-startTime)/1000);

                JSONObject resultJson = JSONObject.parseObject(result);
                appkey = resultJson.getJSONObject("resultObject").getString("appKey");
                //3.放到缓存
                CacheManagerHelper.getInstance().put("appKey",appkey);
                return appkey;
            }else {
                log.error("Megawatt大模型接口异常响应:{},请求头:{},请求体:{},异常返回:{}",url,headers,jsonStr,response.message());
                throw new RuntimeException("Megawatt大模型接口异常响应,异常信息:"+response.message());
            }
        } catch (Exception e) {
            log.error("获取Megawatt大模型接口的appKey失败:{},请求头:{},请求体:{},调用异常返回:{}",url,headers,jsonStr,e.getMessage());
            throw new RuntimeException("获取Megawatt大模型接口的appKey失败,报错信息:"+e.getMessage());
        }
    }


}
