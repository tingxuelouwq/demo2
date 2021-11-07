package com.example.demo2.user.controller;

import com.example.demo2.user.concurrent.AtomicMultiThreadTxExecutor;
import com.example.demo2.user.concurrent.TxResult;
import com.example.demo2.user.concurrent.TxResults;
import com.example.demo2.user.controller.queryobject.TxQueryObject;
import com.example.demo2.user.dto.TokenDTO;
import com.example.demo2.user.dto.UserDTO;
import com.example.demo2.user.entity.User;
import com.example.demo2.user.repository.UserRepository;
import com.example.demo2.user.service.UserService;
import com.example.demo2.user.service.impl.UserService4Impl;
import com.example.demo2.util.JsonUtil;
import com.example.demo2.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo2.util.DateTimeUtil.*;
import static com.example.demo2.util.DateTimeUtil.DateTimePattern.DATE_LINE;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * kevin<br/>
 * 2020/11/23 15:37<br/>
 */
@Validated
@RestController
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public List<User> listUser(@RequestParam Integer age) {
        return userService.findAll(age);
    }

    @PostMapping("/add")
    public void add(@RequestParam Long regtime) {
        userService.add(msec2DateTime(regtime));
    }

    @PostMapping("/add2")
    public void add2(@RequestParam LocalDateTime regtime) {
        userService.add(regtime);
    }

    @PostMapping("/add3/{regtime}")
    public void add3(@PathVariable LocalDateTime regtime) {
        userService.add(regtime);
    }

    @PostMapping("/add4")
    public void add4(@RequestBody UserAddModel model) {
        userService.add(model.getRegtime());
    }

    @PostMapping("/update")
    public void update(@RequestParam int id) {
        userService.update(id);
    }

    @PostMapping("/update2")
    public void update2(@RequestParam int id) {
        userService.update2(id);
    }

    @PostMapping("/test")
    public void test(@RequestBody List<User> users) {
        users.forEach(user -> System.out.println(user.getUname()));
    }

    @GetMapping("/tx")
    public void tx() {
        userService.tx();
    }

    @GetMapping(value = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserXmlModel json() {
        UserXmlModel user = new UserXmlModel(1, "测试json", 15, "男");
        return user;
    }

    @GetMapping(value = "/xml", produces = MediaType.APPLICATION_XHTML_XML_VALUE)
    public UserXmlModel xml() {
        UserXmlModel user = new UserXmlModel(1, "测试json", 15, "男");
        return user;
    }

    @PostMapping(value = "/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public UserXmlModel xml(@RequestBody UserXmlModel model) {
        System.out.println(model);
        return model;
    }

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/test")
    public void test() {
        String resp = "{\"access_token\":\"A014F234176CB2A00EB58AF497173730-66D2F20CC43396AF3E1E3459E9E6482536F3B509\",\"code\":0,\"message\":\"success!\",\"expires_in\":604800}";
        TokenDTO token = JsonUtil.json2Bean(resp, TokenDTO.class);
        redisUtil.setEx("sms:token", JsonUtil.bean2Json(token), token.getExpiresIn(), SECONDS);
    }

    @GetMapping("/test2")
    public void testInsertLongText() {
        String content = "<p class=\"\">测试稿件，请勿使用<br/></p><p style=\"text-align: center;\" data-mce-style=\"text-align: center;\" class=\"\">测试段落1111111111</p><p style=\"max-width: 100%; text-align: center;\" class=\"video\" data-mce-style=\"max-width: 100%; text-align: center;\">测试段落2222222<br/></p><p>测试稿件，请勿使用</p><p class=\"\">测试稿件，请勿使用</p><p><br/></p><p><br/></p><p>　　</p><p><br/></p>";
        User user = new User();
        user.setDescription(content);
        userRepository.save(user);
    }

    @PostMapping("/test3")
    public void test3(@Valid  @RequestBody List<UserDTO> users) {
        System.out.println(JsonUtil.bean2Json(users));
    }

    @GetMapping("/test23")
    public void test23() {
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

    @Autowired
    private AtomicMultiThreadTxExecutor<TxResult, TxQueryObject, Object> atomicMultiThreadTxExecutor;

    @Autowired
    private UserService4Impl userService4;

    @GetMapping("/atomic-mutli-tx")
    public void testAtomicMultiTx() {
        List<TxQueryObject> list = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            list.add(new TxQueryObject(i));
        }

        TxResults txResults = atomicMultiThreadTxExecutor.execute(userService4, list, null);
        logger.info("successes: " + JsonUtil.bean2Json(txResults.getSuccesses()));
        logger.info("failures: " + JsonUtil.bean2Json(txResults.getFailures()));
    }

    @GetMapping("/tx2")
    public void testTx2() {
        userService.tx2();
    }

    @GetMapping("/time")
    public void time() {
        long gmtTime = 1632723337545L;
        logger.info("origin:" + msec2DateTime(gmtTime));

        logger.info(dateTime2String(transform(msec2ZonedDateTime(gmtTime), ZoneId.of("UTC+8")).toLocalDateTime()));
        logger.info(dateTime2String(transform(msec2ZonedDateTime(gmtTime), ZoneId.of("UTC+8")).toLocalDateTime(), DATE_LINE));
    }

    @GetMapping("/redis-tx")
    public void redisTx() {
        userService.redisTx();
    }

    @GetMapping("/redis-tx2")
    public void redisTx2() {
        userService.redisTx2();
    }

    @GetMapping("/test123")
    public void test1233() {
        try {
            throw new RuntimeException("接口调用失败");
        } catch (Exception ex) {
            String errMsg = ex.getMessage();
            if (ex instanceof RuntimeException) {
                errMsg = ((RuntimeException) ex).getMessage();
            }
            logger.error("出库到报刊失败, errMsg: {}", errMsg, ex);
        }
    }
}
