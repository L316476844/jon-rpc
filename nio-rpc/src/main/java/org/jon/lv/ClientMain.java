package org.jon.lv;

import org.jon.lv.bean.User;
import org.jon.lv.client.RpcClient;
import org.jon.lv.service.UserService;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Package: org.jon.lv.ClientMain
 * Description: 描述
 * Copyright: Copyright (c) 2017
 *
 * @author lv bin
 * Date: 2017/12/28 10:43
 * Version: V1.0.0
 */
public class ClientMain {
    public static void main(String[] args) throws IOException {

        InetSocketAddress inetSocketAddress = new InetSocketAddress(8800);

        UserService userService = RpcClient.getRemoteService(UserService.class, inetSocketAddress);

        userService.sayHello();

        userService.sayHello("jon");

        User user = userService.getUserById(8L);

        System.out.println(user);

        User user2 = userService.findUserByNameAndMobile("jon", "1760210");

        System.out.println(user2);

        User userbean = new User(66L, "张三", "238984334", "上海市");

        System.out.println(userService.saveUser(userbean));

    }
}
