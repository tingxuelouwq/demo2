package com.example.demo2.user.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * kevin<br/>
 * 2021/9/22 10:42<br/>
 */
public class TxWorker<T> implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 子线程回滚栅栏，count=1，表示只要一个子线程出现异常，所以子线程均回滚，即原子性多线程事务
     */
    private CountDownLatch rollbackLatch;
    /**
     * 主线程栅栏，count=子线程数，表示当所有子线程业务结束后，主线程才继续执行
     */
    private CountDownLatch mainThreadLatch;
    /**
     * 回滚标识
     */
    private AtomicBoolean rollbackFlag;

    /**
     * 事务管理器，通过其来提交或回滚事务
     */
    private PlatformTransactionManager transactionManager;

    /**
     * 事务相关的业务层接口，具体的业务层接口需要实现该类
     */
    private TxService<T> txService;
    /**
     * 供<see>{@link TxService#invoke(T)}</see>使用
     */
    private List<T> txTasks;
    /**
     * 收集<see>{@link TxService#invoke(T)}</see>的结果
     */
    private BlockingDeque<TxResult> txResults;

    @Override
    public void run() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);

        txTasks.forEach(txTask -> txResults.add(txService.invoke(txTask)));

        mainThreadLatch.countDown();

        try {
            rollbackLatch.await();
        } catch (InterruptedException e) {
            logger.error("子线程调用await，等待主线程唤醒子线程时出现异常，部分回滚");
            rollbackFlag.set(true);
        }

        if (rollbackFlag.get()) {
            transactionManager.rollback(status);
        } else {
            transactionManager.commit(status);
        }
    }

    public void setRollbackLatch(CountDownLatch rollbackLatch) {
        this.rollbackLatch = rollbackLatch;
    }

    public void setMainThreadLatch(CountDownLatch mainThreadLatch) {
        this.mainThreadLatch = mainThreadLatch;
    }

    public void setRollbackFlag(AtomicBoolean rollbackFlag) {
        this.rollbackFlag = rollbackFlag;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setTxService(TxService<T> txService) {
        this.txService = txService;
    }

    public void setTxTasks(List<T> txTasks) {
        this.txTasks = txTasks;
    }

    public void setTxResults(BlockingDeque<TxResult> txResults) {
        this.txResults = txResults;
    }
}
