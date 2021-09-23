package com.example.demo2.user.service.impl;

import com.example.demo2.user.concurrent.TxResult;
import com.example.demo2.user.concurrent.TxService;
import com.example.demo2.user.controller.queryobject.TxQueryObject;
import com.example.demo2.user.entity.User;
import com.example.demo2.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * kevin<br/>
 * 2021/9/22 15:04<br/>
 */
@Service
public class UserService4Impl implements TxService<TxQueryObject> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public TxResult invoke(TxQueryObject txQueryObject) {
        int id = txQueryObject.getId();

        TxResult txResult = new TxResult();
        txResult.setError(false);
        txResult.setId(id + "");

        User user = userRepository.getOne(id);
        user.setUname("tom");
        userRepository.save(user);

        if (id == 4 || id == 5) {
            txResult.setMessage(id + " error happened");
            txResult.setError(true);
            throw new RuntimeException(txResult.getMessage());
        }
        return txResult;
    }
}
