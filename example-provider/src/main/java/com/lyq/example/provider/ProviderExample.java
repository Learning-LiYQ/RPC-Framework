package com.lyq.example.provider;

import com.lyq.common.service.UserService;
import com.lyq.yuqirpc.bootstrap.ProviderBootstrap;
import com.lyq.yuqirpc.model.ServerRegisterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供者示例
 * @author lyq
 */
public class ProviderExample {
    public static void main(String[] args) {
        List<ServerRegisterInfo<?>> serverRegisterInfoList = new ArrayList<>();
        ServerRegisterInfo<UserService> serverRegisterInfo = new ServerRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serverRegisterInfoList.add(serverRegisterInfo);

        ProviderBootstrap.init(serverRegisterInfoList);
    }
}
