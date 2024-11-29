package com.app.liverooms.liveStreamming;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.app.liverooms.MainApplication;
import com.app.liverooms.R;
import com.app.liverooms.databinding.ItemVideoGridBinding;
import com.app.liverooms.modelclass.LiveUserRoot;
import com.app.liverooms.retrofit.Const;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class LiveListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<LiveUserRoot.UsersItem> userDummies = new ArrayList<>();
    private int adbanner_layout = 2;
    private int live_layout = 1;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == 1) {
            return new VideoListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_grid, parent, false));
        } else {
            return new AdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VideoListViewHolder) {
            ((VideoListViewHolder) holder).setData(position);
        } else {

        }
    }

    @Override
    public int getItemCount() {
        return userDummies.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
      /*  if (i % 3 == 0) {
            return this.adbanner_layout;
        }*/
        return this.live_layout;
    }

    public void addData(List<LiveUserRoot.UsersItem> userDummies) {

        this.userDummies.addAll(userDummies);
        notifyItemRangeInserted(this.userDummies.size(), userDummies.size());
    }

    public void clear() {
        userDummies.clear();
        notifyDataSetChanged();
    }

    public class VideoListViewHolder extends RecyclerView.ViewHolder {
        ItemVideoGridBinding binding;

        public VideoListViewHolder(View itemView) {
            super(itemView);
            binding = ItemVideoGridBinding.bind(itemView);


        }

        public void setData(int position) {
            LiveUserRoot.UsersItem userDummy = userDummies.get(position);
            binding.tvName.setText(userDummy.getName());
            binding.tvCountry.setText(userDummy.getCountry());
            Glide.with(context).load(userDummy.getImage())
                    .apply(MainApplication.requestOptionsLive)
                    .centerCrop().into(binding.image);
            binding.tvViewCount.setText(String.valueOf(userDummy.getView()));
            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(context, HostLiveActivity.class);
                intent.putExtra(Const.DATA, new Gson().toJson(userDummy));
                intent.putExtra(Const.IS_GUEST, true); // Set to true if the user is a guest
                context.startActivity(intent);
            });}
    }

    private class AdViewHolder extends RecyclerView.ViewHolder {
        public AdViewHolder(View inflate) {
            super(inflate);
        }
    }
}
