package com.zorona.liverooms.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zorona.liverooms.modelclass.ChatItem;
import com.zorona.liverooms.modelclass.ChatListRoot;
import com.zorona.liverooms.modelclass.RestResponse;
import com.zorona.liverooms.chat.ChatAdapter;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatViewModel extends ViewModel {
    public ChatAdapter chatAdapter = new ChatAdapter();
    public MutableLiveData<Boolean> sendBtnEnable = new MutableLiveData<>(false);

    public String chatTopic;
    public int start = 0;
    public boolean isLoding = false;

    public void deleteChat(ChatItem chatDummy, int position) {
        Call<RestResponse> call = RetrofitBuilder.create().deleteChat(chatDummy.getId());
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        chatAdapter.removeSingleItem(position);
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {

            }
        });
    }

    public void getOldChat(boolean isLoadMore) {
        if (isLoadMore) {
            start = start + Const.LIMIT;

        } else {
            start = 0;
            chatAdapter.clear();

        }

        Call<ChatListRoot> call = RetrofitBuilder.create().getOldChats(chatTopic, start, Const.LIMIT);
        call.enqueue(new Callback<ChatListRoot>() {
            @Override
            public void onResponse(Call<ChatListRoot> call, Response<ChatListRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getChat().isEmpty()) {
                        chatAdapter.addData(response.body().getChat());
                        isLoding = false;

                    }
                }
            }

            @Override
            public void onFailure(Call<ChatListRoot> call, Throwable t) {

            }
        });
    }

}
