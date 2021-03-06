package com.example.demo2.user.service.impl;

import com.example.demo2.user.entity.User;
import com.example.demo2.user.repository.UserRepository;
import com.example.demo2.user.service.User2Service;
import com.example.demo2.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * kevin<br/>
 * 2020/11/5 18:36<br/>
 */
@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;

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

    @Autowired
    private User2Service user2Service;

    @Override
    public void tx2() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        list.parallelStream().forEach(id -> {
            try {
                user2Service.tx2(id);
            } catch (Exception ex) {
                logger.error("id=" + id, ex);
            }
        });
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void redisTx() {
        String key = "name";

//        String name = redisTemplate.opsForValue().get(key);
//        logger.info("name=" + name);

//        redisTemplate.setEnableTransactionSupport(true);
//        redisTemplate.multi();
//        redisTemplate.opsForValue().set("pipe:0", "0");
//        redisTemplate.opsForValue().set("pipe:1", "1");
//        redisTemplate.opsForValue().set("pipe:2", "2");
//        System.out.println(redisTemplate.exec());

        SessionCallback<Object> callback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForValue().set("pipe:0", "0");
                operations.opsForValue().set("pipe:1", "1");
                operations.opsForValue().set("pipe:2", "2");
//                int i = 1 / 0;
                return operations.exec();
            }
        };

        System.out.println(redisTemplate.execute(callback));
    }

    @Override
    public void redisTx2() {
        String key = "name";

        String name = redisTemplate.opsForValue().get(key);
        logger.info("name=" + name);

        user2Service.redisTx2();
    }

    private void doTest() throws Exception {
        User user = userRepository.getOne(1);
        user.setUname("xxxx");
        userRepository.save(user);
        throw new Exception();
    }
}
