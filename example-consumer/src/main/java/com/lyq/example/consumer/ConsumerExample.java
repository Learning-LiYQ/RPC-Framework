package com.lyq.example.consumer;

import com.lyq.common.model.User;
import com.lyq.common.service.UserService;
import com.lyq.yuqirpc.RpcApplication;
import com.lyq.yuqirpc.config.RpcConfig;
import com.lyq.yuqirpc.proxy.ServiceProxyFactory;
import com.lyq.yuqirpc.utils.ConfigUtils;

public class ConsumerExample {
    public static void main(String[] args) {
        // 初始化RPC框架
        RpcApplication.init();
        // 通过RPC框架获取UserService的实现类
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("lllyyyqqq");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.toString());
        }
        else {
            System.out.println("newUser is null");
        }

        int num = userService.getNumber();
        System.out.println("number is " + num);
    }
}
