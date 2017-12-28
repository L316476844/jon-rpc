package org.jon.lv;

import org.jon.lv.bean.User;
import org.jon.lv.client.ConsumerRpcProxy;
import org.jon.lv.service.UserService;

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
    public static void main(String[] args) {

        InetSocketAddress inetSocketAddress = new InetSocketAddress(6600);

        UserService userService = ConsumerRpcProxy.getRemoteService(UserService.class, inetSocketAddress);

        userService.sayHello();

        userService.sayHello("jon");

        User user = userService.getUserById(8L);

        System.out.println(user);

        User user2 = userService.findUserByNameAndMobile("jon", "1760210");

        System.out.println(user2);

        System.out.println(userService.saveUser(user2));

    }
}
