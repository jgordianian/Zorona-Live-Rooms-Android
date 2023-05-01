package com.zorona.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

public class AdsRoot {

    @SerializedName("advertisement")
    private Advertisement advertisement;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public Advertisement getAdvertisement() {
        return advertisement;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class Advertisement {

        @SerializedName("reward")
        private String reward;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("native")
        private String jsonMemberNative;

        @SerializedName("interstitial")
        private String interstitial;

        @SerializedName("show")
        private boolean show;

        @SerializedName("banner")
        private String banner;

        @SerializedName("_id")
        private String id;

        @SerializedName("updatedAt")
        private String updatedAt;

        public String getReward() {
            return reward;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getJsonMemberNative() {
            return jsonMemberNative;
        }

        public String getInterstitial() {
            return interstitial;
        }

        public boolean isShow() {
            return show;
        }

        public String getBanner() {
            return banner;
        }

        public String getId() {
            return id;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}