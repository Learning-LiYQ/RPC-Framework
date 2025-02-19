package com.lyq.yuqirpc.config;

import lombok.Data;

/**
 * 注册中心配置类
 * @author lyq
 */
@Data
public class RegistryConfig {
    /**
     * 注册中心
     */
    private String registry = "nacos";

    /**
     * 注册中心地址
     */
    private String address = "http://192.168.31.18:8848";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间（毫秒）
     */
    private Long timeout = 10000L;
}
