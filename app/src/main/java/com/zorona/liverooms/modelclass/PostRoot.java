package com.zorona.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostRoot {

    @SerializedName("post")
    private List<PostItem> post;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public List<PostItem> getPost() {
        return post;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class PostItem {
        public boolean isLike() {
            return isLike;
        }

        public void setLike(boolean like) {
            isLike = like;
        }

        public void setLike(int like) {
            this.like = like;
        }

        @SerializedName("isLike")
        private boolean isLike;

        @SerializedName("like")
        private int like;

        @SerializedName("caption")
        private String caption;

        @SerializedName("userId")
        private String userId;

        @SerializedName("isVIP")
        private boolean isVIP;


        @SerializedName("allowComment")
        private boolean allowComment;

        public boolean isAllowComment() {
            return allowComment;
        }

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("userImage")
        private String userImage;

        @SerializedName("post")
        private String post;

        @SerializedName("name")
        private String name;

        @SerializedName("comment")
        private int comment;

        @SerializedName("location")
        private String location;

        @SerializedName("_id")
        private String id;

        @SerializedName("time")
        private String time;

        public boolean isIsLike() {
            return isLike;
        }

        public int getLike() {
            return like;
        }

        public String getCaption() {
            return caption;
        }

        public String getUserId() {
            return userId;
        }

        public boolean isIsVIP() {
            return isVIP;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUserImage() {
            return userImage;
        }

        public String getPost() {
            return post;
        }

        public String getName() {
            return name;
        }

        public int getComment() {
            return comment;
        }

        public String getLocation() {
            return location;
        }

        public String getId() {
            return id;
        }

        public String getTime() {
            if (time.trim().equals("0 minutes ago")) {
                return "Just Now";
            }
            return time;
        }
    }
}