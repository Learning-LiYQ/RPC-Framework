package com.lyq.common.service;

import com.lyq.common.model.User;

/**
 * 用户服务
 */
public interface UserService {
    /**
     * 获取用户
     * @param user
     * @return
     */
    User getUser(User user);

    /**
     * 获取数字
     * 用于测试 Mock 服务
     * @return
     */
    default int getNumber() {
        return 1;
    }
}
