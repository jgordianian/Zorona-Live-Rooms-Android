package com.app.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HeshtagsRoot {

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    @SerializedName("hashtag")
    private List<HashtagItem> hashtag;

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public List<HashtagItem> getHashtag() {
        return hashtag;
    }

    public static class HashtagItem {

        @SerializedName("_id")
        private String id;

        @SerializedName("hashtag")
        private String hashtag;


        @SerializedName("count")
        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getId() {
            return id;
        }

        public String getHashtag() {
            return hashtag;
        }
    }
}