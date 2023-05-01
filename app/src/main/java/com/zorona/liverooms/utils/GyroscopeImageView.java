package com.zorona.liverooms.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

public class GyroscopeImageView extends AppCompatImageView {
    public double mAngleX;
    public double mAngleY;
    private float mLenX;
    private float mLenY;
    private float mOffsetX;
    private float mOffsetY;
    private double mScaleX;
    private double mScaleY;

    public GyroscopeImageView(Context context) {
        super(context);
        init();
    }

    public GyroscopeImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public GyroscopeImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        setScaleType(ImageView.ScaleType.CENTER);
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        super.setScaleType(ImageView.ScaleType.CENTER);
    }

    public void update(double d, double d2) {
        double d3 = d * 0.8999999761581421d;
        this.mScaleX = d3;
        double d4 = d2 * 0.8999999761581421d;
        this.mScaleY = d4;
        float f = (float) (((double) this.mLenX) * d3);
        this.mOffsetX = f;
        this.mOffsetY = (float) (((double) this.mLenY) * d4);
        setTranslationX(-f);
        setTranslationY(-this.mOffsetY);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int size = (View.MeasureSpec.getSize(i) - getPaddingLeft()) - getPaddingRight();
        int size2 = (View.MeasureSpec.getSize(i2) - getPaddingTop()) - getPaddingBottom();
        if (getDrawable() != null) {
            int intrinsicWidth = getDrawable().getIntrinsicWidth();
            int intrinsicHeight = getDrawable().getIntrinsicHeight();
            this.mLenX = Math.abs(((float) (intrinsicWidth - size)) * 0.08f);
            this.mLenY = Math.abs(((float) (intrinsicHeight - size2)) * 0.15f);
        }
    }
}
