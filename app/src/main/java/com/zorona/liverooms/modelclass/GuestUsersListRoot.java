package com.app.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GuestUsersListRoot {

    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private List<GuestProfileRoot.User> user;

    @SerializedName("status")
    private boolean status;

    public String getMessage() {
        return message;
    }

    public List<GuestProfileRoot.User> getUser() {
        return user;
    }

    public boolean isStatus() {
        return status;
    }
}