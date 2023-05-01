package com.zorona.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

public class RestResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    @SerializedName("isLiked")
    private boolean isLiked = false;

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }
}