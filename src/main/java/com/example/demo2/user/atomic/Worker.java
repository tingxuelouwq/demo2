package com.example.demo2.user.atomic;

/**
 * kevin<br/>
 * 2021/11/4 10:46<br/>
 */
public class Worker extends Thread {

    private Result result = Result.NOTEND;

    private Boss boss;
    private String name;
    private int timeInSecondes;
    private boolean success;

    private volatile boolean canceling = false;

    public Worker(Boss boss, String name, int timeInSecondes, boolean success) {
        this.boss = boss;
        this.name = name;
        this.timeInSecondes = timeInSecondes;
        this.success = success;
    }

    public Result getResult() {
        return result;
    }

    @Override
    public void run() {
        int interval = 100;
        int total = 0;

        for (; ; ) {
            SleepHelper.sleepMilli(interval);   // 模拟业务执行时间，实际中时间不固定，可能在处理计算任务或者IO任务
            total += interval;
            if (total / 1000 > timeInSecondes) {    // 每100ms检查一下任务是否cancel
                result = success ? Result.SUCCESSED : Result.FAILED;
                System.out.println(name + "任务结束!" + result);
                break;
            }

            if (canceling) {
                rollback();
                result = Result.CANCELED;
                canceling = false;
                System.out.println(name + "任务结束!" + result);
                break;
            }
        }

        boss.end(this); // 通知Boss本任务执行结束
    }

    private void rollback() {
        System.out.println(name + " rollback start...");
        SleepHelper.sleepMilli(500);
        System.out.println(name + " rollback end...");
    }

    public void cancel() {
        canceling = true;
    }
}
