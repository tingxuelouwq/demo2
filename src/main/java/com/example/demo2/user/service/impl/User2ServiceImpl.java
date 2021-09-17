package com.example.demo2.user.service.impl;

import com.example.demo2.user.service.User2Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * kevin<br/>
 * 2020/11/5 18:38<br/>
 */
@Service
public class User2ServiceImpl implements User2Service {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional
    @Override
    public void test2() throws InterruptedException {
        logger.info("test2开始");
        Thread.sleep(2000);
        logger.info("test2结束");
        throw new RuntimeException("user2异常");
    }
}
