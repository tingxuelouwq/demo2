package com.example.demo2.user.concurrent;

/**
 * kevin<br/>
 * 2021/9/22 10:36<br/>
 */
public class TxResult {

    /**
     * 结果索引，和任务索引一致
     */
    private Integer index;
    /**
     * 消息说明
     */
    private String message;
    /**
     * 任务是否失败
     */
    private boolean error;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
