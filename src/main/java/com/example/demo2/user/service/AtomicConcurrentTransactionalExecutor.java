package com.example.demo2.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * kevin<br/>
 * 2021/9/18 14:05<br/>
 */
@Component
public class AtomicConcurrentTransactionalExecutor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final int defaultThreadSize = 5;

    private final int defaultAwaitTerminationTimeout = 10;

    @Autowired
    private TransactionalWorker transactionalWorker;

    public boolean execute(List<Runnable> tasks) {
        return execute(tasks, defaultThreadSize, defaultAwaitTerminationTimeout);
    }

    public boolean execute(List<Runnable> tasks, int awaitTerminationTimeout) {
        return execute(tasks, defaultThreadSize, awaitTerminationTimeout);
    }

    public boolean execute(List<Runnable> tasks, int threadSize, int awaitTerminationTimeout) {
        int taskNum = tasks.size();
        CyclicBarrier workerCyclicBarrier = new CyclicBarrier(taskNum);
        AtomicInteger successCounter = new AtomicInteger(taskNum);
        ExecutorService executorService = Executors.newFixedThreadPool(threadSize);
        tasks.forEach(task -> executorService.submit(
                () -> transactionalWorker.run(workerCyclicBarrier, successCounter, task)));

        shutdown(executorService, awaitTerminationTimeout, TimeUnit.SECONDS);
        return successCounter.get() == 0;
    }

    private boolean shutdown(ExecutorService pool, int awaitTerminationTimeout, TimeUnit timeUnit) {
        try {
            pool.shutdown();
            boolean done = false;
            try {
                done = awaitTerminationTimeout > 0 && pool.awaitTermination(awaitTerminationTimeout, timeUnit);
            } catch (InterruptedException e) {
                logger.error("线程池awaitTermination出错!", e);
            }
            if (!done) {
                pool.shutdownNow();
                if(awaitTerminationTimeout > 0) {
                    pool.awaitTermination(awaitTerminationTimeout, timeUnit);
                }
            }
        } catch (Exception e) {
            logger.error("线程池shutdown出错!", e);
            return false;
        }
        return true;
    }
}




