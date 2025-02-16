package com.lyq.example.provider;

import com.lyq.common.service.UserService;
import com.lyq.yuqirpc.RpcApplication;
import com.lyq.yuqirpc.registry.LocalRegistry;
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
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        // 启动web服务
        NettyHttpServer nettyHttpServer = new NettyHttpServer();
        nettyHttpServer.doStart(8080);
    }
}
