package com.lyq.yuqirpc.registry;

import com.lyq.yuqirpc.registry.nacos.NacosRegistry;
import com.lyq.yuqirpc.spi.SpiLoader;

/**
 * 注册中心工厂
 */
public class RegistryFactory {
    static {
        SpiLoader.load(Registry.class);
    }

    public static final Registry DEFAULT_REGISTRY = new NacosRegistry();

    public static Registry getInstance(String key){
        return SpiLoader.getInstance(Registry.class, key);
    }
}
