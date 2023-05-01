package com.zorona.liverooms.user.guestUser;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.zorona.liverooms.MyLoader;
import com.zorona.liverooms.R;
import com.zorona.liverooms.activity.BaseFragment;
import com.zorona.liverooms.databinding.FragmentGuestUserPostsBinding;
import com.zorona.liverooms.modelclass.GuestProfileRoot;
import com.zorona.liverooms.modelclass.PostRoot;
import com.zorona.liverooms.modelclass.RestResponse;
import com.zorona.liverooms.posts.FeedGridAdapter;
import com.zorona.liverooms.posts.FeedListActivity;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.retrofit.RetrofitBuilder;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GuestUserPostsFragment extends BaseFragment {


    FragmentGuestUserPostsBinding binding;
    FeedGridAdapter feedGridAdapter = new FeedGridAdapter();
    private GuestProfileRoot.User user;
    private int start = 0;
    MyLoader myLoader = new MyLoader();
    public GuestUserPostsFragment(GuestProfileRoot.User user) {
        // Required empty public constructor
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_guest_user_posts, container, false);
        binding.setLoader(myLoader);
        initMain();
        getData(false);
        return binding.getRoot();
    }

    //todo pagiation
    private void getData(boolean isLoadMore) {
        if (isLoadMore) {
            start = start + Const.LIMIT;
        } else {
            myLoader.isFristTimeLoading.set(true);
            feedGridAdapter.clear();
            start = 0;
        }

        myLoader.noData.set(false);
        Call<PostRoot> call = RetrofitBuilder.create().getUserPostList(user.getUserId(), start, Const.LIMIT);
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

    private void initMain() {


        binding.rvFeed.setAdapter(feedGridAdapter);

        binding.swipeRefresh.setOnRefreshListener((refreshLayout) -> getData(false));
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> getData(true));


        feedGridAdapter.setOnFeedGridAdapterClickLisnter(new FeedGridAdapter.OnFeedGridAdapterClickLisnter() {
            @Override
            public void onFeedClick(int position) {
                startActivity(new Intent(getActivity(), FeedListActivity.class)
                        .putExtra(Const.USERID, user.getId())
                        .putExtra(Const.POSITION, position).putExtra(Const.DATA, new Gson().toJson(feedGridAdapter.getList())));
            }

            @Override
            public void onDeleteClick(PostRoot.PostItem postItem, int position) {
                if (postItem.getUserId().equalsIgnoreCase(sessionManager.getUser().getId())) {
                    Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.delete_popup);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


                    Button yes = dialog.findViewById(R.id.yes);
                    Button no = dialog.findViewById(R.id.no);

                    yes.setOnClickListener(v -> {
                        DeletePost(postItem, position);
                        dialog.dismiss();
                    });

                    no.setOnClickListener(v -> dialog.dismiss());

                    dialog.show();
                }
            }
        });

    }


    public void DeletePost(PostRoot.PostItem postItem, int pos) {
        Call<RestResponse> call = RetrofitBuilder.create().deletePost(postItem.getId());

        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.body().isStatus() && response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
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
}