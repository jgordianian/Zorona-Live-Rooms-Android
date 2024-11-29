package com.app.liverooms.bottomsheets;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;

import com.app.liverooms.R;
import com.app.liverooms.activity.LoginActivityActivity;
import com.app.liverooms.databinding.BottomSheetGenderBinding;
import com.app.liverooms.modelclass.RestResponse;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.retrofit.RetrofitBuilder;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BottomSheetGender_g {

    private final BottomSheetDialog bottomSheetDialog;
    private final BottomSheetGenderBinding sheetDilogBinding;
    ObservableBoolean isValidUserName = new ObservableBoolean(false);
    private Context context;
    private String selected = "";
    private boolean submitButtonEnable = false;

    public BottomSheetGender_g(LoginActivityActivity.LoginType loginType, String gender, String userName, String image, Context context, OnGenderSelectListner onReportedListner) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        this.context = context;
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        selected = gender;

        sheetDilogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_gender, null, false);
        bottomSheetDialog.setContentView(sheetDilogBinding.getRoot());
        bottomSheetDialog.show();
        sheetDilogBinding.pd.setVisibility(View.GONE);
        if (!gender.isEmpty()) {
            sheetDilogBinding.lytGender.setVisibility(View.GONE);
        }
        if (userName.isEmpty()) {
            sheetDilogBinding.lytUserName.setVisibility(View.VISIBLE);
        } else {
            sheetDilogBinding.lytUserName.setVisibility(View.GONE);
        }
        if (loginType==LoginActivityActivity.LoginType.quick) {
            sheetDilogBinding.lytname.setVisibility(View.VISIBLE);
        } else {
            sheetDilogBinding.lytname.setVisibility(View.GONE);
        }

        sheetDilogBinding.male.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_stok_round));
        sheetDilogBinding.female.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_stok_round));
        sheetDilogBinding.male.setOnClickListener(v -> {
            selected = Const.MALE;
            sheetDilogBinding.male.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_stok_round_pink));
            sheetDilogBinding.female.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_stok_round));
        });
        sheetDilogBinding.female.setOnClickListener(v -> {
            selected = Const.FEMALE;
            sheetDilogBinding.female.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_stok_round_pink));
            sheetDilogBinding.male.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_stok_round));
        });

        sheetDilogBinding.etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkValidation(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (isValidUserName.get()) {
            sheetDilogBinding.btnSubmit.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.graylight));
            sheetDilogBinding.btnSubmit.setEnabled(false);
        } else {
            sheetDilogBinding.btnSubmit.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.pink));
            sheetDilogBinding.btnSubmit.setEnabled(true);
        }

        sheetDilogBinding.btnSubmit.setOnClickListener(v -> {
            String name = sheetDilogBinding.etName.getText().toString();
            if (loginType==LoginActivityActivity.LoginType.quick) {
                if (name.isEmpty()) {
                    Toast.makeText(context, "Enter Name First", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (selected.isEmpty()) {
                Toast.makeText(context, "Select Gender First", Toast.LENGTH_SHORT).show();
                return;
            }
            onReportedListner.onSelect(selected, name, sheetDilogBinding.etUserName.getText().toString().trim());
            bottomSheetDialog.dismiss();
        });

    }

    private void checkValidation(String toString) {

        sheetDilogBinding.pd.setVisibility(View.VISIBLE);
        sheetDilogBinding.btnSubmit.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.graylight));
        Call<RestResponse> call = RetrofitBuilder.create().checkUserName(toString, "");
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code()==200) {
                    if (!response.body().isStatus()) {
                        sheetDilogBinding.etUserName.setError("Username already taken");
                        isValidUserName.set(false);
                    } else {
                        isValidUserName.set(true);
                    }
                }
                sheetDilogBinding.pd.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {

            }
        });
    }

    public interface OnGenderSelectListner {
        void onSelect(String g, String name, String username);
        // void onCancel();
    }
}
