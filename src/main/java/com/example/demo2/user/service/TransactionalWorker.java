package com.example.demo2.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * kevin<br/>
 * 2021/9/18 14:09<br/>
 */
@Component
public class TransactionalWorker {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param workerCyclicBarrier
     * @param successCounter
     * @param runnable
     */
    @Transactional(rollbackFor = Exception.class)
    public void run(CyclicBarrier workerCyclicBarrier, AtomicInteger successCounter, Runnable runnable) {
        boolean isSuccess = false;
        try {
            runnable.run();
            successCounter.decrementAndGet();
            isSuccess = true;
        } catch (Exception e) {
            logger.error("TransactionalWorker当前线程执行出错!", e);
            isSuccess = false;
            throw e;
        } finally {
            try {
                workerCyclicBarrier.await();
            } catch (Exception e) {
                logger.error("TransactionalWorker当前线程执行CyclicBarrier.await出错!", e);
                if (isSuccess) {
                    // 要回滚计数，否则：假设全部线程都操作成功，但刚好超时，主线程shutdown线程池后，计数为0，会返回成功
                    successCounter.incrementAndGet();
                }
            }
        }
        if (successCounter.get() != 0) {
            logger.error("TransactionalWorker其他线程执行出错, 回滚事务!");
            throw new RuntimeException("TransactionalWorker其他线程执行出错, 回滚事务!");
        }
    }
}