package org.jon.lv.spi.impl;

import org.jon.lv.spi.HelloService;

/**
 * Package: org.jon.lv.spi.impl.CHelloServiceImpl
 * Description: 描述
 * Copyright: Copyright (c) 2017
 *
 * @author lv bin
 * Date: 2018/1/18 14:21
 * Version: V1.0.0
 */
public class CHelloServiceImpl implements HelloService{
    @Override
    public String sayHello() {
        return "Welcome to C world";
    }
}
