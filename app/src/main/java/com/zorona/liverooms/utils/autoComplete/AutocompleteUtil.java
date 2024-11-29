package com.app.liverooms.utils.autoComplete;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.widget.EditText;

import com.app.liverooms.R;
import com.app.liverooms.modelclass.GuestProfileRoot;
import com.app.liverooms.modelclass.HeshtagsRoot;
import com.google.android.material.color.MaterialColors;
import com.otaliastudios.autocomplete.Autocomplete;
import com.otaliastudios.autocomplete.AutocompleteCallback;
import com.otaliastudios.autocomplete.CharPolicy;


final public class AutocompleteUtil {

    public static void setupForHashtags(Context context, EditText input) {
        int color = MaterialColors.getColor(context, R.attr.colorSurface, Color.BLACK);
        Autocomplete.<HeshtagsRoot.HashtagItem>on(input)
                .with(5)
                .with(new ColorDrawable(color))
                .with(new CharPolicy('#'))
                .with(new HashtagPresenter(context))
                .with(new AutocompleteCallback<HeshtagsRoot.HashtagItem>() {

                    @Override
                    public boolean onPopupItemClicked(Editable editable, HeshtagsRoot.HashtagItem item) {
                        int[] range = CharPolicy.getQueryRange(editable);
                        if (range == null) {
                            return false;
                        }

                        editable.replace(range[0], range[1], item.getHashtag());
                        return true;
                    }

                    @Override
                    public void onPopupVisibilityChanged(boolean shown) {
                    }
                })
                .build();
    }

    public static void setupForUsers(Context context, EditText input) {
        int color = MaterialColors.getColor(context, R.attr.colorSurface, Color.BLACK);
        Autocomplete.<GuestProfileRoot.User>on(input)
                .with(5)
                .with(new ColorDrawable(color))
                .with(new CharPolicy('@'))
                .with(new UserPresenter(context))
                .with(new AutocompleteCallback<GuestProfileRoot.User>() {

                    @Override
                    public boolean onPopupItemClicked(Editable editable, GuestProfileRoot.User item) {
                        int[] range = CharPolicy.getQueryRange(editable);
                        if (range == null) {
                            return false;
                        }

                        editable.replace(range[0], range[1], item.getUsername());
                        return true;
                    }

                    @Override
                    public void onPopupVisibilityChanged(boolean shown) {
                    }
                })
                .build();
    }
}
