package com.app.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

public class ChatItem {

    @SerializedName("date")
    private String date;

    @SerializedName("image")
    private String image;

    @SerializedName("senderId")
    private String senderId;

    @SerializedName("messageType")
    private String messageType;

    @SerializedName("topic")
    private String topic;

    @SerializedName("_id")
    private String id;

    @SerializedName("message")
    private String message;

    public String getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getTopic() {
        return topic;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}