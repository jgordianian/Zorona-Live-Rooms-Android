package com.app.liverooms.viewModel;

import android.content.Context;
import android.util.Log;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.liverooms.modelclass.PostRoot;
import com.app.liverooms.posts.FeedAdapter;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedListViewModel extends ViewModel {
    public FeedAdapter feedAdapter = new FeedAdapter();
    public int start = 0;
    private Context context;
    private String type;
    public ObservableBoolean isFirstTimeLoading = new ObservableBoolean(true);
    public ObservableBoolean isLoadMoreLoading = new ObservableBoolean(true);
    public ObservableBoolean noData = new ObservableBoolean(false);
    public MutableLiveData<Boolean> isLoadCompleted = new MutableLiveData<>();

    public void getPostData(boolean isLoadMore, String userId) {
        Call<PostRoot> call;

        if (isLoadMore) {
            start = start + Const.LIMIT;
            isLoadMoreLoading.set(true);
        } else {
            start = 0;
            feedAdapter.clear();
            isFirstTimeLoading.set(true);
        }

        if (type.equals(Const.TYPE_FOLLOWING)) {
            call = RetrofitBuilder.create().getFollowingPost(userId, type, start, Const.LIMIT);

        } else if (type.equals(Const.USER)) {
            call = RetrofitBuilder.create().getUserPostList(userId, start, Const.LIMIT);

        } else {
            Log.d("<<<<<TAG>>>>>", "getPostData: " + type);
            call = RetrofitBuilder.create().getPostList(userId, type, start, Const.LIMIT);


        }

        noData.set(false);
        call.enqueue(new Callback<PostRoot>() {
            @Override
            public void onResponse(Call<PostRoot> call, Response<PostRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getPost().isEmpty()) {
                        feedAdapter.addData(response.body().getPost());

                    } else if (start == 0) {
                        noData.set(true);
                    }
                }


                isLoadCompleted.postValue(true);

            }

            @Override
            public void onFailure(Call<PostRoot> call, Throwable t) {

            }
        });

    }


    public void init(Context context, String type) {

        this.context = context;
        this.type = type;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d("TAG", "onCleared: ");
        feedAdapter.clear();
    }
}
