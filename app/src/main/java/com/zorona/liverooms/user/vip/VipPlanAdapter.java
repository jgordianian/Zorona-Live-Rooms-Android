package com.app.liverooms.user.vip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.liverooms.R;
import com.app.liverooms.databinding.ItemVipPlanBinding;
import com.app.liverooms.modelclass.VipPlanRoot;

import java.util.ArrayList;
import java.util.List;

public class VipPlanAdapter extends RecyclerView.Adapter<VipPlanAdapter.VipPlanViewHolder> {

    private Context context;
    private int selected = -1;
    OnPlanClickLisnter onPlanClickLisnter;
    private List<VipPlanRoot.VipPlanItem> vipPlan = new ArrayList<>();

    public OnPlanClickLisnter getOnPlanClickLisnter() {
        return onPlanClickLisnter;
    }

    public void setOnPlanClickLisnter(OnPlanClickLisnter onPlanClickLisnter) {
        this.onPlanClickLisnter = onPlanClickLisnter;
    }

    @Override
    public VipPlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new VipPlanViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vip_plan, parent, false));
    }

    @Override
    public void onBindViewHolder(VipPlanViewHolder holder, int position) {

        if (selected==position) {
            holder.binding.lytBack.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.pink));
            holder.binding.tvDays.setTextColor(ContextCompat.getColor(context, R.color.pink));
            holder.binding.tvDaysString.setTextColor(ContextCompat.getColor(context, R.color.pink));
            holder.binding.tvDes.setTextColor(ContextCompat.getColor(context, R.color.pink));
            holder.binding.lytInner.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
        } else {
            holder.binding.lytBack.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.bg_back_image));
            holder.binding.lytInner.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.black_back));
            holder.binding.tvDays.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.binding.tvDaysString.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.binding.tvDes.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return vipPlan.size();
    }

    public void addData(List<VipPlanRoot.VipPlanItem> vipPlan) {

        this.vipPlan = vipPlan;
        notifyDataSetChanged();
    }

    public void setSelected(int i) {
        selected = i;
    }

    public interface OnPlanClickLisnter {
        void onPlanClick(VipPlanRoot.VipPlanItem vipPlanItem);
    }

    public class VipPlanViewHolder extends RecyclerView.ViewHolder {
        ItemVipPlanBinding binding;

        public VipPlanViewHolder(View itemView) {
            super(itemView);
            binding = ItemVipPlanBinding.bind(itemView);
        }

        public void setData(int position) {

            VipPlanRoot.VipPlanItem plan = vipPlan.get(position);
            binding.tvDays.setText(String.valueOf(plan.getValidity()));

            binding.tvDaysString.setText(plan.getValidityType());
            binding.tvAmount.setText(String.valueOf(plan.getRupee()) + " ₹");

            binding.tvName.setText(plan.getName());

            if (plan.getTag() != null && !plan.getTag().isEmpty()) {
                binding.tvDes.setVisibility(View.VISIBLE);
                binding.tvDes.setText(plan.getTag());
            } else {
                binding.tvDes.setVisibility(View.GONE);
            }
            binding.getRoot().setOnClickListener(v -> {
                selected = position;
                notifyDataSetChanged();
                onPlanClickLisnter.onPlanClick(vipPlan.get(position));
            });
        }
    }
}
