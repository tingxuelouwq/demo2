package com.example.demo2.user.atomic;

/**
 * kevin<br/>
 * 2021/11/4 10:49<br/>
 */
public class SleepHelper {

    public static void sleepMilli(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // do nothing
        }
    }
}
