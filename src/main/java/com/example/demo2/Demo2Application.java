package com.example.demo2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = {"org.xinhua.cbcloud", "com.example.demo2"})
@EnableAsync
@EnableAspectJAutoProxy(exposeProxy = true)
public class Demo2Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo2Application.class, args);
    }

    @PostConstruct
    void started() {
        // 服务启动的时候，将当前时区设置为UTC
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
