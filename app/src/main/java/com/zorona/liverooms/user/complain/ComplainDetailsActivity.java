package com.zorona.liverooms.user.complain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.zorona.liverooms.BuildConfig;
import com.zorona.liverooms.MainApplication;
import com.zorona.liverooms.R;
import com.zorona.liverooms.activity.BaseActivity;
import com.zorona.liverooms.databinding.ActivityComplainDetailsBinding;
import com.zorona.liverooms.modelclass.ComplainRoot;
import com.google.gson.Gson;

public class ComplainDetailsActivity extends BaseActivity {
    ActivityComplainDetailsBinding binding;
    private ComplainRoot.ComplainItem tickit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complain_details);


        Intent intent = getIntent();
        String datastr = intent.getStringExtra("tickit");
        if (datastr != null) {
            tickit = new Gson().fromJson(datastr, ComplainRoot.ComplainItem.class);
            if (tickit != null) {
                setData();
            }
        }
    }

    private void setData() {
        binding.tvTitle.setText(tickit.getContact());
        binding.tvDescription.setText(tickit.getMessage());

        binding.tvtime.setText(tickit.getCreatedAt());
        if (tickit.isSolved()) {
            binding.status.setText("SOLVED");
        } else {
            binding.status.setText("OPEN");
            binding.status.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        }
        if (tickit.getImage().equals("")) {
            binding.imageview.setVisibility(View.GONE);
            binding.tvImage.setVisibility(View.GONE);
        } else {

            Glide.with(this).load(BuildConfig.BASE_URL + tickit.getImage())
                    .apply(MainApplication.requestOptions)

                    .into(binding.imageview);
        }
    }

}