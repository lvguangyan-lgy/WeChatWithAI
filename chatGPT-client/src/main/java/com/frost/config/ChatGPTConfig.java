package com.frost.config;

import com.frost.utils.SSLSocketClientUtil;
import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.util.Proxys;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.X509TrustManager;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;


@Configuration
public class ChatGPTConfig {

    @Value("${chatGPT.appKey}")
    private String appKey;
    @Value("${chatGPT.host}")
    private String apiHost;
    @Value("${proxy.host}")
    private String proxyHost;
    @Value("${proxy.port}")
    private int proxyPort;


    @Bean
    public ChatGPT chatGPT(){
        //国内需要代理
        Proxy proxy = Proxys.http(proxyHost, proxyPort);
        ChatGPT chatGPT = ChatGPT.builder()
                .apiKey(appKey)
                .proxy(proxy)
                .apiHost(apiHost) //gpt服务地址
                .build()
                .init();
        return chatGPT;
    }

    @Bean
    public OkHttpClient client(){
        //构建客户端
        Proxy proxy = Proxys.http(proxyHost, proxyPort);
        X509TrustManager manager = SSLSocketClientUtil.getX509TrustManager();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(5, 1, TimeUnit.SECONDS))
                .readTimeout(5000L, TimeUnit.MILLISECONDS)
                .sslSocketFactory(SSLSocketClientUtil.getSocketFactory(manager), manager)// 忽略校验
                .hostnameVerifier(SSLSocketClientUtil.getHostnameVerifier());//忽略校验
        builder.proxy(proxy);

        OkHttpClient client = builder.build();
        return client;
    }

}
