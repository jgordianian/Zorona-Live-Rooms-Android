package com.app.liverooms.user.wallet;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.app.liverooms.R;
import com.app.liverooms.activity.BaseActivity;
import com.app.liverooms.databinding.ActivityCashOutBinding;
import com.app.liverooms.modelclass.ReedemListRoot;
import com.app.liverooms.modelclass.RestResponse;
import com.app.liverooms.modelclass.UserRoot;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.retrofit.RetrofitBuilder;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CashOutActivity extends BaseActivity {
    ActivityCashOutBinding binding;

    List<String> paymentGateways = new ArrayList<>();
    int minRcoinForCashout = 0, settingCurrency = 1;
    private String selectedPaymentGateway;
    private int amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cash_out);

        minRcoinForCashout = sessionManager.getSetting().getMinRcoinForCashOut();// min rcoin for cashout

        binding.tvSettingRcoin.setText(sessionManager.getSetting().getRCoinForCaseOut() + Const.CoinName);
        binding.tvSettingCurrency.setText(settingCurrency + " " + Const.getCurrency());

        paymentGateways.clear();
        paymentGateways.addAll(sessionManager.getSetting().getPaymentGateway());

        if (paymentGateways != null && !paymentGateways.isEmpty()) {
            changeDetails(paymentGateways.get(0));
            binding.rvReedemMethods.setAdapter(new ReedemMethodAdapter(paymentGateways, this::changeDetails));


        } else {
            Toast.makeText(this, "No Payment Method Found", Toast.LENGTH_SHORT).show();
        }

        UserRoot.User user = sessionManager.getUser();
        user.setrCoin(sessionManager.getUser().getRCoin());
        sessionManager.saveUser(user);

        initListner();
        getReedemHistotry();
    }

    private void getReedemHistotry() {
        Call<ReedemListRoot> call = RetrofitBuilder.create().getReedemHistotry(sessionManager.getUser().getId());
        call.enqueue(new Callback<ReedemListRoot>() {
            @Override
            public void onResponse(Call<ReedemListRoot> call, Response<ReedemListRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getRedeem().isEmpty()) {
                        binding.rvHistory.setAdapter(new ReedemHistoryAdapter(response.body().getRedeem()));
                    }
                }
            }

            @Override
            public void onFailure(Call<ReedemListRoot> call, Throwable t) {

            }
        });
    }

    private void initListner() {
        binding.btnSubmit.setOnClickListener(v -> submitData());
        binding.etReedemCoin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    if (!s.toString().isEmpty()) {
                        amount = Integer.parseInt(s.toString());
                        if (amount < minRcoinForCashout) {

                            binding.tvNote.setText("Minimum amount is " + minRcoinForCashout + Const.CoinName);
                            binding.tvNote.setTextColor(ContextCompat.getColor(CashOutActivity.this, R.color.red));
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                binding.tvNote.setTextColor(ContextCompat.getColor(CashOutActivity.this, R.color.yellow));
                                binding.tvNote.setText("Withdrawable Rcoins " + sessionManager.getUser().getRCoin());
                            }, 1000);

                        } else if (amount > sessionManager.getUser().getRCoin()) {
                            binding.tvNote.setText("You not have enough Rcoins");
                            binding.tvNote.setTextColor(ContextCompat.getColor(CashOutActivity.this, R.color.red));
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                binding.tvNote.setTextColor(ContextCompat.getColor(CashOutActivity.this, R.color.yellow));
                                binding.tvNote.setText("Withdrawable Rcoins " + sessionManager.getUser().getRCoin());

                            }, 1000);
                        } else {
                            int diamond = amount / 100;
                            //  binding.tvDiamondsValue.setText("You Will Receive " + String.valueOf(diamond) + " Diamonds");
                        }

                        binding.tvSettingRcoin.setText(amount + Const.CoinName);
                        int cash = amount * settingCurrency / sessionManager.getSetting().getRCoinForCaseOut();
                        binding.tvSettingCurrency.setText(cash + " " + Const.getCurrency());

                    }
                } else {
                    binding.tvSettingRcoin.setText(sessionManager.getSetting().getRCoinForCaseOut() + Const.CoinName);
                    binding.tvSettingCurrency.setText(settingCurrency + " " + Const.getCurrency());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void submitData() {
        if (amount < sessionManager.getSetting().getRCoinForCaseOut()) {
            Toast.makeText(this, "Minimum amount is " + minRcoinForCashout + Const.CoinName, Toast.LENGTH_SHORT).show();
            return;
        }
        if (amount > sessionManager.getUser().getRCoin()) {
            Toast.makeText(this, "Insufficient " + Const.CoinName, Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedPaymentGateway == null || selectedPaymentGateway.isEmpty()) {
            Toast.makeText(this, "No Payment Method Found", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.etDetails.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter valid Details", Toast.LENGTH_SHORT).show();
            return;
        }

        String des = binding.etDetails.getText().toString();
        if (des.isEmpty()) {
            Toast.makeText(this, "Please Enter Your Details", Toast.LENGTH_SHORT).show();
            return;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sessionManager.getUser().getId());
        jsonObject.addProperty("paymentGateway", selectedPaymentGateway);
        jsonObject.addProperty("description", des);
        jsonObject.addProperty("rCoin", amount);
        binding.loder.setVisibility(View.VISIBLE);
        binding.btnSubmit.setEnabled(false);
        Call<RestResponse> call = RetrofitBuilder.create().cashOutDiamonds(jsonObject);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        Toast.makeText(CashOutActivity.this, "Redeem Request sent Successfully", Toast.LENGTH_SHORT).show();
                        UserRoot.User user = sessionManager.getUser();
                        int currentCoin = user.getRCoin() - amount;
                        user.setrCoin(currentCoin);
                        sessionManager.saveUser(user);
                        binding.etDetails.setText("");
                        binding.etReedemCoin.setText("");
                        getReedemHistotry();
                    }
                }
                binding.btnSubmit.setEnabled(true);
                binding.loder.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                binding.btnSubmit.setEnabled(true);
            }
        });


    }

    private void changeDetails(String s) {
        selectedPaymentGateway = s;
        if (s.equalsIgnoreCase("CNT-WALLET")) {
            binding.etDetails.setHint("Enter your CNT-WALLET details");
        } else if (s.equalsIgnoreCase("PAYPAL")) {
            binding.etDetails.setHint("Enter your PAYPAL details");
        } else {
            binding.etDetails.setHint("Enter your PAYONEER account details");
        }
    }
}