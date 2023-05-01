package com.zorona.liverooms.user.complain;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.zorona.liverooms.MyLoader;
import com.zorona.liverooms.R;
import com.zorona.liverooms.activity.BaseActivity;
import com.zorona.liverooms.databinding.ActivityComplainListBinding;
import com.zorona.liverooms.modelclass.ComplainRoot;
import com.zorona.liverooms.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplainListActivity extends BaseActivity {
    ActivityComplainListBinding binding;
    MyLoader myLoader = new MyLoader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complain_list);
        binding.setLoader(myLoader);


        getData();
        initListner();
    }

    private void initListner() {
        binding.swipe.setOnRefreshListener(refreshLayout -> getData());
    }

    private void getData() {

        binding.noData.setVisibility(View.GONE);
        myLoader.isFristTimeLoading.set(true);
        Call<ComplainRoot> call = RetrofitBuilder.create().getComplains(sessionManager.getUser().getId());
        call.enqueue(new Callback<ComplainRoot>() {
            @Override
            public void onResponse(Call<ComplainRoot> call, Response<ComplainRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && !response.body().getComplain().isEmpty()) {
                    binding.rvTikit.setAdapter(new TikitAdaptor_a(response.body().getComplain()));

                } else {
                    binding.noData.setVisibility(View.VISIBLE);
                }

                myLoader.isFristTimeLoading.set(false);
                binding.swipe.finishRefresh();
            }

            @Override
            public void onFailure(Call<ComplainRoot> call, Throwable t) {

            }
        });
    }
}