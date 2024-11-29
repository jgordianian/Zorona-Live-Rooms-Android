package com.app.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

public class CoinRecordRoot {

    @SerializedName("diamond")
    private Diamond diamond;

    @SerializedName("rCoin")
    private RCoin rCoin;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public Diamond getDiamond() {
        return diamond;
    }

    public RCoin getRCoin() {
        return rCoin;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class Diamond {

        @SerializedName("income")
        private int income;

        @SerializedName("outgoing")
        private int outgoing;

        public int getIncome() {
            return income;
        }

        public int getOutgoing() {
            return outgoing;
        }
    }

    public static class RCoin {

        @SerializedName("income")
        private int income;

        @SerializedName("outgoing")
        private int outgoing;

        public int getIncome() {
            return income;
        }

        public int getOutgoing() {
            return outgoing;
        }
    }
}