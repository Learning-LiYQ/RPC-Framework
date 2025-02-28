package com.lyq.rpcspringbootstarter.bootstrap;

import com.lyq.rpcspringbootstarter.annotation.RpcService;
import com.lyq.yuqirpc.RpcApplication;
import com.lyq.yuqirpc.config.RegistryConfig;
import com.lyq.yuqirpc.config.RpcConfig;
import com.lyq.yuqirpc.model.ServiceMetaInfo;
import com.lyq.yuqirpc.registry.LocalRegistry;
import com.lyq.yuqirpc.registry.Registry;
import com.lyq.yuqirpc.registry.RegistryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 服务提供者启动类
 * @author lyq
 */
@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {

    /**
     * 在Bean初始化之后，注册服务
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        // 如果该类使用了RpcService注解，则向注册中心注册服务
        if (rpcService != null) {
            // 1.获取服务的基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();

            // 2.注册服务
            // 本地注册
            LocalRegistry.register(serviceName, interfaceClass);
            // 把服务注册到注册中心
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
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
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
