package com.lyq.yuqirpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.lyq.yuqirpc.RpcApplication;
import com.lyq.yuqirpc.model.RpcRequest;
import com.lyq.yuqirpc.model.RpcResponse;
import com.lyq.yuqirpc.serializer.JdkSerializer;
import com.lyq.yuqirpc.serializer.Serializer;
import com.lyq.yuqirpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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
        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            // 发送请求
            // todo:地址暂时硬编码，需要通过注册中心和服务发现机制解决
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
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
