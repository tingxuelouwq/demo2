package com.example.demo2.user.service.impl;

import com.example.demo2.user.service.User2Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * kevin<br/>
 * 2020/11/5 18:50<br/>
 */
@Component
public class AsyncTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private User2Service user2Service;

    @Async
    public void archive() throws InterruptedException {
        logger.info("archive方法开始");

        user2Service.test2();

        logger.info("archive方法结束");
    }
}
