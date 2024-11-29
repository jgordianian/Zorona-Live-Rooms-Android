package com.app.liverooms.reels.record.workers;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.daasuu.mp4compose.VideoFormatMimeType;
import com.daasuu.mp4compose.composer.Mp4Composer;
import com.daasuu.mp4compose.filter.GlWatermarkFilter;
import com.app.liverooms.R;
import com.app.liverooms.SharedConstants;
import com.app.liverooms.utils.VideoUtil;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;


public class WatermarkWorker extends ListenableWorker {

    public static final String KEY_INPUT = "input";
    public static final String KEY_NOTIFICATION = "notification";
    public static final String KEY_OUTPUT = "output";
    public static final String KEY_USERNAME = "username";
    private static final String TAG = "WatermarkWorker";

    public WatermarkWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    private ForegroundInfo createForegroundInfo(Context context) {
        Notification notification =
                new NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
                        .setContentTitle(context.getString(R.string.notification_watermark_title))
                        .setTicker(context.getString(R.string.notification_watermark_title))
                        .setContentText(context.getString(R.string.notification_watermark_description))
                        .setSmallIcon(R.drawable.ic_baseline_movie_filter_24)
                        .setOngoing(true)
                        .setOnlyAlertOnce(true)
                        .build();
        return new ForegroundInfo(SharedConstants.NOTIFICATION_WATERMARK, notification);
    }

    @NonNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public ListenableFuture<Result> startWork() {
        File input = new File(getInputData().getString(KEY_INPUT));
        File output = new File(getInputData().getString(KEY_OUTPUT));
        String username = getInputData().getString(KEY_USERNAME);
        return CallbackToFutureAdapter.getFuture(completer -> {
            doActualWork(input, output, username, completer);
            return null;
        });
    }

    private GlWatermarkFilter createWatermark(Resources resources, int vw, String username) {
        Bitmap watermark =
                BitmapFactory.decodeResource(resources, R.drawable.logo_watermark);
        int ow = watermark.getWidth();
        int optimal = (int) (vw * .2);
        if (ow > optimal) {
            int oh = watermark.getHeight();
            float ratio = (float) ow / (float) oh;
            int fw = optimal;
            int fh = optimal;
            if (1 > ratio) {
                fw = (int) ((float) optimal * ratio);
            } else {
                fh = (int) ((float) optimal / ratio);
            }

            Bitmap scaled = Bitmap.createScaledBitmap(watermark, fw, fh, true);
            watermark.recycle();
            watermark = scaled;
        }

        int padding = resources.getDimensionPixelSize(R.dimen.watermark_username_padding);
        if (padding > 0) {
            int sw = watermark.getWidth() + (padding * 2);
            int sh = watermark.getHeight() + (padding * 2);
            Bitmap padded = Bitmap.createBitmap(sw, sh, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(padded);
            Paint paint = new Paint();
            paint.setFilterBitmap(true);
            canvas.drawBitmap(watermark, padding, padding, paint);
            watermark.recycle();
            watermark = padded;
        }

        GlWatermarkFilter.Position position;
        switch (resources.getInteger(R.integer.watermark_position)) {
            case 1:
                position = GlWatermarkFilter.Position.LEFT_TOP;
                break;
            case 2:
                position = GlWatermarkFilter.Position.RIGHT_TOP;
                break;
            case 3:
                position = GlWatermarkFilter.Position.RIGHT_BOTTOM;
                break;
            default:
                position = GlWatermarkFilter.Position.LEFT_BOTTOM;
                break;
        }
        if (resources.getBoolean(R.bool.watermark_username_enabled)) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(resources.getDimension(R.dimen.watermark_username_size));
            Rect rect = new Rect();
            paint.getTextBounds(username, 0, username.length(), rect);
            Size size = new Size(
                    rect.width() + (2 * padding),
                    rect.height() + (2 * padding)
            );
            size = new Size(
                    Math.max(size.getWidth(), watermark.getWidth()),
                    watermark.getHeight() + size.getHeight()
            );
            Bitmap combined =
                    Bitmap.createBitmap(size.getWidth(), size.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(combined);
            boolean right = position == GlWatermarkFilter.Position.RIGHT_TOP ||
                    position == GlWatermarkFilter.Position.RIGHT_BOTTOM;
            if (right) {
                canvas.drawBitmap(watermark, size.getWidth() - watermark.getWidth(), 0f, null);
            } else {
                canvas.drawBitmap(watermark, 0f, 0f, null);
            }

            paint.setColor(Color.BLACK);
            canvas.drawText(
                    username,
                    padding + 1,
                    watermark.getHeight() + padding + 1,
                    paint
            );
            paint.setColor(Color.WHITE);
            canvas.drawText(
                    username,
                    padding,
                    watermark.getHeight() + padding,
                    paint
            );
            watermark.recycle();
            watermark = combined;
        }

        return new GlWatermarkFilter(watermark, position);
    }

    private void doActualWork(File input, File output, String username, CallbackToFutureAdapter.Completer<Result> completer) throws IOException {
        Context context = getApplicationContext();
        if (getInputData().getBoolean(KEY_NOTIFICATION, false)) {
            setForegroundAsync(createForegroundInfo(context));
        }

        Size size = VideoUtil.getDimensions(input.getAbsolutePath());
        GlWatermarkFilter watermark =
                createWatermark(context.getResources(), size.getWidth(), username);
        Mp4Composer composer = new Mp4Composer(input.getAbsolutePath(), output.getAbsolutePath());
        composer.filter(watermark);
        composer.listener(new Mp4Composer.Listener() {

            @Override
            public void onProgress(double progress) {
            }

            @Override
            public void onCompleted() {
                Log.d(TAG, "MP4 composition has finished.");
                completer.set(Result.success());
                if (!input.delete()) {
                    Log.w(TAG, "Could not delete input file: " + output);
                }
            }

            @Override
            public void onCanceled() {
                Log.d(TAG, "MP4 composition was cancelled.");
                completer.setCancelled();
                if (!input.delete()) {
                    Log.w(TAG, "Could not delete input file: " + output);
                }

                if (!output.delete()) {
                    Log.w(TAG, "Could not delete failed output file: " + output);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.d(TAG, "MP4 composition failed with error.", e);
                completer.setException(e);
                if (!input.delete()) {
                    Log.w(TAG, "Could not delete input file: " + output);
                }

                if (!output.delete()) {
                    Log.w(TAG, "Could not delete failed output file: " + output);
                }
            }
        });
        composer.videoFormatMimeType(VideoFormatMimeType.AVC);
        composer.start();
    }
}
