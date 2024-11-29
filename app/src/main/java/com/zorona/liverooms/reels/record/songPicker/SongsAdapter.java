package com.app.liverooms.reels.record.songPicker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.app.liverooms.BuildConfig;
import com.app.liverooms.MainApplication;
import com.app.liverooms.modelclass.SongRoot;
import com.app.liverooms.R;
import com.app.liverooms.databinding.ItemSongBinding;

import java.util.ArrayList;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW1 = 1;
    private static final int VIEW2 = 2;
    OnSongClickListner onSongClickListner;
    private List<SongRoot.SongItem> songItems = new ArrayList<>();

    public OnSongClickListner getOnSongClickListner() {
        return onSongClickListner;
    }

    public void setOnSongClickListner(OnSongClickListner onSongClickListner) {
        this.onSongClickListner = onSongClickListner;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false));


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SongsViewHolder) holder).setData(position);
    }

    @Override
    public int getItemCount() {
        return songItems.size();
    }

    public void addData(List<SongRoot.SongItem> songDummies) {

        this.songItems.addAll(songDummies);
        notifyItemRangeInserted(this.songItems.size(), songDummies.size());
    }

    public interface OnSongClickListner {
        void onSongClick(SongRoot.SongItem songDummy);
    }

    public class SongsViewHolder extends RecyclerView.ViewHolder {
        ItemSongBinding binding;

        public SongsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSongBinding.bind(itemView);
            Glide.with(itemView).load(R.drawable.song)
                    .apply(MainApplication.requestOptions)
                    .circleCrop().into(binding.imgSong);
        }

        public void setData(int position) {
            SongRoot.SongItem songDummy = songItems.get(position);
            Glide.with(binding.getRoot()).load(BuildConfig.BASE_URL + songDummy.getImage())
                    .apply(MainApplication.requestOptions)
                    .circleCrop().into(binding.imgSong);
            binding.title.setText(songDummy.getTitle());
            binding.info.setText(songDummy.getSinger());
            binding.getRoot().setOnClickListener(v -> onSongClickListner.onSongClick(songDummy));
        }
    }
}

