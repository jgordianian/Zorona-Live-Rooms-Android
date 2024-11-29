package com.app.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

public class StripePaymentRoot2_e {

    @SerializedName("requires_action")
    private boolean requiresAction;
    @SerializedName("status")
    private boolean status;
    @SerializedName("payment_intent_client_secret")
    private String paymentIntentClientSecret;

    public boolean isStatus() {
        return status;
    }

    public boolean isRequiresAction() {
        return requiresAction;
    }

    public String getPaymentIntentClientSecret() {
        return paymentIntentClientSecret;
    }
}