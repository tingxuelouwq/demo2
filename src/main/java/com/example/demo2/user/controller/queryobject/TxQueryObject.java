package com.example.demo2.user.controller.queryobject;

import com.example.demo2.user.concurrent.TxTask;

/**
 * kevin<br/>
 * 2021/9/22 15:03<br/>
 */
public class TxQueryObject extends TxTask {

    private int id;

    public TxQueryObject(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}


