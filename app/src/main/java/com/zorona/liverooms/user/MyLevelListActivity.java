package com.zorona.liverooms.user;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.zorona.liverooms.BuildConfig;
import com.zorona.liverooms.R;
import com.zorona.liverooms.activity.BaseActivity;
import com.zorona.liverooms.modelclass.LevelRoot;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.retrofit.RetrofitBuilder;
import com.zorona.liverooms.databinding.ActivityMyLevelListBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyLevelListActivity extends BaseActivity {
    ActivityMyLevelListBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_level_list);


        Glide.with(this).load(BuildConfig.BASE_URL + sessionManager.getUser().getLevel().getImage()).into(binding.myLevelImage);
        binding.tvMyLevel.setText(String.valueOf(sessionManager.getUser().getLevel().getName()));
        binding.tvSpentCoin.setText("You Spent " + String.valueOf(sessionManager.getUser().getSpentCoin()) + Const.CoinName);
        getLevelData();


    }

    private void getLevelData() {
        binding.loder.setVisibility(View.VISIBLE);
        Call<LevelRoot> call = RetrofitBuilder.create().getLevels();
        call.enqueue(new Callback<LevelRoot>() {
            @Override
            public void onResponse(Call<LevelRoot> call, Response<LevelRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getLevel().isEmpty()) {
                        binding.rvFeed.setAdapter(new LevelsAdapter(response.body().getLevel()));
                    }
                }
                binding.loder.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<LevelRoot> call, Throwable t) {

            }
        });
    }

}