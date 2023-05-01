package com.zorona.liverooms.modelclass;

import android.text.format.DateUtils;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ComplainRoot {

    @SerializedName("message")
    private String message;

    @SerializedName("complain")
    private List<ComplainItem> complain;

    @SerializedName("status")
    private boolean status;

    public String getMessage() {
        return message;
    }

    public List<ComplainItem> getComplain() {
        return complain;
    }

    public boolean isStatus() {
        return status;
    }

    public static class ComplainItem {

        @SerializedName("image")
        private String image;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("contact")
        private String contact;

        @SerializedName("solved")
        private boolean solved;

        @SerializedName("_id")
        private String id;

        @SerializedName("message")
        private String message;

        @SerializedName("userId")
        private String userId;

        @SerializedName("updatedAt")
        private String updatedAt;

        public String getImage() {
            return image;
        }

        public String getCreatedAt() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                long time = sdf.parse(createdAt).getTime();
                long now = System.currentTimeMillis();
                CharSequence ago =
                        DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);

                return ago.toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return createdAt;
        }

        public String getContact() {
            return contact;
        }

        public boolean isSolved() {
            return solved;
        }

        public String getId() {
            return id;
        }

        public String getMessage() {
            return message;
        }

        public String getUserId() {
            return userId;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}