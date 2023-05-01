package com.zorona.liverooms.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.zorona.liverooms.R;
import com.zorona.liverooms.activity.BaseActivity;
import com.zorona.liverooms.modelclass.GuestProfileRoot;
import com.zorona.liverooms.modelclass.GuestUsersListRoot;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.retrofit.RetrofitBuilder;
import com.zorona.liverooms.retrofit.UserApiCall;
import com.zorona.liverooms.user.guestUser.GuestActivity;
import com.zorona.liverooms.viewModel.ViewModelFactory;
import com.zorona.liverooms.databinding.ActivitySearchBinding;
import com.zorona.liverooms.databinding.ItemSearchUsersBinding;
import com.zorona.liverooms.databinding.ItemSearchUsersHistoryBinding;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends BaseActivity {
    ActivitySearchBinding binding;


    private Call<GuestUsersListRoot> call;
    private SearchViewModel viewModel;

    public void onBackPressed() {
        super.onBackPressed();
        doTransition(Const.UP_TO_BOTTOM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new SearchViewModel()).createFor()).get(SearchViewModel.class);
        binding.setViewModel(viewModel);

        binding.rvMessage.setAdapter(viewModel.searchUserAdapter);
        viewModel.searchUser(false);
        // getSearchHistory();


        initLister();
    }

    private void getSearchHistory() {
        if (sessionManager.getSearchHistory().isEmpty()) {
            viewModel.searchUser(false);
            binding.rvMessage.setAdapter(viewModel.searchUserAdapter);
            return;
        }

        viewModel.searchHistoryUserAdapter.addData(sessionManager.getSearchHistory());
        binding.rvMessage.setAdapter(viewModel.searchHistoryUserAdapter);
        viewModel.searchHistoryUserAdapter.setOnUserClickLisnter(new SearchHistoryUserAdapter.OnUserClickLisnter() {
            @Override
            public void onDeleteClick(GuestProfileRoot.User userDummy, ItemSearchUsersHistoryBinding binding, int position) {
                sessionManager.removefromSearchHistory(userDummy);
                viewModel.searchHistoryUserAdapter.remove(position);
            }

            @Override
            public void onUserClick(GuestProfileRoot.User userDummy, ItemSearchUsersHistoryBinding binding, int pos) {
                startActivity(new Intent(SearchActivity.this, GuestActivity.class).putExtra(Const.USERID, userDummy.getUserId()));

            }
        });

    }


    private void initLister() {


        binding.swipeRefresh.setOnRefreshListener((refreshLayout) -> {
            viewModel.searchUser(false);
        });
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
            viewModel.searchUser(true);
        });
        viewModel.isLoadCompleted.observe(this, aBoolean -> {
            if (aBoolean) {
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
                viewModel.isFirstTimeLoading.set(false);

            }
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.keyword = s.toString();
                if (viewModel.keyword.isEmpty()) {
                    //getSearchHistory();
                    viewModel.searchUser(false);
                } else {
                    binding.rvMessage.setAdapter(viewModel.searchUserAdapter);
                    viewModel.searchUser(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        viewModel.searchUserAdapter.setOnUserClickLisnter(new SearchUserAdapter.OnUserClickLisnter() {
            @Override
            public void onFollowClick(GuestProfileRoot.User userDummy, ItemSearchUsersBinding binding, int position) {

                binding.pd.setVisibility(View.VISIBLE);
                binding.tvFollow.setVisibility(View.GONE);
                userApiCall.followUnfollowUser(!userDummy.isFollow(), userDummy.getUserId(), "", new UserApiCall.OnFollowUnfollowListner() {
                    @Override
                    public void onFollowSuccess() {
                        userDummy.setFollow(true);
                        viewModel.searchUserAdapter.notifyItemChanged(position, userDummy);
                        binding.tvFollow.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onUnfollowSuccess() {
                        userDummy.setFollow(false);
                        viewModel.searchUserAdapter.notifyItemChanged(position, userDummy);
                        binding.tvFollow.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFail() {
                        viewModel.searchUserAdapter.notifyItemChanged(position, userDummy);
                        binding.tvFollow.setVisibility(View.VISIBLE);
                    }
                });

            }

            @Override
            public void onUserClick(GuestProfileRoot.User userDummy, ItemSearchUsersBinding binding, int pos) {
                sessionManager.addToSearchHistory(userDummy);
                startActivity(new Intent(SearchActivity.this, GuestActivity.class).putExtra(Const.USERID, userDummy.getUserId()));
            }
        });


    }


    public class SearchViewModel extends ViewModel {
        public SearchUserAdapter searchUserAdapter = new SearchUserAdapter();
        public SearchHistoryUserAdapter searchHistoryUserAdapter = new SearchHistoryUserAdapter();
        public ObservableBoolean isFirstTimeLoading = new ObservableBoolean(false);

        public ObservableBoolean noData = new ObservableBoolean(false);
        public MutableLiveData<Boolean> isLoadCompleted = new MutableLiveData<>();
        public String keyword = "";
        private int start = 0;


        public void searchUser(boolean isLoadMore) {

            if (isLoadMore) {
                start = start + Const.LIMIT;

            } else {
                start = 0;
                searchUserAdapter.clear();
                isFirstTimeLoading.set(true);
            }


            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userId", sessionManager.getUser().getId());
            jsonObject.addProperty("value", viewModel.keyword);
            jsonObject.addProperty("start", start);
            jsonObject.addProperty("limit", Const.LIMIT);
            if (call != null) {
                call.cancel();
            }
            noData.set(false);
            call = RetrofitBuilder.create().searchUser(jsonObject);
            call.enqueue(new Callback<GuestUsersListRoot>() {
                @Override
                public void onResponse(Call<GuestUsersListRoot> call, Response<GuestUsersListRoot> response) {
                    if (response.code() == 200) {
                        if (response.body().isStatus() && !response.body().getUser().isEmpty()) {
                            searchUserAdapter.addData(response.body().getUser());
                        } else if (start == 0) {
                            noData.set(true);
                        }
                    }
                    isLoadCompleted.postValue(true);

                }

                @Override
                public void onFailure(Call<GuestUsersListRoot> call, Throwable t) {

                }
            });
        }
    }
}