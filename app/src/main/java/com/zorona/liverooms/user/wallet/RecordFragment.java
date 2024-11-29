package com.app.liverooms.user.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.app.liverooms.R;
import com.app.liverooms.activity.BaseFragment;
import com.app.liverooms.databinding.FragmentMyRecordBinding;
import com.app.liverooms.modelclass.CoinRecordRoot;
import com.app.liverooms.modelclass.CustomDate;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.retrofit.RetrofitBuilder;
import com.app.liverooms.utils.datepicker.utils.DateUtils;
import com.app.liverooms.utils.datepicker.view.datePicker.DatePicker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RecordFragment extends BaseFragment {


    FragmentMyRecordBinding binding;
    private BottomSheetDialog bottomSheetDialog;
    private DatePicker datePicker;
    private int selectedType = 1;
    private CustomDate selectedDate;
    private CustomDate startDate;
    private CustomDate endDate;

    //SessionManager sessionManager;
    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_record, container, false);
        //   sessionManager=new SessionManager(getActivity());
        initMain();
        initDatePiker();
        initListner();

        int day = DateUtils.getDay(DateUtils.getCurrentTime());
        int month = DateUtils.getMonth(DateUtils.getCurrentTime()) + 1;
        int year = DateUtils.getYear(DateUtils.getCurrentTime());

        selectedDate = new CustomDate(day, month, year);
        startDate = selectedDate;
        endDate = selectedDate;
        binding.tvDate1.setText(selectedDate.getDateForHuman());
        binding.tvDate2.setText(selectedDate.getDateForHuman());
        getRecordData();
        binding.lytDiamonds.setOnClickListener(v -> startActivity(new Intent(getActivity(), HistoryActivity.class)
                .putExtra(Const.TYPE, Const.DIAMOND).putExtra(Const.STARTDATE, new Gson().toJson(startDate)).putExtra(Const.ENDDATE, new Gson().toJson(endDate))));
        binding.lytRcoins.setOnClickListener(v -> startActivity(new Intent(getActivity(), HistoryActivity.class)
                .putExtra(Const.TYPE, Const.RCOINS).putExtra(Const.STARTDATE, new Gson().toJson(startDate)).putExtra(Const.ENDDATE, new Gson().toJson(endDate))));


        return binding.getRoot();
    }

    private void initDatePiker() {
        binding.lytDatePicker.lytDatePicker.setVisibility(View.GONE);
        datePicker = binding.lytDatePicker.datepicker;
        datePicker.setOffset(3);
        datePicker.setTextSize(19);
        datePicker.setPickerMode(DatePicker.DAY_ON_FIRST);
        datePicker.setDarkModeEnabled(true);
        datePicker.setMaxDate(DateUtils.getTimeMiles(2099, 12, 25));
        datePicker.setDate(DateUtils.getCurrentTime());
        datePicker.setMinDate(DateUtils.getTimeMiles(2022, 0, 1));
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
            getRecordData();
        });


    }

    private void getRecordData() {
        Log.d("TAG", "getRecordData: start " + startDate.getDateForServer());
        Log.d("TAG", "getRecordData: end " + endDate.getDateForServer());
        Call<CoinRecordRoot> call = RetrofitBuilder.create().getCoinRecord(sessionManager.getUser().getId(), startDate.getDateForServer(), endDate.getDateForServer());
        call.enqueue(new Callback<CoinRecordRoot>() {
            @Override
            public void onResponse(Call<CoinRecordRoot> call, Response<CoinRecordRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        if (response.body().getDiamond() != null) {
                            CoinRecordRoot.Diamond diamonds = response.body().getDiamond();
                            binding.tvDimondsIncome.setText(String.valueOf(diamonds.getIncome()));
                            binding.tvDiamondsOutcome.setText(String.valueOf(diamonds.getOutgoing()));
                        }
                        if (response.body().getRCoin() != null) {
                            CoinRecordRoot.RCoin rCoins = response.body().getRCoin();
                            binding.tvRcoinIncome.setText(String.valueOf(rCoins.getIncome()));
                            binding.tvRcoinOutcome.setText(String.valueOf(rCoins.getOutgoing()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CoinRecordRoot> call, Throwable t) {

            }
        });
    }

    private void initListner() {
        binding.lytDate1.setOnClickListener(v -> {
            selectedType = 1;
            binding.lytDatePicker.lytDatePicker.setVisibility(View.VISIBLE);
        });
        binding.lytDate2.setOnClickListener(v -> {
            selectedType = 2;
            binding.lytDatePicker.lytDatePicker.setVisibility(View.VISIBLE);
        });

    }

    private void initMain() {

        Random random = new Random();
        binding.tvRcoinIncome.setText(String.valueOf(random.nextInt(999)));
        binding.tvRcoinOutcome.setText(String.valueOf(random.nextInt(999)));
        binding.tvDimondsIncome.setText(String.valueOf(random.nextInt(999)));
        binding.tvDiamondsOutcome.setText(String.valueOf(random.nextInt(999)));
    }

}