package com.zorona.liverooms.models;

public class Post_dummy {
    String lication, time, caption, image;
    User_dummy userDummy;
    int commentCount, likeCount;

    public Post_dummy(String lication, String time, String caption, String image, User_dummy userDummy, int commentCount, int likeCount) {
        this.lication = lication;
        this.time = time;
        this.caption = caption;
        this.image = image;
        this.userDummy = userDummy;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
    }

    public String getLication() {
        return lication;
    }

    public String getTime() {
        return time;
    }

    public String getCaption() {
        return caption;
    }

    public String getImage() {
        return image;
    }

    public User_dummy getUser() {
        return userDummy;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getLikeCount() {
        return likeCount;
    }
}
