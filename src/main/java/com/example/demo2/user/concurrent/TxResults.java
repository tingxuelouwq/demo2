package com.example.demo2.user.concurrent;

import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * kevin<br/>
 * 2021/9/23 18:27<br/>
 * 原子性多线程事务执行结果
 */
public class TxResults<R extends TxResult> {

    /**
     * 执行成功的结果集
     */
    private List<R> successes;
    /**
     * 执行失败的结果集
     */
    private List<R> failures;
    /**
     * 错误标识，若全部成功，则为true，否则为false
     */
    private boolean error;

    public TxResults(List<R> successes, List<R> failures) {
        this.successes = successes;
        this.failures = failures;

        if (CollectionUtils.isEmpty(failures)) {
            error = false;
        } else {
            error = true;
        }
    }

    public List<R> getSuccesses() {
        return successes;
    }

    public void setSuccesses(List<R> successes) {
        this.successes = successes;
    }

    public List<R> getFailures() {
        return failures;
    }

    public void setFailures(List<R> failures) {
        this.failures = failures;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
