package com.zorona.liverooms.viewModel;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zorona.liverooms.modelclass.GiftCategoryRoot;
import com.zorona.liverooms.modelclass.GiftRoot;
import com.zorona.liverooms.retrofit.RetrofitBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmojiSheetViewModel extends ViewModel {

    public MutableLiveData<GiftRoot.GiftItem> selectedGift = new MutableLiveData<>();
    public MutableLiveData<GiftRoot.GiftItem> finelGift = new MutableLiveData<>();
    public MutableLiveData<Integer> localUserCoin = new MutableLiveData<>();
    public MutableLiveData<Boolean> onRechargeTap = new MutableLiveData<>();
    public ObservableBoolean isLoading = new ObservableBoolean();
    public ObservableBoolean noData = new ObservableBoolean(false);

    public MutableLiveData<List<GiftCategoryRoot.CategoryItem>> categoryItemMutableLiveData = new MutableLiveData<>();

    public void getGiftCategory() {
        isLoading.set(true);
        Call<GiftCategoryRoot> call = RetrofitBuilder.create().getGiftCategory();
        call.enqueue(new Callback<GiftCategoryRoot>() {
            @Override
            public void onResponse(Call<GiftCategoryRoot> call, Response<GiftCategoryRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getCategory().isEmpty()) {
                        categoryItemMutableLiveData.setValue(response.body().getCategory());
                    } else {
                        noData.set(true);
                    }
                }
                isLoading.set(false);
            }

            @Override
            public void onFailure(Call<GiftCategoryRoot> call, Throwable t) {

            }
        });
    }

}
