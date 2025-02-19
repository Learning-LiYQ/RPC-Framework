package com.lyq.example.provider;

import com.lyq.common.service.UserService;
import com.lyq.yuqirpc.RpcApplication;
import com.lyq.yuqirpc.config.RpcConfig;
import com.lyq.yuqirpc.model.ServiceMetaInfo;
import com.lyq.yuqirpc.registry.LocalRegistry;
import com.lyq.yuqirpc.registry.Registry;
import com.lyq.yuqirpc.registry.RegistryFactory;
import com.lyq.yuqirpc.server.NettyHttpServer;

/**
 * 服务提供者示例
 * @author lyq
 */
public class ProviderExample {
    public static void main(String[] args) {
        // 初始化RPC框架
        RpcApplication.init();
        // 注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // 向注册中心注册服务
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryCfg().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 启动web服务
        NettyHttpServer nettyHttpServer = new NettyHttpServer();
        nettyHttpServer.doStart(8080);
    }
}
