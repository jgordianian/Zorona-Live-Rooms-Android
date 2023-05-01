package com.zorona.liverooms.utils;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocialSpan {

    private List<SocialSpanPattern> mPatterns = new ArrayList<>();

    public void apply(TextView into, @Nullable CharSequence text) {
        if (into instanceof EditText) {
            if (TextUtils.isEmpty(text)) {
                text = "";
            }

            into.setText(text, TextView.BufferType.SPANNABLE);
            into.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Spannable spannable = ((EditText) into).getText();
                    if (!TextUtils.isEmpty(spannable)) {
                        CharacterStyle[] spans =
                                spannable.getSpans(0, s.length(), CharacterStyle.class);
                        for (CharacterStyle span : spans) {
                            spannable.removeSpan(span);
                        }

                        match(spannable);
                    }
                }
            });
        } else {
            into.setText(match(text));
            into.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private SpannableStringBuilder match(@Nullable CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            text = "";
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        for (SocialSpanPattern pattern : mPatterns) {
            Matcher matcher = pattern.pattern.matcher(ssb);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                StyledClickableSpan span = new StyledClickableSpan(pattern);
                ssb.setSpan(span, start, end, 0);
            }
        }

        return ssb;
    }

    private void match(Spannable spannable) {
        for (SocialSpanPattern pattern : mPatterns) {
            Matcher matcher = pattern.pattern.matcher(spannable);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                StyledClickableSpan span = new StyledClickableSpan(pattern);
                spannable.setSpan(span, start, end, 0);
            }
        }
    }

    public void match(SocialSpanPattern pattern) {
        mPatterns.add(pattern);
    }

    public interface OnSpanClickListener {

        void onSpanClicked(String text);
    }

    public interface OnSpanStyleListener {

        void onDrawState(TextPaint ds);
    }

    private static class StyledClickableSpan extends ClickableSpan {

        private SocialSpanPattern mPattern;

        public StyledClickableSpan(SocialSpanPattern pattern) {
            mPattern = pattern;
        }

        @Override
        public void onClick(@NonNull View widget) {
            if (mPattern.listener2!=null) {
                TextView tv = (TextView) widget;
                Spanned span = (Spanned) tv.getText();
                int start = span.getSpanStart(this);
                int end = span.getSpanEnd(this);
                CharSequence text = span.subSequence(start, end);
                mPattern.listener2.onSpanClicked(text.toString());
            }

            widget.invalidate();
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            if (mPattern.listener1!=null) {
                mPattern.listener1.onDrawState(ds);
            }

            super.updateDrawState(ds);
        }
    }

    public static class SocialSpanPattern {

        public final Pattern pattern;
        @Nullable
        public final OnSpanStyleListener listener1;
        @Nullable
        public final OnSpanClickListener listener2;

        public SocialSpanPattern(
                Pattern pattern,
                @Nullable OnSpanStyleListener listener1,
                @Nullable OnSpanClickListener listener2
        ) {
            this.pattern = pattern;
            this.listener1 = listener1;
            this.listener2 = listener2;
        }
    }
}
