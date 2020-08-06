package com.google.androidbookingstaffapp.model.entities;

import com.google.firebase.Timestamp;

public class MyNotification {
    private String uid, title , content;
    private boolean read;
    private Timestamp serverTimestamp;

    public MyNotification() {
    }

    public String getUid() {
        return uid;
    }

    public MyNotification setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public MyNotification setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public MyNotification setContent(String content) {
        this.content = content;
        return this;
    }

    public boolean isRead() {
        return read;
    }

    public MyNotification setRead(boolean read) {
        this.read = read;
        return this;
    }

    public Timestamp getServerTimestamp() {
        return serverTimestamp;
    }

    public MyNotification setServerTimestamp(Timestamp serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
        return this;
    }
}
