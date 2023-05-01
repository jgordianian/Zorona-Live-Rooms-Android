package com.zorona.liverooms.reels.record.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;
import java.util.Locale;

public class MergeAudiosWorker extends Worker {

    public static final String KEY_INPUT_1 = "input_1";
    public static final String KEY_INPUT_1_VOLUME = "input_1_volume";
    public static final String KEY_INPUT_2 = "input_2";
    public static final String KEY_INPUT_2_VOLUME = "input_2_volume";
    public static final String KEY_OUTPUT = "output";
    private static final String TAG = "MergeAudiosWorker";

    public MergeAudiosWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public Result doWork() {
        File input1 = new File(getInputData().getString(KEY_INPUT_1));
        float volume1 = getInputData().getFloat(KEY_INPUT_1_VOLUME, 0f);
        File input2 = new File(getInputData().getString(KEY_INPUT_2));
        float volume2 = getInputData().getFloat(KEY_INPUT_2_VOLUME, 0f);
        File output = new File(getInputData().getString(KEY_OUTPUT));
        boolean success = doActualWork(input1, volume1, input2, volume2, output);
        if (!success && !output.delete()) {
            Log.w(TAG, "Could not delete failed output file: " + output);
        }

        return success ? Result.success() : Result.failure();
    }

    private boolean doActualWork(File input1, float volume1, File input2, float volume2, File output) {
        int code;
        if (volume1 >= 0) {
            String filter = String.format(
                    Locale.US,
                    "[0:a]volume=%.2f[a0];[1:a]volume=%.2f[a1];[a0][a1]amix=inputs=2[out]",
                    volume1,
                    volume2);
            code = FFmpeg.execute(new String[]{
                    "-i", input1.getAbsolutePath(), "-i", input2.getAbsolutePath(),
                    "-filter_complex", filter, "-map", "[out]", "-vn",
                    output.getAbsolutePath(),
            });
        } else {
            String filter = String.format(
                    Locale.US,
                    "[0:a]volume=%.2f[a0];[a0]amix=inputs=1[out]",
                    volume2);
            code = FFmpeg.execute(new String[]{
                    "-i", input2.getAbsolutePath(),
                    "-filter_complex", filter, "-map", "[out]",
                    output.getAbsolutePath(),
            });
        }

        return code==0;
    }
}
