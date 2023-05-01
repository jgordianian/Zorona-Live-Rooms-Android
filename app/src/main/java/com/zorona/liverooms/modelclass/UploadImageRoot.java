package com.zorona.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

public class UploadImageRoot {

    @SerializedName("chat")
    private ChatItem chat;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public ChatItem getChat() {
        return chat;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }
}