package com.app.liverooms.retrofit;

import android.content.Context;

import com.app.liverooms.modelclass.RestResponse;
import com.app.liverooms.SessionManager;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommenApiCalling {
    SessionManager sessionManager;
    private Context context;

    public CommenApiCalling(Context context) {

        this.context = context;
        sessionManager = new SessionManager(context);
    }

    public void toggleLikePost(String postId, OnToggleLikeListner onToggleLikeListner) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("userId", sessionManager.getUser().getId());
        jsonObject.addProperty("postId", postId);
        Call<RestResponse> call = RetrofitBuilder.create().toggleLikePost(jsonObject);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code()==200) {

                    if (response.body() != null) {
                        onToggleLikeListner.onToggleLiked(response.body().isLiked());
                    }

                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {

            }
        });
    }

    public void toggleLikeRelite(String reliteId, OnToggleLikeListner onToggleLikeListner) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("userId", sessionManager.getUser().getId());
        jsonObject.addProperty("videoId", reliteId);
        Call<RestResponse> call = RetrofitBuilder.create().toggleLikePost(jsonObject);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {

                    if (response.body() != null) {
                        onToggleLikeListner.onToggleLiked(response.body().isLiked());
                    }

                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {

            }
        });
    }

    public interface OnToggleLikeListner {
        void onToggleLiked(boolean isLiked);
    }
}
