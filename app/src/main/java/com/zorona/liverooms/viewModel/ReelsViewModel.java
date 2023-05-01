package com.zorona.liverooms.viewModel;

import android.content.Context;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zorona.liverooms.modelclass.ReliteRoot;
import com.zorona.liverooms.SessionManager;
import com.zorona.liverooms.reels.ReelsAdapter;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReelsViewModel extends ViewModel {

    public ReelsAdapter reelsAdapter = new ReelsAdapter();
    public int start = 0;
    public ObservableBoolean isFirstTimeLoading = new ObservableBoolean(true);
    public ObservableBoolean isLoadMoreLoading = new ObservableBoolean(true);
    public ObservableBoolean noData = new ObservableBoolean(false);
    public MutableLiveData<Boolean> isLoadCompleted = new MutableLiveData<>();
    SessionManager sessionManager;
    String type = "ALL";
    private Context context;

    public void init(Context context) {

        this.context = context;
        sessionManager = new SessionManager(context);
    }

    public void getReliteData(boolean isLoadMore, String userId, boolean isFromMainFrament, boolean isFromOtherProfile) {
        if (userId.equals(sessionManager.getUser().getId())) {
            type = "profile";    // if redirect via profile
        }

        if (isFromOtherProfile) {
            type = "profile";
        }

        if (isFromMainFrament) {
            type = "ALL";    // mainframgnet.userid==localid thus type=profile issue remove karva mate
        }
        if (isLoadMore) {
            start = start + Const.LIMIT;
            isLoadMoreLoading.set(true);
        } else {
            start = 0;
            reelsAdapter.clear();
            isFirstTimeLoading.set(true);
        }

        noData.set(false);
        Call<ReliteRoot> call = RetrofitBuilder.create().getRelites(userId, type, start, Const.LIMIT);
        call.enqueue(new Callback<ReliteRoot>() {
            @Override
            public void onResponse(Call<ReliteRoot> call, Response<ReliteRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getVideo().isEmpty()) {
                        reelsAdapter.addData(response.body().getVideo());
                        if (start == 0) {
                            reelsAdapter.playVideoAt(0);
                        }
                    } else if (start == 0) {
                        noData.set(true);
                    }
                }
                isLoadCompleted.postValue(true);
            }

            @Override
            public void onFailure(Call<ReliteRoot> call, Throwable t) {

            }
        });
    }
}
