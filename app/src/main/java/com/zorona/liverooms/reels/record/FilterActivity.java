package com.zorona.liverooms.reels.record;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.daasuu.gpuv.egl.filter.GlBrightnessFilter;
import com.daasuu.gpuv.egl.filter.GlExposureFilter;
import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.filter.GlGammaFilter;
import com.daasuu.gpuv.egl.filter.GlGrayScaleFilter;
import com.daasuu.gpuv.egl.filter.GlHazeFilter;
import com.daasuu.gpuv.egl.filter.GlInvertFilter;
import com.daasuu.gpuv.egl.filter.GlMonochromeFilter;
import com.daasuu.gpuv.egl.filter.GlPixelationFilter;
import com.daasuu.gpuv.egl.filter.GlPosterizeFilter;
import com.daasuu.gpuv.egl.filter.GlSepiaFilter;
import com.daasuu.gpuv.egl.filter.GlSharpenFilter;
import com.daasuu.gpuv.egl.filter.GlSolarizeFilter;
import com.daasuu.gpuv.egl.filter.GlVignetteFilter;
import com.daasuu.gpuv.player.GPUPlayerView;
import com.daasuu.mp4compose.filter.GlWatermarkFilter;
import com.zorona.liverooms.R;
import com.zorona.liverooms.activity.BaseActivity;
import com.zorona.liverooms.databinding.ActivityFilterBinding;
import com.zorona.liverooms.dilog.CustomDialogClass;
import com.zorona.liverooms.reels.record.workers.VideoFilterWorker;
import com.zorona.liverooms.utils.BitmapUtil;
import com.zorona.liverooms.utils.TempUtil;
import com.zorona.liverooms.utils.VideoUtil;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class FilterActivity extends BaseActivity {

    public static final String EXTRA_SONG = "song";
    public static final String EXTRA_VIDEO = "video";
    private static final String TAG = "FilterActivity";

    private FilterActivityViewModel mModel;
    private SimpleExoPlayer mPlayer;
    private GPUPlayerView mPlayerView;
    private String mSong;
    private String mVideo;
    ActivityFilterBinding binding;
    private CustomDialogClass progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter);
        mModel = new ViewModelProvider(this).get(FilterActivityViewModel.class);
        mSong = getIntent().getStringExtra(EXTRA_SONG);
        mVideo = getIntent().getStringExtra(EXTRA_VIDEO);
        Log.d(TAG, "onCreate:songid " + mSong);

        Bitmap frame = null;
        try {
            frame = VideoUtil.getFrameAtTime(mVideo, TimeUnit.SECONDS.toMicros(3));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap square = BitmapUtil.getSquareThumbnail(frame, 250);
        //noinspection ConstantConditions
        frame.recycle();
        Bitmap rounded = BitmapUtil.addRoundCorners(square, 25);
        square.recycle();
        FilterAdapter adapter = new FilterAdapter(this, rounded);
        adapter.setListener(this::applyFilter);
        RecyclerView filters = findViewById(R.id.filters);
        filters.setAdapter(adapter);


        binding.btnDone.setOnClickListener(v -> submitForFilter());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File video = new File(mVideo);
        if (!video.delete()) {
            Log.w(TAG, "Could not delete input video: " + video);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayerView.onPause();
        mPlayer.setPlayWhenReady(false);
        mPlayer.stop(true);
        mPlayer.release();
        mPlayer = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayer = new SimpleExoPlayer.Builder(this).build();
        mPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL);

        mPlayer.setThrowsWhenUsingWrongThread(false);

        DefaultDataSourceFactory factory =
                new DefaultDataSourceFactory(this, getString(R.string.app_name));
        ProgressiveMediaSource source = new ProgressiveMediaSource.Factory(factory)
                .createMediaSource(Uri.fromFile(new File(mVideo)));
        mPlayer.setPlayWhenReady(true);
        mPlayer.prepare(source);
        mPlayerView = findViewById(R.id.player);
        mPlayerView.setSimpleExoPlayer(mPlayer);
        mPlayerView.onResume();
    }

    public void applyFilter(VideoFilter filter) {
        Log.d(TAG, "User wants to apply " + filter.name() + " filter.");
        GPUPlayerView player = findViewById(R.id.player);
        switch (mModel.filter = filter) {
            case BRIGHTNESS: {
                GlBrightnessFilter glf = new GlBrightnessFilter();
                glf.setBrightness(0.2f);
                player.setGlFilter(glf);
                break;
            }
            case EXPOSURE:
                player.setGlFilter(new GlExposureFilter());
                break;
            case GAMMA: {
                GlGammaFilter glf = new GlGammaFilter();
                glf.setGamma(2f);
                player.setGlFilter(glf);
                break;
            }
            case GRAYSCALE:
                player.setGlFilter(new GlGrayScaleFilter());
                break;
            case HAZE: {
                GlHazeFilter glf = new GlHazeFilter();
                glf.setSlope(-0.5f);
                player.setGlFilter(glf);
                break;
            }
            case INVERT:
                player.setGlFilter(new GlInvertFilter());
                break;
            case MONOCHROME:
                player.setGlFilter(new GlMonochromeFilter());
                break;
            case PIXELATED: {
                GlPixelationFilter glf = new GlPixelationFilter();
                glf.setPixel(5);
                player.setGlFilter(glf);
                break;
            }
            case POSTERIZE:
                player.setGlFilter(new GlPosterizeFilter());
                break;
            case SEPIA:
                player.setGlFilter(new GlSepiaFilter());
                break;
            case SHARP: {
                GlSharpenFilter glf = new GlSharpenFilter();
                glf.setSharpness(1f);
                player.setGlFilter(glf);
                break;
            }
            case SOLARIZE:
                player.setGlFilter(new GlSolarizeFilter());
                break;
            case VIGNETTE:
                player.setGlFilter(new GlVignetteFilter());
                break;
            default:
                player.setGlFilter(new GlFilter());
                break;
        }
    }

    private void closeFinally(File clip) {

        Intent intent = new Intent(FilterActivity.this, UploadActivity.class);
        intent.putExtra(UploadActivity.EXTRA_SONG, mSong);
        intent.putExtra(UploadActivity.EXTRA_VIDEO, clip.getAbsolutePath());
        startActivity(intent);
        finish();

//        Context context = getApplicationContext();
//
//        File fixed = TempUtil.createNewFile(this, ".mp4");
//
//        Size size = VideoUtil.getDimensions(clip.getAbsolutePath());
//        GlWatermarkFilter watermark =
//                createWatermark(context.getResources(), size.getWidth(), "");
//        Mp4Composer composer = new Mp4Composer(clip.getAbsolutePath(), fixed.getAbsolutePath());
//        composer.filter(watermark);
//        composer.listener(new Mp4Composer.Listener() {
//            @Override
//            public void onProgress(double progress) {
//                Log.d(TAG, "onProgress: ");
//            }
//
//            @Override
//            public void onCompleted() {
//                progress.dismiss();
//                Intent intent = new Intent(FilterActivity.this, UploadActivity.class);
//                intent.putExtra(UploadActivity.EXTRA_SONG, mSong);
//                intent.putExtra(UploadActivity.EXTRA_VIDEO, fixed.getAbsolutePath());
//                startActivity(intent);
//                finish();
//
////                onBackPressed();
////                Log.d(TAG, "MP4 composition has finished. " + fixed.getAbsolutePath());
//            }
//
//            @Override
//            public void onCanceled() {
//                Log.d(TAG, "MP4 composition was cancelled.");
//            }
//
//            @Override
//            public void onFailed(Exception e) {
//                Log.d(TAG, "MP4 composition failed with error.", e);
//            }
//        });
//        composer.videoFormatMimeType(VideoFormatMimeType.AVC);
//        composer.start();


    }


    private GlWatermarkFilter createWatermark(Resources resources, int vw, String username) {
        Bitmap watermark =
                BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
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


    private void submitForFilter() {
        Log.d(TAG, "submitForFilter: ");
        mPlayer.setPlayWhenReady(false);
        progress = new CustomDialogClass(this, R.style.customStyle);
        progress.setCancelable(false);
        progress.show();
        File filtered = TempUtil.createNewFile(this, ".mp4");
        Data data = new Data.Builder()
                .putString(VideoFilterWorker.KEY_INPUT, mVideo)
                .putString(VideoFilterWorker.KEY_OUTPUT, filtered.getAbsolutePath())
                .putString(VideoFilterWorker.KEY_FILTER, mModel.filter.name())
                .build();
        Log.d(TAG, "submitForFilter: " + mModel.filter.name());
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(VideoFilterWorker.class)
                .setInputData(data)
                .build();
        WorkManager wm = WorkManager.getInstance(this);
        wm.enqueue(request);
        wm.getWorkInfoByIdLiveData(request.getId())
                .observe(this, info -> {
                    boolean ended = info.getState() == WorkInfo.State.CANCELLED
                            || info.getState() == WorkInfo.State.FAILED;
                    if (info.getState() == WorkInfo.State.SUCCEEDED) {
//                        progress.dismiss();
                        closeFinally(filtered);
                    } else if (ended) {
//                        progress.dismiss();
                    }
                });
    }

    public static class FilterActivityViewModel extends ViewModel {

        public VideoFilter filter = VideoFilter.NONE;
    }
}
