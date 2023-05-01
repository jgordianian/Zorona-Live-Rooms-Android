package com.zorona.liverooms.user.wallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zorona.liverooms.R;
import com.zorona.liverooms.databinding.ItemReedemMethodBinding;

import java.util.List;

public class ReedemMethodAdapter extends RecyclerView.Adapter<ReedemMethodAdapter.ReedemMethodViewHolder> {

    private Context context;
    private int selectedPos = 0;
    private List<String> paymentGateways;
    private OnReedemMethodClickListner onReedemMethodClickListner;

    public ReedemMethodAdapter(List<String> paymentGateways, OnReedemMethodClickListner onReedemMethodClickListner) {

        this.paymentGateways = paymentGateways;
        this.onReedemMethodClickListner = onReedemMethodClickListner;
    }

    @Override
    public ReedemMethodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ReedemMethodViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reedem_method, parent, false));
    }

    @Override
    public void onBindViewHolder(ReedemMethodViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return paymentGateways.size();
    }

    public interface OnReedemMethodClickListner {
        void onMethodChange(String s);
    }

    public class ReedemMethodViewHolder extends RecyclerView.ViewHolder {
        ItemReedemMethodBinding binding;

        public ReedemMethodViewHolder(View itemView) {
            super(itemView);
            binding = ItemReedemMethodBinding.bind(itemView);
        }

        public void setData(int position) {
            binding.tvText.setText(paymentGateways.get(position));
            if (selectedPos==position) {
                binding.lytMain.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.pink));
            } else {
                binding.lytMain.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.graydark));
            }
            binding.getRoot().setOnClickListener(v -> {
                selectedPos = position;
                onReedemMethodClickListner.onMethodChange(paymentGateways.get(position));
                notifyDataSetChanged();
            });
        }
    }
}
