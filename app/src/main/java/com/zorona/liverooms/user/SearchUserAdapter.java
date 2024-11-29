package com.app.liverooms.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.liverooms.R;
import com.app.liverooms.databinding.ItemSearchUsersBinding;
import com.app.liverooms.modelclass.GuestProfileRoot;

import java.util.ArrayList;
import java.util.List;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.SearchUserViewHolder> {

    private Context context;
    private List<GuestProfileRoot.User> users = new ArrayList<>();
    OnUserClickLisnter onUserClickLisnter;

    public OnUserClickLisnter getOnUserClickLisnter() {
        return onUserClickLisnter;
    }

    public void setOnUserClickLisnter(OnUserClickLisnter onUserClickLisnter) {
        this.onUserClickLisnter = onUserClickLisnter;
    }

    @Override
    public SearchUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SearchUserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_users, parent, false));
    }

    @Override
    public void onBindViewHolder(SearchUserViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void addData(List<GuestProfileRoot.User> userDummies) {
        this.users.addAll(userDummies);
        notifyItemRangeInserted(this.users.size(), userDummies.size());
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    public interface OnUserClickLisnter {
        void onFollowClick(GuestProfileRoot.User userDummy, ItemSearchUsersBinding binding, int position);

        void onUserClick(GuestProfileRoot.User userDummy, ItemSearchUsersBinding binding, int pos);
    }

    public class SearchUserViewHolder extends RecyclerView.ViewHolder {
        ItemSearchUsersBinding binding;

        {

        }

        public SearchUserViewHolder(View itemView) {
            super(itemView);
            binding = ItemSearchUsersBinding.bind(itemView);
        }

        public void setData(int position) {
            GuestProfileRoot.User userDummy = users.get(position);
            binding.imageUser.setUserImage(userDummy.getImage(), userDummy.isVIP());
            binding.tvusername.setText(userDummy.getName());
            //   binding.tvBio.setVisibility(userDummy.getBio().isEmpty()?View.GONE:View.VISIBLE);

            if (userDummy.isFollow()) {
                binding.pd.setVisibility(View.GONE);
                binding.tvFollow.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.graylight));
                binding.tvFollow.setText("Unfollow");
                binding.tvFollow.setOnClickListener(v -> onUserClickLisnter.onFollowClick(userDummy, binding, position));
            } else {
                binding.pd.setVisibility(View.GONE);
                binding.tvFollow.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.pink));
                binding.tvFollow.setText("Follow");
                binding.tvFollow.setOnClickListener(v -> onUserClickLisnter.onFollowClick(userDummy, binding, position));
            }
            if (userDummy.getBio() != null && !userDummy.getBio().isEmpty()) {
                binding.tvBio.setText(userDummy.getBio());
            } else {
                binding.tvBio.setText(userDummy.getUsername());
            }

            binding.getRoot().setOnClickListener(v -> onUserClickLisnter.onUserClick(userDummy, binding, position));
        }


    }
}
