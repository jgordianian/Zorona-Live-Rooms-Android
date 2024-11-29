package com.app.liverooms.user;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.liverooms.R;
import com.app.liverooms.databinding.ItemSearchUsersHistoryBinding;
import com.app.liverooms.modelclass.GuestProfileRoot;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryUserAdapter extends RecyclerView.Adapter<SearchHistoryUserAdapter.SearchHistoryUserViewHolder> {

    OnUserClickLisnter onUserClickLisnter;
    private Context context;
    private List<GuestProfileRoot.User> users = new ArrayList<>();

    public OnUserClickLisnter getOnUserClickLisnter() {
        return onUserClickLisnter;
    }

    public void setOnUserClickLisnter(OnUserClickLisnter onUserClickLisnter) {
        this.onUserClickLisnter = onUserClickLisnter;
    }

    @Override
    public SearchHistoryUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SearchHistoryUserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_users_history, parent, false));
    }

    @Override
    public void onBindViewHolder(SearchHistoryUserViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void addData(List<GuestProfileRoot.User> userDummies) {
        Log.d("TAG", "addData: search isxe " + userDummies.size());
        this.users.addAll(userDummies);
        notifyItemRangeInserted(this.users.size(), userDummies.size());
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    public void remove(int position) {
        users.remove(users.get(position));
        notifyItemRemoved(position);
    }

    public interface OnUserClickLisnter {
        void onDeleteClick(GuestProfileRoot.User userDummy, ItemSearchUsersHistoryBinding binding, int position);

        void onUserClick(GuestProfileRoot.User userDummy, ItemSearchUsersHistoryBinding binding, int pos);
    }

    public class SearchHistoryUserViewHolder extends RecyclerView.ViewHolder {
        ItemSearchUsersHistoryBinding binding;

        public SearchHistoryUserViewHolder(View itemView) {
            super(itemView);
            binding = ItemSearchUsersHistoryBinding.bind(itemView);
        }

        public void setData(int position) {
            GuestProfileRoot.User userDummy = users.get(position);
            binding.imageUser.setUserImage(userDummy.getImage(), userDummy.isVIP());
            binding.tvusername.setText(userDummy.getName());
            //   binding.tvBio.setVisibility(userDummy.getBio().isEmpty()?View.GONE:View.VISIBLE);

            binding.btnRemove.setOnClickListener(v -> onUserClickLisnter.onDeleteClick(userDummy, binding, position));
            if (userDummy.getBio() != null && !userDummy.getBio().isEmpty()) {
                binding.tvBio.setText(userDummy.getBio());
            } else {
                binding.tvBio.setText(userDummy.getUsername());
            }

            binding.getRoot().setOnClickListener(v -> onUserClickLisnter.onUserClick(userDummy, binding, position));
        }


    }
}
