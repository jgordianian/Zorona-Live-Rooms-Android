package com.zorona.liverooms.models;

public class Chat_dummy {
    public static int USER1 = 1, ME = 0, PHOTO = 1, EMOJI = 2, TEXT = 3;
    public int type;
    String data;
    int SENDER;

    public Chat_dummy() {
    }

    public Chat_dummy(String data, int type, int SENDER) {
        this.data = data;
        this.type = type;
        this.SENDER = SENDER;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getSENDER() {
        return SENDER;
    }

    public void setSENDER(int SENDER) {
        this.SENDER = SENDER;
    }

 /*   public static  enum Type{
        PHOTO,EMOJI,TEXT
    }*/
}
