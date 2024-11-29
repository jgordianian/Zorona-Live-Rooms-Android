package com.app.liverooms;

import android.widget.TextView;

import com.app.liverooms.R;
import com.app.liverooms.modelclass.StickerRoot;

import java.util.ArrayList;
import java.util.List;

public class RayziUtils {


    public static void incriseDecriseCount(TextView tvFollowrs, boolean isFollow) {
        if (!tvFollowrs.getText().toString().isEmpty()) {
            int followrs = Integer.parseInt(tvFollowrs.getText().toString());
            if (isFollow) {
                followrs = followrs + 1;
                tvFollowrs.setText(String.valueOf(followrs));
            } else {
                if (followrs > 0) {
                    followrs = followrs - 1;
                    tvFollowrs.setText(String.valueOf(followrs));
                }
            }
        }
    }


    public static String getTimeFromSeconds(int seconds) {
        int p1 = seconds % 60;
        int p2 = seconds / 60;
        int p3 = p2 % 60;
        p2 = p2 / 60;

        String sec;
        String hour;
        String min;
        if (p1 < 10) {
            sec = "0" + p1;
        } else {
            sec = String.valueOf(p1);
        }
        if (p2 < 10) {
            hour = "0" + p2;
        } else {
            hour = String.valueOf(p2);
        }
        if (p3 < 10) {
            min = "0" + p3;
        } else {
            min = String.valueOf(p3);
        }
        return hour + ":" + min + ":" + sec;

    }

    public static int getImageFromNumber(int count) {
        if (count == 10) {
            return R.drawable.x10;
        } else if (count == 9) {
            return R.drawable.x9;
        } else if (count == 8) {
            return R.drawable.x8;
        } else if (count == 7) {
            return R.drawable.x7;
        } else if (count == 6) {
            return R.drawable.x6;
        } else if (count == 5) {
            return R.drawable.x5;
        } else if (count == 4) {
            return R.drawable.x4;
        } else if (count == 3) {
            return R.drawable.x3;
        } else if (count == 2) {
            return R.drawable.x2;
        } else {
            return R.drawable.x1;
        }

    }

    private static List<StickerRoot.StickerItem> sticker = new ArrayList<>();

    public static List<StickerRoot.StickerItem> getSticker() {
        return sticker;
    }

    public static void setstickers(List<StickerRoot.StickerItem> sticker) {
        RayziUtils.sticker = sticker;
    }

    public enum Privacy {
        PUBLIC, FOLLOWRS, PRIVATE
    }

}
