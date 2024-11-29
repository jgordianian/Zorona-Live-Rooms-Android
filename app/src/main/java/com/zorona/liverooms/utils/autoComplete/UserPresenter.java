package com.app.liverooms.utils.autoComplete;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.app.liverooms.SessionManager;
import com.app.liverooms.modelclass.GuestProfileRoot;
import com.app.liverooms.modelclass.GuestUsersListRoot;
import com.app.liverooms.retrofit.RetrofitBuilder;
import com.google.gson.JsonObject;
import com.otaliastudios.autocomplete.RecyclerViewPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserPresenter extends RecyclerViewPresenter<GuestProfileRoot.User> {

    private static final String TAG = "UserPresenter";
    private final Context mContext;
    SessionManager sessionManager;
    private UserAdapter mAdapter;
    private Call<GuestUsersListRoot> call;

    public UserPresenter(Context context) {
        super(context);
        mContext = context;
        sessionManager = new SessionManager(context);
    }

    @Override
    protected UserAdapter instantiateAdapter() {
        return mAdapter = new UserAdapter(mContext, this::dispatchClick);
    }

    @Override
    protected void onQuery(@Nullable CharSequence q) {
        Log.v(TAG, "Querying '" + q + "' for users autocomplete.");
        if (call != null) {
            call.cancel();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sessionManager.getUser().getId());
        jsonObject.addProperty("value", q.toString());
        call = RetrofitBuilder.create().searchUser(jsonObject);
        call.enqueue(new Callback<GuestUsersListRoot>() {
            @Override
            public void onResponse(Call<GuestUsersListRoot> call, Response<GuestUsersListRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getUser().isEmpty()) {
                        //mAdapter.clear();
                        mAdapter.submitData(response.body().getUser());
                    }
                }
            }

            @Override
            public void onFailure(Call<GuestUsersListRoot> call, Throwable t) {

            }
        });


       /* REST rest = MainApplication.getContainer().get(REST.class);
        mCall = rest.usersIndex(q != null ? q.toString() : null, 1);
        mCall.enqueue(new Callback<Wrappers.Paginated<User>>() {

            @Override
            public void onResponse(
                    @Nullable Call<Wrappers.Paginated<User>> call,
                    @Nullable Response<Wrappers.Paginated<User>> response
            ) {
                Log.v(TAG, "Server responded with " + response.code() + " status.");
                if (response.isSuccessful()) {
                    Wrappers.Paginated<User> users = response.body();
                    mAdapter.submitData(users.data);
                }
            }

            @Override
            public void onFailure(
                    @Nullable Call<Wrappers.Paginated<User>> call,
                    @Nullable Throwable t
            ) {
                Log.e(TAG, "Fetching users has failed.", t);
            }
        });*/
    }
}
