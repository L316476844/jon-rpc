package org.jon.lv.service.impl;

import org.jon.lv.bean.User;
import org.jon.lv.service.UserService;

import java.util.Random;

/**
 * Package: org.jon.lv.service.impl.UserServiceImpl
 * Description: 描述
 * Copyright: Copyright (c) 2017
 *
 * @author lv bin
 * Date: 2017/12/27 14:27
 * Version: V1.0.0
 */
public class UserServiceImpl implements UserService {


    @Override
    public void sayHello() {
        System.out.println("======================sayHello===");
    }

    @Override
    public void sayHello(String word) {
        System.out.println("======================sayHello---param===" + word);
    }

    @Override
    public User getUserById(Long id) {
        User user = new User(id, "name".concat(String.valueOf(id)),
                "mobile".concat(String.valueOf(id)), "address".concat(String.valueOf(id)));

        System.out.println("$$$$$$$$$$$$$$$$$$$$getUserById$$$$$$$" + user.toString());

        return user;
    }

    @Override
    public User findUserByNameAndMobile(String name, String mobile) {
        User user = new User(new Random().nextLong(), name, mobile, "address");

        System.out.println("^^^^^^^^^^findUserByNameAndMobile^^^^^^^^^" + user.toString());

        return user;
    }

    @Override
    public Long saveUser(User user) {
        System.out.println("*************************" + user.toString());
        return new Random().nextLong();
    }
}
