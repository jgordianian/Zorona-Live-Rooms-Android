package com.app.liverooms.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

final public class TempUtil {

    private static final long CLEANUP_CUTOFF =
            System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3);

    private static final String TAG = "TempUtil";

    public static void cleanupStaleFiles(Context context) {
        IOFileFilter filter = FileFilterUtils.and(
                FileFilterUtils.fileFileFilter(),
                FileFilterUtils.ageFileFilter(CLEANUP_CUTOFF),
                FileFilterUtils.or(
                        FileFilterUtils.suffixFileFilter(".gif", IOCase.INSENSITIVE),
                        FileFilterUtils.suffixFileFilter(".png", IOCase.INSENSITIVE),
                        FileFilterUtils.suffixFileFilter(".mp4", IOCase.INSENSITIVE)
                )
        );
        Collection<File> stale =
                FileUtils.listFiles(context.getCacheDir(), filter, null);
        if (!stale.isEmpty()) {
            for (File file : stale) {
                FileUtils.deleteQuietly(file);
            }
        }
    }

    public static File createCopy(Context context, Uri uri, @Nullable String suffix) {
        File temp = createNewFile(context, suffix);
        try (InputStream is = context.getContentResolver().openInputStream(uri);
             OutputStream os = new FileOutputStream(temp)) {
            IOUtils.copy(is, os);
        } catch (Exception e) {
            Log.e(TAG, "Could not copy " + uri);
        }

        return temp;
    }

    public static File createNewFile(Context context, @Nullable String suffix) {
        return createNewFile(context.getCacheDir(), suffix);
    }

    public static File createNewFile(File directory, @Nullable String suffix) {
        return new File(directory, UUID.randomUUID().toString() + suffix);
    }

    public static File createNewFile(String directory, @Nullable String suffix) {
        return new File(directory, UUID.randomUUID().toString() + suffix);
    }

    private static final String LOG_FOLDER_NAME = "log";
    private static final String LOG_FILE_NAME = "agora-rtc.log";

    public static String initializeLogFile(Context context) {
        File folder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            folder = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), LOG_FOLDER_NAME);
        } else {
            String path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator +
                    context.getPackageName() + File.separator +
                    LOG_FOLDER_NAME;
            folder = new File(path);
            if (!folder.exists() && !folder.mkdir()) folder = null;
        }

        if (folder != null && !folder.exists() && !folder.mkdir()) return "";
        else return new File(folder, LOG_FILE_NAME).getAbsolutePath();
    }
}
