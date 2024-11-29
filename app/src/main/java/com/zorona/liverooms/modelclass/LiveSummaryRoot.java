package com.app.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

public class LiveSummaryRoot {

    @SerializedName("message")
    private String message;

    @SerializedName("liveStreamingHistory")
    private LiveStreamingHistory liveStreamingHistory;

    @SerializedName("status")
    private boolean status;

    public String getMessage() {
        return message;
    }

    public LiveStreamingHistory getLiveStreamingHistory() {
        return liveStreamingHistory;
    }

    public boolean isStatus() {
        return status;
    }

    public static class LiveStreamingHistory {

        @SerializedName("duration")
        private String duration;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("comments")
        private int comments;

        @SerializedName("rCoin")
        private int rCoin;

        @SerializedName("_id")
        private String id;

        @SerializedName("user")
        private int user;

        @SerializedName("userId")
        private String userId;

        @SerializedName("gifts")
        private int gifts;

        @SerializedName("fans")
        private int fans;

        @SerializedName("updatedAt")
        private String updatedAt;

        public String getDuration() {
            return duration;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public int getComments() {
            return comments;
        }

        public int getRCoin() {
            return rCoin;
        }

        public String getId() {
            return id;
        }

        public int getUser() {
            return user;
        }

        public String getUserId() {
            return userId;
        }

        public int getGifts() {
            return gifts;
        }

        public int getFans() {
            return fans;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}