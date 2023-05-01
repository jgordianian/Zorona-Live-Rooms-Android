package com.zorona.liverooms.user.wallet;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.zorona.liverooms.R;
import com.zorona.liverooms.SessionManager;
import com.zorona.liverooms.activity.BaseFragment;
import com.zorona.liverooms.bottomsheets.BottomSheetPaymentMathod;
import com.zorona.liverooms.bottomsheets.BottomSheetStripeCard;
import com.zorona.liverooms.databinding.FragmentRechargeBinding;
import com.zorona.liverooms.modelclass.DiamondPlanRoot;
import com.zorona.liverooms.modelclass.StripePaymentRoot2_e;
import com.zorona.liverooms.modelclass.UserRoot;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.retrofit.RetrofitBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RechargeFragment extends BaseFragment {


    private static final String TAG = "rechargefragment";
    private static BottomSheetPaymentMathod.PaymentMethodType paymentType;
    CoinPurchaseAdapter coinPurchaseAdapter = new CoinPurchaseAdapter();
    private String country;
    private String currency;
    private PaymentMethodCreateParams paymentMethodCreateParams;
    private Stripe stripe;

    private BillingClient billingClient;
    private String planId;


    private boolean apiCalled = false;
    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            // To be implemented in a later section.
            Log.d(TAG, "onPurchasesUpdated: 1");
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                Log.d(TAG, "onPurchasesUpdated: size  " + purchases.size());
                if (!purchases.isEmpty()) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                handlePurchase(purchases.get(0));
                            }
                        });
                    }
                }
                for (Purchase purchase : purchases) {
                    //  Toast.makeText(WalletActivity.this, "thy gyu", Toast.LENGTH_SHORT).show();

                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                // Handle any other error codes.
            }
        }
    };

    void handlePurchase(Purchase purchase) {
        // Purchase retrieved from BillingClient#queryPurchasesAsync or your PurchasesUpdatedListener.

        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.


        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        if (!apiCalled) {
            Log.d(TAG, "handlePurchase: qwetuioooi2wqwertyukiol==================");
            apiCalled = true;
            callPurchaseApiGooglePay(purchase);

        } else {
            Log.d(TAG, "handlePurchase: sdsd");
        }


        ConsumeResponseListener listener = (billingResult, purchaseToken) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                Log.d(TAG, "handlePurchase: consume");
                // Handle the success of the consume operation.
            }
        };

        billingClient.consumeAsync(consumeParams, listener);


    }

    public RechargeFragment() {
        // Required empty public constructor
    }

    SessionManager sessionManager;
    FragmentRechargeBinding binding;

    private DiamondPlanRoot.DiamondPlanItem selectedPlan;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recharge, container, false);
        sessionManager = new SessionManager(getActivity());

        PaymentConfiguration.init(
                getActivity(),
                sessionManager.getSetting().getStripePublishableKey());

        if (sessionManager.getStringValue(Const.COUNTRY).equalsIgnoreCase("India")) {
            country = "IN";
            currency = "INR";
        } else {
            country = "US";
            currency = "USD";
        }

        billingClient = BillingClient.newBuilder(getActivity())
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.d(TAG, "onBillingSetupFinished: ");
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.tvMyCoins.setText(String.valueOf(sessionManager.getUser().getDiamond()));
                                initMain();
                                initListner();

                            }
                        });
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d(TAG, "onBillingServiceDisconnected: ");
            }
        });

        return binding.getRoot();
    }

    private void initListner() {
        coinPurchaseAdapter.setOnCoinPlanClickListner(new CoinPurchaseAdapter.OnCoinPlanClickListner() {
            @Override
            public void onPlanClick(DiamondPlanRoot.DiamondPlanItem coinPlan) {
                RechargeFragment.this.selectedPlan = coinPlan;
                new BottomSheetPaymentMathod(RechargeFragment.this.getActivity(), new BottomSheetPaymentMathod.OnPaymentOptionListner() {
                    @Override
                    public void onPaymentOptionSelected(BottomSheetPaymentMathod.PaymentMethodType type) {
                        RechargeFragment.paymentType = type;
                        if (paymentType == BottomSheetPaymentMathod.PaymentMethodType.GOOGLE) {
                            if (billingClient.isReady()) {
                                Log.d(TAG, "onClick: fd " + selectedPlan.getId());
                                planId = selectedPlan.getId();
                                RechargeFragment.this.setUpSku(selectedPlan.getProductKey());
                            } else {
                                Log.d("TAG", "paymetMethord: bp not init");
                            }

                        } else {
                            new BottomSheetStripeCard(RechargeFragment.this.getActivity(), new BottomSheetStripeCard.OnStripeCardFillLister() {
                                @Override
                                public void onPayButtonClick(PaymentMethodCreateParams paymentMethodCreateParams) {
                                    RechargeFragment.this.paymentMethodCreateParams = paymentMethodCreateParams;
                                    stripe = new Stripe(getActivity(), PaymentConfiguration.getInstance(getActivity()).getPublishableKey());
                                    stripe.createPaymentMethod(paymentMethodCreateParams, new ApiResultCallback<PaymentMethod>() {
                                        @RequiresApi(api = Build.VERSION_CODES.R)
                                        @Override
                                        public void onSuccess(@NonNull PaymentMethod result) {
                                            Log.d(TAG, "onSuccess: ");
                                            pay(result.id, null);
                                            // Create and confirm the PaymentIntent by calling the sample server's /pay endpoint.

                                        }

                                        @Override
                                        public void onError(@NonNull Exception e) {
                                            Log.d(TAG, "onError: " + e.getMessage());
                                        }
                                    });
                                }
                            });
                        }
                    }
                });

            }
        });
    }

    private void setUpSku(String productid) {
        List<String> skuList = new ArrayList<>();
        //  skuList.add("tanzanite");
//         skuList.add("android.test.purchased");
        skuList.add(productid);

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> {
                    // Process the result.
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                Log.d(TAG, "setUpSku : 111 " + skuDetailsList.size());
                            } catch (Exception e) {
                                Log.d(TAG, "setUpSku: >>>>>>>>>> " +e.getMessage());
                                e.printStackTrace();
                            }
                            if (skuDetailsList.isEmpty()) {
                                Toast.makeText(getActivity(), "Purchase error", Toast.LENGTH_SHORT).show();
                                return;

                            } else {
                                lunchPayment(skuDetailsList.get(0));
                            }
                        });
                    } else {
                        Log.d(TAG, "setUpSku: get act is null");
                    }

                });
    }

    private void lunchPayment(SkuDetails s) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(s)
                .build();
        int responseCode = billingClient.launchBillingFlow(getActivity(), billingFlowParams).getResponseCode();

        Log.d(TAG, "lunchPayment: " + responseCode);
    }




    @RequiresApi(api = Build.VERSION_CODES.R)
    public void pay(@Nullable String paymentMethodId, @Nullable String paymentIntentId) {
        // ...continued in the next step

        JsonObject jsonObject = new JsonObject();
        if (paymentMethodId != null) {
            jsonObject.addProperty("payment_method_id", paymentMethodId);
            jsonObject.addProperty("userId", sessionManager.getUser().getId());
            jsonObject.addProperty("planId", selectedPlan.getId());
            jsonObject.addProperty("currency", currency.toLowerCase());
            stripePurchased(jsonObject);
        } else {
            jsonObject.addProperty("payment_intent_id", paymentIntentId);
            jsonObject.addProperty("userId", sessionManager.getUser().getId());
            jsonObject.addProperty("planId", selectedPlan.getId());
            jsonObject.addProperty("currency", currency.toLowerCase());
            stripePurchasedDone(jsonObject);
        }


    }

    private void stripePurchasedDone(JsonObject jsonObject) {
        Call<UserRoot> call = RetrofitBuilder.create().purchsePlanStripeDiamons(jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200 && response.body().isStatus()) {

                    Toast.makeText(getActivity(), "plan purchased", Toast.LENGTH_SHORT).show();
                    sessionManager.saveUser(response.body().getUser());
                    binding.tvMyCoins.setText(String.valueOf(sessionManager.getUser().getDiamond()));
                } else {

                    Toast.makeText(getActivity(), "Purchase error", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void stripePurchased(JsonObject jsonObject) {
        Call<StripePaymentRoot2_e> call = RetrofitBuilder.create().setStripeDiamonds(jsonObject);
        call.enqueue(new Callback<StripePaymentRoot2_e>() {
            @Override
            public void onResponse(Call<StripePaymentRoot2_e> call, Response<StripePaymentRoot2_e> response) {
                if (response.code() == 200 && response.body().isStatus()) {
                    if (response.body().getPaymentIntentClientSecret() != null) {
                        if (getActivity() != null) {

                            getActivity().runOnUiThread(() ->
                                    stripe.handleNextActionForPayment(RechargeFragment.this, response.body().getPaymentIntentClientSecret()));
                        }
//                stripe.confirmPayment(PurchaseActivity_tt.this, confirmParams);

                    }
                } else {
                    Toast.makeText(getActivity(), "Purchse error", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<StripePaymentRoot2_e> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    private void callPurchaseApiGooglePay(Purchase purchase) {
        if (getActivity() == null) return;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sessionManager.getUser().getId());
        jsonObject.addProperty("planId", selectedPlan.getId());
        jsonObject.addProperty("productId", selectedPlan.getProductKey());
        jsonObject.addProperty("packageName", getActivity().getPackageName());
        jsonObject.addProperty("token", purchase.getPurchaseToken());
        Call<UserRoot> call = RetrofitBuilder.create().callPurchaseApiGooglePayDiamond(jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {

                    if (response.body().isStatus() && response.body().getUser() != null) {
                        Toast.makeText(getActivity(), "Purchased", Toast.LENGTH_SHORT).show();
                        sessionManager.saveUser(response.body().getUser());
                        binding.tvMyCoins.setText(String.valueOf(sessionManager.getUser().getDiamond()));
                    } else {
                        Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
                    }


                    apiCalled = false;
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {

            }
        });
    }

    private void initMain() {

        Call<DiamondPlanRoot> call = RetrofitBuilder.create().getDiamondsPlan();
        call.enqueue(new Callback<DiamondPlanRoot>() {
            @Override
            public void onResponse(Call<DiamondPlanRoot> call, Response<DiamondPlanRoot> response) {
                if (response.code()==200) {
                    if (response.body().isStatus() && !response.body().getCoinPlan().isEmpty()) {
                        coinPurchaseAdapter.addData(response.body().getCoinPlan());
                    }
                }
            }

            @Override
            public void onFailure(Call<DiamondPlanRoot> call, Throwable t) {

            }
        });
        binding.rvRecharge.setAdapter(coinPurchaseAdapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
        super.onActivityResult(requestCode, resultCode, data);


    }

    private void displayAlert(@NonNull String title,
                              @Nullable String message, boolean b) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message);

        builder.setPositiveButton("Ok", (dialog, which) -> {

        });
        builder.create().show();
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        private final WeakReference<RechargeFragment> activityRef;

        PaymentResultCallback(@NonNull RechargeFragment activity) {
            activityRef = new WeakReference<>(activity);
        }

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final RechargeFragment activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                activity.displayAlert("Payment completed",
                        gson.toJson(paymentIntent), true);
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert("Payment failed",
                        paymentIntent.getLastPaymentError().getMessage(), false);
            } else if (status == PaymentIntent.Status.RequiresConfirmation) {
                // After handling a required action on the client, the status of the PaymentIntent is
                // requires_confirmation. You must send the PaymentIntent ID to your backend
                // and confirm it to finalize the payment. This step enables your integration to
                // synchronously fulfill the order on your backend and return the fulfillment result
                // to your client.
                activity.pay(null, paymentIntent.getId());
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final RechargeFragment activity = activityRef.get();
            if (activity == null) {
                return;
            }

            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString(), false);
        }
    }

}