package com.zorona.liverooms.utils.autoComplete;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.zorona.liverooms.modelclass.HeshtagsRoot;
import com.zorona.liverooms.retrofit.RetrofitBuilder;
import com.otaliastudios.autocomplete.RecyclerViewPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HashtagPresenter extends RecyclerViewPresenter<HeshtagsRoot.HashtagItem> {

    private static final String TAG = "HashtagPresenter";
    private final Context mContext;
    private HashtagAdapter mAdapter;
    private Call<HeshtagsRoot> heshtagsRootCall;

    public HashtagPresenter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected HashtagAdapter instantiateAdapter() {
        return mAdapter = new HashtagAdapter(mContext, this::dispatchClick);
    }

    @Override
    protected void onQuery(@Nullable CharSequence q) {
        Log.d(TAG, "Querying '" + q + "' for hashtags autocomplete.");
        if (heshtagsRootCall != null) {
            heshtagsRootCall.cancel();
        }

        heshtagsRootCall = RetrofitBuilder.create().searchHashtag(q.toString());
        heshtagsRootCall.enqueue(new Callback<HeshtagsRoot>() {
            @Override
            public void onResponse(Call<HeshtagsRoot> call, Response<HeshtagsRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getHashtag().isEmpty()) {
                        mAdapter.submitData(response.body().getHashtag());
                    }
                }
            }

            @Override
            public void onFailure(Call<HeshtagsRoot> call, Throwable t) {

            }
        });

       /* REST rest = MainApplication.getContainer().get(REST.class);
        mCall = rest.hashtagsIndex(q != null ? q.toString() : null, 1);
        mCall.enqueue(new Callback<Wrappers.Paginated<Hashtag>>() {

            @Override
            public void onResponse(
                    @Nullable Call<Wrappers.Paginated<Hashtag>> call,
                    @Nullable Response<Wrappers.Paginated<Hashtag>> response
            ) {
                Log.v(TAG, "Server responded with " + response.code() + " status.");
                if (response.isSuccessful()) {
                    Wrappers.Paginated<Hashtag> hashtags = response.body();
                    mAdapter.submitData(hashtags.data);
                }
            }

            @Override
            public void onFailure(
                    @Nullable Call<Wrappers.Paginated<Hashtag>> call,
                    @Nullable Throwable t
            ) {
                Log.e(TAG, "Fetching hashtags has failed.", t);
            }
        });*/
    }
}
