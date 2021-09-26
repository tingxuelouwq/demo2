package com.example.demo2.user.concurrent;

/**
 * kevin<br/>
 * 2021/9/22 10:45<br/>
 */
public interface TxService<R extends TxResult, T extends TxTask, U> {

     R invoke(T t, U extraArg);
}
