package com.zorona.liverooms.purchase;

import android.app.Activity;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
//import com.android.billingclient.api.PurchasesResult;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetailsParams;

import java.util.ArrayList;
import java.util.List;


public class Myplaystorebilling {

    private static final String TAG = "purchased";
    private final BillingClient billingClient;
    private final Activity activity;
    private final OnPurchaseComplete onPurchaseComplete;

    private boolean isConnected = false;

    public Myplaystorebilling(Activity activity, OnPurchaseComplete onPurchaseComplete) {

        this.activity = activity;
        this.onPurchaseComplete = onPurchaseComplete;

        PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                // To be implemented in a later section.
                Log.d(TAG, "onPurchasesUpdated: 1");
                if (billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK
                        && purchases!=null) {
                    Log.d(TAG, "onPurchasesUpdated: size  " + purchases.size());
                    if (!purchases.isEmpty()) {

                        handlePurchase(purchases.get(0));

                    }
                    for (Purchase purchase : purchases) {
                        //  Toast.makeText(WalletActivity.this, "thy gyu", Toast.LENGTH_SHORT).show();

                    }
                } else if (billingResult.getResponseCode()==BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                    onPurchaseComplete.onPurchaseResult(false, null);
                } else {
                    // Handle any other error codes.
                    onPurchaseComplete.onPurchaseResult(false, null);
                }
            }
        };

        billingClient = BillingClient.newBuilder(activity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.d(TAG, "onBillingSetupFinished: ");
                    isConnected = true;
                    onPurchaseComplete.onConnected(true);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d(TAG, "onBillingServiceDisconnected: ");
                isConnected = false;
            }
        });

    }

    void handlePurchase(Purchase purchase) {

        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();



        Log.d(TAG, "handlePurchase: 1");
        ConsumeResponseListener listener = (billingResult, purchaseToken) -> {
            Log.d(TAG, "handlePurchase: 2 ");
            if (billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK) {

                try {
                    Log.d(TAG, "handlePurchase: counsume " + billingResult.getResponseCode());
                    Log.d(TAG, "handlePurchase: counsume " + billingResult.getDebugMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Handle the success of the consume operation.
            }
        };

        billingClient.consumeAsync(consumeParams, listener);

        onPurchaseComplete.onPurchaseResult(true, purchase);
    }


    public void startPurchase(String productId, String skuType) {
        if (isConnected) {
            Log.d(TAG, "startPurchase: " + productId);

            List<String> skuList = new ArrayList<>();
            skuList.add(productId);
            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            params.setSkusList(skuList).setType(skuType);
            billingClient.querySkuDetailsAsync(params.build(),
                    (billingResult1, skuDetailsList) -> {
                        // Process the result.
                        Log.d(TAG, "startPurchase:1 " + skuDetailsList);
                        Log.d(TAG, "billingResult1:1 " + billingResult1);

                        BillingFlowParams billingFlowParams = null;
                        if (skuDetailsList != null) {
                            Log.d(TAG, "startPurchase:2 " + skuDetailsList.size());
                            Log.d("TAG", "startPurchase: " + skuDetailsList.get(0));
                            billingFlowParams = BillingFlowParams.newBuilder()
                                    .setSkuDetails(skuDetailsList.get(0))
                                    .build();
                        }
                        if (billingFlowParams != null) {
                            billingClient.launchBillingFlow(activity, billingFlowParams);
                        }

                    });

        } else {
            Log.d(TAG, "startPurchase: sdsd");
        }
    }

    //check if subscription is running or not
    public void checkSubscription(String productId, String skuType) {
        if (isConnected) {
            Log.d(TAG, "startPurchase: " + productId);

            List<String> skuList = new ArrayList<>();
            skuList.add(productId);
            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            params.setSkusList(skuList).setType(skuType);
            billingClient.querySkuDetailsAsync(params.build(),
                    (billingResult1, skuDetailsList) -> {
                        // Process the result.
                        Log.d(TAG, "startPurchase:1 " + skuDetailsList);
                        Log.d(TAG, "billingResult1:1 " + billingResult1);

                        BillingFlowParams billingFlowParams = null;
                        if (skuDetailsList != null) {
                            Log.d(TAG, "startPurchase:2 " + skuDetailsList.size());
                            Log.d("TAG", "startPurchase: " + skuDetailsList.get(0));
                            billingFlowParams = BillingFlowParams.newBuilder()
                                    .setSkuDetails(skuDetailsList.get(0))
                                    .build();
                        }
                        if (billingFlowParams != null) {
                            billingClient.launchBillingFlow(activity, billingFlowParams);
                        }

                    });

        } else {
            Log.d(TAG, "startPurchase: sdsd");
        }
    }

    public void onDestroy() {
        if (isConnected)
            billingClient.endConnection();
    }

    public interface OnPurchaseComplete {

        void onConnected(boolean isConnect);

        void onPurchaseResult(boolean isPurchaseSuccess, Purchase purchase);
    }
}
