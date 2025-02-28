package com.lyq.yuqirpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务注册信息（用于快速启动）
 * @param <T>
 * @author lyq
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerRegisterInfo<T> {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 实现类
     */
    private Class<? extends T> implClass;
}
