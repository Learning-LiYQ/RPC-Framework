package com.lyq.example.consumer;

import com.lyq.yuqirpc.config.RpcConfig;
import com.lyq.yuqirpc.utils.ConfigUtils;

public class ConsumerExample {
    public static void main(String[] args) {
        RpcConfig rpcConfig = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpcConfig);
    }
}
