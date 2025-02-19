package com.lyq.yuqirpc;

import com.lyq.yuqirpc.config.RegistryConfig;
import com.lyq.yuqirpc.config.RpcConfig;
import com.lyq.yuqirpc.constant.RpcConstant;
import com.lyq.yuqirpc.registry.Registry;
import com.lyq.yuqirpc.registry.RegistryFactory;
import com.lyq.yuqirpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC框架应用
 * 存放项目用到的全局变量，通过双检锁单例模式实现
 * @author lyq
 */
@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    /**
     * 自定义配置初始化
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("Rpc init, rpcConfig:{}", newRpcConfig.toString());
        // 注册中心初始化
        RegistryConfig registryCfg = rpcConfig.getRegistryCfg();
        Registry registry = RegistryFactory.getInstance(registryCfg.getRegistry());
        registry.init(registryCfg);
        log.info("registry init, registryCfg:{}", registryCfg);
    }

    /**
     * 初始化
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // 加载失败，使用默认配置
            newRpcConfig = new RpcConfig();
        }

        init(newRpcConfig);
    }

    /**
     * 单例模式，获取配置类
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
