package com.zorona.liverooms.models;

public class ChatUser_dummy {
    User_dummy userDummy;
    String time;
    String message;

    public ChatUser_dummy() {
    }

    public ChatUser_dummy(User_dummy userDummy, String time, String message) {
        this.userDummy = userDummy;
        this.time = time;
        this.message = message;
    }

    public User_dummy getUser() {
        return userDummy;
    }

    public void setUser(User_dummy userDummy) {
        this.userDummy = userDummy;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
