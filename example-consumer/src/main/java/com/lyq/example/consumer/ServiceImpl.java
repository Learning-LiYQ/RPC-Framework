package com.lyq.example.consumer;

import com.lyq.common.model.User;
import com.lyq.common.service.UserService;
import com.lyq.rpcspringbootstarter.annotation.RpcReference;
import org.springframework.stereotype.Service;

@Service
public class ServiceImpl {
    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("liyuqi123");
        User serviceUser = userService.getUser(user);
        System.out.println(serviceUser.getName());
    }
}
