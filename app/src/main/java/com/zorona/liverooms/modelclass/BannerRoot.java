package com.app.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BannerRoot {

    @SerializedName("banner")
    private List<BannerItem> banner;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public List<BannerItem> getBanner() {
        return banner;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class BannerItem {

        @SerializedName("image")
        private String image;

        @SerializedName("__v")
        private int V;

        @SerializedName("_id")
        private String id;

        @SerializedName("isVIP")
        private boolean isVIP;

        @SerializedName("URL")
        private String uRL;

        public String getImage() {
            return image;
        }

        public int getV() {
            return V;
        }

        public String getId() {
            return id;
        }

        public boolean isIsVIP() {
            return isVIP;
        }

        public String getURL() {
            return uRL;
        }
    }
}