package com.zorona.liverooms.posts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zorona.liverooms.BuildConfig;
import com.zorona.liverooms.modelclass.PostRoot;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.user.guestUser.GuestActivity;
import com.zorona.liverooms.MainApplication;
import com.zorona.liverooms.R;
import com.zorona.liverooms.SessionManager;
import com.zorona.liverooms.databinding.ItemFeedBinding;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    OnPostClickListner onPostClickListner;
    SessionManager sessionManager;
    private Context context;
    private List<PostRoot.PostItem> postDummies = new ArrayList<>();

    public OnPostClickListner getOnPostClickListner() {
        return onPostClickListner;
    }

    public void setOnPostClickListner(OnPostClickListner onPostClickListner) {
        this.onPostClickListner = onPostClickListner;
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        sessionManager = new SessionManager(context);
        return new FeedViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false));
    }

    @Override
    public void onBindViewHolder(FeedAdapter.FeedViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return postDummies.size();
    }

    public void addData(List<PostRoot.PostItem> postDummies) {

        this.postDummies.addAll(postDummies);
        notifyItemRangeInserted(this.postDummies.size(), postDummies.size());
    }

    public void clear() {
        postDummies.clear();
        notifyDataSetChanged();
    }

    public interface OnPostClickListner {
        void onLikeClick(PostRoot.PostItem postDummy, int position, ItemFeedBinding binding);

        void onCommentListClick(PostRoot.PostItem postDummy);

        void onLikeListClick(PostRoot.PostItem postDummy);

        void onShareClick(PostRoot.PostItem postDummy);

        void onMentionClick(String userName);

    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        ItemFeedBinding binding;

        public FeedViewHolder(View itemView) {
            super(itemView);
            binding = ItemFeedBinding.bind(itemView);
        }

        public void setData(int position) {
            PostRoot.PostItem postDummy = postDummies.get(position);
            Glide.with(context).load(BuildConfig.BASE_URL + postDummy.getPost())
                    .apply(MainApplication.requestOptionsFeed)
                    .into(binding.imagepost);
            binding.imagepost.setAdjustViewBounds(true);
            binding.imgUser.setUserImage(postDummy.getUserImage(), postDummy.isIsVIP());

            binding.tvCaption.setText(postDummy.getCaption());
            binding.tvCaption.setHashtagEnabled(true);
            binding.tvCaption.setMentionEnabled(true);
            //   binding.tvCaption.setMentionColor(ContextCompat.getColor(context,R.color.pink));
            binding.tvCaption.setHashtagColor(ContextCompat.getColor(context, R.color.text_gray));
            binding.tvCaption.setMentionColors(ContextCompat.getColorStateList(context, R.color.pink));
            binding.tvComments.setText(String.valueOf(postDummy.getComment()) + " Comments");
            binding.tvLikes.setText(String.valueOf(postDummy.getLike()) + " Likes");
            binding.tvusername.setText(postDummy.getName());
            binding.tvtime.setText(postDummy.getTime());
            binding.rvLocation.setText(postDummy.getLocation());
            binding.imgUser.setOnClickListener(v -> context.startActivity(new Intent(context, GuestActivity.class).putExtra(Const.USERID, postDummy.getUserId())));


            binding.likeButton.setLiked(postDummy.isIsLike());
            if (postDummy.isAllowComment()) {
                binding.lytComments.setVisibility(View.VISIBLE);
            } else {
                binding.lytComments.setVisibility(View.GONE);
            }
            binding.lytComments.setOnClickListener(v -> onPostClickListner.onCommentListClick(postDummy));
            binding.tvLikes.setOnClickListener(v -> onPostClickListner.onLikeListClick(postDummy));
            binding.btnShare.setOnClickListener(v -> onPostClickListner.onShareClick(postDummy));

            binding.likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    onPostClickListner.onLikeClick(postDummy, position, binding);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    onPostClickListner.onLikeClick(postDummy, position, binding);
                }
            });

            binding.tvCaption.setOnMentionClickListener((view, text) -> onPostClickListner.onMentionClick(text.toString()));
/*
            binding.likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    int like = Integer.parseInt(binding.tvLikes.getText().toString()) + 1;
                    binding.tvLikes.setText(String.valueOf(like));
                    onPostClickListner.onLikeClick(postDummy,position, binding);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    int like = Integer.parseInt(binding.tvLikes.getText().toString()) - 1;
                    binding.tvLikes.setText(String.valueOf(like));
                    onPostClickListner.onLikeClick(postDummy, position, binding);
                }
            });
*/


        }
    }
}
