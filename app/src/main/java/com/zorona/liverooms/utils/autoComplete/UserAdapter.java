package com.zorona.liverooms.utils.autoComplete;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zorona.liverooms.MainApplication;
import com.zorona.liverooms.R;
import com.zorona.liverooms.modelclass.GuestProfileRoot;

import java.util.List;


class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final Context mContext;
    private final OnClickListener mListener;
    private List<GuestProfileRoot.User> mItems;

    protected UserAdapter(@NonNull Context context, @NonNull OnClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final GuestProfileRoot.User user = mItems.get(position);
        Glide.with(holder.itemView).load(user.getImage())
                .apply(MainApplication.requestOptions)
                .circleCrop().into(holder.photo);


        holder.name.setText(user.getName());
        holder.username.setText("@" + user.getUsername());
        holder.itemView.setOnClickListener(v -> mListener.onUserClick(user));
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(mContext)
                .inflate(R.layout.item_user_slim, parent, false);
        return new UserViewHolder(root);
    }

    public void submitData(List<GuestProfileRoot.User> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    interface OnClickListener {

        void onUserClick(GuestProfileRoot.User user);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        public ImageView photo;
        public TextView name;
        public TextView username;

        public UserViewHolder(@NonNull View root) {
            super(root);
            photo = root.findViewById(R.id.photo);
            name = root.findViewById(R.id.name);
            username = root.findViewById(R.id.username);
        }
    }

}
