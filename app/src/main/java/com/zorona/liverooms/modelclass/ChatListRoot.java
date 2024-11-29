package com.app.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatListRoot {

    @SerializedName("chat")
    private List<ChatItem> chat;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public List<ChatItem> getChat() {
        return chat;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }
}