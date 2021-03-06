package org.jon.lv;

import org.jon.lv.server.ServerProviderRegisterCenter;
import org.jon.lv.service.UserService;
import org.jon.lv.service.impl.UserServiceImpl;

import java.io.IOException;

/**
 * Package: org.jon.lv.ServerMain
 * Description: 描述
 * Copyright: Copyright (c) 2017
 *
 * @author lv bin
 * Date: 2017/12/28 10:43
 * Version: V1.0.0
 */
public class ServerMain {
    public static void main(String[] args) throws IOException {
        ServerProviderRegisterCenter.registerService(UserService.class, UserServiceImpl.class);

        ServerProviderRegisterCenter.startServer(6600);
    }
}
