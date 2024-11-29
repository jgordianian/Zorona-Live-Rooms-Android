package com.app.liverooms.user.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.app.liverooms.R;
import com.app.liverooms.activity.BaseFragment;
import com.app.liverooms.databinding.FragmentRcoinBinding;
import com.app.liverooms.modelclass.UserRoot;
import com.app.liverooms.popups.PopupBuilder;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.retrofit.RetrofitBuilder;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RcoinFragment extends BaseFragment {


    FragmentRcoinBinding binding;
    private PopupBuilder popupBuilder;

    public RcoinFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rcoin, container, false);
        popupBuilder = new PopupBuilder(getActivity());
        initMain();
        return binding.getRoot();
    }

    private void initMain() {
        binding.tvSettingRcoin.setText(String.valueOf(sessionManager.getSetting().getRCoinForDiamond()) + Const.CoinName);
        binding.tvRcoin.setText(String.valueOf(sessionManager.getUser().getRCoin()));
        binding.tvWithdrawingRcoin.setText(String.valueOf(sessionManager.getUser().getWithdrawalRcoin()));
        binding.btnConvert.setOnClickListener(v -> {
            popupBuilder.showRcoinConvertPopup(false, sessionManager.getUser().getRCoin(), rcoin -> converRcoinToDiamond(rcoin));
        });

        binding.btnCashout.setOnClickListener(v -> {
            if (!sessionManager.getUser().getLevel().getAccessibleFunction().isCashOut()) {
                new PopupBuilder(getActivity()).showSimplePopup("You are not able to cashout at your level", "Dismiss", () -> {
                });
                return;
            }
            startActivity(new Intent(getActivity(), CashOutActivity.class));
           /* PopupBuilder popupBuilder = new PopupBuilder(getActivity());
            popupBuilder.showRcoinConvertPopup(true, myRcoin, rcoin -> {
                double cash = rcoin / 100;

            });
*/
        });

    }

    private void converRcoinToDiamond(int rcoin) {
        binding.loder.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sessionManager.getUser().getId());
        jsonObject.addProperty("rCoin", rcoin);
        Call<UserRoot> call = RetrofitBuilder.create().convertRcoinToDiamond(jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        sessionManager.saveUser(response.body().getUser());
                        double dimonds = rcoin / sessionManager.getSetting().getRCoinForDiamond();
                        String s = "Your " + rcoin + Const.CoinName + "Successfully Converted into " + dimonds + " Diamonds";
                        popupBuilder.showSimplePopup(s, "Continue", () -> initMain());
                    } else {
                        popupBuilder.showSimplePopup(response.body().getMessage(), "Continue", () -> initMain());
                    }
                }
                binding.loder.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
                binding.loder.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initMain();
    }
}