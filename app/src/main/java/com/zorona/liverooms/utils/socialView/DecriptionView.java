package com.app.liverooms.utils.socialView;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.liverooms.R;
import com.app.liverooms.SessionManager;
import com.app.liverooms.databinding.ItemHashtagMentionViewBinding;
import com.app.liverooms.modelclass.GuestUsersListRoot;
import com.app.liverooms.modelclass.HeshtagsRoot;
import com.app.liverooms.posts.HashtagsAdapter;
import com.app.liverooms.posts.MentionsAdapter;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.retrofit.RetrofitBuilder;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DecriptionView extends LinearLayout {
    private static final String TAG = "HashtagMantionView";
    SessionManager sessionManager;
    MentionsAdapter mentionsAdapter = new MentionsAdapter();
    HashtagsAdapter selectedHashtagsAdapter = new HashtagsAdapter();
    private ItemHashtagMentionViewBinding binding;
    private int hashTagIsComing = 0;
    private Call<GuestUsersListRoot> call;
    private Call<HeshtagsRoot> heshtagsRootCall;

    public DecriptionView(Context context) {
        super(context);
        initView();
    }

    public DecriptionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DecriptionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        sessionManager = new SessionManager(getContext());
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.item_hashtag_mention_view, null, false);
        addView(binding.getRoot());


        binding.tvHashtag.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String startChar = null;
                try {
                    startChar = Character.toString(s.charAt(start));
                    Log.i(getClass().getSimpleName(), "CHARACTER OF NEW WORD: " + startChar);
                } catch (Exception ex) {
                    startChar = "";
                }

                if (startChar.equals("#") || startChar.equals("@")) {
                    // changeTheColor(s.toString().substring(start), start, start + count);
                    hashTagIsComing++;
                }

                if (startChar.equals(" ")) {
                    hashTagIsComing = 0;
                    binding.rvHashtags.setVisibility(View.GONE);
                }

                if (hashTagIsComing!=0) {
                    // changeTheColor(s.toString().substring(start), start, start + count);
                    hashTagIsComing++;
                }
                if (s.toString().isEmpty()) {
                    binding.rvHashtags.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.tvHashtag.setHashtagEnabled(true);
        binding.tvHashtag.setMentionEnabled(true);
        binding.tvHashtag.setHashtagColor(Color.WHITE);
        binding.tvHashtag.setMentionColor(ContextCompat.getColor(getContext(), R.color.pink));

        binding.tvHashtag.setHashtagTextChangedListener((view, text) -> {
            Log.d(TAG, "onChanged: hashtag  " + text);
            if (text.length() > Const.MAX_CHAR_HSHTAG) {
                Toast.makeText(getContext(), "Hashtag is too long", Toast.LENGTH_SHORT).show();

                binding.tvHashtag.setText(text.toString().substring(0, Const.MAX_CHAR_HSHTAG));
                return;
            }
            searchHashtag(text);
        });

        binding.tvHashtag.setMentionTextChangedListener((view, text) -> {
            Log.d(TAG, "onChanged: mantion  " + text);
            searchUser(text);
        });


    }

    private void searchUser(CharSequence text) {
        binding.rvHashtags.setVisibility(View.VISIBLE);
        binding.rvHashtags.setAdapter(mentionsAdapter);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sessionManager.getUser().getId());
        jsonObject.addProperty("value", text.toString());
        if (call!=null) {
            call.cancel();
        }
        call = RetrofitBuilder.create().searchUser(jsonObject);
        call.enqueue(new Callback<GuestUsersListRoot>() {
            @Override
            public void onResponse(Call<GuestUsersListRoot> call, Response<GuestUsersListRoot> response) {
                if (response.code()==200) {
                    if (response.body().isStatus() && !response.body().getUser().isEmpty()) {
                        mentionsAdapter.clear();
                        mentionsAdapter.addData(response.body().getUser());
                    }
                }
            }

            @Override
            public void onFailure(Call<GuestUsersListRoot> call, Throwable t) {

            }
        });

        mentionsAdapter.setOnHashtagsClickLisnter(user -> {
            String t = binding.tvHashtag.getText().toString();
            String finel = t.replace("@" + text, " ");
            binding.tvHashtag.setText(finel + "@" + user.getUsername());
            binding.rvHashtags.setVisibility(View.GONE);
            binding.tvHashtag.setSelection(binding.tvHashtag.length());
        });
    }


    private void searchHashtag(CharSequence text) {
        binding.rvHashtags.setVisibility(View.VISIBLE);
        binding.rvHashtags.setAdapter(selectedHashtagsAdapter);
      //  binding.rvHashtags.requestFocus();
        selectedHashtagsAdapter.setOnHashtagsClickLisnter(hashtag -> {
            String t = binding.tvHashtag.getText().toString();
            String finel = t.replace("#" + text, " ");
            binding.tvHashtag.setText(finel + "#" + hashtag.getHashtag());
            binding.rvHashtags.setVisibility(View.GONE);
            binding.tvHashtag.setSelection(binding.tvHashtag.length());
        });
        if (heshtagsRootCall!=null) {
            heshtagsRootCall.cancel();
        }
        heshtagsRootCall = RetrofitBuilder.create().searchHashtag(text.toString());
        heshtagsRootCall.enqueue(new Callback<HeshtagsRoot>() {
            @Override
            public void onResponse(Call<HeshtagsRoot> call, Response<HeshtagsRoot> response) {
                if (response.code()==200) {
                    if (response.body().isStatus() && !response.body().getHashtag().isEmpty()) {
                        selectedHashtagsAdapter.addData(response.body().getHashtag());
                    }
                }
            }

            @Override
            public void onFailure(Call<HeshtagsRoot> call, Throwable t) {

            }
        });
    }

    public String getDecription() {
        return binding.tvHashtag.getText().toString();
    }

    public List<String> getMentions() {

        return binding.tvHashtag.getMentions();
    }

    public List<String> getHashtags() {
        return binding.tvHashtag.getHashtags();
    }

    public EditText getDecriptionView() {
        return binding.tvHashtag;
    }

    public void setSpan(int i) {
        ((GridLayoutManager) binding.rvHashtags.getLayoutManager()).setSpanCount(i);
    }

    public void setText(String description) {
        binding.tvHashtag.setText(description);
    }
}
