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
import com.otaliastudios.transcoder.engine.TrackType;
import com.otaliastudios.transcoder.source.BlankAudioDataSource;
import com.otaliastudios.transcoder.source.ClipDataSource;
import com.otaliastudios.transcoder.source.DataSource;
import com.otaliastudios.transcoder.strategy.DefaultVideoStrategies;

import java.io.File;
import java.util.concurrent.TimeUnit;


public class PanAudioWorker extends ListenableWorker {

    public static final String KEY_AUDIO = "audio";
    public static final String KEY_DELAY = "delay";
    public static final String KEY_OUTPUT = "output";
    public static final String KEY_VIDEO = "video";
    private static final String TAG = "PanAudioWorker";

    public PanAudioWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public ListenableFuture<Result> startWork() {
        File video = new File(getInputData().getString(KEY_VIDEO));
        File audio = new File(getInputData().getString(KEY_AUDIO));
        long delay = getInputData().getLong(KEY_DELAY, 0);
        File output = new File(getInputData().getString(KEY_OUTPUT));
        return CallbackToFutureAdapter.getFuture(completer -> {
            doActualWork(video, audio, delay, output, completer);
            return null;
        });
    }

    private void doActualWork(File video, File audio, long delay, File output, CallbackToFutureAdapter.Completer<Result> completer) {
        TranscoderOptions.Builder transcoder = Transcoder.into(output.getAbsolutePath());
        DataSource audio2 =
                VideoUtil.createDataSource(getApplicationContext(), audio.getAbsolutePath());
        long duration1 = TimeUnit.MICROSECONDS.toMillis(audio2.getDurationUs());
        if (delay > 0) {
            transcoder.addDataSource(TrackType.AUDIO,
                    new BlankAudioDataSource(TimeUnit.MILLISECONDS.toMicros(delay)));
            transcoder.addDataSource(TrackType.AUDIO,
                    new ClipDataSource(
                            audio2,
                            0,
                            TimeUnit.MILLISECONDS.toMicros(duration1 - delay)));
        } else {
            transcoder.addDataSource(TrackType.AUDIO,
                    new ClipDataSource(
                            audio2,
                            TimeUnit.MILLISECONDS.toMicros(Math.abs(delay)),
                            TimeUnit.MILLISECONDS.toMicros(duration1)));
            transcoder.addDataSource(TrackType.AUDIO,
                    new BlankAudioDataSource(TimeUnit.MILLISECONDS.toMicros(Math.abs(delay))));
        }

        transcoder.addDataSource(TrackType.VIDEO, video.getAbsolutePath());
        transcoder.setListener(new TranscoderListener() {

            @Override
            public void onTranscodeProgress(double progress) {
            }

            @Override
            public void onTranscodeCompleted(int code) {
                Log.d(TAG, "Merging audio/video has finished.");
                completer.set(Result.success());
                if (!video.delete()) {
                    Log.w(TAG, "Could not delete video file: " + video);
                }

                if (!audio.delete()) {
                    Log.w(TAG, "Could not delete audio file: " + audio);
                }
            }

            @Override
            public void onTranscodeCanceled() {
                Log.d(TAG, "Merging audio/video was cancelled.");
                completer.setCancelled();
                if (!output.delete()) {
                    Log.w(TAG, "Could not delete failed output file: " + output);
                }
            }

            @Override
            public void onTranscodeFailed(@NonNull Throwable e) {
                Log.d(TAG, "Merging audio/video failed with error.", e);
                completer.setException(e);
                if (!output.delete()) {
                    Log.w(TAG, "Could not delete failed output file: " + output);
                }
            }
        });
        transcoder.setVideoTrackStrategy(DefaultVideoStrategies.for720x1280());
        transcoder.transcode();
    }
}
