package com.app.liverooms.reels.record.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.otaliastudios.transcoder.Transcoder;
import com.otaliastudios.transcoder.TranscoderListener;
import com.otaliastudios.transcoder.TranscoderOptions;
import com.otaliastudios.transcoder.strategy.DefaultVideoStrategies;

import java.io.File;

public class VideoSpeedWorker2 extends ListenableWorker {

    public static final String KEY_INPUT = "input";
    public static final String KEY_OUTPUT = "output";
    public static final String KEY_SPEED = "speed";
    private static final String TAG = "VideoSpeedWorker2";

    public VideoSpeedWorker2(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public ListenableFuture<Result> startWork() {
        File input = new File(getInputData().getString(KEY_INPUT));
        File output = new File(getInputData().getString(KEY_OUTPUT));
        float speed = getInputData().getFloat(KEY_SPEED, 1f);
        return CallbackToFutureAdapter.getFuture(completer -> {
            doActualWork(input, output, speed, completer);
            return null;
        });
    }

    private void doActualWork(File input, File output, float speed, CallbackToFutureAdapter.Completer<Result> completer) {
        TranscoderOptions.Builder transcoder = Transcoder.into(output.getAbsolutePath());
        transcoder.addDataSource(input.getAbsolutePath());
        transcoder.setListener(new TranscoderListener() {

            @Override
            public void onTranscodeProgress(double progress) {
            }

            @Override
            public void onTranscodeCompleted(int code) {
                Log.d(TAG, "Applying video speed has finished.");
                completer.set(Result.success());
                if (!input.delete()) {
                    Log.w(TAG, "Could not delete input file: " + input);
                }
            }

            @Override
            public void onTranscodeCanceled() {
                Log.d(TAG, "Applying video speed was cancelled.");
                completer.setCancelled();
                if (!input.delete()) {
                    Log.w(TAG, "Could not delete input file: " + input);
                }

                if (!output.delete()) {
                    Log.w(TAG, "Could not delete failed output file: " + output);
                }
            }

            @Override
            public void onTranscodeFailed(@NonNull Throwable e) {
                Log.d(TAG, "Applying video speed failed with error.", e);
                completer.setException(e);
                if (!input.delete()) {
                    Log.w(TAG, "Could not delete input file: " + input);
                }

                if (!output.delete()) {
                    Log.w(TAG, "Could not delete failed output file: " + output);
                }
            }
        });
        transcoder.setSpeed(speed);
        transcoder.setVideoTrackStrategy(
                DefaultVideoStrategies.for720x1280()).build();
        transcoder.transcode();
    }
}
