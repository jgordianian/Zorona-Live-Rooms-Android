package com.app.liverooms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.liverooms.R;
import com.app.liverooms.databinding.ItemDotsBinding;


public class DotAdaptr extends RecyclerView.Adapter<DotAdaptr.DotViewHolder> {
    private final int slides;
    private Context context;

    private int color;
    private int selctedPos = 0;

    public DotAdaptr(int slides, int color) {

        this.slides = slides;
        this.color = color;
    }

    @NonNull
    @Override
    public DotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new DotViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dots, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DotViewHolder holder, int position) {
        if (selctedPos == position) {
            holder.binding.dot.setBackgroundTintList(ContextCompat.getColorStateList(context, color));
        } else {
            holder.binding.dot.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return slides;
    }


    public void changePos(int pos) {
        selctedPos = pos;
        notifyDataSetChanged();
    }

    public void changeDot(int scrollPosition) {
        selctedPos = scrollPosition;
        notifyDataSetChanged();
    }

    public class DotViewHolder extends RecyclerView.ViewHolder {
        ItemDotsBinding binding;

        public DotViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemDotsBinding.bind(itemView);
        }
    }
}
