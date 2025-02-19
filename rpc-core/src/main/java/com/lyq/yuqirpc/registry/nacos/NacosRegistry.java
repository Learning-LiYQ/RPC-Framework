package com.lyq.yuqirpc.registry.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.lyq.yuqirpc.config.RegistryConfig;
import com.lyq.yuqirpc.model.ServiceMetaInfo;
import com.lyq.yuqirpc.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class NacosRegistry implements Registry {

    private NamingService namingService;

    @Override
    public void init(RegistryConfig registryConfig) {
//        Properties properties = new Properties();
//        properties.setProperty("serverAddr", registryConfig.getAddress());
//        properties.setProperty("username", registryConfig.getUsername());
//        properties.setProperty("password", registryConfig.getPassword());
        try {
            namingService = NacosFactory.createNamingService(registryConfig.getAddress());
        } catch (NacosException e) {
            throw new RuntimeException("初始化 Nacos 注册中心失败", e);
        }
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        try {
            Instance instance = new Instance();
            instance.setIp(serviceMetaInfo.getServiceHost());
            instance.setPort(serviceMetaInfo.getServicePort());
            namingService.registerInstance(serviceMetaInfo.getServiceKey(), instance);
        } catch (NacosException e) {
            throw new Exception("注册 Nacos 服务失败", e);
        }
    }

    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) throws Exception {
        try {
            namingService.deregisterInstance(serviceMetaInfo.getServiceKey(), serviceMetaInfo.getServiceHost(), serviceMetaInfo.getServicePort());
        } catch (NacosException e) {
            throw new Exception("注销 Nacos 服务失败", e);
        }
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        List<ServiceMetaInfo> serviceMetaInfos = new ArrayList<>();
        try {
            List<Instance> instances = namingService.getAllInstances(serviceKey);
            for (Instance instance : instances) {
                ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
                serviceMetaInfo.setServiceName(serviceKey);
                serviceMetaInfo.setServiceHost(instance.getIp());
                serviceMetaInfo.setServicePort(instance.getPort());
                serviceMetaInfos.add(serviceMetaInfo);
            }
        } catch (NacosException e) {
            throw new RuntimeException("Failed to discover services from Nacos", e);
        }
        return serviceMetaInfos;
    }

    @Override
    public void destroy() {
        // Nacos client does not require explicit destruction
    }
} 