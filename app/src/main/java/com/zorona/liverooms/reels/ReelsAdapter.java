package com.zorona.liverooms.reels;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zorona.liverooms.BuildConfig;
import com.zorona.liverooms.R;
import com.zorona.liverooms.databinding.ItemReelsBinding;
import com.zorona.liverooms.modelclass.ReliteRoot;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;

public class ReelsAdapter extends RecyclerView.Adapter<ReelsAdapter.ReelsViewHolder> {

    private List<ReliteRoot.VideoItem> reels = new ArrayList<>();
    private Context context;
    private int playAtPosition = 0;

    @Override
    public ReelsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ReelsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reels, parent, false));
    }

    OnReelsVideoAdapterListner onReelsVideoAdapterListner;

    public OnReelsVideoAdapterListner getOnReelsVideoAdapterListner() {
        return onReelsVideoAdapterListner;
    }

    public void setOnReelsVideoAdapterListner(OnReelsVideoAdapterListner onReelsVideoAdapterListner) {
        this.onReelsVideoAdapterListner = onReelsVideoAdapterListner;
    }

    @Override
    public void onBindViewHolder(ReelsAdapter.ReelsViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return reels.size();
    }

    public List<ReliteRoot.VideoItem> getList() {
        return reels;
    }

    public void addData(List<ReliteRoot.VideoItem> reels) {

        this.reels.addAll(reels);
        notifyItemRangeInserted(this.reels.size(), reels.size());
    }

    public void playVideoAt(int position) {
        playAtPosition = position;
        notifyDataSetChanged();
    }

    public void clear() {
        reels.clear();
        notifyDataSetChanged();
    }

    public interface OnReelsVideoAdapterListner {
        void onItemClick(ItemReelsBinding reelsBinding, int pos, int type);

        void onClickUser(ReliteRoot.VideoItem reel);

        void onClickShare(ReliteRoot.VideoItem reel);

        void onClickCommentList(ReliteRoot.VideoItem id);

        void onClickLikeList(ReliteRoot.VideoItem id);

        void onHashTagClick(String hashTag);

        void onMentionClick(String userName);

        void onDoubleClick(ReliteRoot.VideoItem model, MotionEvent event, ItemReelsBinding binding);

        void onClickLike(ItemReelsBinding reelsBinding, int pos);

        void onClickReport(ReliteRoot.VideoItem reel);
    }

    public class ReelsViewHolder extends RecyclerView.ViewHolder {
        ItemReelsBinding binding;

        public ReelsViewHolder(View itemView) {
            super(itemView);
            binding = ItemReelsBinding.bind(itemView);
        }

        public void setData(int position) {

            ReliteRoot.VideoItem reel = reels.get(position);
            binding.imgUser.setUserImage(reel.getUserImage(), reel.isVIP());


            //   Glide.with(binding.getRoot()).load(reel.getUser().getImage()).circleCrop().into(binding.imgUser);
            binding.tvUserName.setText(reel.getName());
            binding.tvDescreption.setText(reel.getCaption());

            binding.tvLikeCount.setText(String.valueOf(reel.getLikeCount()));
            binding.tvCommentCount.setText(String.valueOf(reel.getComment()));

            if (reel.isAllowComment()) {
                binding.lytComments.setVisibility(View.VISIBLE);
            } else {
                binding.lytComments.setVisibility(View.GONE);
            }
            binding.tvLikeCount.setOnClickListener(v -> onReelsVideoAdapterListner.onClickLikeList(reel));
            binding.tvCommentCount.setOnClickListener(v -> onReelsVideoAdapterListner.onClickCommentList(reel));
            binding.imgShare.setOnClickListener(v -> onReelsVideoAdapterListner.onClickShare(reel));
            binding.btnRepot.setOnClickListener(v -> onReelsVideoAdapterListner.onClickReport(reel));
            binding.likebtn.setLiked(reel.isLike());

            if (reel.isIsOriginalAudio()) {
                binding.tvSoundName.setText("Original Audio");
            } else if (reel.getSong() != null) {
                binding.tvSoundName.setText(reel.getSong().getTitle());
                Glide.with(binding.getRoot()).load(BuildConfig.BASE_URL + reel.getSong().getImage())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .circleCrop().into(binding.imgSong);
            }

            if (position == playAtPosition) {
                Animation animation = AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.slow_rotate);
                onReelsVideoAdapterListner.onItemClick(binding, playAtPosition, 1);
                binding.lytSound.startAnimation(animation);
            }

            binding.likebtn.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    onReelsVideoAdapterListner.onClickLike(binding, position);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    onReelsVideoAdapterListner.onClickLike(binding, position);
                }
            });

            binding.imgUser.setOnClickListener(v -> onReelsVideoAdapterListner.onClickUser(reel));
            binding.imgComment.setOnClickListener(v -> onReelsVideoAdapterListner.onClickCommentList(reel));
            binding.tvDescreption.setOnMentionClickListener((view, text) -> onReelsVideoAdapterListner.onMentionClick(text.toString()));
            binding.playerView.setOnTouchListener(new View.OnTouchListener() {
                GestureDetector gestureDetector = new GestureDetector(binding.getRoot().getContext(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        Log.d("TAGA", "onSingleTapUp: ");

                        return true;
                    }

                    @Override
                    public void onShowPress(MotionEvent e) {
                        Log.d("TAGA", "onShowPress: ");
                        super.onShowPress(e);
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        Log.d("TAGA", "onSingleTapConfirmed: ");
                        onReelsVideoAdapterListner.onItemClick(binding, position, 2);
                        return super.onSingleTapConfirmed(e);
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        // onReelsVideoAdapterListner.onItemClick(reel, position, 8, binding);
                        super.onLongPress(e);
                    }

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        Log.d("TAGA", "onDoubleTap: ");
                        onReelsVideoAdapterListner.onDoubleClick(reel, e, binding);
                        return true;
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return false;
                }
            });

        }
    }

}
