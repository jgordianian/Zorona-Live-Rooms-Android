package com.zorona.liverooms.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zorona.liverooms.MainApplication;
import com.zorona.liverooms.R;
import com.zorona.liverooms.databinding.ItemUserprofileImageviewBinding;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;

public class UserProfileImageView extends RelativeLayout {

    ItemUserprofileImageviewBinding binding;


    public UserProfileImageView(Context context) {
        super(context);
        init();
    }

    public UserProfileImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }


    public UserProfileImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public UserProfileImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BackColor);
        int mColor = a.getColor(R.styleable.BackColor_backColor, getContext().getColor(R.color.transparent));
        ColorStateList color = a.getColorStateList(R.styleable.BackColor_backColor);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.item_userprofile_imageview, null, false);

        binding.imguser.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_round_pink));
        binding.imguser.setBackgroundTintList(color);
        addView(binding.getRoot());
    }

    private void init() {

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.item_userprofile_imageview, null, false);
        addView(binding.getRoot());

    }

    public void setUserImage(String imageUrl) {
        if (binding != null) {
            Glide.with(this).load(imageUrl)
                    .apply(MainApplication.requestOptions)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d("TAG", "onLoadFailed: " + e.getMessage());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).circleCrop().into(binding.imguser);
            binding.imgvip.setVisibility(GONE);
        }

    }

    public void setUserImage(String imageUrl, boolean isVip) {
        if (binding != null) {
            Glide.with(this).load(imageUrl)
                    .apply(MainApplication.requestOptions)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d("TAG", "onLoadFailed: " + e.getMessage());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("TAG", "onResourceReady: " + imageUrl);
                            return false;
                        }
                    }).circleCrop().into(binding.imguser);
            if (isVip) {
                binding.imgvip.setVisibility(VISIBLE);
            } else {
                binding.imgvip.setVisibility(GONE);
            }
        }
    }
}
