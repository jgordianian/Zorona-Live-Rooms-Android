package com.zorona.liverooms.reels;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zorona.liverooms.modelclass.ReliteRoot;
import com.zorona.liverooms.MainApplication;
import com.zorona.liverooms.R;
import com.zorona.liverooms.databinding.ItemVidProfileListBinding;

import java.util.ArrayList;
import java.util.List;

public class ProfileVideoGridAdapter extends RecyclerView.Adapter<ProfileVideoGridAdapter.FeedViewHolder> {

    OnVideoGridClickListner onVideoGridClickListner;
    private List<ReliteRoot.VideoItem> reels = new ArrayList<>();

    public OnVideoGridClickListner getOnVideoGridClickListner() {
        return onVideoGridClickListner;
    }

    public void setOnVideoGridClickListner(OnVideoGridClickListner onVideoGridClickListner) {
        this.onVideoGridClickListner = onVideoGridClickListner;
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FeedViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vid_profile_list, parent, false));

    }

    @Override
    public void onBindViewHolder(ProfileVideoGridAdapter.FeedViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return reels.size();
    }

    public void addData(List<ReliteRoot.VideoItem> reels) {

        this.reels.addAll(reels);
        notifyItemRangeInserted(this.reels.size(), reels.size());
    }

    public List<ReliteRoot.VideoItem> getList() {
        return reels;
    }

    public void clear() {
        reels.clear();
        notifyDataSetChanged();
    }

    public interface OnVideoGridClickListner {
        void onVideoClick(int position);

        void onDeleteClick(ReliteRoot.VideoItem postItem, int position);
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        ItemVidProfileListBinding binding;

        public FeedViewHolder(View itemView) {
            super(itemView);
            binding = ItemVidProfileListBinding.bind(itemView);
        }

        public void setData(int position) {
            ReliteRoot.VideoItem reel = reels.get(position);

            Glide.with(binding.getRoot()).load(reel.getVideo())
                    .apply(MainApplication.requestOptions)
                    .centerCrop().into(binding.imgThumb);
            binding.tvViewCount.setText(String.valueOf(reel.getLikeCount()));
            binding.getRoot().setOnClickListener(v -> onVideoGridClickListner.onVideoClick(position));

            binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onVideoGridClickListner.onDeleteClick(reel, position);
                    return true;
                }
            });
        }
    }
}
