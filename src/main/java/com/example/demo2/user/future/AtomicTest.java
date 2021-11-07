package com.example.demo2.user.future;

import java.io.IOException;

/**
 * kevin<br/>
 * 2021/11/4 14:19<br/>
 */
public class AtomicTest {

    public static void main(String[] args) throws IOException {
        Boss boss = new Boss();

        Worker t1 = new Worker("t1", 3, Result.SUCCESS);
        Worker t2 = new Worker("t2", 4, Result.SUCCESS);
        Worker t3 = new Worker("t3", 1, Result.FAIL);

        boss.addTask(t1);
        boss.addTask(t2);
        boss.addTask(t3);

        boss.start();

        System.in.read();
    }
}
