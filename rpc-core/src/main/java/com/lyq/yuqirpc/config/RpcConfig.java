package com.lyq.yuqirpc.config;

import lombok.Data;

/**
 * RPC框架配置类
 * @author lyq
 */
@Data
public class RpcConfig {
    /**
     * 名称
     */
    private String name = "yuqi-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "127.0.0.1";

    /**
     * 服务器端口号
     */
    private String serverPort = "8080";

    /**
     * 是否模拟调用
     */
    private boolean mock = false;

    /**
     * 序列化器
     */
    private String serializer = "jdk";
}
