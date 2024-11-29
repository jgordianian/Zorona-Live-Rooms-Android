package com.app.liverooms.retrofit;

import android.content.Context;
import android.util.Log;

import com.app.liverooms.modelclass.ChatTopicRoot;
import com.app.liverooms.modelclass.FollowUnfollowResponse;
import com.app.liverooms.modelclass.GuestProfileRoot;
import com.app.liverooms.modelclass.UserRoot;
import com.app.liverooms.SessionManager;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserApiCall {


    SessionManager sessionManager;
    private Context context;

    public UserApiCall(Context context) {
        this.context = context;
        sessionManager = new SessionManager(context);
    }

    public void getUser(OnUserApiListner onUserApiListner) {
        Call<UserRoot> call = RetrofitBuilder.create().getUser(sessionManager.getUser().getId());
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code()==200) {
                    if (response.body().isStatus() && response.body().getUser()!=null) {
                        onUserApiListner.onUserGetted(response.body().getUser());

                    }
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }

    public void getGuestProfile(String guestId, OnGuestUserApiListner onGuestUserApiListner) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sessionManager.getUser().getId());
        jsonObject.addProperty("profileUserId", guestId);
        Call<GuestProfileRoot> call = RetrofitBuilder.create().getGuestUser(jsonObject);
        call.enqueue(new Callback<GuestProfileRoot>() {
            @Override
            public void onResponse(Call<GuestProfileRoot> call, Response<GuestProfileRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getUser() != null) {
                        onGuestUserApiListner.onUserGetted(response.body().getUser());
                    } else {
                        onGuestUserApiListner.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(Call<GuestProfileRoot> call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.getMessage());
            }
        });
    }

    public void getGuestProfileByUserName(String userName, OnGuestUserApiListner onGuestUserApiListner) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sessionManager.getUser().getId());
        jsonObject.addProperty("username", userName);
        Call<GuestProfileRoot> call = RetrofitBuilder.create().getGuestUser(jsonObject);
        call.enqueue(new Callback<GuestProfileRoot>() {
            @Override
            public void onResponse(Call<GuestProfileRoot> call, Response<GuestProfileRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getUser() != null) {
                        onGuestUserApiListner.onUserGetted(response.body().getUser());
                    } else {
                        onGuestUserApiListner.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(Call<GuestProfileRoot> call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.getMessage());
            }
        });
    }

    public void followUnfollowUser(boolean followNow, String guestId, String liveStreamingId, OnFollowUnfollowListner onFollowUnfollowListner) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fromUserId", sessionManager.getUser().getId());
        jsonObject.addProperty("toUserId", guestId);
        if (!liveStreamingId.isEmpty()) {
            jsonObject.addProperty("liveStreamingId", liveStreamingId);
        }
        Call<FollowUnfollowResponse> call = RetrofitBuilder.create().toggleFollowUnfollow(jsonObject);
        call.enqueue(new Callback<FollowUnfollowResponse>() {
            @Override
            public void onResponse(Call<FollowUnfollowResponse> call, Response<FollowUnfollowResponse> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().isStatus()) {
                            if (response.body().isFollow()) {
                                onFollowUnfollowListner.onFollowSuccess();
                            } else {
                                onFollowUnfollowListner.onUnfollowSuccess();
                            }
                        } else {
                            onFollowUnfollowListner.onFail();
                        }
                    }
                } else {
                    onFollowUnfollowListner.onFail();
                }
            }

            @Override
            public void onFailure(Call<FollowUnfollowResponse> call, Throwable t) {
                onFollowUnfollowListner.onFail();
            }
        });
    }

    public void createChatTopic(String localId, String guestId, OnChatTopicCreateLister onChatTopicCreateLister) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("senderUserId", localId);
        jsonObject.addProperty("receiverUserId", guestId);
        Call<ChatTopicRoot> call = RetrofitBuilder.create().createChatRoom(jsonObject);
        call.enqueue(new Callback<ChatTopicRoot>() {
            @Override
            public void onResponse(Call<ChatTopicRoot> call, Response<ChatTopicRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        if (response.body().getChatTopic().getId() != null && !response.body().getChatTopic().getId().isEmpty()) {
                            onChatTopicCreateLister.onTopicCreatd(response.body().getChatTopic().getId());
                        } else {
                            onChatTopicCreateLister.onTopicCreatd("");
                        }
                    } else {
                        onChatTopicCreateLister.onTopicCreatd("");
                    }
                } else {
                    onChatTopicCreateLister.onTopicCreatd("");
                }
            }

            @Override
            public void onFailure(Call<ChatTopicRoot> call, Throwable t) {
                onChatTopicCreateLister.onTopicCreatd("");
            }
        });

    }

    public interface OnChatTopicCreateLister {
        void onTopicCreatd(String s);
    }

    public interface OnFollowUnfollowListner {
        void onFollowSuccess();

        void onUnfollowSuccess();

        void onFail();
    }

    public interface OnUserApiListner {
        void onUserGetted(UserRoot.User user);
    }

    public interface OnGuestUserApiListner {
        void onUserGetted(GuestProfileRoot.User user);

        void onFailure();
    }
}
