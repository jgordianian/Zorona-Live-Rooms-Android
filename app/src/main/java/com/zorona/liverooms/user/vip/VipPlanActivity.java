package com.app.liverooms.user.vip;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.app.liverooms.R;
import com.app.liverooms.activity.BaseActivity;
import com.app.liverooms.activity.SpleshActivity;
import com.app.liverooms.adapter.DotAdaptr;
import com.app.liverooms.bottomsheets.BottomSheetPaymentMathod;
import com.app.liverooms.bottomsheets.BottomSheetStripeCard;
import com.app.liverooms.databinding.ActivityVipPlanBinding;
import com.app.liverooms.modelclass.BannerRoot;
import com.app.liverooms.modelclass.StripePaymentRoot2_e;
import com.app.liverooms.modelclass.UserRoot;
import com.app.liverooms.modelclass.VipPlanRoot;
import com.app.liverooms.popups.PopupBuilder;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.retrofit.RetrofitBuilder;
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

public class VipPlanActivity extends BaseActivity {
    private static final String TAG = "vipplanact";
    private static BottomSheetPaymentMathod.PaymentMethodType paymentType;

    ActivityVipPlanBinding binding;
    VipImagesAdapter vipImagesAdapter = new VipImagesAdapter();
    VipPlanAdapter vipPlanAdapter = new VipPlanAdapter();

    MutableLiveData<VipPlanRoot.VipPlanItem> selectedPlan = new MutableLiveData<>(null);
    PaymentMethodCreateParams paymentMethodCreateParams;
    private String country;
    private String currency;
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handlePurchase(purchases.get(0));
                        }
                    });
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vip_plan);

        PaymentConfiguration.init(
                this,
                sessionManager.getSetting().getStripePublishableKey());
        if (sessionManager.getStringValue(Const.COUNTRY).equalsIgnoreCase("India")) {
            country = "IN";
            currency = "INR";
        } else {
            country = "US";
            currency = "USD";
        }

        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.d(TAG, "onBillingSetupFinished: ");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.rvPlan.setAdapter(vipPlanAdapter);
                            initData();
                            initListner();
                        }
                    });
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d(TAG, "onBillingServiceDisconnected: ");
            }
        });
    }

    private void callPurchaseApiGooglePay(Purchase purchase) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sessionManager.getUser().getId());
        jsonObject.addProperty("planId", selectedPlan.getValue().getId());
        jsonObject.addProperty("productId", selectedPlan.getValue().getProductKey());
        jsonObject.addProperty("packageName", getPackageName());
        jsonObject.addProperty("token", purchase.getPurchaseToken());
        Call<UserRoot> call = RetrofitBuilder.create().callPurchaseApiGooglePayVip(jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getUser() != null) {
                        Toast.makeText(VipPlanActivity.this, "Purchased", Toast.LENGTH_SHORT).show();
                        sessionManager.saveUser(response.body().getUser());
                        showSuccessPopup();
                    } else {
                        Toast.makeText(VipPlanActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }


                    apiCalled = false;
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {

            }
        });
    }

    private void initListner() {
        vipPlanAdapter.setOnPlanClickLisnter(vipPlanItem -> selectedPlan.setValue(vipPlanItem));

        binding.btnPurchase.setOnClickListener(v -> {
            new BottomSheetPaymentMathod(this, type -> {
                VipPlanActivity.paymentType = type;
                if (paymentType == BottomSheetPaymentMathod.PaymentMethodType.GOOGLE) {

                    if (billingClient.isReady()) {
                        Log.d(TAG, "onClick: fd " + selectedPlan.getValue().getId());
                        planId = selectedPlan.getValue().getId();
                        setUpSku(selectedPlan.getValue().getProductKey());
                    } else {
                        Log.d("TAG", "paymetMethord: bp not init");
                    }


                } else {
                    new BottomSheetStripeCard(this, new BottomSheetStripeCard.OnStripeCardFillLister() {
                        @Override
                        public void onPayButtonClick(PaymentMethodCreateParams paymentMethodCreateParams) {
                            VipPlanActivity.this.paymentMethodCreateParams = paymentMethodCreateParams;
                            stripe = new Stripe(VipPlanActivity.this, PaymentConfiguration.getInstance(VipPlanActivity.this).getPublishableKey());
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

            });
        });
        selectedPlan.observe(this, vipPlanItem -> {
            binding.btnPurchase.setEnabled(vipPlanItem != null);
            if (vipPlanItem != null) {
                binding.btnPurchase.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.pink));

            } else {
                binding.btnPurchase.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.graylight));
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.d(TAG, "run: " + skuDetailsList.size());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (skuDetailsList.isEmpty()) {
                                Toast.makeText(VipPlanActivity.this, "Purchase error", Toast.LENGTH_SHORT).show();
                                return;

                            } else {
                                lunchPayment(skuDetailsList.get(0));
                            }

                        }
                    });

                });
    }


    private void lunchPayment(SkuDetails s) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(s)
                .build();
        int responseCode = billingClient.launchBillingFlow(this, billingFlowParams).getResponseCode();

        Log.d(TAG, "lunchPayment: " + responseCode);
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    public void pay(@Nullable String paymentMethodId, @Nullable String paymentIntentId) {
        // ...continued in the next step

        if (paymentMethodId != null) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("payment_method_id", paymentMethodId);
            jsonObject.addProperty("userId", sessionManager.getUser().getId());
            jsonObject.addProperty("planId", selectedPlan.getValue().getId());
            jsonObject.addProperty("currency", currency.toLowerCase());
            stripePurchased(jsonObject);
        } else {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("payment_intent_id", paymentIntentId);
            jsonObject.addProperty("userId", sessionManager.getUser().getId());
            jsonObject.addProperty("planId", selectedPlan.getValue().getId());
            jsonObject.addProperty("currency", currency.toLowerCase());
            stripePurchasedDone(jsonObject);
        }


    }

    private void stripePurchased(JsonObject jsonObject) {
        Call<StripePaymentRoot2_e> call = RetrofitBuilder.create().setStripeVip(jsonObject);
        call.enqueue(new Callback<StripePaymentRoot2_e>() {
            @Override
            public void onResponse(Call<StripePaymentRoot2_e> call, Response<StripePaymentRoot2_e> response) {
                if (response.code() == 200 && response.body().isStatus()) {
                    if (response.body().getPaymentIntentClientSecret() != null) {

                        runOnUiThread(() ->
                                stripe.handleNextActionForPayment(VipPlanActivity.this, response.body().getPaymentIntentClientSecret()));

//                stripe.confirmPayment(PurchaseActivity_tt.this, confirmParams);

                    }
                } else {
                    Toast.makeText(VipPlanActivity.this, "Purchase error", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<StripePaymentRoot2_e> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void stripePurchasedDone(JsonObject jsonObject) {
        Call<UserRoot> call = RetrofitBuilder.create().purchsePlanStripeVip(jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200 && response.body().isStatus()) {
                    Toast.makeText(VipPlanActivity.this, "plan purchased", Toast.LENGTH_SHORT).show();
                    sessionManager.saveUser(response.body().getUser());
                    showSuccessPopup();
                } else {

                    Toast.makeText(VipPlanActivity.this, "Purchase error", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void showSuccessPopup() {
        new PopupBuilder(VipPlanActivity.this).showReliteDiscardPopup("You are VIP now",
                "Restart app", "Continue", "", () -> {
                    Intent intent = new Intent(VipPlanActivity.this, SpleshActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finishAffinity();
                    startActivity(intent);
                });
    }

    private void initData() {
        binding.shimmer.setVisibility(View.VISIBLE);
        binding.rvBanner.setVisibility(View.GONE);
        Call<VipPlanRoot> call = RetrofitBuilder.create().getVipPlan();
        call.enqueue(new Callback<VipPlanRoot>() {
            @Override
            public void onResponse(Call<VipPlanRoot> call, Response<VipPlanRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getVipPlan().isEmpty()) {
                        vipPlanAdapter.addData(response.body().getVipPlan());
                        vipPlanAdapter.setSelected(0);
                        selectedPlan.setValue(response.body().getVipPlan().get(0));
                    }
                }
                binding.shimmer.setVisibility(View.GONE);
                binding.rvBanner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<VipPlanRoot> call, Throwable t) {

            }
        });


        binding.shimmerBanner.setVisibility(View.VISIBLE);
        Call<BannerRoot> call1 = RetrofitBuilder.create().getBanner("VIP");
        call1.enqueue(new Callback<BannerRoot>() {
            @Override
            public void onResponse(Call<BannerRoot> call, Response<BannerRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getBanner().isEmpty()) {
                        vipImagesAdapter.addData(response.body().getBanner());
                        binding.rvBanner.setAdapter(vipImagesAdapter);
                        if (vipImagesAdapter.getItemCount() > 1) {
                            setVIpSlider();
                        }
                    }

                }
                binding.shimmerBanner.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<BannerRoot> call, Throwable t) {

            }
        });
    }

    private void setVIpSlider() {


        DotAdaptr dotAdapter = new DotAdaptr(vipImagesAdapter.getItemCount(), R.color.pink);
        binding.rvDots.setAdapter(dotAdapter);
        binding.rvBanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager myLayoutManager = (LinearLayoutManager) binding.rvBanner.getLayoutManager();
                int scrollPosition = myLayoutManager.findFirstVisibleItemPosition();
                dotAdapter.changeDot(scrollPosition);
            }
        });
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int pos = 0;
            boolean flag = true;

            @Override
            public void run() {
                if (pos == vipImagesAdapter.getItemCount() - 1) {
                    flag = false;
                } else if (pos == 0) {
                    flag = true;
                }
                if (flag) {
                    pos++;
                } else {
                    pos--;
                }
                binding.rvBanner.smoothScrollToPosition(pos);
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable, 2000);
    }

    @Override
    public void onClickBack(View view) {
        super.onClickBack(view);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
        super.onActivityResult(requestCode, resultCode, data);


    }

    private void displayAlert(@NonNull String title,
                              @Nullable String message, boolean b) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);

        builder.setPositiveButton("Ok", (dialog, which) -> {

        });
        builder.create().show();
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        private final WeakReference<VipPlanActivity> activityRef;

        PaymentResultCallback(@NonNull VipPlanActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final VipPlanActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                activity.runOnUiThread(() -> {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    activity.displayAlert("Payment completed",
                            gson.toJson(paymentIntent), true);
                });
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.runOnUiThread(() -> activity.displayAlert("Payment failed",
                        paymentIntent.getLastPaymentError().getMessage(), false));
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
            final VipPlanActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            // Payment request failed – allow retrying using the same payment method
            activity.runOnUiThread(() -> {
                activity.displayAlert("Error", e.toString(), false);
            });
        }
    }

}