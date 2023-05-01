package com.zorona.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

public class CallRequestRoot {

    @SerializedName("callId")
    private String callId;

    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }


    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public String getCallId() {
        return callId;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }
}