package com.lyq.example.provider;

import com.lyq.common.model.User;
import com.lyq.common.service.UserService;

/**
 * 用户服务实现
 */
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
