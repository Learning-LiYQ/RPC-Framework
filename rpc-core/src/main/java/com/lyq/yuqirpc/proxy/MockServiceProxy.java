package com.lyq.yuqirpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 模拟代理服务
 * @author lyq
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     * 根据方法的返回值类型，返回默认值
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        log.info("Mock invoke {}", method.getName());
        return getDefaultObject(returnType);
    }

    /**
     * 根据类型返回默认值
     * @param type
     * @return
     */
    private Object getDefaultObject(Class<?> type) {
        // 基本类型
        if (type.isPrimitive()) {
            if (type == boolean.class) {
                return false;
            }
            else if (type == short.class) {
                return (short) 0;
            }
            else if (type == int.class) {
                return 0;
            }
            else if (type == long.class) {
                return 0L;
            }
        }
        // 对象类型
        return null;
    }
}
