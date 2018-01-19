package org.jon.lv.spi.impl;

import org.jon.lv.spi.HelloService;

/**
 * Package: org.jon.lv.spi.impl.JavaHelloServiceImpl
 * Description: 描述
 * Copyright: Copyright (c) 2017
 *
 * @author lv bin
 * Date: 2018/1/18 14:22
 * Version: V1.0.0
 */
public class JavaHelloServiceImpl implements HelloService {
    @Override
    public String sayHello() {
        return "Welcome to Java world";
    }
}
