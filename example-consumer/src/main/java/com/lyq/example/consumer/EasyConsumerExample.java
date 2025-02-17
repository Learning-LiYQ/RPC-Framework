package com.lyq.example.consumer;

import com.lyq.common.model.User;
import com.lyq.common.service.UserService;
import com.lyq.yuqirpc.proxy.ServiceProxyFactory;

public class EasyConsumerExample {
    public static void main(String[] args) {
        // 通过RPC框架获取UserService的实现类
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("tony");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.toString());
        }
        else {
            System.out.println("newUser is null");
        }
    }
}
