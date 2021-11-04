package com.example.demo2.user.future;

import com.example.demo2.user.atomic.SleepHelper;

/**
 * kevin<br/>
 * 2021/11/4 14:46<br/>
 */
public class Worker {

    private String name;
    private int timeInSecondes;
    private Result ret;

    private volatile boolean canceling = false;
    private volatile boolean canceled = false;

    public Worker(String name, int timeInSecondes, Result ret) {
        this.name = name;
        this.timeInSecondes = timeInSecondes;
        this.ret = ret;
    }

    public Result runTask() {
        int interval = 100;
        int total = 0;

        for (; ; ) {
            SleepHelper.sleepMilli(interval);   // 模拟业务执行时间，实际中时间不固定，可能在处理计算任务或者IO任务
            if (canceling) {
                continue;
            }
            total += interval;
            if (total >= timeInSecondes) {
                break;
            }
            if (canceled) {
                return Result.CANCELED;
            }
        }

        System.out.println(name + " end!");
        return ret;
    }

    public void cancel() {
        canceling = true;
        synchronized (this) {
            System.out.println(name + " canceling");
            SleepHelper.sleepMilli(50);
            System.out.println(name + " canceled");
        }
        canceled = true;
    }
}
