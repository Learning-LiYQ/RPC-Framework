package com.lyq.yuqirpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.lyq.yuqirpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpiLoader {
    /**
     * 存储已加载的类
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 对象实例缓存（避免重复new），类路径->对象实例（单例模式）
     */
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统SPI目录
     */
    private static final String SYSTEM_SPI_SIR = "META-INF/rpc/system/";

    /**
     * 用户自定义SPI目录
     */
    private static final String CUSTOM_SPI_SIR = "META-INF/rpc/custom/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{SYSTEM_SPI_SIR, CUSTOM_SPI_SIR};

    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOADER_CLASS_LIST = Arrays.asList(Serializer.class);

    /**
     * 加载所有类型
     */
    public static void loadAll() {
        log.info("加载所有SPI");
        for (Class<?> aClass : LOADER_CLASS_LIST) {
            load(aClass);
        }
    }

    /**
     * 加载某个类型
     * @param loadClass
     * @return
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        log.info("加载类型为 {} 的SPI", loadClass.getName());
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for (String dir : SCAN_DIRS) {
            List<URL> resources = ResourceUtil.getResources(dir + loadClass.getName());
            // 读取每个资源文件
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] strArray = line.split("=");
                        if (strArray.length > 1) {
                            String key = strArray[0];
                            String className = strArray[1];
                            keyClassMap.put(key, Class.forName(className));
                        }
                    }
                } catch (Exception e) {
                    log.error("spi resource load error", e);
                }
            }
        }
        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }

    /**
     * 获取某个接口的实例
     * @param tClass
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T getInstance(Class<T> tClass, String key) {
        String tClassName = tClass.getName();
        Map<String, Class<?>> classMap = loaderMap.get(tClassName);
        if (classMap == null) {
            throw new RuntimeException(String.format("spiLoader未加载类型：%s", tClassName));
        }
        if (!classMap.containsKey(key)) {
            throw new RuntimeException(String.format("spiLoader 的%s不存在key=%s的类型", tClassName, key));
        }
        // 得到要加载的类型
        Class<?> implClass = classMap.get(key);
        // 先从缓存中加载实例
        String implClassName = implClass.getName();
        if (!instanceCache.containsKey(implClassName)) {
            try {
                instanceCache.put(implClassName, implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                String errorMsg = String.format("%s 类实例化失败", implClassName);
                throw new RuntimeException(errorMsg, e);
            }
        }
        return (T) instanceCache.get(implClassName);
    }
}
