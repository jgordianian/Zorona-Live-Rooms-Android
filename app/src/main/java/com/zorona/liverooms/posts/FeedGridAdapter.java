package com.app.liverooms.posts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.app.liverooms.BuildConfig;
import com.app.liverooms.modelclass.PostRoot;
import com.app.liverooms.MainApplication;
import com.app.liverooms.R;
import com.app.liverooms.databinding.ItemFeedGridListBinding;

import java.util.ArrayList;
import java.util.List;


public class FeedGridAdapter extends RecyclerView.Adapter<FeedGridAdapter.FeedViewHolder> {

    OnFeedGridAdapterClickLisnter onFeedGridAdapterClickLisnter;
    private Context context;
    private List<PostRoot.PostItem> postDummies = new ArrayList<>();

    public OnFeedGridAdapterClickLisnter getOnFeedGridAdapterClickLisnter() {
        return onFeedGridAdapterClickLisnter;
    }

    public void setOnFeedGridAdapterClickLisnter(OnFeedGridAdapterClickLisnter onFeedGridAdapterClickLisnter) {
        this.onFeedGridAdapterClickLisnter = onFeedGridAdapterClickLisnter;
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new FeedViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_grid_list, parent, false));

    }

    @Override
    public void onBindViewHolder(FeedGridAdapter.FeedViewHolder holder, int position) {


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

    public List<PostRoot.PostItem> getList() {
        return postDummies;
    }

    public void clear() {
        postDummies.clear();
        notifyDataSetChanged();
    }

    public interface OnFeedGridAdapterClickLisnter {
        void onFeedClick(int position);

        void onDeleteClick(PostRoot.PostItem postItem, int position);
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        ItemFeedGridListBinding binding;

        public FeedViewHolder(View itemView) {
            super(itemView);
            binding = ItemFeedGridListBinding.bind(itemView);
        }

        public void setData(int position) {
            PostRoot.PostItem postDummy = postDummies.get(position);

            Glide.with(context).load(BuildConfig.BASE_URL + postDummy.getPost())
                    .apply(MainApplication.requestOptionsFeed)
                    .centerCrop().into(binding.imagepost);
            binding.imagepost.setAdjustViewBounds(true);
            binding.tvLikes.setText(String.valueOf(postDummy.getLike()));
            binding.imagepost.setOnClickListener(v -> onFeedGridAdapterClickLisnter.onFeedClick(position));

            binding.imagepost.setOnLongClickListener(v -> {
                onFeedGridAdapterClickLisnter.onDeleteClick(postDummy, position);
                return true;
            });
        }
    }
}
