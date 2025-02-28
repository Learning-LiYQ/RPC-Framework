package com.lyq.example.provider;

import com.lyq.rpcspringbootstarter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc(needServer = false)
public class SpringBootProviderExample {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootProviderExample.class, args);
    }
}
