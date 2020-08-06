package com.google.androidbookingstaffapp.model.fcm;

import java.util.Map;

public class FcmSendData {

    private Map<String , String> data;
    private String to;

    public FcmSendData(Map<String, String> data, String to) {
        this.data = data;
        this.to = to;
    }

    public FcmSendData() {
    }

    public Map<String, String> getData() {
        return data;
    }

    public FcmSendData setData(Map<String, String> data) {
        this.data = data;
        return this;
    }

    public String getTo() {
        return to;
    }

    public FcmSendData setTo(String to) {
        this.to = to;
        return this;
    }
}
