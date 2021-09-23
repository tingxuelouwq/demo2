package com.example.demo2.user.dto;

import com.example.demo2.user.concurrent.TxResult;

/**
 * kevin<br/>
 * 2021/9/23 17:32<br/>
 */
public class UserTxResult extends TxResult {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
