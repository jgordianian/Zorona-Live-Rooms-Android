package com.zorona.liverooms.reels.record;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.zorona.liverooms.R;
import com.zorona.liverooms.activity.BaseActivity;
import com.zorona.liverooms.databinding.ActivityPanAudioBinding;
import com.zorona.liverooms.dilog.CustomDialogClass;
import com.zorona.liverooms.reels.record.workers.PanAudioWorker;
import com.zorona.liverooms.reels.record.workers.SplitAudioWorker;
import com.zorona.liverooms.utils.TempUtil;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.material.slider.Slider;

import java.io.File;
import java.io.IOException;
import java.util.Locale;


public class PanAudioActivity extends BaseActivity implements AnalyticsListener {

    public static final String EXTRA_SONG = "song";
    public static final String EXTRA_VIDEO = "video";
    private static final String TAG = "PanAudioActivity";
    private final Handler mHandler = new Handler();
    private String mAudio;
    private boolean mDeleteOnExit = true;
    private PanAudioActivityViewModel mModel;
    private SimpleExoPlayer mPlayer1;
    private MediaPlayer mPlayer2;
    private final Runnable mHandlerCallback = this::stopAudioPlayer;
    private String mSong;
    private String mVideo;
    ActivityPanAudioBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pan_audio);


        mModel = new ViewModelProvider(this).get(PanAudioActivityViewModel.class);
        mSong = getIntent().getStringExtra(EXTRA_SONG);
        mVideo = getIntent().getStringExtra(EXTRA_VIDEO);
        mPlayer1 = new SimpleExoPlayer.Builder(this).build();
        mPlayer1.addAnalyticsListener(this);
        mPlayer1.setVolume(0);
        PlayerView player = findViewById(R.id.player);
        player.setPlayer(mPlayer1);
        Slider delay = findViewById(R.id.delay);
        delay.setLabelFormatter(value ->
                String.format(Locale.US, "%dms", (int) value - 2500));
        delay.setValue(mModel.delay.getValue());
        delay.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {

            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                mModel.delay.postValue(slider.getValue());
            }
        });
        View overlay = findViewById(R.id.overlay);
        overlay.setOnClickListener(v -> {
            if (mPlayer1.getPlayWhenReady()) {
                mPlayer1.setPlayWhenReady(false);
            } else {
                startVideoPlayer();
            }
        });
        mModel.delay.observe(this, volume -> {
            stopVideoPlayer();
            if (mModel.split) {
                mHandler.postDelayed(this::startVideoPlayer, 250);
            }
        });
        submitForSplit();


        binding.btnDone.setOnClickListener(v -> submitForAdjustment());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopVideoPlayer();
        mPlayer1.release();
        File audio = new File(mAudio);
        if (!audio.delete()) {
            Log.w(TAG, "Could not delete temporary audio: " + audio);
        }

        File video = new File(mVideo);
        if (mDeleteOnExit && !video.delete()) {
            Log.w(TAG, "Could not delete input video: " + video);
        }
    }

    @Override
    public void onIsPlayingChanged(@Nullable EventTime time, boolean playing) {
        if (playing) {
            startAudioPlayer();
        } else {
            stopAudioPlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopVideoPlayer();
    }

    @Override
    public void onPlayerStateChanged(@Nullable EventTime time, boolean ready, int state) {
        if (state==Player.STATE_ENDED) {
            mPlayer1.setPlayWhenReady(false);
            mPlayer1.seekTo(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mModel.split && mPlayer1.getPlaybackState()==Player.STATE_IDLE) {
            startVideoPlayer();
        }
    }

    private void proceedToFilter(File file) {
        Log.v(TAG, "Proceeding to filter screen with " + file);
        Intent intent = new Intent(this, FilterActivity.class);
        intent.putExtra(FilterActivity.EXTRA_SONG, mSong);
        intent.putExtra(FilterActivity.EXTRA_VIDEO, file.getAbsolutePath());
        startActivity(intent);
        finish();
    }

    private void setupAudio() {
        mPlayer2 = new MediaPlayer();
        try {
            mPlayer2.setDataSource(mAudio);
            mPlayer2.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Media player failed to initialize :?", e);
        }
    }

    private void startAudioPlayer() {
        if (mPlayer2==null || mPlayer2.isPlaying()) {
            return;
        }

        long delay = Math.round(mModel.delay.getValue()) - 2500;
        if (delay < 0) {
            mPlayer2.seekTo((int) Math.abs(delay));
            mPlayer2.start();
        } else if (delay > 0) {
            mPlayer2.seekTo(0);
            mHandler.postDelayed(() -> mPlayer2.start(), Math.abs(delay));
        } else {
            mPlayer2.seekTo(0);
            mPlayer2.start();
        }
    }

    private void startVideoPlayer() {
        DefaultDataSourceFactory factory =
                new DefaultDataSourceFactory(this, getString(R.string.app_name));
        ProgressiveMediaSource source = new ProgressiveMediaSource.Factory(factory)
                .createMediaSource(Uri.fromFile(new File(mVideo)));
        mPlayer1.setPlayWhenReady(true);
        mPlayer1.prepare(source, true, true);
    }

    private void stopAudioPlayer() {
        if (mPlayer2!=null && mPlayer2.isPlaying()) {
            mPlayer2.pause();
        }

        mHandler.removeCallbacks(mHandlerCallback);
    }

    private void stopVideoPlayer() {
        mModel.window = mPlayer1.getCurrentWindowIndex();
        mPlayer1.setPlayWhenReady(false);
        mPlayer1.stop();
    }

    private void submitForAdjustment() {
        long delay = Math.round(mModel.delay.getValue());
        if (delay==2500) {
            mDeleteOnExit = false;
            proceedToFilter(new File(mVideo));
            return;
        }

        File output = TempUtil.createNewFile(this, ".mp4");
        Data data = new Data.Builder()
                .putString(PanAudioWorker.KEY_VIDEO, mVideo)
                .putString(PanAudioWorker.KEY_AUDIO, mAudio)
                .putLong(PanAudioWorker.KEY_DELAY, delay - 2500)
                .putString(PanAudioWorker.KEY_OUTPUT, output.getAbsolutePath())
                .build();
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(PanAudioWorker.class)
                .setInputData(data)
                .build();
        WorkManager wm = WorkManager.getInstance(this);
        wm.enqueue(request);
        CustomDialogClass progress = new CustomDialogClass(this, R.style.customStyle);
        progress.setCancelable(false);
        progress.show();
        wm.getWorkInfoByIdLiveData(request.getId())
                .observe(this, info -> {
                    boolean ended = info.getState() == WorkInfo.State.CANCELLED
                            || info.getState() == WorkInfo.State.FAILED
                            || info.getState() == WorkInfo.State.SUCCEEDED;
                    if (ended) {
                        progress.dismiss();
                    }

                    if (info.getState() == WorkInfo.State.SUCCEEDED) {
                        proceedToFilter(output);
                    }
                });
    }

    private void submitForSplit() {
        mAudio = TempUtil.createNewFile(this, ".mp4").getAbsolutePath();
        Data data = new Data.Builder()
                .putString(SplitAudioWorker.KEY_INPUT, mVideo)
                .putString(SplitAudioWorker.KEY_OUTPUT, mAudio)
                .build();
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(SplitAudioWorker.class)
                .setInputData(data)
                .build();
        WorkManager wm = WorkManager.getInstance(this);
        wm.enqueue(request);
        CustomDialogClass progress = new CustomDialogClass(this, R.style.customStyle);
        progress.setCancelable(false);
        progress.show();
        wm.getWorkInfoByIdLiveData(request.getId())
                .observe(this, info -> {
                    boolean ended = info.getState() == WorkInfo.State.CANCELLED
                            || info.getState() == WorkInfo.State.FAILED
                            || info.getState() == WorkInfo.State.SUCCEEDED;
                    if (ended) {
                        progress.dismiss();
                    }

                    if (info.getState() == WorkInfo.State.SUCCEEDED) {
                        mModel.split = true;
                        setupAudio();
                        startVideoPlayer();
                    }
                });
    }

    public static class PanAudioActivityViewModel extends ViewModel {

        public MutableLiveData<Float> delay = new MutableLiveData<>(2500f);

        public boolean split;
        public int window;
    }
}
