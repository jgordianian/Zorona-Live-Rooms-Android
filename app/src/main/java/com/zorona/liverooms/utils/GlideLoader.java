package com.zorona.liverooms.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.zorona.liverooms.R;

import java.io.File;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class GlideLoader {
    private Context mContext;
    private CircularProgressDrawable circularProgressDrawable;

    public GlideLoader(Context context) {
        this.mContext = context;
        circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(20f);
        circularProgressDrawable.setColorSchemeColors(
                getColor(R.color.napier_green),
                getColor(R.color.india_red),
                getColor(R.color.kellygreen),
                getColor(R.color.tufs_blue),
                getColor(R.color.tiffanyblue),
                getColor(R.color.Sanddtorm),
                getColor(R.color.salmonpink_1)
        );
        circularProgressDrawable.start();
    }

    void loadWithCircleCrop(String imageUrl, ImageView imageView) {
        if (mContext != null && imageView != null) {
            Glide.with(mContext).load(imageUrl).apply(
                    new RequestOptions().circleCrop().error(
                            R.color.transparent
                    ).priority(Priority.HIGH)
            ).into(imageView);
        }
    }

    void loadImage(String imageUrl, ImageView imageView) {
        if (mContext != null && imageView != null) {
            Glide.with(mContext).load(imageUrl).apply(
                    new RequestOptions().error(
                            R.color.transparent
                    ).priority(Priority.HIGH)
            ).into(imageView);
        }
    }

    void loadNotificationImage(Drawable drawable, ImageView imageView) {
        if (imageView != null && drawable != null) {
            imageView.setImageDrawable(drawable);
        }

    }

    void loadMediaImage(String imageUrl, ImageView imageView) {
        if (mContext != null && imageView != null) {

            Glide.with(mContext).load(new File(imageUrl)).apply(
                    new RequestOptions()
                            .placeholder(circularProgressDrawable).error(
                            R.color.transparent
                    ).priority(Priority.HIGH)
            ).into(imageView);
        }
    }

    void loadMediaRoundImage(String imageUrl, ImageView imageView) {
        if (mContext != null && imageView != null) {
            Glide.with(mContext).load(new File(imageUrl)).apply(
                    new RequestOptions().circleCrop().placeholder(circularProgressDrawable).error(
                            R.color.transparent
                    ).priority(Priority.HIGH)
            ).into(imageView);
        }
    }

    void loadMediaRoundBitmap(Bitmap bitmap, ImageView imageView) {
        if (mContext != null && imageView != null) {
            Glide.with(mContext).load(bitmap).apply(
                    new RequestOptions().circleCrop().placeholder(circularProgressDrawable).error(
                            R.color.transparent
                    ).priority(Priority.HIGH)
            ).into(imageView);
        }
    }

    void loadBlurImage(String imageUrl, ImageView imageView) {
        if (mContext != null && imageView != null) {
            Glide.with(mContext).load(imageUrl)
                    .transform(new BlurTransformation())
                    .apply(
                            new RequestOptions().error(
                                    R.color.transparent
                            ).priority(Priority.HIGH)
                    )
                    .into(imageView);
        }
    }

    public void loadRoundDrawable(Drawable imageUrl, ImageView imageView) {
        if (mContext != null && imageView != null) {
            Glide.with(mContext).load(imageUrl).apply(
                    new RequestOptions().circleCrop().placeholder(circularProgressDrawable).error(
                            R.color.transparent
                    ).priority(Priority.HIGH)
            ).into(imageView);
        }
    }

    private int getColor(int color) {
        return ContextCompat.getColor(mContext, color);
    }
}
