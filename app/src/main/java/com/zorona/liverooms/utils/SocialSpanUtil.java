package com.app.liverooms.utils;

import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.regex.Pattern;


final public class SocialSpanUtil {

    private static final Pattern PATTERN_HASHTAG = Pattern.compile("#\\w+");
    private static final Pattern PATTERN_MENTION = Pattern.compile("@\\w[\\w.]+\\w");
    private static final Pattern PATTERN_URL = Pattern.compile("(?:(?:https?)://)(?:\\S+(?::\\S*)?@)?(?:(?!10(?:\\.\\d{1,3}){3})(?!127(?:\\.\\d{1,3}){3})(?!169\\.254(?:\\.\\d{1,3}){2})(?!192\\.168(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)*[a-z\\x{00a1}-\\x{ffff}0-9]+)(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)*[a-z\\x{00a1}-\\x{ffff}0-9]+)*(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}]{2,})))(?::\\d{2,5})?(?:/[^\\s]*)?");

    public static void apply(
            TextView into,
            @Nullable CharSequence text,
            @Nullable OnSocialLinkClickListener listener
    ) {
        SocialSpan span = new SocialSpan();
        if (listener!=null) {
            span.match(
                    new SocialSpan.SocialSpanPattern(
                            PATTERN_HASHTAG, null, listener::onSocialHashtagClick));
        } else {
            span.match(new SocialSpan.SocialSpanPattern(PATTERN_HASHTAG, null, null));
        }

        if (listener!=null) {
            span.match(
                    new SocialSpan.SocialSpanPattern(
                            PATTERN_MENTION, null, listener::onSocialMentionClick));
        } else {
            span.match(new SocialSpan.SocialSpanPattern(PATTERN_MENTION, null, null));
        }

        if (listener!=null) {
            span.match(
                    new SocialSpan.SocialSpanPattern(
                            PATTERN_URL, null, listener::onSocialUrlClick));
        } else {
            span.match(new SocialSpan.SocialSpanPattern(PATTERN_URL, null, null));
        }

        span.apply(into, text);
    }

    public interface OnSocialLinkClickListener {

        void onSocialHashtagClick(String hashtag);

        void onSocialMentionClick(String username);

        void onSocialUrlClick(String url);
    }
}
