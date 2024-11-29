package com.app.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

public class FollowUnfollowResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    @SerializedName("isFollow")
    private boolean isFollow = false;

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }
}