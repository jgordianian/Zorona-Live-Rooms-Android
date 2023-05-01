package com.zorona.liverooms.modelclass;

import android.text.format.DateUtils;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ReedemListRoot {

    @SerializedName("redeem")
    private List<RedeemItem> redeem;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public List<RedeemItem> getRedeem() {
        return redeem;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class RedeemItem {

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("rCoin")
        private int rCoin;

        @SerializedName("description")
        private String description;

        @SerializedName("_id")
        private String id;

        @SerializedName("userId")
        private String userId;

        @SerializedName("status")
        private String status;

        @SerializedName("paymentGateway")
        private String paymentGateway;

        @SerializedName("updatedAt")
        private String updatedAt;

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
            if (createdAt.equals("0 minutes ago")) {
                createdAt = "Just Now";
            }
            return createdAt;
        }

        public int getRCoin() {
            return rCoin;
        }

        public String getDescription() {
            return description;
        }

        public String getId() {
            return id;
        }

        public String getUserId() {
            return userId;
        }

        public String getStatus() {
            return status;
        }

        public String getPaymentGateway() {
            return paymentGateway;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}