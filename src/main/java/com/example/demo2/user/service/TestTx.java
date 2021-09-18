package com.example.demo2.user.service;

/**
 * kevin<br/>
 * 2021/9/18 16:53<br/>
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * 客户 服务实现类
 * </p>
 *
 * @author WuZhiWei
 * @since 2018-07-06
 */
@Transactional(rollbackFor = {Exception.class})
@Service
public class TestTx {

    @Autowired
    private UserService userService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    public void test() throws InterruptedException {
        CountDownLatch rollBackLatch = new CountDownLatch(1);
        CountDownLatch mainThreadLatch = new CountDownLatch(5);
        AtomicBoolean rollbackFlag = new AtomicBoolean(false);
        for (int i = 1; i <= 5; i++) {
            int t = i;
            new Thread(() -> {
                if (rollbackFlag.get()) return; //如果其他线程已经报错 就停止线程
                //设置一个事务
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 事物隔离级别，开启新事务，这样会比较安全些。
                TransactionStatus status = transactionManager.getTransaction(def); // 获得事务状态
                try {
                    userService.test(t);
                    mainThreadLatch.countDown();
                    rollBackLatch.await();//线程等待
                    if (rollbackFlag.get()) {
                        transactionManager.rollback(status);
                    } else {
                        transactionManager.commit(status);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //如果出错了 就放开锁 让别的线程进入提交/回滚  本线程进行回滚
                    rollbackFlag.set(true);
                    rollBackLatch.countDown();
                    mainThreadLatch.countDown();
                    transactionManager.rollback(status);
                }
            }).start();
        }

        try {
//            int i = 1 / 0; //主线程执行相关业务报错
        } catch (Exception e) {
            rollbackFlag.set(true);
            rollBackLatch.countDown();
        }
        //主线程业务执行完毕 如果其他线程也执行完毕 且没有报异常 正在阻塞状态中 唤醒其他线程 提交所有的事务
        //如果其他线程或者主线程报错 则不会进入if 会触发回滚
        if (!rollbackFlag.get()){
            mainThreadLatch.await();
            rollBackLatch.countDown();
        }
    }

}
