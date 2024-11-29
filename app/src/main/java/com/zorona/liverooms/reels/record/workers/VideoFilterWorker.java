package com.app.liverooms.reels.record.workers;

import android.content.Context;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.daasuu.mp4compose.FillMode;
import com.daasuu.mp4compose.VideoFormatMimeType;
import com.daasuu.mp4compose.composer.Mp4Composer;
import com.daasuu.mp4compose.filter.GlBrightnessFilter;
import com.daasuu.mp4compose.filter.GlExposureFilter;
import com.daasuu.mp4compose.filter.GlGammaFilter;
import com.daasuu.mp4compose.filter.GlGrayScaleFilter;
import com.daasuu.mp4compose.filter.GlHazeFilter;
import com.daasuu.mp4compose.filter.GlInvertFilter;
import com.daasuu.mp4compose.filter.GlMonochromeFilter;
import com.daasuu.mp4compose.filter.GlPixelationFilter;
import com.daasuu.mp4compose.filter.GlPosterizeFilter;
import com.daasuu.mp4compose.filter.GlSepiaFilter;
import com.daasuu.mp4compose.filter.GlSharpenFilter;
import com.daasuu.mp4compose.filter.GlSolarizeFilter;
import com.daasuu.mp4compose.filter.GlVignetteFilter;
import com.app.liverooms.SharedConstants;
import com.app.liverooms.reels.record.VideoFilter;
import com.app.liverooms.utils.VideoUtil;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;


public class VideoFilterWorker extends ListenableWorker {

    public static final String KEY_FILTER = "filter";
    public static final String KEY_INPUT = "input";
    public static final String KEY_OUTPUT = "output";
    private static final String TAG = "VideoFilterWorker";

    public VideoFilterWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public ListenableFuture<Result> startWork() {
        File input = new File(getInputData().getString(KEY_INPUT));
        File output = new File(getInputData().getString(KEY_OUTPUT));
        return CallbackToFutureAdapter.getFuture(completer -> {
            doActualWork(input, output, completer);
            return null;
        });
    }

    private void doActualWork(File input, File output, CallbackToFutureAdapter.Completer<Result> completer) throws IOException {
        Size size = VideoUtil.getDimensions(input.getAbsolutePath());
        int width = size.getWidth();
        int height = size.getHeight();
        if (width > SharedConstants.MAX_RESOLUTION || height > SharedConstants.MAX_RESOLUTION) {
            if (width > height) {
                height = SharedConstants.MAX_RESOLUTION * height / width;
                width = SharedConstants.MAX_RESOLUTION;
            } else {
                width = SharedConstants.MAX_RESOLUTION * width / height;
                height = SharedConstants.MAX_RESOLUTION;
            }
        }
        if (width % 2!=0) {
            width += 1;
        }

        if (height % 2!=0) {
            height += 1;
        }

        Log.v(TAG, "Original: " + width + "x" + height + "px; scaled: " + width + "x" + height + "px");
        Mp4Composer composer = new Mp4Composer(input.getAbsolutePath(), output.getAbsolutePath());
        // composer.videoBitrate((int) (.07 * 30 * width * height));
        composer.videoBitrate((int) (.07 * 30 * width * height));
        composer.fillMode(FillMode.PRESERVE_ASPECT_FIT);
        composer.size(width, height);
        VideoFilter filter = VideoFilter.valueOf(getInputData().getString(KEY_FILTER));
        Log.d(TAG, "doActualWork: filtername " + filter.name());
        switch (filter) {
            case BRIGHTNESS: {
                GlBrightnessFilter glf = new GlBrightnessFilter();
                glf.setBrightness(0.2f);
                composer.filter(glf);
                break;
            }
            case EXPOSURE:
                composer.filter(new GlExposureFilter());
                break;
            case GAMMA: {
                GlGammaFilter glf = new GlGammaFilter();
                glf.setGamma(2f);
                composer.filter(glf);
                break;
            }
            case GRAYSCALE:
                composer.filter(new GlGrayScaleFilter());
                break;
            case HAZE: {
                GlHazeFilter glf = new GlHazeFilter();
                glf.setSlope(-0.5f);
                composer.filter(glf);
                break;
            }
            case INVERT:
                composer.filter(new GlInvertFilter());
                break;
            case MONOCHROME:
                composer.filter(new GlMonochromeFilter());
                break;
            case PIXELATED:
                composer.filter(new GlPixelationFilter());
                break;
            case POSTERIZE:
                composer.filter(new GlPosterizeFilter());
                break;
            case SEPIA:
                composer.filter(new GlSepiaFilter());
                break;
            case SHARP: {
                GlSharpenFilter glf = new GlSharpenFilter();
                glf.setSharpness(1f);
                composer.filter(glf);
                break;
            }
            case SOLARIZE:
                composer.filter(new GlSolarizeFilter());
                break;
            case VIGNETTE:
                composer.filter(new GlVignetteFilter());
                break;
            default:
                break;
        }

        composer.listener(new Mp4Composer.Listener() {

            @Override
            public void onProgress(double progress) {
            }

            @Override
            public void onCompleted() {
                Log.d(TAG, "MP4 composition has finished.");
                completer.set(Result.success());
            }

            @Override
            public void onCanceled() {
                Log.d(TAG, "MP4 composition was cancelled.");
                completer.setCancelled();
                if (!output.delete()) {
                    Log.w(TAG, "Could not delete failed output file: " + output);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.d(TAG, "MP4 composition failed with error.", e);
                completer.setException(e);
                if (!output.delete()) {
                    Log.w(TAG, "Could not delete failed output file: " + output);
                }
            }
        });
        composer.videoFormatMimeType(VideoFormatMimeType.AVC);
        composer.start();
    }
}
