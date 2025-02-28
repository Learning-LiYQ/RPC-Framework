package com.lyq.example.consumer;

import com.lyq.rpcspringbootstarter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc(needServer = false)
public class SpringBootConsumerExample {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootConsumerExample.class, args);
    }
}
