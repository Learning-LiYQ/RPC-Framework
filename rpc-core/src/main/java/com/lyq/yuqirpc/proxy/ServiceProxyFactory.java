package com.lyq.yuqirpc.proxy;

import com.lyq.yuqirpc.RpcApplication;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂
 * 根据动态代理根据要生成的对象类型，自动生成代理对象
 * @author lyq
 */
public class ServiceProxyFactory {
    /**
     * 根据服务类获取代理对象
     * @param serviceClass
     * @return
     * @param <T>
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        // 如果 Mock配置为true，则使用 Mock代理
        if (RpcApplication.getRpcConfig().isMock()) {
            return getMockProxy(serviceClass);
        }

        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }

    /**
     * 根据服务类获取Mock代理对象
     * @param serviceClass
     * @return
     * @param <T>
     */
    public static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockServiceProxy());
    }
}
