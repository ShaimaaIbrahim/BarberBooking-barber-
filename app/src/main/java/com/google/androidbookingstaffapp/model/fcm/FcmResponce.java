package com.google.androidbookingstaffapp.model.fcm;

import java.util.List;

public class FcmResponce {

    private long multicast_id;
    private  int canonical_id;
    private List<Result> results;
    private int success;
    private int failure;

    public long getMulticast_id() {
        return multicast_id;
    }

    public FcmResponce setMulticast_id(long multicast_id) {
        this.multicast_id = multicast_id;
        return this;
    }

    public int getSuccess() {
        return success;
    }

    public FcmResponce setSuccess(int success) {
        this.success = success;
        return this;
    }

    public int getFailure() {
        return failure;
    }

    public FcmResponce setFailure(int failure) {
        this.failure = failure;
        return this;
    }

    public FcmResponce() {
    }

    public int getCanonical_id() {
        return canonical_id;
    }

    public FcmResponce setCanonical_id(int canonical_id) {
        this.canonical_id = canonical_id;
        return this;
    }

    public List<Result> getResults() {
        return results;
    }

    public FcmResponce setResults(List<Result> results) {
        this.results = results;
        return this;
    }

    class Result{

        private String message_id;

        public Result() {
        }

        public String getMessage_id() {
            return message_id;
        }

        public Result setMessage_id(String message_id) {
            this.message_id = message_id;
            return this;
        }
    }
}
