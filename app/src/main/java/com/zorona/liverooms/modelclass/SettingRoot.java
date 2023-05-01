package com.zorona.liverooms.modelclass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SettingRoot {

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    @SerializedName("setting")
    private Setting setting;

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public Setting getSetting() {
        return setting;
    }

    public static class Setting {

        @SerializedName("isAppActive")
        private boolean isAppActive;

        @SerializedName("callCharge")
        private int callCharge;

        @SerializedName("maxSecondForVideo")
        private int maxSecondForVideo;

        @SerializedName("googlePlayEmail")
        private String googlePlayEmail;

        @SerializedName("minRcoinForCashOut")
        private int minRcoinForCashOut;

        @SerializedName("stripeSwitch")
        private boolean stripeSwitch;

        @SerializedName("loginBonus")
        private int loginBonus;

        @SerializedName("privacyPolicyLink")
        private String privacyPolicyLink;

        @SerializedName("agoraKey")
        private String agoraKey;

        @SerializedName("rCoinForDiamond")
        private int rCoinForDiamond;

        @SerializedName("agoraCertificate")
        private String agoraCertificate;

        @SerializedName("googlePlaySwitch")
        private boolean googlePlaySwitch;

        @SerializedName("freeDiamondForAd")
        private int freeDiamondForAd;

        @SerializedName("privacyPolicyText")
        private String privacyPolicyText;

        @SerializedName("currency")
        private String currency;


        @SerializedName("googlePlayKey")
        private String googlePlayKey;

        @SerializedName("referralBonus")
        private int referralBonus;

        @SerializedName("stripeSecretKey")
        private String stripeSecretKey;

        @SerializedName("stripePublishableKey")
        private String stripePublishableKey;

        @SerializedName("maxAdPerDay")
        private int maxAdPerDay;

        @SerializedName("chatCharge")
        private int chatCharge;

        @SerializedName("_id")
        private String id;

        @SerializedName("rCoinForCashOut")
        private int rCoinForCaseOut;

        @SerializedName("paymentGateway")
        private List<String> paymentGateway;

        public boolean isIsAppActive() {
            return isAppActive;
        }

        public int getCallCharge() {
            return callCharge;
        }

        public int getMaxSecondForVideo() {
            return maxSecondForVideo;
        }

        public String getGooglePlayEmail() {
            return googlePlayEmail;
        }

        public int getMinRcoinForCashOut() {
            return minRcoinForCashOut;
        }

        public boolean isStripeSwitch() {
            return stripeSwitch;
        }

        public int getLoginBonus() {
            return loginBonus;
        }

        public String getPrivacyPolicyLink() {
            return privacyPolicyLink;
        }

        public String getAgoraKey() {
            return agoraKey;
        }

        public int getRCoinForDiamond() {
            return rCoinForDiamond;
        }

        public String getAgoraCertificate() {
            return agoraCertificate;
        }

        public boolean isGooglePlaySwitch() {
            return googlePlaySwitch;
        }

        public int getFreeDiamondForAd() {
            return freeDiamondForAd;
        }

        public String getPrivacyPolicyText() {
            return privacyPolicyText;
        }

        public String getCurrency() {
            return currency;
        }


        public String getGooglePlayKey() {
            return googlePlayKey;
        }

        public int getReferralBonus() {
            return referralBonus;
        }

        public String getStripeSecretKey() {
            return stripeSecretKey;
        }

        public String getStripePublishableKey() {
            return stripePublishableKey;
        }

        public int getMaxAdPerDay() {
            return maxAdPerDay;
        }

        public int getChatCharge() {
            return chatCharge;
        }

        public String getId() {
            return id;
        }

        public int getRCoinForCaseOut() {
            return rCoinForCaseOut;
        }

        public List<String> getPaymentGateway() {
            return paymentGateway;
        }
    }
}