package com.example.demo2.user.service.impl;

import com.example.demo2.user.entity.User;
import com.example.demo2.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * kevin<br/>
 * 2020/12/31 11:00<br/>
 */
@Service
public class User3ServiceImpl {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void tx2(Integer id) {
        User user = userRepository.getOne(id);
        user.setUname("tom");
        userRepository.save(user);

        if (id == 3) {
            throw new RuntimeException();
        }
    }
}
