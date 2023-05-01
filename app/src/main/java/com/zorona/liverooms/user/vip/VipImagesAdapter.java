package com.zorona.liverooms.user.vip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zorona.liverooms.BuildConfig;
import com.zorona.liverooms.MainApplication;
import com.zorona.liverooms.R;
import com.zorona.liverooms.activity.WebActivity;
import com.zorona.liverooms.databinding.ItemVipSliderBinding;
import com.zorona.liverooms.modelclass.BannerRoot;

import java.util.ArrayList;
import java.util.List;

public class VipImagesAdapter extends RecyclerView.Adapter<VipImagesAdapter.VipImagesViewHolder> {

    private List<BannerRoot.BannerItem> banner = new ArrayList<>();
    private Context context;

    @Override
    public VipImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new VipImagesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vip_slider, parent, false));
    }

    @Override
    public void onBindViewHolder(VipImagesViewHolder holder, int position) {
        Glide.with(context).load(BuildConfig.BASE_URL + banner.get(position).getImage())
                .apply(MainApplication.requestOptions)
                .into(holder.bannerBinding.image);
        holder.bannerBinding.image.setOnClickListener(v -> WebActivity.open(context, "", banner.get(position).getURL()));
    }

    @Override
    public int getItemCount() {
        return banner.size();
    }

    public void addData(List<BannerRoot.BannerItem> banner) {

        this.banner = banner;
        notifyDataSetChanged();
    }


    public class VipImagesViewHolder extends RecyclerView.ViewHolder {
        ItemVipSliderBinding bannerBinding;

        public VipImagesViewHolder(View itemView) {
            super(itemView);
            bannerBinding = ItemVipSliderBinding.bind(itemView);
        }
    }
}
