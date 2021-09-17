package com.example.demo2.user.service.impl;

import com.example.demo2.user.entity.User;
import com.example.demo2.user.repository.UserRepository;
import com.example.demo2.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * kevin<br/>
 * 2020/11/5 18:36<br/>
 */
@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AsyncTask asyncTask;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private User3ServiceImpl user3Service;

    @Override
    public void test() throws InterruptedException {
        asyncTask.archive();
        logger.info("休眠3s");
        Thread.sleep(3000);
        logger.info("test方法结束");
    }

    @Override
    public List<User> findAll(Integer age) {
        return userRepository.findAllByAgeAfter(age);
    }

    @Override
    public void add(LocalDateTime regtime) {
        User user = new User();
        user.setRegtime(regtime);
        userRepository.save(user);
    }

    @Override
    public void update(int id) {
        logger.info("keyFrames回调开始");
        User user = userRepository.getOne(id);
        user.setAge(25);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        userRepository.save(user);
        logger.info("keyFrames回调结束");
    }

    @Override
    public void update2(int id) {
        logger.info("final回调开始");
        User user = userRepository.getOne(id);
        user.setAge(100);
        userRepository.save(user);
        logger.info("final回调结束");
    }

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    public void tx() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        list.parallelStream().forEach(id -> {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = transactionManager.getTransaction(def);
            try {
                User user = userRepository.getOne(id);
                user.setUname("tom");
                userRepository.save(user);

                if (id == 2) {
                    throw new RuntimeException();
                }

                transactionManager.commit(status);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                transactionManager.rollback(status);
            }
        });
    }

    @Override
    public void tx2() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        int threads = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        int count = list.size();
        CountDownLatch latch= new CountDownLatch(count);
        AtomicReference<Boolean> isError = new AtomicReference<>(false);

        list.forEach(id -> {
            executorService.submit(() -> {
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus status = transactionManager.getTransaction(def);
                try {
                    User user = userRepository.getOne(id);
                    user.setUname("tom");
                    userRepository.save(user);

                    if (id == 2) {
                        throw new RuntimeException();
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    isError.set(true);
                }

                latch.countDown();

                try {
                    latch.await();
                    if (isError.get()) {
                        transactionManager.rollback(status);
                    } else {
                        transactionManager.commit(status);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
