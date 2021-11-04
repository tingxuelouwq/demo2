package com.example.demo2.user.atomic;

import java.util.ArrayList;
import java.util.List;

/**
 * kevin<br/>
 * 2021/11/4 10:55<br/>
 */
public class Boss  extends Thread {

    private List<Worker> workers = new ArrayList<>();

    public void addTask(Worker t) {
        workers.add(t);
    }

    @Override
    public void run() {
        workers.stream().forEach(t -> t.start());
    }

    public void end(Worker worker) {
        if (worker.getResult() == Result.FAILED) {
            for (Worker w : workers) {
                if (w != worker) {
                    w.cancel();
                }
            }
        }
    }
}
