package com.lyq.example.provider;

import com.lyq.common.service.UserService;
import com.lyq.yuqirpc.registry.LocalRegistry;
import com.lyq.yuqirpc.server.NettyHttpServer;

/**
 * 简易服务提供者示例
 * @author lyq
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        // 启动web服务
        NettyHttpServer nettyHttpServer = new NettyHttpServer();
        nettyHttpServer.doStart(8080);
    }
}
