package com.google.androidbookingstaffapp.model.fcm;

public  class MyToken {

    private String userPhone;
    private FcmCommon.TOKEN_TYPE token_type;
    private String token;

    public MyToken() {
    }

    public String getUserPhone() {
        return userPhone;
    }

    public MyToken setUserPhone(String userPhone) {
        this.userPhone = userPhone;
        return this;
    }

    public FcmCommon.TOKEN_TYPE getToken_type() {
        return token_type;
    }

    public MyToken setToken_type(FcmCommon.TOKEN_TYPE token_type) {
        this.token_type = token_type;
        return this;
    }

    public String getToken() {
        return token;
    }

    public MyToken setToken(String token) {
        this.token = token;
        return this;
    }
}
