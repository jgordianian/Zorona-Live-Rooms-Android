package com.app.liverooms.user.wallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.liverooms.R;
import com.app.liverooms.databinding.ItemPurchaseCoinBinding;
import com.app.liverooms.modelclass.DiamondPlanRoot;
import com.app.liverooms.retrofit.Const;

import java.util.ArrayList;
import java.util.List;

public class CoinPurchaseAdapter extends RecyclerView.Adapter<CoinPurchaseAdapter.CoinViewHolder> {

    private Context context;
    OnCoinPlanClickListner onCoinPlanClickListner;
    private List<DiamondPlanRoot.DiamondPlanItem> coinList = new ArrayList<>();

    public OnCoinPlanClickListner getOnCoinPlanClickListner() {
        return onCoinPlanClickListner;
    }

    public void setOnCoinPlanClickListner(OnCoinPlanClickListner onCoinPlanClickListner) {
        this.onCoinPlanClickListner = onCoinPlanClickListner;
    }

    @Override
    public CoinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CoinViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_coin, parent, false));
    }

    @Override
    public void onBindViewHolder(CoinViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return coinList.size();
    }

    public void addData(List<DiamondPlanRoot.DiamondPlanItem> coinList) {

        this.coinList.addAll(coinList);
        notifyItemRangeInserted(this.coinList.size(), coinList.size());
    }

    public interface OnCoinPlanClickListner {
        void onPlanClick(DiamondPlanRoot.DiamondPlanItem coinPlan);
    }

    public class CoinViewHolder extends RecyclerView.ViewHolder {
        ItemPurchaseCoinBinding binding;

        public CoinViewHolder(View itemView) {
            super(itemView);
            binding = ItemPurchaseCoinBinding.bind(itemView);
        }

        public void setData(int position) {
            DiamondPlanRoot.DiamondPlanItem coinPlan = coinList.get(position);
            binding.tvCoin.setText(String.valueOf(coinPlan.getDiamonds()));
            binding.tvLabel.setVisibility(coinPlan.getTag().isEmpty() ? View.GONE : View.VISIBLE);
            binding.tvLabel.setText(coinPlan.getTag());
            binding.tvAmount.setText(String.valueOf(coinPlan.getDollar()) + Const.getCurrency());
            binding.getRoot().setOnClickListener(v -> onCoinPlanClickListner.onPlanClick(coinPlan));
        }
    }
}
