package com.zorona.liverooms.reels.record;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.zorona.liverooms.R;
import com.zorona.liverooms.activity.BaseActivity;
import com.zorona.liverooms.dilog.CustomDialogClass;
import com.zorona.liverooms.reels.record.workers.MergeAudioVideoWorker2;
import com.zorona.liverooms.reels.record.workers.MergeAudiosWorker;
import com.zorona.liverooms.utils.TempUtil;
import com.zorona.liverooms.utils.VideoUtil;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.hmomeni.verticalslider.VerticalSlider;

import java.io.File;
import java.io.IOException;
import java.util.Locale;


public class VolumeActivity extends BaseActivity implements AnalyticsListener {

    public static final String EXTRA_AUDIO = "audio";
    public static final String EXTRA_SONG = "song";
    public static final String EXTRA_VIDEO = "video";

    private static final String TAG = "VolumeActivity";

    private final Handler mHandler = new Handler();
    private VolumeActivityViewModel mModel;
    private SimpleExoPlayer mPlayer1;
    private MediaPlayer mPlayer2;
    private final Runnable mHandlerCallback = this::stopAudioPlayer;
    private String mAudio;
    private String mSong;
    private String mVideo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume);

        TextView done = findViewById(R.id.imgDone);
//        done.setImageResource(R.drawable.ic_baseline_check_24);
        done.setOnClickListener(v -> submitForMerge());
        mModel = new ViewModelProvider(this).get(VolumeActivityViewModel.class);
        mAudio = getIntent().getStringExtra(EXTRA_AUDIO);
        mSong = getIntent().getStringExtra(EXTRA_SONG);
        mVideo = getIntent().getStringExtra(EXTRA_VIDEO);
        mModel.audio = false;
        try {
            mModel.audio = VideoUtil.hasTrack(mVideo, "soun");
        } catch (Exception e) {
            Log.e(TAG, "The video does not seem to have audio track.");
        }

        mPlayer1 = new SimpleExoPlayer.Builder(this).build();
        mPlayer1.addAnalyticsListener(this);
        mPlayer2 = new MediaPlayer();
        try {
            mPlayer2.setDataSource(mAudio);
            mPlayer2.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Media player failed to initialize :?", e);
        }

        mPlayer2.setOnCompletionListener(player -> mHandler.removeCallbacks(mHandlerCallback));


        VerticalSlider video = findViewById(R.id.video);
//        song.setLabelFormatter(value -> String.format(Locale.US, "%d%%", (int) value));
        video.setProgress(mModel.video.getValue().intValue());
        video.setOnProgressChangeListener(new VerticalSlider.OnSliderProgressChangeListener() {
            @Override
            public void onChanged(int i, int i1) {
                mModel.video.postValue(Float.valueOf(i));
            }
        });
        video.setEnabled(mModel.audio);

        PlayerView player = findViewById(R.id.player);
        player.setPlayer(mPlayer1);
        VerticalSlider song = findViewById(R.id.sound);
//        song.setLabelFormatter(value -> String.format(Locale.US, "%d%%", (int) value));
        song.setProgress(mModel.song.getValue().intValue());
        song.setOnProgressChangeListener(new VerticalSlider.OnSliderProgressChangeListener() {
            @Override
            public void onChanged(int i, int i1) {
                mModel.song.postValue(Float.valueOf(i));
            }
        });

        View overlay = findViewById(R.id.overlay);
        overlay.setOnClickListener(v -> mPlayer1.setPlayWhenReady(!mPlayer1.getPlayWhenReady()));
        mModel.video.observe(this, volume -> mPlayer1.setVolume(volume / 100));
        mModel.song.observe(this, volume ->
                mPlayer2.setVolume(volume / 100, volume / 100));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopVideoPlayer();
        mPlayer1.release();
        File video = new File(mVideo);
        if (!video.delete()) {
            Log.w(TAG, "Could not delete input video: " + video);
        }

        File audio = new File(mAudio);
        if (!audio.delete()) {
            Log.w(TAG, "Could not delete input audio: " + audio);
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
        if (state == Player.STATE_ENDED) {
            mPlayer1.setPlayWhenReady(false);
            mPlayer1.seekTo(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayer1.getPlaybackState() == Player.STATE_IDLE) {
            startVideoPlayer();
        }
    }

    @Override
    public void onSeekProcessed(@Nullable EventTime time) {
        long position1 = mPlayer1.getCurrentPosition();
        long duration1 = mPlayer1.getDuration();
        long duration2 = mPlayer2.getDuration();
        long maximum = Math.min(duration1, duration2);
        if (position1 < maximum) {
            mPlayer2.seekTo((int) position1);
        } else if (mPlayer2.isPlaying()) {
            mPlayer2.pause();
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

    private void proceedToPanning(File file) {
        Log.v(TAG, "Proceeding to audio pan screen with " + file);
        Intent intent = new Intent(this, PanAudioActivity.class);
        intent.putExtra(PanAudioActivity.EXTRA_SONG, mSong);
        intent.putExtra(PanAudioActivity.EXTRA_VIDEO, file.getAbsolutePath());
        startActivity(intent);
        finish();
    }

    private void startAudioPlayer() {
        if (mPlayer2.isPlaying()) {
            return;
        }

        long position1 = mPlayer1.getCurrentPosition();
        long duration1 = mPlayer1.getDuration();
        long duration2 = mPlayer2.getDuration();
        long maximum = Math.min(duration1, duration2);
        if (position1 < maximum) {
            mPlayer2.seekTo((int) position1);
            mPlayer2.start();
            mHandler.postDelayed(mHandlerCallback, maximum - position1);
        }
    }

    private void startVideoPlayer() {
        DefaultDataSourceFactory factory =
                new DefaultDataSourceFactory(this, getString(R.string.app_name));
        ProgressiveMediaSource source = new ProgressiveMediaSource.Factory(factory)
                .createMediaSource(Uri.fromFile(new File(mVideo)));
        mPlayer1.setPlayWhenReady(true);
        mPlayer1.seekTo(mModel.window, mModel.position);
        mPlayer1.prepare(source, false, false);
    }

    private void stopAudioPlayer() {
        if (mPlayer2.isPlaying()) {
            mPlayer2.pause();
        }

        mHandler.removeCallbacks(mHandlerCallback);
    }

    private void stopVideoPlayer() {
        mModel.position = mPlayer1.getCurrentPosition();
        mModel.window = mPlayer1.getCurrentWindowIndex();
        mPlayer1.setPlayWhenReady(false);
        mPlayer1.stop();
    }

    private void submitForMerge() {
        //noinspection ConstantConditions
        float video = mModel.video.getValue() / 100;
        //noinspection ConstantConditions
        float song = mModel.song.getValue() / 100;
        Log.v(TAG, String.format(Locale.US, "Merging audios by volumes: %.2f / %.2f", video, song));
        File merged1 = TempUtil.createNewFile(this, ".aac");
        Data data1 = new Data.Builder()
                .putString(MergeAudiosWorker.KEY_INPUT_1, mVideo)
                .putFloat(MergeAudiosWorker.KEY_INPUT_1_VOLUME, mModel.audio ? video : -1)
                .putString(MergeAudiosWorker.KEY_INPUT_2, mAudio)
                .putFloat(MergeAudiosWorker.KEY_INPUT_2_VOLUME, song)
                .putString(MergeAudiosWorker.KEY_OUTPUT, merged1.getAbsolutePath())
                .build();
        OneTimeWorkRequest request1 = new OneTimeWorkRequest.Builder(MergeAudiosWorker.class)
                .setInputData(data1)
                .build();
        File merged2 = TempUtil.createNewFile(this, ".mp4");
        Data data2 = new Data.Builder()
                .putString(MergeAudioVideoWorker2.KEY_VIDEO, mVideo)
                .putString(MergeAudioVideoWorker2.KEY_AUDIO, merged1.getAbsolutePath())
                .putString(MergeAudioVideoWorker2.KEY_OUTPUT, merged2.getAbsolutePath())
                .build();
        OneTimeWorkRequest request2 = new OneTimeWorkRequest.Builder(MergeAudioVideoWorker2.class)
                .setInputData(data2)
                .build();
        WorkManager wm = WorkManager.getInstance(this);
        wm.beginWith(request1).then(request2).enqueue();

        CustomDialogClass progress = new CustomDialogClass(this, R.style.customStyle);
        progress.setCancelable(false);
        progress.show();


        wm.getWorkInfoByIdLiveData(request2.getId())
                .observe(this, info -> {
                    boolean ended = info.getState() == WorkInfo.State.CANCELLED
                            || info.getState() == WorkInfo.State.FAILED
                            || info.getState() == WorkInfo.State.SUCCEEDED;
                    if (ended) {
                        progress.dismiss();
                    }

                    if (info.getState() == WorkInfo.State.SUCCEEDED) {
                        setResult(RESULT_OK);
                        if (getResources().getBoolean(R.bool.skip_pan_audio_screen)) {
                            proceedToFilter(merged2);
                        } else {
                            proceedToPanning(merged2);
                        }
                    }
                });
    }

    public static class VolumeActivityViewModel extends ViewModel {

        public boolean audio;

        public MutableLiveData<Float> song = new MutableLiveData<>(100.0f);
        public MutableLiveData<Float> video = new MutableLiveData<>(100.0f);

        public long position;
        public int window;
    }
}
