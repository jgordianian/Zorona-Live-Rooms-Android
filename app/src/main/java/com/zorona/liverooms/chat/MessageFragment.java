package com.app.liverooms.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.app.liverooms.activity.BaseFragment;
import com.app.liverooms.modelclass.ChatUserListRoot;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.retrofit.RetrofitBuilder;
import com.app.liverooms.MyLoader;
import com.app.liverooms.R;
import com.app.liverooms.databinding.FragmentMessageBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MessageFragment extends BaseFragment {
    FragmentMessageBinding binding;
    private int start = 0;

    public MessageFragment() {
        // Required empty public constructor
    }

    MyLoader myLoader = new MyLoader();
    ChatUserAdapter chatUserAdapter = new ChatUserAdapter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false);
        binding.setMyLoder(myLoader);
        initView();
        getChatUserList(false);
        binding.swipeRefresh.setOnRefreshListener((refreshLayout) -> {
            getChatUserList(false);
        });
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
            getChatUserList(true);
        });

        return binding.getRoot();
    }

    private void getChatUserList(boolean isLoadMore) {

        myLoader.noData.set(false);
        if (isLoadMore) {
            start = start + Const.LIMIT;

        } else {
            start = 0;
            chatUserAdapter.clear();
            myLoader.isFristTimeLoading.set(true);
        }


        Call<ChatUserListRoot> call = RetrofitBuilder.create().getChatUserList(sessionManager.getUser().getId(), start, Const.LIMIT);
        call.enqueue(new Callback<ChatUserListRoot>() {
            @Override
            public void onResponse(Call<ChatUserListRoot> call, Response<ChatUserListRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getChatList().isEmpty()) {
                        chatUserAdapter.addData(response.body().getChatList());
                    } else if (start == 0) {
                        myLoader.noData.set(true);
                    }
                }
                myLoader.isFristTimeLoading.set(false);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
            }

            @Override
            public void onFailure(Call<ChatUserListRoot> call, Throwable t) {

            }
        });
    }

    private void initView() {

        binding.rvMessage.setAdapter(chatUserAdapter);


    }
}