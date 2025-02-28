package com.lyq.yuqirpc.bootstrap;

import com.lyq.yuqirpc.RpcApplication;
import com.lyq.yuqirpc.config.RegistryConfig;
import com.lyq.yuqirpc.config.RpcConfig;
import com.lyq.yuqirpc.model.ServerRegisterInfo;
import com.lyq.yuqirpc.model.ServiceMetaInfo;
import com.lyq.yuqirpc.registry.LocalRegistry;
import com.lyq.yuqirpc.registry.Registry;
import com.lyq.yuqirpc.registry.RegistryFactory;
import com.lyq.yuqirpc.server.NettyHttpServer;

import java.util.List;

/**
 * 服务提供者初始化
 * @author lyq
 */
public class ProviderBootstrap {
    public static void init(List<ServerRegisterInfo<?>> serverRegisterInfos) {
        // Rpc框架初始化（读取配置、连接注册中心）
        RpcApplication.init();
        // 获取全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        for (ServerRegisterInfo<?> serverRegisterInfo : serverRegisterInfos) {
            String serviceName = serverRegisterInfo.getServiceName();
            // 本地注册
            LocalRegistry.register(serviceName, serverRegisterInfo.getImplClass());
            // 把服务注册到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryCfg();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + "服务注册失败", e);
            }
        }

        // 启动web服务器
        NettyHttpServer nettyHttpServer = new NettyHttpServer();
        nettyHttpServer.doStart(rpcConfig.getServerPort());
    }
}
