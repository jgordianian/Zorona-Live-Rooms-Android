package com.app.liverooms.comments;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.liverooms.R;
import com.app.liverooms.databinding.ItemCommentBinding;
import com.app.liverooms.modelclass.PostCommentRoot;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<PostCommentRoot.CommentsItem> commentDummies = new ArrayList<>();

    OnCommentClickLister onCommentClickLister;

    public OnCommentClickLister getOnCommentClickLister() {
        return onCommentClickLister;
    }

    public void setOnCommentClickLister(OnCommentClickLister onCommentClickLister) {
        this.onCommentClickLister = onCommentClickLister;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentViewHOlder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false));


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((CommentViewHOlder) holder).setData(position);

    }

    @Override
    public int getItemCount() {
        return commentDummies.size();
    }

    public void addData(List<PostCommentRoot.CommentsItem> commentDummies) {

        this.commentDummies.addAll(commentDummies);
        notifyItemRangeInserted(this.commentDummies.size(), commentDummies.size());
    }

    public void addSingleComment(PostCommentRoot.CommentsItem commentDummy) {
        this.commentDummies.add(0,commentDummy);
        notifyItemInserted(0);
    }

    public void removeSingleItem(int position) {
        commentDummies.remove(commentDummies.get(position));
        notifyDataSetChanged();
    }

    public interface OnCommentClickLister {
        void onLongPressComment(PostCommentRoot.CommentsItem commentDummy, int position);
    }

    public class CommentViewHOlder extends RecyclerView.ViewHolder {
        ItemCommentBinding binding;

        public CommentViewHOlder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCommentBinding.bind(itemView);

        }

        public void setData(int position) {
            PostCommentRoot.CommentsItem commentDummy = commentDummies.get(position);

            binding.imgUser.setUserImage(commentDummy.getImage(), commentDummy.isVIP());

            if (commentDummy.getComment().isEmpty()) {
                binding.tvComment.setText(commentDummy.getName());
            } else {
                binding.tvComment.setText(commentDummy.getComment());
            }
            binding.tvDate.setText(commentDummy.getTime());
            Log.d("TAG", "setData: " + commentDummy.getName());
            binding.tvUserName.setText(commentDummy.getName());
            binding.getRoot().setOnLongClickListener(v -> {
                onCommentClickLister.onLongPressComment(commentDummy, position);
                return true;
            });
        }
    }
}
