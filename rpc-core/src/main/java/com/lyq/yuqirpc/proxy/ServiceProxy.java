package com.lyq.yuqirpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.lyq.yuqirpc.RpcApplication;
import com.lyq.yuqirpc.config.RegistryConfig;
import com.lyq.yuqirpc.config.RpcConfig;
import com.lyq.yuqirpc.constant.RpcConstant;
import com.lyq.yuqirpc.model.RpcRequest;
import com.lyq.yuqirpc.model.RpcResponse;
import com.lyq.yuqirpc.model.ServiceMetaInfo;
import com.lyq.yuqirpc.registry.Registry;
import com.lyq.yuqirpc.registry.RegistryFactory;
import com.lyq.yuqirpc.registry.RegistryKeys;
import com.lyq.yuqirpc.serializer.JdkSerializer;
import com.lyq.yuqirpc.serializer.Serializer;
import com.lyq.yuqirpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 服务代理（基于JDK动态代理）
 * @function 通过代理简化客户端的调用流程
 * @author lyq
 */
public class ServiceProxy implements InvocationHandler {
    private final Serializer serializer;

    public ServiceProxy() {
        // 根据配置获取序列化器
        this.serializer = SerializerFactory.getSerializer(RpcApplication.getRpcConfig().getSerializer());
    }

    /**
     * 调用代理
     * 用户调用方法时会改为调用invoke方法
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serviceName = method.getDeclaringClass().getName();
        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);

            // 根据配置获取注册中心实例，从注册中心获取服务和服务地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryCfg().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }
            // todo:暂时取第一个，可以做负载均衡
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

            // 发送请求
            try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
                    .body(bodyBytes)
                    .execute()) {
                byte[] bytes = httpResponse.bodyBytes();
                // 反序列化
                RpcResponse rpcResponse = serializer.deserialize(bytes, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
