package com.example.demo2.user.concurrent;

/**
 * kevin<br/>
 * 2021/9/22 10:45<br/>
 */
public interface TxService<T extends TxTask> {

    TxResult invoke(T t);
}
