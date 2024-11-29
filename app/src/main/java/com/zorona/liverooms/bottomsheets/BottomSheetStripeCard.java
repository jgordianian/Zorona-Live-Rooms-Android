package com.app.liverooms.bottomsheets;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.app.liverooms.R;
import com.app.liverooms.databinding.BottomSheetCardBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

public class BottomSheetStripeCard {
    private final BottomSheetDialog bottomSheetDialog2;
    private final BottomSheetCardBinding bottomSheetCardBinding;

    public BottomSheetStripeCard(Context context, OnStripeCardFillLister onStripeCardFillLister) {

        bottomSheetDialog2 = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog2.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetCardBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_card, null, false);
        bottomSheetDialog2.setContentView(bottomSheetCardBinding.getRoot());
        bottomSheetCardBinding.cardInputWidget.setPostalCodeEnabled(false);
        bottomSheetCardBinding.cardInputWidget.setPostalCodeRequired(false);
        bottomSheetCardBinding.btnclose.setOnClickListener(v -> bottomSheetDialog2.dismiss());

        bottomSheetCardBinding.btnPay.setOnClickListener(v -> {
            bottomSheetDialog2.dismiss();

            CardInputWidget cardInputWidget = bottomSheetCardBinding.cardInputWidget;
            cardInputWidget.setPostalCodeRequired(false);
            cardInputWidget.setPostalCodeEnabled(false);

            onStripeCardFillLister.onPayButtonClick(cardInputWidget.getPaymentMethodCreateParams());
        });
        bottomSheetDialog2.show();

    }

    public interface OnStripeCardFillLister {
        void onPayButtonClick(PaymentMethodCreateParams paymentMethodCreateParams);
    }
}
