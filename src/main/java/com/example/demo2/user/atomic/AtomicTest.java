package com.example.demo2.user.atomic;

import java.io.IOException;

/**
 * kevin<br/>
 * 2021/11/4 10:58<br/>
 */
public class AtomicTest {

    public static void main(String[] args) throws IOException {
        Boss boss = new Boss();
        Worker t1 = new Worker(boss, "t1", 3, true);
        Worker t2 = new Worker(boss, "t2", 4, true);
        Worker t3 = new Worker(boss, "t3", 1, false);

        boss.addTask(t1);
        boss.addTask(t2);
        boss.addTask(t3);

        boss.start();

        System.in.read();
    }
}
