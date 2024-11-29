package com.app.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DiamondPlanRoot {

    @SerializedName("coinPlan")
    private List<DiamondPlanItem> coinPlan;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public List<DiamondPlanItem> getCoinPlan() {
        return coinPlan;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class DiamondPlanItem {

        @SerializedName("productKey")
        private String productKey;

        public String getProductKey() {
            return productKey;
        }

        @SerializedName("rupee")
        private int rupee;

        @SerializedName("diamonds")
        private int diamonds;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("_id")
        private String id;

        @SerializedName("tag")
        private String tag;

        @SerializedName("dollar")
        private int dollar;

        @SerializedName("updatedAt")
        private String updatedAt;

        public int getRupee() {
            return rupee;
        }

        public int getDiamonds() {
            return diamonds;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getId() {
            return id;
        }

        public String getTag() {
            return tag;
        }

        public int getDollar() {
            return dollar;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}