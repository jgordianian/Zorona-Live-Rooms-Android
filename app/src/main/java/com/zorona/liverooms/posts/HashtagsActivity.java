package com.app.liverooms.posts;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.databinding.DataBindingUtil;

import com.app.liverooms.R;
import com.app.liverooms.activity.BaseActivity;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.z_demo.Demo_contents;
import com.app.liverooms.databinding.ActivityHashtagsBinding;

import java.util.ArrayList;
import java.util.List;

public class HashtagsActivity extends BaseActivity {
    ActivityHashtagsBinding binding;
    List<String> selectedHashtags = new ArrayList<>();
    HashtagsAdapter selectedHashtagsAdapter = new HashtagsAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hashtags);
        Intent intent = getIntent();
        String hashtag = intent.getStringExtra(Const.DATA);
        if (hashtag!=null && !hashtag.isEmpty()) {
            binding.etHashtags.setText(hashtag);
        }
        initView();
        initLister();
    }

    private void initLister() {
        binding.btnDone.setOnClickListener(v -> {
            Intent intent = getIntent();
            intent.putExtra(Const.DATA, binding.etHashtags.getText().toString().trim());
            setResult(RESULT_OK, intent);
            finish();
        });
        selectedHashtagsAdapter.setOnHashtagsClickLisnter(hashtag -> {

            addHashtags();
        });
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                selectedHashtagsAdapter.clear();
                if (s.toString().isEmpty()) {

                }
                List<String> searchedHashtag = new ArrayList<>();
                for (String h : Demo_contents.getHashtags()) {
                    if (h.toLowerCase().contains(s.toString().toLowerCase())) {
                        searchedHashtag.add(h);
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void initView() {


        binding.rvSelectedHashtags.setAdapter(selectedHashtagsAdapter);


    }

    private void addHashtags() {
if (binding.etHashtags.getText().toString().contains("Add Hashtags")) {

    binding.etHashtags.setText("");
}
        for (int i = 0; i < selectedHashtags.size(); i++) {
            binding.etHashtags.setText(binding.etHashtags.getText().toString() + " " + selectedHashtags.get(i));
        }
    }
}