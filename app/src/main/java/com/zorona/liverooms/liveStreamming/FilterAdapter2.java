package com.zorona.liverooms.liveStreamming;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zorona.liverooms.utils.Filters.FilterRoot;
import com.zorona.liverooms.utils.Filters.FilterUtils;
import com.zorona.liverooms.MainApplication;
import com.zorona.liverooms.R;
import com.zorona.liverooms.databinding.ItemFiltersBinding;

import java.util.List;

public class FilterAdapter2 extends RecyclerView.Adapter<FilterAdapter2.FilterViewHolder> {
    List<FilterRoot> filters = FilterUtils.getFilter2();
    private Context context;
    private OnFilterClickListnear onFilterClickListnear;


    public FilterAdapter2() {

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
            //  holder.binding.imgf1.setImageDrawable(ContextCompat.getDrawable(context, filterRoot.getFilter()));
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
