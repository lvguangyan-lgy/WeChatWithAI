package com.rgzn.ttd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TtdApplication {
    public static void main(String[] args) {
        SpringApplication.run(TtdApplication.class, args);
    }
}

