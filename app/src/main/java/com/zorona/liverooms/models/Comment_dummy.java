package com.app.liverooms.models;

public class Comment_dummy {
    User_dummy userDummy;
    String date, comment;

    public Comment_dummy() {

    }

    public Comment_dummy(User_dummy userDummy, String date, String comment) {
        this.userDummy = userDummy;
        this.date = date;
        this.comment = comment;
    }

    public User_dummy getUser() {
        return userDummy;
    }

    public void setUser(User_dummy userDummy) {
        this.userDummy = userDummy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
