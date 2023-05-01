package com.zorona.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class LevelRoot implements Serializable {

    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("level")
    private List<LevelItem> level;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<LevelItem> getLevel() {
        return level;
    }

    public void setLevel(List<LevelItem> level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return
                "LevelRoot{" +
                        "status = '" + status + '\'' +
                        ",message = '" + message + '\'' +
                        ",level = '" + level + '\'' +
                        "}";
    }

    public static class LevelItem implements Serializable {

        @SerializedName("accessibleFunction")
        private AccessibleFunction accessibleFunction;

        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("image")
        private String image;

        @SerializedName("coin")
        private int coin;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("updatedAt")
        private String updatedAt;

        public AccessibleFunction getAccessibleFunction() {
            return accessibleFunction;
        }

        public void setAccessibleFunction(AccessibleFunction accessibleFunction) {
            this.accessibleFunction = accessibleFunction;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getCoin() {
            return coin;
        }

        public void setCoin(int coin) {
            this.coin = coin;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        @Override
        public String toString() {
            return
                    "LevelItem{" +
                            "accessibleFunction = '" + accessibleFunction + '\'' +
                            ",_id = '" + id + '\'' +
                            ",name = '" + name + '\'' +
                            ",image = '" + image + '\'' +
                            ",coin = '" + coin + '\'' +
                            ",createdAt = '" + createdAt + '\'' +
                            ",updatedAt = '" + updatedAt + '\'' +
                            "}";
        }
    }

    public static class AccessibleFunction implements Serializable {

        @SerializedName("cashOut")
        private boolean cashOut;

        @SerializedName("freeCall")
        private boolean freeCall;

        @SerializedName("liveStreaming")
        private boolean liveStreaming;

        @SerializedName("uploadPost")
        private boolean uploadPost;

        @SerializedName("uploadVideo")
        private boolean uploadVideo;

        public boolean isCashOut() {
            return cashOut;
        }

        public void setCashOut(boolean cashOut) {
            this.cashOut = cashOut;
        }

        public boolean isFreeCall() {
            return freeCall;
        }

        public void setFreeCall(boolean freeCall) {
            this.freeCall = freeCall;
        }

        public boolean isLiveStreaming() {
            return liveStreaming;
        }

        public void setLiveStreaming(boolean liveStreaming) {
            this.liveStreaming = liveStreaming;
        }

        public boolean isUploadPost() {
            return uploadPost;
        }

        public void setUploadPost(boolean uploadPost) {
            this.uploadPost = uploadPost;
        }

        public boolean isUploadVideo() {
            return uploadVideo;
        }

        public void setUploadVideo(boolean uploadVideo) {
            this.uploadVideo = uploadVideo;
        }

        @Override
        public String toString() {
            return
                    "AccessibleFunction{" +
                            "cashOut = '" + cashOut + '\'' +
                            ",freeCall = '" + freeCall + '\'' +
                            ",liveStreaming = '" + liveStreaming + '\'' +
                            ",uploadPost = '" + uploadPost + '\'' +
                            ",uploadVideo = '" + uploadVideo + '\'' +
                            "}";
        }
    }
}