package com.zorona.liverooms.user.wallet;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zorona.liverooms.R;
import com.zorona.liverooms.databinding.ItemCoinHistoryBinding;
import com.zorona.liverooms.user.guestUser.GuestActivity;
import com.zorona.liverooms.modelclass.HistoryListRoot;
import com.zorona.liverooms.retrofit.Const;

import java.util.ArrayList;
import java.util.List;

public class CoinHistoryAdapter extends RecyclerView.Adapter<CoinHistoryAdapter.CoinHistoryViewHolder> {

    private Context context;
    private int selectedPos = 0;
    private List<HistoryListRoot.HistoryItem> historyList = new ArrayList<>();
    private String coinType;

    @Override
    public CoinHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CoinHistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin_history, parent, false));
    }

    @Override
    public void onBindViewHolder(CoinHistoryViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void addData(List<HistoryListRoot.HistoryItem> history) {

        this.historyList.addAll(history);
        notifyItemRangeInserted(this.historyList.size(), history.size());
    }

    public void setCoinType(String coinType) {

        this.coinType = coinType;
    }

    public void clear() {
        historyList.clear();
        notifyDataSetChanged();
    }

    public class CoinHistoryViewHolder extends RecyclerView.ViewHolder {
        ItemCoinHistoryBinding binding;

        public CoinHistoryViewHolder(View itemView) {
            super(itemView);
            binding = ItemCoinHistoryBinding.bind(itemView);
        }

        public void setData(int position) {
            HistoryListRoot.HistoryItem historyItem = historyList.get(position);
            Log.d("TAG", "setData: " + position + "          " + historyItem.toString());
            String amount;
            Log.d("TAG", "setData: diamond " + historyItem.getDiamond());

            if (historyItem.isIsAdd()) {
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.green));
            }

            setTitleFromType(historyItem);


            amount = historyItem.isIsAdd() ? "+" : "-";
            if (coinType.equals(Const.DIAMOND)) {
                amount = amount + historyItem.getDiamond();
                Log.d("TAG", "setData: amountD " + amount);
            } else {
                amount = amount + historyItem.getRCoin();
                Log.d("TAG", "setData: amountR " + amount);
            }
            Log.d("TAG", "setData: amount " + amount);
            binding.tvAmount.setText(amount);

            binding.tvtime.setText(historyItem.getDate());


            binding.tvUserName.setText(Html.fromHtml("<font color='#FFFFFF'>by </font>@" + historyItem.getUserName()));


            if (historyItem.getUserName() != null && !historyItem.getUserName().isEmpty()) {
                binding.tvUserName.setVisibility(View.VISIBLE);
            } else {
                binding.tvUserName.setVisibility(View.GONE);
            }
            binding.tvUserName.setOnClickListener(v -> {
                context.startActivity(new Intent(context, GuestActivity.class).putExtra(Const.USERID, historyItem.getUserId()));
            });
        }

        private String setTitleFromType(HistoryListRoot.HistoryItem historyItem) {

            switch (historyItem.getType()) {
                case 0:

                    if (historyItem.getUserId() == null || historyItem.getUserId().isEmpty()) {
                        if (!historyItem.isIsAdd()) {
                            binding.tvText.setText("Gift Broadcast during livestream by you");

                        }
                    } else if (historyItem.isIsAdd()) {
                        binding.tvText.setText("gift received ");
                        binding.tvUserName.setText("@" + historyItem.getUserName());
                    } else if (!historyItem.isIsAdd()) {
                        binding.tvText.setText("gift send to");
                        binding.tvUserName.setText("@" + historyItem.getUserName());
                    }
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_gift));
                    break;
                case 1:
                    binding.tvText.setText(Const.CoinName + " converted to diamonds");
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rcoin));
                    break;
                case 2:
                    binding.tvText.setText("Diamond purchase");
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.diamond));
                    break;
                case 3:
                    binding.tvText.setText("You was call to");
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.videocall));
                    break;
                case 4:
                    binding.tvText.setText("Watching ads");
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ads));
                    break;
                case 5:
                    binding.tvText.setText("login bonus");
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.moneybag));
                    break;
                case 6:
                    binding.tvText.setText("Referral bonus");
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.moneybag));
                    break;
                case 7:
                    binding.tvText.setText("Cash out");
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.withdraw));
                    break;
                case 8:
                    if (historyItem.isIsAdd()) {
                        binding.tvText.setText("Added By Admin");
                        binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.green));
                    } else {
                        binding.tvText.setText("Reduce By Admin");
                        binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
                    }
                    if (historyItem.getRCoin() == 0) {
                        binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.diamond));
                    } else if (historyItem.getDiamond() == 0) {

                        binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rcoin));
                    }


                    break;


            }
            return "";
        }
    }
}
