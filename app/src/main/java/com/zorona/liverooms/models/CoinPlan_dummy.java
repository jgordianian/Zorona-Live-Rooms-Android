package com.zorona.liverooms.models;

public class CoinPlan_dummy {
    int coin;
    long amount;
    String label;

    public CoinPlan_dummy() {
    }

    public CoinPlan_dummy(int coin, long amount, String label) {
        this.coin = coin;
        this.amount = amount;
        this.label = label;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }
}
