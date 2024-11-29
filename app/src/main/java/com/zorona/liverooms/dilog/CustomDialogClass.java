package com.app.liverooms.dilog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.app.liverooms.R;


public class CustomDialogClass extends Dialog {

    public Dialog d;

    public CustomDialogClass(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.item_dailog);


    }


}