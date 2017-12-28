package org.jon.lv.service;

import org.jon.lv.bean.User;

/**
 * Package: org.jon.lv.service.UserService
 * Description: 描述
 * Copyright: Copyright (c) 2017
 *
 * @author lv bin
 * Date: 2017/12/27 14:24
 * Version: V1.0.0
 */
public interface UserService {

    /**
     * 无返回值方法
     */
    void  sayHello();

    /**
     * 有参无返回值接口
     * @param word
     */
    void sayHello(String word);

    /**
     * 又返回值接口
     * @param id
     * @return
     */
    User  getUserById(Long id);

    /**
     * 多个请求参数
     * @param name
     * @param mobile
     * @return
     */
    User  findUserByNameAndMobile(String name, String mobile);

    /**
     * 参数为对象类型
     * @param user
     * @return
     */
    Long saveUser(User user);

}
