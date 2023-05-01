package com.zorona.liverooms.user.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.zorona.liverooms.MyLoader;
import com.zorona.liverooms.R;
import com.zorona.liverooms.activity.BaseActivity;
import com.zorona.liverooms.databinding.ActivityHistoryBinding;
import com.zorona.liverooms.modelclass.CustomDate;
import com.zorona.liverooms.modelclass.HistoryListRoot;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.retrofit.RetrofitBuilder;
import com.zorona.liverooms.utils.datepicker.utils.DateUtils;
import com.zorona.liverooms.utils.datepicker.view.datePicker.DatePicker;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends BaseActivity {
    ActivityHistoryBinding binding;
    private DatePicker datePicker;
    CoinHistoryAdapter coinHistoryAdapter = new CoinHistoryAdapter();
    private CustomDate selectedDate;
    private CustomDate startDate;
    private CustomDate endDate;
    private String coinType = "";
    private int selectedType = 1;  // for start date , enddate
    private int start = 0;
    MyLoader myLoader = new MyLoader();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history);
        binding.setMyLoder(myLoader);
        initView();

        initLister();
        Intent intent = getIntent();
        coinType = intent.getStringExtra(Const.TYPE);
        if (!coinType.isEmpty()) {
            if (coinType.equals(Const.RCOINS)) {
                binding.lytTop.setBackground(ContextCompat.getDrawable(this, R.color.purple));
                binding.tvTitle.setText(Const.CoinName + " Record");
            }
            coinHistoryAdapter.setCoinType(coinType);
            String sDate = intent.getStringExtra(Const.STARTDATE);
            if (sDate != null && !sDate.isEmpty()) {
                startDate = new Gson().fromJson(sDate, CustomDate.class);
            }
            String eDate = intent.getStringExtra(Const.ENDDATE);
            if (eDate != null && !eDate.isEmpty()) {
                endDate = new Gson().fromJson(eDate, CustomDate.class);
            }

            binding.tvDate1.setText(startDate.getDateForHuman());
            binding.tvDate2.setText(endDate.getDateForHuman());
            getRecordData(false);
        }


    }

    private void initView() {

        binding.rvHistory.setAdapter(coinHistoryAdapter);

        binding.lytDatePicker.lytDatePicker.setVisibility(View.GONE);
        datePicker = binding.lytDatePicker.datepicker;
        datePicker.setOffset(3);
        datePicker.setTextSize(19);
        datePicker.setPickerMode(DatePicker.DAY_ON_FIRST);
        datePicker.setDarkModeEnabled(true);
        datePicker.setMaxDate(DateUtils.getTimeMiles(2099, 12, 25));
        datePicker.setDate(DateUtils.getCurrentTime());
        datePicker.setMinDate(DateUtils.getTimeMiles(2022, 0, 1));
    }

    private void initLister() {
        datePicker.setDataSelectListener((date, day, month, year) -> {
            Log.d("TAG", "onDateSelected: " + date);
            Log.d("TAG", "onDateSelected: " + day);
            Log.d("TAG", "onDateSelected: " + month);
            Log.d("TAG", "onDateSelected: " + year);
            selectedDate = new CustomDate(day, month + 1, year);
        });
        binding.lytDatePicker.tvCancel.setOnClickListener(v -> binding.lytDatePicker.lytDatePicker.setVisibility(View.GONE));
        binding.lytDatePicker.tvConfirm.setOnClickListener(v -> {
            binding.lytDatePicker.lytDatePicker.setVisibility(View.GONE);
            if (startDate.getDateForHuman() != null) {
                if (selectedType == 1) {
                    startDate = selectedDate;
                    binding.tvDate1.setText(startDate.getDateForHuman());
                } else {
                    endDate = selectedDate;
                    binding.tvDate2.setText(endDate.getDateForHuman());

                }
            }
            getRecordData(false);
        });

        binding.lytDate1.setOnClickListener(v -> {
            selectedType = 1;
            binding.lytDatePicker.lytDatePicker.setVisibility(View.VISIBLE);
        });
        binding.lytDate2.setOnClickListener(v -> {
            selectedType = 2;
            binding.lytDatePicker.lytDatePicker.setVisibility(View.VISIBLE);
        });

        binding.swipeRefresh.setOnRefreshListener((refreshLayout) -> {
            getRecordData(false);
        });
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
            getRecordData(true);
        });
    }

    private void getRecordData(boolean isLoadMore) {
        Log.d("TAG", "getRecordData: start " + startDate.getDateForServer());
        Log.d("TAG", "getRecordData: end " + endDate.getDateForServer());
        Log.d("TAG", "getRecordData: coinType " + coinType);


        myLoader.noData.set(false);
        if (isLoadMore) {
            start = start + Const.LIMIT;

        } else {
            start = 0;
            coinHistoryAdapter.clear();
            myLoader.isFristTimeLoading.set(true);
        }
        Call<HistoryListRoot> call;
        call = RetrofitBuilder.create().getCoinHostory(sessionManager.getUser().getId(), startDate.getDateForServer(), endDate.getDateForServer(), coinType, start, Const.LIMIT);
        call.enqueue(new Callback<HistoryListRoot>() {
            @Override
            public void onResponse(Call<HistoryListRoot> call, Response<HistoryListRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getHistory().isEmpty()) {
                        coinHistoryAdapter.addData(response.body().getHistory());
                        binding.tvIncome.setText(String.valueOf(response.body().getIncomeTotal()));
                        binding.tvOutcome.setText(String.valueOf(response.body().getOutgoingTotal()));
                    } else if (start == 0) {
                        myLoader.noData.set(true);
                    }
                }
                myLoader.isFristTimeLoading.set(false);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();

            }

            @Override
            public void onFailure(Call<HistoryListRoot> call, Throwable t) {

            }
        });
    }


}