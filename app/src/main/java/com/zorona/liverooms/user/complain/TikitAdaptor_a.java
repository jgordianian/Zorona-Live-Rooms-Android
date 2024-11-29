package com.app.liverooms.user.complain;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.liverooms.R;
import com.app.liverooms.databinding.ItemTikitBinding;
import com.app.liverooms.modelclass.ComplainRoot;
import com.google.gson.Gson;

import java.util.List;

public class TikitAdaptor_a extends RecyclerView.Adapter<TikitAdaptor_a.TikitViewHolder> {
    Context context;

    private boolean isClicked = false;
    private List<ComplainRoot.ComplainItem> data;

    public TikitAdaptor_a(List<ComplainRoot.ComplainItem> data) {

        this.data = data;
    }


    @NonNull
    @Override
    public TikitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_tikit, parent, false);
        return new TikitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TikitViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class TikitViewHolder extends RecyclerView.ViewHolder {
        ItemTikitBinding binding;
        private boolean isFold = false;

        public TikitViewHolder(@NonNull View itemView) {

            super(itemView);
            binding = ItemTikitBinding.bind(itemView);
        }

        public void setData(int position) {
            ComplainRoot.ComplainItem tikit = data.get(position);
            binding.tvTitle.setText(tikit.getMessage());


            binding.tvtime.setText(tikit.getCreatedAt());

            if (tikit.isSolved()) {
                binding.status.setText("SOLVED");
            } else {
                binding.status.setText(" OPEN ");
                binding.status.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green));
            }

            itemView.setOnClickListener(v -> {

                context.startActivity(new Intent(context, ComplainDetailsActivity.class).putExtra("tickit", new Gson().toJson(tikit)));
            });
        }
    }
}
