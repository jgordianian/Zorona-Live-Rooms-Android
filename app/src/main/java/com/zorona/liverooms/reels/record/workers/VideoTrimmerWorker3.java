package com.zorona.liverooms.reels.record.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.zorona.liverooms.utils.VideoUtil;
import com.google.common.util.concurrent.ListenableFuture;
import com.otaliastudios.transcoder.Transcoder;
import com.otaliastudios.transcoder.TranscoderListener;
import com.otaliastudios.transcoder.TranscoderOptions;
import com.otaliastudios.transcoder.source.ClipDataSource;
import com.otaliastudios.transcoder.strategy.DefaultVideoStrategies;

import java.io.File;
import java.util.concurrent.TimeUnit;


public class VideoTrimmerWorker3 extends ListenableWorker {

    public static final String KEY_INPUT = "input";
    public static final String KEY_OUTPUT = "output";
    public static final String KEY_START = "start";
    public static final String KEY_END = "end";
    private static final String TAG = "VideoTrimmerWorker3";

    public VideoTrimmerWorker3(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        return CallbackToFutureAdapter.getFuture(completer -> {
            doActualWork(completer);
            return null;
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void doActualWork(CallbackToFutureAdapter.Completer<Result> completer) {
        File input = new File(getInputData().getString(KEY_INPUT));
        File output = new File(getInputData().getString(KEY_OUTPUT));
        long start = getInputData().getLong(KEY_START, 0);
        long end = getInputData().getLong(KEY_END, 0);
        TranscoderOptions.Builder transcoder = Transcoder.into(output.getAbsolutePath());
        transcoder.addDataSource(
                new ClipDataSource(
                        VideoUtil.createDataSource(getApplicationContext(), input.getAbsolutePath()),
                        TimeUnit.MILLISECONDS.toMicros(start),
                        TimeUnit.MILLISECONDS.toMicros(end)));
        transcoder.setListener(new TranscoderListener() {

            @Override
            public void onTranscodeProgress(double progress) {
            }

            @Override
            public void onTranscodeCompleted(int code) {
                Log.d(TAG, "Trimming video has finished.");
                completer.set(Result.success());
                if (!input.delete()) {
                    Log.w(TAG, "Could not delete video file: " + input);
                }
            }

            @Override
            public void onTranscodeCanceled() {
                Log.d(TAG, "Trimming video was cancelled.");
                completer.setCancelled();
                if (!output.delete()) {
                    Log.w(TAG, "Could not delete failed output file: " + output);
                }
            }

            @Override
            public void onTranscodeFailed(@NonNull Throwable e) {
                Log.d(TAG, "Trimming video failed with error.", e);
                completer.setException(e);
                if (!output.delete()) {
                    Log.w(TAG, "Could not delete failed output file: " + output);
                }
            }
        });
        transcoder.setVideoTrackStrategy(
                DefaultVideoStrategies.for720x1280()).build();
        transcoder.transcode();
    }
}
