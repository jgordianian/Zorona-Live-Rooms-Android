package com.zorona.liverooms.models;

public class Reels_dummy {
    User_dummy userDummy;
    String caption, video;
    int likes, comments;

    public Reels_dummy() {
    }

    public Reels_dummy(User_dummy userDummy, String caption, String video, int likes, int comments) {
        this.userDummy = userDummy;
        this.caption = caption;
        this.video = video;
        this.likes = likes;
        this.comments = comments;
    }

    public User_dummy getUser() {
        return userDummy;
    }

    public void setUser(User_dummy userDummy) {
        this.userDummy = userDummy;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }
}
