package com.example.demo2.user.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * kevin<br/>
 * 2021/9/22 10:07<br/>
 * 原子性多线程事务执行器
 */
@Component
public class AtomicMultiThreadTxExecutor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final int defaultThreadSize = 5;

    @Autowired
    private PlatformTransactionManager transactionManager;

    public <T extends TxTask> List<TxResult> execute(List<T> tasks, TxService<T> txService) {
        return execute(tasks, txService, defaultThreadSize);
    }

    public <T extends TxTask> List<TxResult> execute(List<T> tasks, TxService<T> txService, int nThreads) {
        int nTasks = tasks.size();
        int nTasksPerThread = 1;    // 每个线程需要执行多少个任务
        if (nTasks > nThreads * nTasksPerThread) {
            nTasksPerThread = nTasks % nThreads == 0 ? nTasks / nThreads : nTasks / nThreads + 1;
            nThreads = nTasks % nTasksPerThread == 0 ? nTasks / nTasksPerThread : nTasks / nTasksPerThread + 1;
        } else {
            nThreads = nTasks;
        }

        CountDownLatch rollbackLatch = new CountDownLatch(1);
        CountDownLatch mainThreadLatch = new CountDownLatch(nThreads);
        AtomicBoolean rollbackFlag = new AtomicBoolean(false);
        BlockingDeque<TxResult> txResults = new LinkedBlockingDeque<>();
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        List<TxResult> failures = new ArrayList<>();

        for (int i = 0; i < nThreads; i++) {
            int endIndex = (i + 1) * nTasksPerThread;
            List<T> subTasks = tasks.subList(i * nTasksPerThread, Math.min(endIndex, nTasks));
            TxWorker<T> txWorker = new TxWorker<>();
            txWorker.setRollbackLatch(rollbackLatch);
            txWorker.setMainThreadLatch(mainThreadLatch);
            txWorker.setRollbackFlag(rollbackFlag);
            txWorker.setTransactionManager(transactionManager);
            txWorker.setTxService(txService);
            txWorker.setTxTasks(subTasks);
            txWorker.setTxResults(txResults);
            executorService.submit(txWorker);
        }

        try {
            // 等待所有子线程执行完毕mainThreadLatch#countDown()
            mainThreadLatch.await();

            // 查看子线程中业务的执行情况，如果存在异常，则置位回滚标识进而回滚所有子线程
            for (int i = 0; i < nTasks; i++) {
                TxResult txResult = txResults.take();
                if (txResult.isError()) {
                    logger.error("子线程中业务执行存在异常，整体回滚，docId: {}, message: {}", txResult.getDocId(), txResult.getMessage());
                    rollbackFlag.set(true);
                    failures.add(txResult);
                }
            }
        } catch (InterruptedException e) {
            logger.error("主线程调用await，等待所有子线程执行完毕时出现异常，整体回滚");
            rollbackFlag.set(true);
        } finally {
            rollbackLatch.countDown();
            executorService.shutdown();
            logger.info("原子性多线程事务结束，关闭线程池释放资源, hasShutdown=" + executorService.isShutdown());
        }
        return failures;
    }
}
