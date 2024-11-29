package com.app.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

public class ChatTopicRoot {

    @SerializedName("chatTopic")
    private ChatTopic chatTopic;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public ChatTopic getChatTopic() {
        return chatTopic;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class ChatTopic {

        @SerializedName("createdAt")
        private String createdAt;


        @SerializedName("receiverUser")
        private String receiverUser;

        @SerializedName("chat")
        private String chat;

        @SerializedName("senderUser")
        private String senderUser;

        @SerializedName("_id")
        private String id;

        @SerializedName("updatedAt")
        private String updatedAt;

        public String getCreatedAt() {
            return createdAt;
        }

        public String getReceiverUser() {
            return receiverUser;
        }

        public String getChat() {
            return chat;
        }

        public String getSenderUser() {
            return senderUser;
        }

        public String getId() {
            return id;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}