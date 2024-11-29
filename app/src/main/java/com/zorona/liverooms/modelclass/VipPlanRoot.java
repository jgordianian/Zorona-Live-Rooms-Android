package com.app.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VipPlanRoot {

    @SerializedName("vipPlan")
    private List<VipPlanItem> vipPlan;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public List<VipPlanItem> getVipPlan() {
        return vipPlan;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class VipPlanItem {

        @SerializedName("isAutoRenew")
        private boolean isAutoRenew;

        @SerializedName("name")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @SerializedName("rupee")
        private int rupee;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("productKey")
        private String productKey;

        public String getProductKey() {
            return productKey;
        }

        @SerializedName("validityType")
        private String validityType;

        @SerializedName("isDelete")
        private boolean isDelete;

        @SerializedName("_id")
        private String id;

        @SerializedName("validity")
        private int validity;

        @SerializedName("tag")
        private String tag;

        @SerializedName("dollar")
        private int dollar;

        @SerializedName("updatedAt")
        private String updatedAt;

        public boolean isIsAutoRenew() {
            return isAutoRenew;
        }

        public int getRupee() {
            return rupee;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getValidityType() {
            return validityType;
        }

        public boolean isIsDelete() {
            return isDelete;
        }

        public String getId() {
            return id;
        }

        public int getValidity() {
            return validity;
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