package com.app.liverooms.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;

import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.otaliastudios.transcoder.source.DataSource;
import com.otaliastudios.transcoder.source.FilePathDataSource;
import com.otaliastudios.transcoder.source.UriDataSource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;


final public class VideoUtil {

    private static final String TAG = "VideoUtil";

    public static DataSource createDataSource(Context context, String file) {
        if (file.startsWith(ContentResolver.SCHEME_CONTENT)) {
            return new UriDataSource(context, Uri.parse(file));
        }

        if (file.startsWith(ContentResolver.SCHEME_FILE)) {
            file = Uri.parse(file).getPath();
        }

        return new FilePathDataSource(file);
    }

  /*  public static OneTimeWorkRequest createDownloadRequest(Clip clip, File output, boolean notification) {
        Data data = new Data.Builder()
                .putString(FileDownloadWorker.KEY_INPUT, clip.video)
                .putString(FileDownloadWorker.KEY_OUTPUT, output.getAbsolutePath())
                .putBoolean(FileDownloadWorker.KEY_NOTIFICATION, notification)
                .build();
        return new OneTimeWorkRequest.Builder(FileDownloadWorker.class)
                .setInputData(data)
                .build();
    }

    public static OneTimeWorkRequest createWatermarkRequest(Clip clip, File input, File output, boolean notification) {
        Data data = new Data.Builder()
                .putString(WatermarkWorker.KEY_INPUT, input.getAbsolutePath())
                .putString(WatermarkWorker.KEY_USERNAME, "@" + clip.user.username)
                .putString(WatermarkWorker.KEY_OUTPUT, output.getAbsolutePath())
                .putBoolean(WatermarkWorker.KEY_NOTIFICATION, notification)
                .build();
        return new OneTimeWorkRequest.Builder(WatermarkWorker.class)
                .setInputData(data)
                .build();
    }*/


    @NotNull
    public static Size getDimensions(String path) throws IOException {
        int width = 0, height = 0;
        MediaMetadataRetriever mmr = null;
        try {
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            String w = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String h = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            if (w!=null && h!=null) {
                width = Integer.parseInt(w);
                height = Integer.parseInt(h);
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to extract thumbnail from " + path, e);
        } finally {
            if (mmr!=null) {
                mmr.release();
            }
        }

        return new Size(width, height);
    }

    public static long getDuration(Context context, Uri uri) throws IOException {
        MediaMetadataRetriever mmr = null;
        try {
            mmr = new MediaMetadataRetriever();
            if (TextUtils.equals(uri.getScheme(), ContentResolver.SCHEME_FILE)) {
                mmr.setDataSource(uri.getPath());
            } else {
                mmr.setDataSource(context, uri);
            }

            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (duration!=null) {
                return Long.parseLong(duration);
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to extract duration from " + uri, e);
        } finally {
            if (mmr!=null) {
                mmr.release();
            }
        }

        return 0;
    }

    @Nullable
    public static Bitmap getFrameAtTime(String path, long micros) throws IOException {
        MediaMetadataRetriever mmr = null;
        try {
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (duration!=null) {
                long millis = Long.parseLong(duration);
                if (micros > TimeUnit.MILLISECONDS.toMicros(millis)) {
                    return mmr.getFrameAtTime(TimeUnit.MILLISECONDS.toMicros(millis));
                }
                return mmr.getFrameAtTime(micros);
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to extract thumbnail from " + path, e);
        } finally {
            if (mmr!=null) {
                mmr.release();
            }
        }

        return null;
    }

    public static boolean hasTrack(String path, String type) throws IOException {
        Movie movie = MovieCreator.build(path);
        for (Track track : movie.getTracks()) {
            if (TextUtils.equals(track.getHandler(), type)) {
                return true;
            }
        }

        return false;
    }

    public static double getFileSizeInMB(File file) {
        DecimalFormat df = new DecimalFormat("###.##");
        df.setMaximumFractionDigits(2);
        double fileSizeInBytes = file.length();
        Log.d(TAG, "getFileSizeInbyte   " + fileSizeInBytes);
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        double fileSizeInKB = fileSizeInBytes / 1024;
        Log.d(TAG, "getFileSizeInKb   " + fileSizeInKB);
        //  Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        double fileSizeInMB = fileSizeInKB / 1024;
        Log.d(TAG, "getFileSizeInMb   " + fileSizeInMB);
        return Double.parseDouble(df.format(fileSizeInMB));
    }
}
