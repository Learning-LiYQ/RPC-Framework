package com.lyq.yuqirpc.serializer;

import com.lyq.yuqirpc.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化器工厂
 * 用于获取序列化器
 * @author lyq
 */
public class SerializerFactory {
    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认序列化器为jdk
     */
    public static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取序列化器实例
     * @param key
     * @return
     */
    public static Serializer getSerializer(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }
}
