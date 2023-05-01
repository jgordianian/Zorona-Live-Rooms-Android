package com.zorona.liverooms.bottomsheets;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.zorona.liverooms.R;
import com.zorona.liverooms.SessionManager;
import com.zorona.liverooms.databinding.BottomSheetReportBinding;
import com.zorona.liverooms.modelclass.RestResponse;
import com.zorona.liverooms.retrofit.RetrofitBuilder;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetReport_g {

    private final BottomSheetDialog bottomSheetDialog;
    private boolean submitButtonEnable = false;

    public BottomSheetReport_g(Context context, String onotherId, OnReportedListner onReportedListner) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });


        BottomSheetReportBinding sheetDilogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_report, null, false);
        bottomSheetDialog.setContentView(sheetDilogBinding.getRoot());
        bottomSheetDialog.show();

        sheetDilogBinding.btnclose.setOnClickListener(v -> bottomSheetDialog.dismiss());
        sheetDilogBinding.etDes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//ll
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    sheetDilogBinding.btnSubmit.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.graylight));
                    submitButtonEnable = false;
                } else {
                    sheetDilogBinding.btnSubmit.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.pink));
                    sheetDilogBinding.btnSubmit.setTextColor(ContextCompat.getColor(context, R.color.white));
                    submitButtonEnable = true;

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//ll
            }
        });
        sheetDilogBinding.btnSubmit.setOnClickListener(v -> {
            if (submitButtonEnable) {

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("toUserId", new SessionManager(context).getUser().getId());
                jsonObject.addProperty("fromUserId", onotherId);
                jsonObject.addProperty("description", sheetDilogBinding.etDes.getText().toString().trim());
                Call<RestResponse> call = RetrofitBuilder.create().reportThisUser(jsonObject);
                call.enqueue(new Callback<RestResponse>() {
                    @Override
                    public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                        if (response.code() == 200 && response.body().isStatus()) {

                            onReportedListner.onReported();
                            bottomSheetDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
                    }
                });
            }
        });

    }

    public interface OnReportedListner {
        void onReported();
    }
}
