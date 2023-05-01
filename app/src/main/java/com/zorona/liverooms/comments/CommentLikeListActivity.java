package com.zorona.liverooms.comments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.zorona.liverooms.R;
import com.zorona.liverooms.bottomsheets.BottomSheetCommentDetails;
import com.zorona.liverooms.modelclass.PostCommentRoot;
import com.zorona.liverooms.modelclass.PostRoot;
import com.zorona.liverooms.modelclass.ReliteRoot;
import com.zorona.liverooms.modelclass.RestResponse;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.viewModel.CommentLikeListViewModel;
import com.zorona.liverooms.viewModel.ViewModelFactory;
import com.zorona.liverooms.SessionManager;
import com.zorona.liverooms.activity.BaseActivity;
import com.zorona.liverooms.databinding.FragmentCommentBinding;
import com.zorona.liverooms.retrofit.RetrofitBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentLikeListActivity extends BaseActivity {


    public static final int COMMENTS = 1;
    public static final int LIKES = 2;
    FragmentCommentBinding binding;

    SessionManager sessionManager;
    private PostRoot.PostItem postDummy;
    private CommentLikeListViewModel viewModel;
    private ReliteRoot.VideoItem relite;
    private int type;  // reels or post
    private int viewType; // like or comment


    public void onBackPressed() {
        super.onBackPressed();
        doTransition(Const.UP_TO_BOTTOM);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_comment);
        sessionManager = new SessionManager(this);

        Intent intent = getIntent();
        viewType = intent.getIntExtra(Const.TYPE, 0);
        String dataStr = intent.getStringExtra(Const.POSTDATA);
        if (dataStr != null && !dataStr.isEmpty()) {
            postDummy = new Gson().fromJson(dataStr, PostRoot.PostItem.class);
            type = CommentLikeListViewModel.POST;
        }

        String dataStr2 = intent.getStringExtra(Const.RELITEDATA);
        if (dataStr2 != null && !dataStr2.isEmpty()) {
            relite = new Gson().fromJson(dataStr2, ReliteRoot.VideoItem.class);
            type = CommentLikeListViewModel.RELITE;
        }


        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new CommentLikeListViewModel()).createFor()).get(CommentLikeListViewModel.class);
        viewModel.init(this);
        binding.setViewModel(viewModel);
        viewModel.listCountFinel.observe(this, integer -> {
            binding.tvCommentCount.setText(String.valueOf(integer));
        });

        initMain();
        if (viewType == COMMENTS) {
            binding.lytBottom.setVisibility(View.VISIBLE);
            binding.tvViewType.setText("Comments");
        } else {
            binding.lytBottom.setVisibility(View.GONE);
            binding.tvViewType.setText("Likes");
        }
    }


    private void initMain() {
        if (postDummy != null) {
            if (viewType == COMMENTS) {
                viewModel.getCommentList(postDummy.getId(), type, false);
            } else {
                viewModel.getLikeList(postDummy.getId(), type, false);
            }
        }
        if (relite != null) {
            if (viewType == COMMENTS) {
                viewModel.getCommentList(relite.getId(), type, false);
            } else {
                viewModel.getLikeList(relite.getId(), type, false);
            }
        }


        binding.etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null && s.equals("")) {
                    binding.noData.setVisibility(View.VISIBLE);
                } else {
                    binding.noData.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.btnsend.setOnClickListener(v -> {
            String comment = binding.etComment.getText().toString();
            if (!comment.isEmpty()) {
                binding.etComment.setText("");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("userId", sessionManager.getUser().getId());
                if (type == CommentLikeListViewModel.POST) {
                    jsonObject.addProperty("postId", postDummy.getId());
                } else {
                    jsonObject.addProperty("videoId", relite.getId());
                }
                jsonObject.addProperty("comment", comment);

                viewModel.isLoading.set(true);
                Call<RestResponse> call = RetrofitBuilder.create().addComment(jsonObject);
                call.enqueue(new Callback<RestResponse>() {
                    @Override
                    public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                        if (response.code() == 200) {
                            if (response.body() != null && response.body().isStatus()) {
                                PostCommentRoot.CommentsItem commentsItem = new PostCommentRoot.CommentsItem();
                                commentsItem.setUserId(sessionManager.getUser().getId());
                                commentsItem.setComment(comment);
                                commentsItem.setImage(sessionManager.getUser().getImage());
                                commentsItem.setTime("Just Now");
                                commentsItem.setName(sessionManager.getUser().getName());
                                commentsItem.setVIP(sessionManager.getUser().isIsVIP());
                                commentsItem.setUsername(sessionManager.getUser().getUsername());
                                viewModel.commentAdapter.addSingleComment(commentsItem);
                                viewModel.commentCount++;
                                viewModel.listCountFinel.postValue(viewModel.commentCount);
                                binding.rvComments.scrollToPosition(0);
                                viewModel.noData.set(false);
                            }
                        }
                        viewModel.isLoading.set(false);
                    }

                    @Override
                    public void onFailure(Call<RestResponse> call, Throwable t) {

                    }
                });

            }
        });


        viewModel.commentAdapter.setOnCommentClickLister((commentDummy, position) -> {
            new BottomSheetCommentDetails(this, commentDummy, () -> {
                viewModel.deleteComment(commentDummy, position);
            });
        });
    }
}