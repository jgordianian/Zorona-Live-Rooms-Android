package com.app.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchLocationRoot {

    @SerializedName("response")
    private List<ResponseItem> response;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public List<ResponseItem> getResponse() {
        return response;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class ResponseItem {

        @SerializedName("city")
        private String city;

        @SerializedName("_id")
        private String id;

        @SerializedName("countryName")
        private String countryName;

        @SerializedName("state")
        private String state;

        public String getCity() {
            return city;
        }

        public String getId() {
            return id;
        }

        public String getCountryName() {
            return countryName;
        }

        public String getState() {
            return state;
        }
    }
}