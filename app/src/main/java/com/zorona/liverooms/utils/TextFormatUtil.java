package com.app.liverooms.utils;

import android.annotation.SuppressLint;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

final public class TextFormatUtil {

    private static final DateFormat mDateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public static Date toDate(String input) {
        try {
            return mDateFormat.parse(input);
        } catch (ParseException ignore) {
        }

        return null;
    }

    @NotNull
    @SuppressLint("DefaultLocale")
    public static String toShortNumber(long count) {
        if (count < 1000) {
            return count + "";
        }
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format(
                "%.1f %c",
                count / Math.pow(1000, exp),
                "kMGTPE".charAt(exp - 1)
        );
    }

    @NotNull
    public static String toMMSS(long millis) {
        long mm = TimeUnit.MILLISECONDS.toMinutes(millis)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        long ss = TimeUnit.MILLISECONDS.toSeconds(millis)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        return String.format(Locale.US, "%02d:%02d", mm, ss);
    }
}
