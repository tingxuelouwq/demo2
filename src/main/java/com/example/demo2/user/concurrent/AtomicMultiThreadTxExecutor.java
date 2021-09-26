package com.example.demo2.user.concurrent;

import com.example.demo2.util.JsonUtil;
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
 * 原子性多线程事务执行器，使用方法：<br/>
 * 1.业务层接口实现类要实现<see>{@link TxService}</see>接口<br/>
 * 2.任务要继承<see>{@link TxTask}</see>类<br/>
 * 3.任务执行结果要继承<see>txResult</see>类<br/>
 *
 * 泛型参数类型说明：<br/>
 * R：单个任务的执行结果类型
 * T: 任务类型
 * U：执行任务所需额外参数的类型
 */
@Component
public class AtomicMultiThreadTxExecutor<R extends TxResult, T extends TxTask, U> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final int DEFAULT_THREAD_SIZE = 5;

    @Autowired
    private PlatformTransactionManager transactionManager;

    public TxResults<R> execute(TxService<R, T, U> txService, List<T> tasks, U extraArg) {
        return execute(DEFAULT_THREAD_SIZE, txService, tasks, extraArg);
    }

    public TxResults<R> execute(int nThreads, TxService<R, T, U> txService, List<T> tasks, U extraArg) {
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
        BlockingDeque<R> txResults = new LinkedBlockingDeque<>();
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        List<R> successes = new ArrayList<>();
        List<R> failures = new ArrayList<>();

        for (int i = 0; i < nThreads; i++) {
            int endIndex = (i + 1) * nTasksPerThread;
            List<T> subTasks = tasks.subList(i * nTasksPerThread, Math.min(endIndex, nTasks));
            TxWorker<R, T, U> txWorker = new TxWorker<>();
            txWorker.setRollbackLatch(rollbackLatch);
            txWorker.setMainThreadLatch(mainThreadLatch);
            txWorker.setRollbackFlag(rollbackFlag);
            txWorker.setTransactionManager(transactionManager);
            txWorker.setTxService(txService);
            txWorker.setTxTasks(subTasks);
            txWorker.setExtraArg(extraArg);
            txWorker.setTxResults(txResults);
            executorService.submit(txWorker);
        }

        try {
            // 等待所有子线程执行完毕mainThreadLatch#countDown()
            mainThreadLatch.await();

            // 查看子线程中业务的执行情况，如果存在异常，则置位回滚标识进而回滚所有子线程
            for (int i = 0; i < nTasks; i++) {
                R txResult = txResults.take();
                if (txResult.isError()) {
                    logger.error("子线程中业务执行存在异常，整体回滚，txResult: {}", JsonUtil.bean2Json(txResult));
                    rollbackFlag.set(true);
                    failures.add(txResult);
                } else {
                    successes.add(txResult);
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

        return new TxResults<>(successes, failures);
    }
}
