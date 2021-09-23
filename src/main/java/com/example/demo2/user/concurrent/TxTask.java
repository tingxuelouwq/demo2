package com.example.demo2.user.concurrent;

/**
 * kevin<br/>
 * 2021/9/22 12:02<br/>
 */
public class TxTask {

    private String docId;

    public TxTask() {
    }

    public TxTask(String docId) {
        this.docId = docId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
