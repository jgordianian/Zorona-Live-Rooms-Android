package com.app.liverooms.activity;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_IDLE;
import static com.google.android.exoplayer2.Player.STATE_READY;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.app.liverooms.R;
import com.app.liverooms.databinding.ActivityTestVideoBinding;
import com.app.liverooms.reels.record.FilterActivity;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

public class TestVideoActivity extends AppCompatActivity {
    private static final String TAG = "sass";
    ActivityTestVideoBinding binding;
    private SimpleExoPlayer player;

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test_video);

        Intent intent = getIntent();
        String videoURL = intent.getStringExtra(FilterActivity.EXTRA_VIDEO);

        player = new SimpleExoPlayer.Builder(this).build();
        binding.playerview.setPlayer(player);
        Log.d(TAG, "setvideoURL: " + videoURL);
        Uri uri = Uri.parse(videoURL);
        MediaSource mediaSource = buildMediaSource(uri);
        Log.d(TAG, "initializePlayer: " + uri);
        player.setPlayWhenReady(true);
        player.seekTo(0, 0);
        player.prepare(mediaSource, false, false);
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {

                    case STATE_BUFFERING:
                        Log.d(TAG, "buffer: " + uri);
                        break;
                    case STATE_ENDED:
//                        Toast.makeText(FakeActivity.this, "Live Ended!", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> finish(), 2000);
                        Log.d(TAG, "end: " + uri);
                        break;
                    case STATE_IDLE:
                        Log.d(TAG, "idle: " + uri);

                        break;

                    case STATE_READY:

                        Log.d(TAG, "ready: " + uri);

                        break;
                    default:
                        break;
                }
            }
        });


    }
}