package com.zorona.liverooms.posts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zorona.liverooms.R;
import com.zorona.liverooms.databinding.ItemHashtagsBinding;
import com.zorona.liverooms.modelclass.HeshtagsRoot;

import java.util.ArrayList;
import java.util.List;


public class HashtagsAdapter extends RecyclerView.Adapter<HashtagsAdapter.HashtagViewHolder> {
    private Context context;
    OnHashtagsClickLisnter onMentionssClickLisnter;
    private List<HeshtagsRoot.HashtagItem> hashtags = new ArrayList<>();

    public OnHashtagsClickLisnter getOnHashtagsClickLisnter() {
        return onMentionssClickLisnter;
    }

    public void setOnHashtagsClickLisnter(OnHashtagsClickLisnter onMentionssClickLisnter) {
        this.onMentionssClickLisnter = onMentionssClickLisnter;
    }

    @NonNull
    @Override
    public HashtagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new HashtagViewHolder(LayoutInflater.from(context).inflate(R.layout.item_hashtags, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HashtagViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return hashtags.size();
    }

    public void addData(List<HeshtagsRoot.HashtagItem> hashtags) {
        this.hashtags.addAll(hashtags);
        notifyItemRangeInserted(this.hashtags.size(), hashtags.size());
    }

    public void clear() {
        this.hashtags.clear();
        notifyDataSetChanged();
    }

    public interface OnHashtagsClickLisnter {
        void onHashtagClick(HeshtagsRoot.HashtagItem hashtag);
    }

    public class HashtagViewHolder extends RecyclerView.ViewHolder {
        ItemHashtagsBinding binding;

        public HashtagViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemHashtagsBinding.bind(itemView);

        }

        public void setData(int position) {
            HeshtagsRoot.HashtagItem hashtagItem = hashtags.get(position);
            binding.tvHashtag.setText(hashtagItem.getHashtag());
            binding.getRoot().setOnClickListener(v -> onMentionssClickLisnter.onHashtagClick(hashtags.get(position)));
        }
    }
}
