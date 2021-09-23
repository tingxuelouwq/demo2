package com.example.demo2.user.concurrent;

/**
 * kevin<br/>
 * 2021/9/22 10:36<br/>
 */
public class TxResult {

    private String docId;
    private String message;
    private boolean error;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
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
