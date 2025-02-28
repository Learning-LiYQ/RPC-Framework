package com.lyq.rpcspringbootstarter.annotation;

import com.lyq.rpcspringbootstarter.bootstrap.RpcConsumerBootstrap;
import com.lyq.rpcspringbootstarter.bootstrap.RpcInitBootstrap;
import com.lyq.rpcspringbootstarter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启动RPC注解
 * @author lyq
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * 是否需要启动server
     * @return
     */
    boolean needServer() default true;
}
