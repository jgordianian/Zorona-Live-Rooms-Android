package com.app.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

public class GuestProfileRoot {

    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private User user;

    @SerializedName("status")
    private boolean status;

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public boolean isStatus() {
        return status;
    }

    public static class User {
        @SerializedName("image")
        private String image;
        @SerializedName("country")
        private String country;
        @SerializedName("gender")
        private String gender;
        @SerializedName("level")
        private Level level;
        @SerializedName("bio")
        private String bio;
        @SerializedName("video")
        private int video;
        @SerializedName("userId")
        private String userId;
        @SerializedName("isVIP")
        private boolean isVIP;
        @SerializedName("isFollow")
        private boolean isFollow;
        @SerializedName("followers")
        private int followers;
        @SerializedName("post")
        private int post;
        @SerializedName("following")
        private int following;
        @SerializedName("name")
        private String name;
        @SerializedName("_id")
        private String id;
        @SerializedName("age")
        private int age;
        @SerializedName("username")
        private String username;

        public boolean isVIP() {
            return isVIP;
        }

        public void setVIP(boolean VIP) {
            isVIP = VIP;
        }

        public boolean isFollow() {
            return isFollow;
        }

        public void setFollow(boolean follow) {
            isFollow = follow;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public Level getLevel() {
            return level;
        }

        public void setLevel(Level level) {
            this.level = level;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public int getVideo() {
            return video;
        }

        public void setVideo(int video) {
            this.video = video;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public boolean isIsVIP() {
            return isVIP;
        }

        public boolean isIsFollow() {
            return isFollow;
        }

        public int getFollowers() {
            return followers;
        }

        public void setFollowers(int followers) {
            this.followers = followers;
        }

        public int getPost() {
            return post;
        }

        public void setPost(int post) {
            this.post = post;
        }

        public int getFollowing() {
            return following;
        }

        public void setFollowing(int following) {
            this.following = following;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    public static class Level {

        @SerializedName("image")
        private String image;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("name")
        private String name;

        @SerializedName("_id")
        private String id;

        @SerializedName("coin")
        private int coin;

        @SerializedName("updatedAt")
        private String updatedAt;

        public String getImage() {
            return image;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public int getCoin() {
            return coin;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}