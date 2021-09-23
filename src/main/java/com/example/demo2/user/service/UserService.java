package com.example.demo2.user.service;

import com.example.demo2.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * kevin<br/>
 * 2020/11/5 18:35<br/>
 */
public interface UserService {

    List<User> findAll(Integer age);

    void add(LocalDateTime regtime);

    void update(int id);

    void update2(int id);

    void tx();
}
