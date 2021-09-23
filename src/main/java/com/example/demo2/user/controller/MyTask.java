package com.example.demo2.user.controller;

import com.example.demo2.user.concurrent.TxTask;

/**
 * kevin<br/>
 * 2021/9/22 15:03<br/>
 */
public class MyTask extends TxTask {

    private int id;

    public MyTask(String docId, int id) {
        super(docId);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}


