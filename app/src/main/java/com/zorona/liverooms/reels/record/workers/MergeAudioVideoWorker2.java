package com.zorona.liverooms.reels.record.workers;

import android.content.Context;
import android.net.Uri;
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
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class MergeAudioVideoWorker2 extends ListenableWorker {

    public static final String KEY_AUDIO = "audio";
    public static final String KEY_OUTPUT = "output";
    public static final String KEY_VIDEO = "video";
    private static final String TAG = "MergeAudioVideoWorker2";

    public MergeAudioVideoWorker2(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public ListenableFuture<Result> startWork() {
        String audio = getInputData().getString(KEY_AUDIO);
        File video = new File(getInputData().getString(KEY_VIDEO));
        File output = new File(getInputData().getString(KEY_OUTPUT));
        return CallbackToFutureAdapter.getFuture(completer -> {
            doActualWork(video, audio, output, completer);
            return null;
        });
    }

    private void doActualWork(File video, String audio, File output, CallbackToFutureAdapter.Completer<Result> completer) throws IOException {
        DataSource audio2 = VideoUtil.createDataSource(getApplicationContext(), audio);
        TranscoderOptions.Builder transcoder = Transcoder.into(output.getAbsolutePath());
        long duration1 = TimeUnit.MICROSECONDS.toMillis(audio2.getDurationUs());
        long duration2 =
                VideoUtil.getDuration(getApplicationContext(), Uri.fromFile(video));
        if (duration1 > duration2) {
            audio2 = new ClipDataSource(
                    audio2, 0, TimeUnit.MILLISECONDS.toMicros(duration2));
            transcoder.addDataSource(TrackType.AUDIO, audio2);
        } else {
            transcoder.addDataSource(TrackType.AUDIO, audio2);
            transcoder.addDataSource(TrackType.AUDIO,
                    new BlankAudioDataSource(
                            TimeUnit.MILLISECONDS.toMicros(duration2 - duration1)));
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
