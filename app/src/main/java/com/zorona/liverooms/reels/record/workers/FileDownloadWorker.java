package com.app.liverooms.reels.record.workers;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.app.liverooms.R;
import com.app.liverooms.SharedConstants;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class FileDownloadWorker extends Worker {

    public static final String KEY_INPUT = "input";
    public static final String KEY_OUTPUT = "output";
    public static final String KEY_NOTIFICATION = "notification";
    private static final String TAG = "FileDownloadWorker";

    private final OkHttpClient mHttpClient = new OkHttpClient();

    public FileDownloadWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    private ForegroundInfo createForegroundInfo(Context context) {
        Notification notification =
                new NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
                        .setContentTitle(context.getString(R.string.notification_download_title))
                        .setTicker(context.getString(R.string.notification_download_title))
                        .setContentText(context.getString(R.string.notification_download_description))
                        .setSmallIcon(R.drawable.ic_baseline_save_alt_24)
                        .setOngoing(true)
                        .setOnlyAlertOnce(true)
                        .build();
        return new ForegroundInfo(SharedConstants.NOTIFICATION_DOWNLOAD, notification);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (getInputData().getBoolean(KEY_NOTIFICATION, false)) {
            setForegroundAsync(createForegroundInfo(getApplicationContext()));
        }

        String input = getInputData().getString(KEY_INPUT);
        //noinspection ConstantConditions
        File output = new File(getInputData().getString(KEY_OUTPUT));
        File temp = null;
        boolean success = false;
        try {
            temp = File.createTempFile("DLD", ".tmp");
            success = doActualWork(input, output, temp);
        } catch (Exception ignore) {
        }

        if (temp != null && !temp.delete()) {
            Log.w(TAG, "Could not delete temp file: " + temp);
        }

        if (!success && !output.delete()) {
            Log.w(TAG, "Could not delete failed output file: " + output);
        }

        return success ? Result.success() : Result.failure();
    }

    private boolean doActualWork(String url, File into, File temp) {
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = mHttpClient.newCall(request).execute();
        } catch (IOException e) {
            Log.e(TAG, "Failed when downloading " + url, e);
        }

        if (response != null && response.isSuccessful()) {
            try (BufferedSink out = Okio.buffer(Okio.sink(temp))) {
                out.writeAll(response.body().source());
                out.close();
                FileUtils.copyFile(temp, into);
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Failed when saving to " + into, e);
            } finally {

                if (temp != null && !temp.delete()) {
                    Log.w(TAG, "Could not delete temporary download: " + temp);
                }
            }
        }

        return false;
    }
}
