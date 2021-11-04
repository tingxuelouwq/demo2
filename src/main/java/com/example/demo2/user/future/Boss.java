package com.example.demo2.user.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * kevin<br/>
 * 2021/11/4 14:46<br/>
 */
public class Boss extends Thread {

    private List<Worker> workers = new ArrayList<>();

    public void addTask(Worker t) {
        workers.add(t);
    }

    @Override
    public void run() {
        workers.stream().forEach(t ->
                CompletableFuture.supplyAsync(() -> t.runTask())
                        .thenAccept(result -> callback(result, t)));
    }

    private void callback(Result result, Worker t) {
        if (Result.FAIL == result) {
            for (Worker worker : workers) {
                if (worker != t) {
                    worker.cancel();
                }
            }
        }
    }
}
