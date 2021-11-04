package com.example.demo2;

import com.example.demo2.user.entity.User;
import com.example.demo2.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class Demo2ApplicationTests {

    @Test
    public void contextLoads() throws Exception {
    }
}
