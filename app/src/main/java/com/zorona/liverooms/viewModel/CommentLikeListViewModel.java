package com.zorona.liverooms.viewModel;

import android.content.Context;
import android.util.Log;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zorona.liverooms.modelclass.PostCommentRoot;
import com.zorona.liverooms.modelclass.RestResponse;
import com.zorona.liverooms.SessionManager;
import com.zorona.liverooms.comments.CommentAdapter;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.retrofit.RetrofitBuilder;
import com.zorona.liverooms.user.SearchUserAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentLikeListViewModel extends ViewModel {
    public static final int POST = 0;
    public static final int RELITE = 1;
    public CommentAdapter commentAdapter = new CommentAdapter();
    public SearchUserAdapter userAdapter = new SearchUserAdapter();
    public int start = 0;
    public int commentCount = 0;
    public MutableLiveData<Integer> listCountFinel = new MutableLiveData<>();
    SessionManager sessionManager;
    private Context context;
    public ObservableBoolean noData = new ObservableBoolean(false);
    public ObservableBoolean isLoading = new ObservableBoolean(false);

    public void getCommentList(String postId, int type, boolean b) {
        Log.d("TAG", "getCommentList: type " + type);
        Call<PostCommentRoot> call;
        if (type == POST) {
            call = RetrofitBuilder.create().getPostCommentList(SessionManager.getUserId(context), postId, start, Const.LIMIT);
        } else {
            call = RetrofitBuilder.create().getReliteCommentList(SessionManager.getUserId(context), postId, start, Const.LIMIT);
        }

        noData.set(false);
        isLoading.set(true);
        call.enqueue(new Callback<PostCommentRoot>() {
            @Override
            public void onResponse(Call<PostCommentRoot> call, Response<PostCommentRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getData().isEmpty()) {
                        commentAdapter.addData(response.body().getData());
                        commentCount = commentCount + response.body().getData().size();
                        listCountFinel.postValue(commentCount);
                    } else if (start == 0) {
                        noData.set(true);
                    }
                }
                isLoading.set(false);
            }

            @Override
            public void onFailure(Call<PostCommentRoot> call, Throwable t) {

            }
        });
    }

    public void init(Context context) {
        this.context = context;
        sessionManager = new SessionManager(context);
    }


    public void getLikeList(String id, int type, boolean isLoadMore) {
        Call<PostCommentRoot> call;
        if (type == POST) {
            call = RetrofitBuilder.create().getPostLikeList(SessionManager.getUserId(context), id, start, Const.LIMIT);
        } else {
            call = RetrofitBuilder.create().getReliteLikeList(SessionManager.getUserId(context), id, start, Const.LIMIT);
        }

        noData.set(false);
        isLoading.set(true);
        call.enqueue(new Callback<PostCommentRoot>() {
            @Override
            public void onResponse(Call<PostCommentRoot> call, Response<PostCommentRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getData().isEmpty()) {
                        commentAdapter.addData(response.body().getData());
                        commentCount = commentCount + response.body().getData().size();
                        listCountFinel.postValue(commentCount);
                    } else if (start == 0) {
                        noData.set(true);
                    }
                }
                isLoading.set(false);
            }

            @Override
            public void onFailure(Call<PostCommentRoot> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }

    public void deleteComment(PostCommentRoot.CommentsItem commentDummy, int position) {
        isLoading.set(true);
        Call<RestResponse> call = RetrofitBuilder.create().deleteComment(commentDummy.getId());
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        commentAdapter.removeSingleItem(position);
                    }
                }
                isLoading.set(false);
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {

            }
        });
    }
}
