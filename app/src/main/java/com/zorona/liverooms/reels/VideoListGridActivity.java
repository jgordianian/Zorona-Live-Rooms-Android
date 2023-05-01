package com.zorona.liverooms.reels;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.zorona.liverooms.activity.BaseActivity;
import com.zorona.liverooms.modelclass.ReliteRoot;
import com.zorona.liverooms.modelclass.RestResponse;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.retrofit.RetrofitBuilder;
import com.zorona.liverooms.MyLoader;
import com.zorona.liverooms.R;
import com.zorona.liverooms.SessionManager;
import com.zorona.liverooms.databinding.ActivityVideoListGridBinding;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoListGridActivity extends BaseActivity {
    ActivityVideoListGridBinding binding;
    ProfileVideoGridAdapter profileVideoGridAdapter = new ProfileVideoGridAdapter();
    SessionManager sessionManager;
    private String userId;
    private int start = 0;
    MyLoader myLoader = new MyLoader();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_list_grid);
        binding.setLoader(myLoader);


        sessionManager = new SessionManager(this);
        Intent intent = getIntent();
        userId = intent.getStringExtra(Const.DATA);
        binding.rvFeed.setAdapter(profileVideoGridAdapter);


        if (userId != null && !userId.isEmpty()) {
            getData(false);
        }
        binding.swipeRefresh.setOnRefreshListener((refreshLayout) -> getData(false));
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> getData(true));


        profileVideoGridAdapter.setOnVideoGridClickListner(new ProfileVideoGridAdapter.OnVideoGridClickListner() {
            @Override
            public void onVideoClick(int position) {
                startActivity(new Intent(VideoListGridActivity.this, ReelsActivity.class)
                        .putExtra(Const.POSITION, position)
                        .putExtra(Const.DATA, new Gson().toJson(profileVideoGridAdapter.getList())));
            }

            @Override
            public void onDeleteClick(ReliteRoot.VideoItem postItem, int position) {
                Dialog dialog = new Dialog(VideoListGridActivity.this);
                dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
                dialog.setContentView(R.layout.delete_popup);

                Button yes = dialog.findViewById(R.id.yes);
                Button no = dialog.findViewById(R.id.no);

                yes.setOnClickListener(v -> {
                    DeleteVideo(postItem, position);

                    dialog.dismiss();
                });

                no.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
            }
        });

    }


    public void DeleteVideo(ReliteRoot.VideoItem postItem, int pos) {
        Call<RestResponse> call = RetrofitBuilder.create().deleteRelite(postItem.getId());

        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.body().isStatus() && response.isSuccessful()) {
                    Toast.makeText(VideoListGridActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    profileVideoGridAdapter.getList().remove(pos);
                    profileVideoGridAdapter.notifyItemRemoved(pos);
                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {

            }
        });
    }

    private void getData(boolean isLoadMore) {
        if (isLoadMore) {
            start = start + Const.LIMIT;
        } else {
            myLoader.isFristTimeLoading.set(true);
            profileVideoGridAdapter.clear();
            start = 0;
        }
        myLoader.noData.set(false);
        Call<ReliteRoot> call = RetrofitBuilder.create().getRelites(SessionManager.getUserId(this), "User", start, Const.LIMIT);
        call.enqueue(new Callback<ReliteRoot>() {
            @Override
            public void onResponse(Call<ReliteRoot> call, Response<ReliteRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getVideo().isEmpty()) {
                        profileVideoGridAdapter.addData(response.body().getVideo());
                    } else if (start == 0) {
                        myLoader.noData.set(true);
                    }
                }

                myLoader.isFristTimeLoading.set(false);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
            }

            @Override
            public void onFailure(Call<ReliteRoot> call, Throwable t) {

            }
        });
    }
}