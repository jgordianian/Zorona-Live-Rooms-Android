package com.app.liverooms.user.guestUser;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.app.liverooms.MyLoader;
import com.app.liverooms.R;
import com.app.liverooms.activity.BaseFragment;
import com.app.liverooms.databinding.FragmentGuestUserReelsBinding;
import com.app.liverooms.modelclass.GuestProfileRoot;
import com.app.liverooms.modelclass.ReliteRoot;
import com.app.liverooms.modelclass.RestResponse;
import com.app.liverooms.reels.ProfileVideoGridAdapter;
import com.app.liverooms.reels.ReelsActivity;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.retrofit.RetrofitBuilder;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GuestUserReelsFragment extends BaseFragment {

    FragmentGuestUserReelsBinding binding;
    ProfileVideoGridAdapter profileVideoGridAdapter = new ProfileVideoGridAdapter();
    MyLoader myLoader = new MyLoader();
    private GuestProfileRoot.User userDummy;
    private int start = 0;
    List<ReliteRoot.VideoItem> videoList = new ArrayList<>();

    public GuestUserReelsFragment(GuestProfileRoot.User userDummy) {
        // Required empty public constructor
        this.userDummy = userDummy;
    }

    public GuestUserReelsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_guest_user_reels, container, false);
        binding.setLoader(myLoader);
        initMain();
        return binding.getRoot();
    }

    private void initMain() {


        binding.rvFeed.setAdapter(profileVideoGridAdapter);
        binding.swipeRefresh.setOnRefreshListener((refreshLayout) -> getData(false));
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> getData(true));

        profileVideoGridAdapter.setOnVideoGridClickListner(new ProfileVideoGridAdapter.OnVideoGridClickListner() {
            @Override
            public void onVideoClick(int position) {
                startActivity(new Intent(getActivity(),
                        ReelsActivity.class).putExtra(Const.POSITION, position).putExtra(Const.DATA, new Gson().toJson(profileVideoGridAdapter.getList())));
            }

            @Override
            public void onDeleteClick(ReliteRoot.VideoItem postItem, int position) {
                if (postItem.getUserId().equalsIgnoreCase(sessionManager.getUser().getId())) {
                    Dialog dialog = new Dialog(getActivity());
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
            }
        });


        getData(false);
    }

    public void DeleteVideo(ReliteRoot.VideoItem postItem, int pos) {
        Call<RestResponse> call = RetrofitBuilder.create().deleteRelite(postItem.getId());

        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.body().isStatus() && response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
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
        Call<ReliteRoot> call = RetrofitBuilder.create().getRelites(userDummy.getUserId(), "User", start, Const.LIMIT);

        call.enqueue(new Callback<ReliteRoot>() {
            @Override
            public void onResponse(@NonNull Call<ReliteRoot> call, @NonNull Response<ReliteRoot> response) {
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