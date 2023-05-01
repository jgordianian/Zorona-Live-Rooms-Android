package com.zorona.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HistoryListRoot {

    @SerializedName("outgoingTotal")
    private int outgoingTotal;

    @SerializedName("incomeTotal")
    private int incomeTotal;

    @SerializedName("history")
    private List<HistoryItem> history;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public int getOutgoingTotal() {
        return outgoingTotal;
    }

    public int getIncomeTotal() {
        return incomeTotal;
    }

    public List<HistoryItem> getHistory() {
        return history;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class HistoryItem {
        @Override
        public String toString() {
            return "HistoryItem{" +
                    "date='" + date + '\'' +
                    ", diamond=" + diamond +
                    ", rCoin=" + rCoin +
                    ", id='" + id + '\'' +
                    ", isAdd=" + isAdd +
                    ", type=" + type +
                    ", userName='" + userName + '\'' +
                    ", userId='" + userId + '\'' +
                    ", paymentGateway=" + paymentGateway +
                    '}';
        }

        @SerializedName("date")
        private String date;

        @SerializedName("diamond")
        private int diamond = 0;

        @SerializedName("rCoin")
        private int rCoin = 0;

        @SerializedName("_id")
        private String id;

        @SerializedName("isAdd")
        private boolean isAdd;

        @SerializedName("type")
        private int type;

        @SerializedName("userName")
        private String userName;

        @SerializedName("userId")
        private String userId;

        @SerializedName("paymentGateway")
        private Object paymentGateway;

        public String getDate() {
            return date;
        }

        public int getDiamond() {
            return diamond;
        }

        public int getRCoin() {
            return rCoin;
        }

        public String getId() {
            return id;
        }

        public boolean isIsAdd() {
            return isAdd;
        }

        public int getType() {
            return type;
        }

        public String getUserName() {
            return userName;
        }

        public String getUserId() {
            return userId;
        }

        public Object getPaymentGateway() {
            return paymentGateway;
        }
    }
}