package com.example.demo2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xinhua.cbcloud.docs.dt.service.DtService;

@SpringBootTest
class Demo2ApplicationTests {


    @Autowired
    private DtService dtService;

    @Test
    void contextLoads() {
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("hello world");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        System.out.println("主线程结束");
    }
}
