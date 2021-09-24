package com.example.demo2.user.concurrent;

/**
 * kevin<br/>
 * 2021/9/24 18:10<br/>
 */
public class TxTask {

    /**
     * 任务的唯一标识符，用于事务成功或失败时，在响应中标记出对应的任务
     */
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
