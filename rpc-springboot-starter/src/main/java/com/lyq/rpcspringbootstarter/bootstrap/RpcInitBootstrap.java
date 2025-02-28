package com.lyq.rpcspringbootstarter.bootstrap;

import com.lyq.rpcspringbootstarter.annotation.EnableRpc;
import com.lyq.yuqirpc.RpcApplication;
import com.lyq.yuqirpc.config.RpcConfig;
import com.lyq.yuqirpc.server.NettyHttpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取EnableRpc的注解值
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName()).get("needServer");

        // 框架初始化
        RpcApplication.init();
        // 获取全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        if (needServer) {
            // 启动服务器
            NettyHttpServer nettyHttpServer = new NettyHttpServer();
            nettyHttpServer.doStart(rpcConfig.getServerPort());
        }
        else {
            log.info("不启动server");
        }
    }
}
