package org.jon.lv.spi;

import java.util.ServiceLoader;

/**
 * Package: org.jon.lv.spi.ServiceLoaderTest
 * Description: 描述
 * Copyright: Copyright (c) 2017
 *
 * @author lv bin
 * Date: 2018/1/18 14:23
 * Version: V1.0.0
 */
public class ServiceLoaderTest {

    public static void main(String[] args) {

        /**
         * 参考：https://juejin.im/post/5a6036d5518825734107f96d
         *
         * 当服务的提供者，提供了服务接口（java.sql.Driver）的一种实现之后，
         * 在jar包的META-INF/services/目录里同时创建一个以服务接口命名的文件。
         * 该文件里就是实现该服务接口的具体实现类。而当外部程序装配这个模块的时候，
         * 就能通过该jar包META-INF/services/里的配置文件找到具体的实现类名，并装载实例化，完成模块的注入。
         *
         * java.util.ServiceLoader 依赖于 resource目录下 META-INF/services/org.jon.lv.spi.HelloService
         * 约定好的实现方式
         */


        ServiceLoader<HelloService> loaders = ServiceLoader.load(HelloService.class);
        for (HelloService loader : loaders) {
            System.out.println(loader.sayHello());
        }
    }
}
