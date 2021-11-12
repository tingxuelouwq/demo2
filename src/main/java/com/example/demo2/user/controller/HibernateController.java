package com.example.demo2.user.controller;

import com.example.demo2.user.entity.NineEntity;
import com.example.demo2.user.service.HibernateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * hibernate按照save、update、delete顺序执行
 */
@RestController
@RequestMapping("/hibernate")
public class HibernateController {

    @Autowired
    private HibernateService nineService;

    @PostMapping("/add")
    public void add(@RequestBody NineEntity nineEntity) {
        nineService.add(nineEntity);
    }

    @GetMapping("/error")
    public void error() {
        nineService.update();
    }

    @GetMapping("/right")
    private void right() {
        nineService.update1();
    }
}
