package com.zorona.liverooms.utils.autoComplete;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zorona.liverooms.R;
import com.zorona.liverooms.modelclass.HeshtagsRoot;
import com.zorona.liverooms.utils.TextFormatUtil;

import java.util.List;


public class HashtagAdapter extends RecyclerView.Adapter<HashtagAdapter.HashtagViewHolder> {

    private final Context mContext;
    private final OnClickListener mListener;
    private List<HeshtagsRoot.HashtagItem> mItems;

    protected HashtagAdapter(@NonNull Context context, @NonNull OnClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull HashtagViewHolder holder, int position) {
        final HeshtagsRoot.HashtagItem hashtag = mItems.get(position);
        holder.name.setText("#" + hashtag.getHashtag());
        holder.clips.setText(
                mContext.getString(R.string.count_clips, TextFormatUtil.toShortNumber(hashtag.getCount())));
        holder.itemView.setOnClickListener(v -> mListener.onHashtagClick(hashtag));
    }

    @NonNull
    @Override
    public HashtagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(mContext)
                .inflate(R.layout.item_hashtag_slim, parent, false);
        return new HashtagViewHolder(root);
    }

    public void submitData(List<HeshtagsRoot.HashtagItem> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    interface OnClickListener {

        void onHashtagClick(HeshtagsRoot.HashtagItem hashtag);
    }

    public class HashtagViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView clips;

        public HashtagViewHolder(@NonNull View root) {
            super(root);
            name = root.findViewById(R.id.name);
            clips = root.findViewById(R.id.clips);
        }
    }
}
