package com.app.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GiftRoot {

    @SerializedName("gift")
    private List<GiftItem> gift;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public List<GiftItem> getGift() {
        return gift;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class GiftItem {

        @SerializedName("image")
        private String image;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("_id")
        private String id;

        @SerializedName("type")
        private int type;

        @SerializedName("category")
        private String category;

        @SerializedName("coin")
        private int coin;


        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        @SerializedName("updatedAt")
        private String updatedAt;

        public String getImage() {
            return image;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getId() {
            return id;
        }

        public int getType() {
            return type;
        }

        public String getCategory() {
            return category;
        }

        public int getCoin() {
            return coin;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        @Override
        public String toString() {
            return "GiftItem{" +
                    "image='" + image + '\'' +
                    ", createdAt='" + createdAt + '\'' +
                    ", id='" + id + '\'' +
                    ", type=" + type +
                    ", category='" + category + '\'' +
                    ", coin=" + coin +
                    ", count=" + count +
                    ", updatedAt='" + updatedAt + '\'' +
                    '}';
        }
    }
}