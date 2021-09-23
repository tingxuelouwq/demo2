package com.example.demo2.user.service.impl;

import com.example.demo2.user.concurrent.TxResult;
import com.example.demo2.user.concurrent.TxService;
import com.example.demo2.user.controller.MyTask;
import com.example.demo2.user.entity.User;
import com.example.demo2.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * kevin<br/>
 * 2021/9/22 15:04<br/>
 */
@Service
@Scope("prototype")
public class UserService4Impl implements TxService<MyTask> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;

    @Override
    public TxResult invoke(MyTask myTask) {
        int id = myTask.getId();

        TxResult txResult = new TxResult();
        txResult.setError(false);
        txResult.setDocId(id + "");

        User user = userRepository.getOne(id);
        user.setUname("tom");
        userRepository.save(user);

        if (id == 4) {
//            txResult.setDocId(id + "");
//            txResult.setMessage("error happened");
//            txResult.setError(true);
        }
        return txResult;
    }
}
