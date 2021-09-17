package com.example.demo2.user.controller;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 * kevin<br/>
 * 2021/4/8 15:21<br/>
 */
@Data
@JacksonXmlRootElement(localName = "User")
public class UserXmlModel {

    private Integer id;
    @JacksonXmlProperty(localName = "UserName")
    private String username;
    private Integer age;
    private String sex;

    public UserXmlModel(Integer id, String username, Integer age, String sex) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.sex = sex;
    }
}
