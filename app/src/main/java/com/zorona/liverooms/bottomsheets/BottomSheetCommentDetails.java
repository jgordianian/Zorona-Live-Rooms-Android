package com.zorona.liverooms.bottomsheets;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.zorona.liverooms.R;
import com.zorona.liverooms.SessionManager;
import com.zorona.liverooms.databinding.BottomSheetMessagedetailBinding;
import com.zorona.liverooms.modelclass.PostCommentRoot;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;


public class BottomSheetCommentDetails {


    private final BottomSheetDialog bottomSheetDialog;
    private final Context context;
    SessionManager sessionManager;
    public BottomSheetCommentDetails(Context context, PostCommentRoot.CommentsItem comment, OnCommentDetailClickLister onCommentDetailClickLister) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        this.context = context;
        sessionManager = new SessionManager(context);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        BottomSheetMessagedetailBinding sheetDilogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_messagedetail, null, false);
        bottomSheetDialog.setContentView(sheetDilogBinding.getRoot());
        bottomSheetDialog.show();

        sheetDilogBinding.tvTime.setText(comment.getTime());
        if (comment.getUserId().equals(sessionManager.getUser().getId())) {
            sheetDilogBinding.tvUnsend.setVisibility(View.VISIBLE);
        } else {
            sheetDilogBinding.tvUnsend.setVisibility(View.GONE);
        }
        sheetDilogBinding.tvCopy.setOnClickListener(v -> {
            ClipboardManager manager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", comment.getComment());
            if (manager != null) {
                manager.setPrimaryClip(clipData);
                Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show();
            }
        });
        sheetDilogBinding.tvUnsend.setOnClickListener(v -> {
            onCommentDetailClickLister.onClickUnsend();
            bottomSheetDialog.dismiss();
        });

    }

    public interface OnCommentDetailClickLister {
        void onClickUnsend();
    }
}
