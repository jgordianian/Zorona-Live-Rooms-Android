package com.app.liverooms.reels.record;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class DiffUtilCallback<T> extends DiffUtil.ItemCallback<T> {

    private final Selector<T> mSelector;

    public DiffUtilCallback(Selector<T> selector) {
        mSelector = selector;
    }

    @Override
    public boolean areContentsTheSame(@NonNull T a, @NonNull T b) {
        Object x = mSelector.key(a);
        Object y = mSelector.key(b);
        if (x instanceof Integer) {
            return y instanceof Integer && (int) x==(int) y;
        }
        return TextUtils.equals((String) x, (String) y);
    }

    @Override
    public boolean areItemsTheSame(@NonNull T a, @NonNull T b) {
        return mSelector.key(a)==mSelector.key(b);
    }

    public interface Selector<T> {

        Object key(T o);
    }
}
