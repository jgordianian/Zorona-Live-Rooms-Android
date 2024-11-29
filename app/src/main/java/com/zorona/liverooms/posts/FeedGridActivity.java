package com.app.liverooms.posts;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.app.liverooms.activity.BaseActivity;
import com.app.liverooms.modelclass.PostRoot;
import com.app.liverooms.modelclass.RestResponse;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.retrofit.RetrofitBuilder;
import com.app.liverooms.MyLoader;
import com.app.liverooms.R;
import com.app.liverooms.SessionManager;
import com.app.liverooms.databinding.ActivityMyFeedBinding;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FeedGridActivity extends BaseActivity {
    ActivityMyFeedBinding binding;
    FeedGridAdapter feedGridAdapter = new FeedGridAdapter();

    private String userId;
    private int start = 0;
    MyLoader myLoader = new MyLoader();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_feed);
        binding.setLoader(myLoader);


        Intent intent = getIntent();
        userId = intent.getStringExtra(Const.DATA);
        binding.rvFeed.setAdapter(feedGridAdapter);

        if (userId != null && !userId.isEmpty()) {
            getData(false);
        }


        binding.swipeRefresh.setOnRefreshListener((refreshLayout) -> getData(false));
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> getData(true));

        feedGridAdapter.setOnFeedGridAdapterClickLisnter(new FeedGridAdapter.OnFeedGridAdapterClickLisnter() {
            @Override
            public void onFeedClick(int position) {
                startActivity(new Intent(FeedGridActivity.this, FeedListActivity.class)
                        .putExtra(Const.USERID, sessionManager.getUser().getId())
                        .putExtra(Const.POSITION, position).putExtra(Const.DATA, new Gson().toJson(feedGridAdapter.getList())));
            }

            @Override
            public void onDeleteClick(PostRoot.PostItem postItem, int position) {
                Dialog dialog = new Dialog(FeedGridActivity.this);
                dialog.setContentView(R.layout.delete_popup);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


                Button yes = dialog.findViewById(R.id.yes);
                Button no = dialog.findViewById(R.id.no);

                yes.setOnClickListener(v -> {
                    DeletePost(postItem, position);
                    dialog.dismiss();
                });

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

    }

    public void DeletePost(PostRoot.PostItem postItem, int pos) {
        Call<RestResponse> call = RetrofitBuilder.create().deletePost(postItem.getId());

        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.body().isStatus() && response.isSuccessful()) {
                    Toast.makeText(FeedGridActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    feedGridAdapter.getList().remove(pos);
                    feedGridAdapter.notifyItemRemoved(pos);
                    //  feedGridAdapter.notifyDataSetChanged();
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
            feedGridAdapter.clear();
            start = 0;
        }

        myLoader.noData.set(false);
        Call<PostRoot> call = RetrofitBuilder.create().getUserPostList(SessionManager.getUserId(this), start, Const.LIMIT);
        call.enqueue(new Callback<PostRoot>() {
            @Override
            public void onResponse(Call<PostRoot> call, Response<PostRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getPost().isEmpty()) {
                        feedGridAdapter.addData(response.body().getPost());
                    } else if (start == 0) {
                        myLoader.noData.set(true);
                    }
                }

                myLoader.isFristTimeLoading.set(false);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
            }

            @Override
            public void onFailure(Call<PostRoot> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }


}