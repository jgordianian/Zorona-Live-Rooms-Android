package com.zorona.liverooms.liveStreamming;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.zorona.liverooms.R;
import com.zorona.liverooms.databinding.ItemViewUsersBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LiveViewUserAdapter extends RecyclerView.Adapter<LiveViewUserAdapter.ChatUserViewHolder> {


    private JSONArray users = new JSONArray();

    @Override
    public ChatUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatUserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_users, parent, false));
    }

    OnLiveUserAdapterClickLisnter onLiveUserAdapterClickLisnter;

    public OnLiveUserAdapterClickLisnter getOnLiveUserAdapterClickLisnter() {
        return onLiveUserAdapterClickLisnter;
    }

    public void setOnLiveUserAdapterClickLisnter(OnLiveUserAdapterClickLisnter onLiveUserAdapterClickLisnter) {
        this.onLiveUserAdapterClickLisnter = onLiveUserAdapterClickLisnter;
    }

    @Override
    public void onBindViewHolder(LiveViewUserAdapter.ChatUserViewHolder holder, int position) {
        holder.setData(position);

    }

    @Override
    public int getItemCount() {
        return users.length();
    }

    public void addData(JSONArray jsonArray) {

        users = jsonArray;
        notifyDataSetChanged();
     /*   for (int i = 0; i < jsonArray.length(); i++) {
            try {
            JSONObject object=jsonArray.getJSONObject(i);

              *//*  for (int j = 0; j < this.users.length(); j++) {
                    JSONObject user=users.getJSONObject(j);
                    if (user.get("userId").equals(object.get("userId"))){
                        users.remove(j);
                    }
                }*//*
                this.users.put(jsonArray.get(i));
                notifyItemInserted(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
    }

    public interface OnLiveUserAdapterClickLisnter {
        void onUserClick(JSONObject userDummy);
    }

    public class ChatUserViewHolder extends RecyclerView.ViewHolder {
        ItemViewUsersBinding binding;

        public ChatUserViewHolder(View itemView) {
            super(itemView);
            binding = ItemViewUsersBinding.bind(itemView);
        }

        public void setData(int position) {
            try {
                JSONObject userDummy = users.getJSONObject(position);
                Log.d("TAG", position + " setData: viewlist  " + position + userDummy.getString("image"));
                binding.imgview.setUserImage(userDummy.getString("image").toString(), userDummy.getBoolean("isVIP"));
                binding.getRoot().setOnClickListener(v -> onLiveUserAdapterClickLisnter.onUserClick(userDummy));
            } catch (Exception o) {
                Log.e("TAG", "setData:viewadapter " + o.getMessage());
            }
        }
    }
}
