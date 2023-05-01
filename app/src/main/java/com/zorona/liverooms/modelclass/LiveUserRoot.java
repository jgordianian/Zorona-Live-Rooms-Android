package com.zorona.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LiveUserRoot {

    @SerializedName("message")
    private String message;

    @SerializedName("users")
    private List<UsersItem> users;

    @SerializedName("status")
    private boolean status;

    public String getMessage() {
        return message;
    }

    public List<UsersItem> getUsers() {
        return users;
    }

    public boolean isStatus() {
        return status;
    }

    public static class UsersItem {

        @SerializedName("image")
        private String image;

        @SerializedName("country")
        private String country;

        @SerializedName("diamond")
        private int diamond;

        @SerializedName("view")
        private int view;

        public int getView() {
            return view;
        }

        public void setView(int view) {
            this.view = view;
        }

        @SerializedName("rCoin")
        private int rCoin;

        @SerializedName("name")
        private String name;

        @SerializedName("channel")
        private String channel;

        @SerializedName("isVIP")
        private boolean isVIP;

        @SerializedName("username")
        private String username;

        @SerializedName("token")
        private String token;

        @SerializedName("liveUserId")
        private String liveUserId;

        @SerializedName("liveStreamingId")
        private String liveStreamingId;
        @SerializedName("_id")
        private String id;

        public String getLiveStreamingId() {
            return liveStreamingId;
        }

        public void setLiveStreamingId(String liveStreamingId) {
            this.liveStreamingId = liveStreamingId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImage() {
            return image;
        }

        public String getCountry() {
            return country;
        }

        public int getDiamond() {
            return diamond;
        }

        public int getRCoin() {
            return rCoin;
        }

        public String getName() {
            return name;
        }

        public String getChannel() {
            return channel;
        }

        public boolean isIsVIP() {
            return isVIP;
        }

        public String getUsername() {
            return username;
        }

        public String getToken() {
            return token;
        }

        public String getLiveUserId() {
            return liveUserId;
        }


        public void setImage(String image) {
            this.image = image;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public void setDiamond(int diamond) {
            this.diamond = diamond;
        }

        public void setrCoin(int rCoin) {
            this.rCoin = rCoin;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public void setVIP(boolean VIP) {
            isVIP = VIP;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public void setLiveUserId(String liveUserId) {
            this.liveUserId = liveUserId;
        }

        @Override
        public String toString() {
            return "UsersItem{" +
                    "image='" + image + '\'' +
                    ", country='" + country + '\'' +
                    ", diamond=" + diamond +
                    ", rCoin=" + rCoin +
                    ", name='" + name + '\'' +
                    ", channel='" + channel + '\'' +
                    ", isVIP=" + isVIP +
                    ", username='" + username + '\'' +
                    ", token='" + token + '\'' +
                    ", liveUserId='" + liveUserId + '\'' +
                    ", liveStreamingId='" + liveStreamingId + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }
}