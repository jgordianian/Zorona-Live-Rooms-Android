package com.zorona.liverooms.posts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zorona.liverooms.R;
import com.zorona.liverooms.databinding.ItemLocationBinding;
import com.zorona.liverooms.modelclass.SearchLocationRoot;

import java.util.ArrayList;
import java.util.List;


public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    private Context context;
    OnLocationClickLisnter onLocationClickLisnter;
    private List<SearchLocationRoot.ResponseItem> locations = new ArrayList<>();

    public OnLocationClickLisnter getOnLocationClickLisnter() {
        return onLocationClickLisnter;
    }

    public void setOnLocationClickLisnter(OnLocationClickLisnter onLocationClickLisnter) {
        this.onLocationClickLisnter = onLocationClickLisnter;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new LocationViewHolder(LayoutInflater.from(context).inflate(R.layout.item_location, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public void addData(List<SearchLocationRoot.ResponseItem> locations) {

        this.locations.addAll(locations);
        notifyItemRangeInserted(this.locations.size(), locations.size());
    }

    public void clear() {
        this.locations.clear();
        notifyDataSetChanged();
    }

    public interface OnLocationClickLisnter {
        void onLocationclick(SearchLocationRoot.ResponseItem hashtag);
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {
        ItemLocationBinding binding;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemLocationBinding.bind(itemView);
        }

        public void setData(int position) {
            SearchLocationRoot.ResponseItem location = locations.get(position);

            binding.tvLocation.setText(location.getCity() + ", " + location.getState() + "," + location.getCountryName());
            binding.getRoot().setOnClickListener(v -> onLocationClickLisnter.onLocationclick(locations.get(position)));
        }
    }
}
