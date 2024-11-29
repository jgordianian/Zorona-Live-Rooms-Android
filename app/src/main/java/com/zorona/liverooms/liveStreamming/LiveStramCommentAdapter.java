package com.app.liverooms.liveStreamming;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.app.liverooms.BuildConfig;
import com.app.liverooms.MainApplication;
import com.app.liverooms.R;
import com.app.liverooms.databinding.ItemLivestramCommentBinding;
import com.app.liverooms.modelclass.LiveStramComment;
import com.app.liverooms.modelclass.UserRoot;

import java.util.ArrayList;
import java.util.List;

public class LiveStramCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW1 = 1;
    private static final int VIEW2 = 2;
    List<LiveStramComment> comments = new ArrayList<>();
    private Context context;

    @Override
    public int getItemViewType(int position) {
      //  if (position==0) return VIEW1;
        return VIEW2;
    }

    OnCommentClickListner onCommentClickListner;

    public OnCommentClickListner getOnCommentClickListner() {
        return onCommentClickListner;
    }

    public void setOnCommentClickListner(OnCommentClickListner onCommentClickListner) {
        this.onCommentClickListner = onCommentClickListner;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == VIEW1) {
            return new NoticeViewHOlder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_livestream_comment_1, parent, false));
        }
        return new CommentViewHOlder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_livestram_comment, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CommentViewHOlder) {
            ((CommentViewHOlder) holder).setCommentData(position);
        }
    }


    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void addSingleComment(LiveStramComment liveStramCommentDummy) {
        this.comments.add(liveStramCommentDummy);
        notifyItemInserted(this.comments.size());
    }

    public class NoticeViewHOlder extends RecyclerView.ViewHolder {

        public NoticeViewHOlder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface OnCommentClickListner {
        void onClickCommet(UserRoot.User userDummy);
    }

    public class CommentViewHOlder extends RecyclerView.ViewHolder {
        ItemLivestramCommentBinding binding;

        public CommentViewHOlder(@NonNull View itemView) {
            super(itemView);
            binding = ItemLivestramCommentBinding.bind(itemView);

        }

        public void setCommentData(int position) {
            LiveStramComment comment = comments.get(position);


            setUserLevel(comment.getUser().getLevel().getImage(), binding.buttomLevel);
            if (comment.isJoined()) {
                binding.tvComment.setText(comment.getUser().getName());
                binding.tvJoined.setVisibility(View.VISIBLE);
                binding.tvComment.setVisibility(View.GONE);
            } else {
                binding.tvJoined.setVisibility(View.GONE);
                binding.tvComment.setVisibility(View.VISIBLE);
                binding.tvComment.setText(comment.getComment());
            }
            binding.tvName.setText(comment.getUser().getName());
            binding.imgUser.setUserImage(comment.getUser().getImage(), comment.getUser().isIsVIP());
            binding.getRoot().setOnClickListener(v -> onCommentClickListner.onClickCommet(comment.getUser()));
        }

        private void setUserLevel(String image, ImageView buttomLevel) {
            Glide.with(context).load(BuildConfig.BASE_URL + image)
                    .apply(MainApplication.requestOptions)
                    .into(buttomLevel);
        }


    }
}
