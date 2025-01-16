/*
package com.rgzn.ttd.configurer;

import com.zhipu.oapi.ClientV4;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

*/
/**
 * 智普Ai客户端 配置
 *//*

@Configuration
public class ZhiPuAiConfigurer {

    @Value("${zhipu.api.secretKey}")
    private String apiSecretKey;

    @Bean
    public ClientV4 getZhiPuClientV4() throws Exception {
        return new ClientV4
                .Builder(apiSecretKey)
                .networkConfig(30,60,60,60, TimeUnit.SECONDS)
                .build();
    }

}

*/
