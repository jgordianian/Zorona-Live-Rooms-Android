package com.zorona.liverooms.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.zorona.liverooms.activity.BaseActivity;
import com.zorona.liverooms.modelclass.GuestUsersListRoot;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.retrofit.RetrofitBuilder;
import com.zorona.liverooms.MyLoader;
import com.zorona.liverooms.R;
import com.zorona.liverooms.databinding.ActivityFollowrsListBinding;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowrsListActivity extends BaseActivity {
    ActivityFollowrsListBinding binding;
    FollowrsUsersAdapter followrsUsersAdapter = new FollowrsUsersAdapter();
    private int type;
    private String userId;
    private int start = 0;
    MyLoader myLoader = new MyLoader();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_followrs_list);
        binding.setMyLoder(myLoader);
        Intent intent = getIntent();
        userId = intent.getStringExtra(Const.USERID);
        type = intent.getIntExtra(Const.TYPE, 0);
        if (userId!=null && !userId.isEmpty()) {

            if (type == 1) {
                getFollowrs(false);
            } else if (type == 2) {
                getFollowing(false);
            }
        }

        binding.rvFeed.setAdapter(followrsUsersAdapter);

        binding.swipeRefresh.setOnRefreshListener((refreshLayout) -> {
            if (type == 1) {
                getFollowrs(false);
            } else if (type == 2) {
                getFollowing(false);
            }
        });
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
            if (type == 1) {
                getFollowrs(true);
            } else if (type == 2) {
                getFollowing(true);
            }
        });
    }

    private void getFollowing(boolean isLoadMore) {

        myLoader.noData.set(false);
        if (isLoadMore) {
            start = start + Const.LIMIT;

        } else {
            start = 0;
            followrsUsersAdapter.clear();
            myLoader.isFristTimeLoading.set(true);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("start", start);
        jsonObject.addProperty("limit", Const.LIMIT);
        Call<GuestUsersListRoot> call = RetrofitBuilder.create().getFollowrsList(jsonObject);
        call.enqueue(new Callback<GuestUsersListRoot>() {
            @Override
            public void onResponse(Call<GuestUsersListRoot> call, Response<GuestUsersListRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getUser().isEmpty()) {
                        followrsUsersAdapter.addData(response.body().getUser());
                    } else if (start == 0) {
                        binding.noData.setVisibility(View.GONE);
                    }
                }
                myLoader.isFristTimeLoading.set(false);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();

            }

            @Override
            public void onFailure(Call<GuestUsersListRoot> call, Throwable t) {

            }
        });
    }

    private void getFollowrs(boolean isLoadMore) {
        myLoader.noData.set(false);
        if (isLoadMore) {
            start = start + Const.LIMIT;

        } else {
            start = 0;
            followrsUsersAdapter.clear();
            myLoader.isFristTimeLoading.set(true);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("start", start);
        jsonObject.addProperty("limit", Const.LIMIT);

        Call<GuestUsersListRoot> call = RetrofitBuilder.create().getFollowingList(jsonObject);
        call.enqueue(new Callback<GuestUsersListRoot>() {
            @Override
            public void onResponse(Call<GuestUsersListRoot> call, Response<GuestUsersListRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getUser().isEmpty()) {
                        followrsUsersAdapter.addData(response.body().getUser());
                    } else if (start == 0) {
                        binding.noData.setVisibility(View.GONE);
                    }
                }
                myLoader.isFristTimeLoading.set(false);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();

            }

            @Override
            public void onFailure(Call<GuestUsersListRoot> call, Throwable t) {

            }
        });
    }
}