package com.zorona.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

public class LiveStreamRoot {

    @SerializedName("liveUser")
    private LiveUser liveUser;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public LiveUser getLiveUser() {
        return liveUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class LiveUser {

        @SerializedName("country")
        private String country;

        @SerializedName("image")
        private String image;

        @SerializedName("rCoin")
        private int rCoin;

        @SerializedName("channel")
        private String channel;

        @SerializedName("liveStreamingId")
        private String liveStreamingId;

        @SerializedName("isVIP")
        private boolean isVIP;

        @SerializedName("token")
        private String token;

        @SerializedName("liveUserId")
        private String liveUserId;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("view")
        private int view;

        @SerializedName("diamond")
        private int diamond;

        @SerializedName("name")
        private String name;

        @SerializedName("isPublic")
        private boolean isPublic;

        @SerializedName("_id")
        private String id;

        @SerializedName("username")
        private String username;

        @SerializedName("updatedAt")
        private String updatedAt;

        public String getCountry() {
            return country;
        }

        public String getImage() {
            return image;
        }

        public int getRCoin() {
            return rCoin;
        }

        public String getChannel() {
            return channel;
        }

        public String getLiveStreamingId() {
            return liveStreamingId;
        }

        public boolean isIsVIP() {
            return isVIP;
        }

        public String getToken() {
            return token;
        }

        public String getLiveUserId() {
            return liveUserId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public int getView() {
            return view;
        }

        public int getDiamond() {
            return diamond;
        }

        public String getName() {
            return name;
        }

        public boolean isIsPublic() {
            return isPublic;
        }

        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}