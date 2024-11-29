package com.app.liverooms.liveStreamming;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.app.liverooms.MyLoader;
import com.app.liverooms.R;
import com.app.liverooms.activity.BaseFragment;
import com.app.liverooms.databinding.FragmentLiveListBinding;
import com.app.liverooms.modelclass.LiveUserRoot;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LiveListFragment extends BaseFragment {

    LiveListAdapter liveListAdapter = new LiveListAdapter();
    FragmentLiveListBinding binding;

    private int start = 0;
    MyLoader myLoader = new MyLoader();
    private String type;

    public LiveListFragment() {
    }

    public LiveListFragment(String type) {
        // Required empty public constructor
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_live_list, container, false);
        binding.setLoader(myLoader);
        initView();
        getData(false);
        initLister();

        return binding.getRoot();

    }

    private void initLister() {

        binding.swipeRefresh.setOnRefreshListener((refreshLayout) -> {
            getData(false);
        });
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
            getData(true);
        });
    }

    private void getData(boolean isLoadMore) {


        if (isLoadMore) {
            start = start + Const.LIMIT;

        } else {
            myLoader.isFristTimeLoading.set(true);
            start = 0;
            liveListAdapter.clear();

        }

        myLoader.noData.set(false);
        Call<LiveUserRoot> call = RetrofitBuilder.create().getLiveUsersList(sessionManager.getUser().getId(), type);
        call.enqueue(new Callback<LiveUserRoot>() {
            @Override
            public void onResponse(Call<LiveUserRoot> call, Response<LiveUserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getUsers().isEmpty()) {
                        liveListAdapter.addData(response.body().getUsers());
                    } else {
                        myLoader.noData.set(true);
                    }
                }
                myLoader.isFristTimeLoading.set(false);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
            }

            @Override
            public void onFailure(Call<LiveUserRoot> call, Throwable t) {

            }
        });
    }

    private void initView() {
      /*  ((GridLayoutManager)binding.rvVideos.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position%3==0){
                    return 2;
                }
                return 1;
            }
        });*/
        binding.rvVideos.setAdapter(liveListAdapter);



    }

}