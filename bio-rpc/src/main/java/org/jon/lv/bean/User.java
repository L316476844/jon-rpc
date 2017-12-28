package org.jon.lv.bean;

import java.io.Serializable;

/**
 * Package: org.jon.lv.bean.User
 * Description: 描述
 * Copyright: Copyright (c) 2017
 *
 * @author lv bin
 * Date: 2017/12/27 14:31
 * Version: V1.0.0
 */
public class User implements Serializable{
    private static final long serialVersionUID = 1646988104772996100L;

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 地址
     */
    private String address;

    public User() {
    }

    public User(Long id, String name, String mobile, String address) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
