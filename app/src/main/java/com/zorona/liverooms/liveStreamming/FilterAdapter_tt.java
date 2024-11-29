package com.app.liverooms.liveStreamming;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.app.liverooms.utils.Filters.FilterRoot;
import com.app.liverooms.utils.Filters.FilterUtils;
import com.app.liverooms.MainApplication;
import com.app.liverooms.R;
import com.app.liverooms.databinding.ItemFiltersBinding;

import java.util.List;

public class FilterAdapter_tt extends RecyclerView.Adapter<FilterAdapter_tt.FilterViewHolder> {
    List<FilterRoot> filters = FilterUtils.getFilters();
    private Context context;
    private OnFilterClickListnear onFilterClickListnear;


    public FilterAdapter_tt() {

    }

    public OnFilterClickListnear getOnFilterClickListnear() {
        return onFilterClickListnear;
    }

    public void setOnFilterClickListnear(OnFilterClickListnear onFilterClickListnear) {
        this.onFilterClickListnear = onFilterClickListnear;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new FilterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_filters, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {

        FilterRoot filterRoot = filters.get(position);
        if (filterRoot.getTitle().equalsIgnoreCase("None")) {
            holder.binding.imgf1.setImageDrawable(null);
            holder.binding.imgf2.setVisibility(View.VISIBLE);
        } else {
            Glide.with(holder.binding.imgf1).load(FilterUtils.getDraw(filterRoot.getTitle()))
                    .apply(MainApplication.requestOptions)
                    .centerCrop().into(holder.binding.imgf1);
            // holder.binding.imgf1.setImageDrawable(ContextCompat.getDrawable(context, filterRoot.getFilter()));
        }
        holder.binding.tvfiltername.setText(filterRoot.getTitle());


        holder.binding.imgf1.setOnClickListener(v -> {
            Log.d("TAG", "onBindViewHolder: ===========" + filterRoot.getTitle());
            onFilterClickListnear.onFilterClick(filterRoot);
        });

    }


    @Override
    public int getItemCount() {
        return filters.size();
    }

    public interface OnFilterClickListnear {
        void onFilterClick(FilterRoot filterRoot);
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder {
        ItemFiltersBinding binding;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemFiltersBinding.bind(itemView);
        }
    }
}
