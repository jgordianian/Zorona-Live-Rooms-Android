package com.zorona.liverooms.reels.record.workers;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.ForegroundInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.zorona.liverooms.ClientDatabase;
import com.zorona.liverooms.Draft;
import com.zorona.liverooms.MainApplication;
import com.zorona.liverooms.SessionManager;
import com.zorona.liverooms.SharedConstants;
import com.zorona.liverooms.modelclass.RestResponse;
import com.zorona.liverooms.reels.record.UploadActivity;
import com.zorona.liverooms.retrofit.RetrofitBuilder;
import com.zorona.liverooms.utils.VideoUtil;
import com.zorona.liverooms.R;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class UploadClipWorker extends Worker {


    private static final String TAG = "UploadClipWorker";
    SessionManager sessionManager;
    private Context context;

    public UploadClipWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
        sessionManager = new SessionManager(context);
    }

    private ForegroundInfo createForegroundInfo(Context context) {
        String cancel = context.getString(R.string.cancel_button);
        PendingIntent intent = WorkManager.getInstance(context)
                .createCancelPendingIntent(getId());
        Notification notification =
                new NotificationCompat.Builder(
                        context, context.getString(R.string.notification_channel_id))
                        .setContentTitle(context.getString(R.string.notification_upload_title))
                        .setTicker(context.getString(R.string.notification_upload_title))
                        .setContentText(context.getString(R.string.notification_upload_description))
                        .setSmallIcon(R.drawable.ic_baseline_publish_24)
                        .setOngoing(true)
                        .setOnlyAlertOnce(true)
                        .addAction(R.drawable.ic_baseline_close_24, cancel, intent)
                        .build();
        return new ForegroundInfo(SharedConstants.NOTIFICATION_UPLOAD, notification);
    }

    @NonNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public Result doWork() {
        setForegroundAsync(createForegroundInfo(getApplicationContext()));

        boolean success = false;
        UploadActivity.LocalVideo relite = sessionManager.getLocalVideo();
        if (relite != null) {
            String songId = relite.getSongId() != null ? relite.getSongId() : "";
            File video = new File(relite.getVideo());

            File screenshot = new File(relite.getScreenshot());
            File preview = new File(relite.getPreview());
            String description = relite.getDecritption();



            boolean hasComments = relite.isHasComments();
            String location = relite.getLocation();
            int privacy = relite.getPrivacy();
            String userId = relite.getUserId();

            long duration = 0;
            try {
                duration = VideoUtil.getDuration(getApplicationContext(), Uri.fromFile(video));
            } catch (IOException e) {
                e.printStackTrace();
            }
            duration = TimeUnit.MILLISECONDS.toSeconds(duration);



            try {
                Log.d(TAG, "doWork: file upload work-------------------------------");
                Log.d(TAG, "doWork: video: " + video + "\nss: " + screenshot + "\npreview: " + preview + "\n songid: " + songId + "\ndec: " + description + "\ndur: " + duration + "\nhascmt: " + hasComments + " location: " + location + " \nuid:" + userId);
                success = doActualWork(
                        video, screenshot, preview, songId, description, (int) duration,
                        hasComments, location, userId, privacy, relite.getHeshtags(), relite.getHeshtags());
            } catch (Exception e) {
                Log.e(TAG, "Failed to upload clip to server.", e);
            }

            if (success && !video.delete()) {
                Log.w(TAG, "Could not delete uploaded Yvideo file.");
            }

            if (success && !screenshot.delete()) {
                Log.w(TAG, "Could not delete uploaded screenshot file.");
            }

            if (success && !preview.delete()) {
                Log.w(TAG, "Could not delete uploaded preview file.");
            }

            if (!success) {
                Draft draft = createDraft(
                        video, screenshot, preview, songId, description, relite.getHeshtags(), relite.getMentions(), privacy,
                        hasComments, location);
                Log.w(TAG, "Failed clip saved as draft with ID " + draft.id + ".");
                EventBus.getDefault().post(new ResetDraftsEvent());
                createDraftNotification(draft);
            }
            return success ? Result.success() : Result.failure();
        }
        Log.e(TAG, "doWork: uploaded success");
        return success ? Result.success() : Result.failure();
    }

    private Draft createDraft(
            File video,
            File screenshot,
            File preview,
            String songId,
            String description,
            String heshtags,
            String mentions,
            int privacy,
            boolean hasComments,
            String location) {

        ClientDatabase db = MainApplication.getContainer().get(ClientDatabase.class);
        Draft draft = new Draft();
        draft.video = video.getAbsolutePath();
        draft.screenshot = screenshot.getAbsolutePath();
        draft.preview = preview.getAbsolutePath();
        draft.songId = songId;
        draft.description = description;

        draft.privacy = privacy;
        draft.hasComments = hasComments;
        draft.location = location;

        db.drafts().insert(draft);
        return draft;
    }

    private void createDraftNotification(Draft draft) {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, UploadActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(UploadActivity.EXTRA_DRAFT, draft);
        PendingIntent pi = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Notification notification =
                new NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
                        .setAutoCancel(true)
                        .setContentIntent(pi)
                        .setContentText(context.getString(R.string.notification_upload_failed_description))
                        .setContentTitle(context.getString(R.string.notification_upload_failed_title))
                        .setSmallIcon(R.drawable.ic_baseline_redo_24)
                        .setTicker(context.getString(R.string.notification_upload_failed_title))
                        .build();
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(SharedConstants.NOTIFICATION_UPLOAD_FAILED, notification);
    }

    private boolean doActualWork(
            File video,
            File screenshot,
            File preview,
            String songId,
            String description,
            int duration,
            boolean hasComments,
            String location,
            String userId,
            int privacy,
            String mentions,
            String heshtags
    ) {


        MultipartBody.Part body1 = null;
        if (video != null && !video.getPath().isEmpty()) {
            File file = new File(video.getAbsolutePath());

            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body1 = MultipartBody.Part.createFormData("video", file.getName(), requestFile);

        }

        MultipartBody.Part body2 = null;
        if (screenshot != null && !screenshot.getPath().isEmpty()) {
            File file = new File(screenshot.getAbsolutePath());
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body2 = MultipartBody.Part.createFormData("screenshot", file.getName(), requestFile);

        }

        MultipartBody.Part body3 = null;
        if (preview != null && !preview.getPath().isEmpty()) {
            File file = new File(preview.getAbsolutePath());
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body3 = MultipartBody.Part.createFormData("thumbnail", file.getName(), requestFile);

        }

        HashMap<String, RequestBody> hashMap = new HashMap<>();
        hashMap.put("userId", RequestBody.create(MediaType.parse("text/plain"), userId));

        if (songId.isEmpty()) {
            hashMap.put("isOriginalAudio", RequestBody.create(MediaType.parse("text/plain"), "true"));
        } else {
            hashMap.put("isOriginalAudio", RequestBody.create(MediaType.parse("text/plain"), "false"));
            hashMap.put("songId", RequestBody.create(MediaType.parse("text/plain"), songId));
        }
        hashMap.put("allowComment", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(hasComments)));
        hashMap.put("caption", RequestBody.create(MediaType.parse("text/plain"), description));
        hashMap.put("showVideo", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(privacy)));
        hashMap.put("location", RequestBody.create(MediaType.parse("text/plain"), location));
        hashMap.put("hashtag", RequestBody.create(MediaType.parse("text/plain"), heshtags));
        hashMap.put("mentionPeople", RequestBody.create(MediaType.parse("text/plain"), mentions));
        hashMap.put("duration", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(duration)));
        hashMap.put("size", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(VideoUtil.getFileSizeInMB(video) + " MB")));

        Log.d(TAG, "doActualWork: size " + VideoUtil.getFileSizeInMB(video));
        Log.d(TAG, "doActualWork: duration " + duration);

        final boolean[] success = {false};

        Call<RestResponse> call = RetrofitBuilder.create().uploadRelite(hashMap, body1, body2, body3);
        Response<RestResponse> response = null;
        try {
            response = call.execute();
        } catch (Exception e) {
            Log.e(TAG, "Failed when uploading clip to server.", e);
        }

        if (response != null) {
            if (response.code() == 422) {
                try {
                    Log.v(TAG, "Server returned validation errors:\n" + response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return response.isSuccessful();
        }

       /* call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                Log.d(TAG, "onResponse: success");
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        success[0] = true;
                        uploadClipLister.onSuccess();
                    } else {
                        uploadClipLister.onFailure();
                        success[0] = false;
                    }
                } else {
                    uploadClipLister.onFailure();
                    success[0] = false;
                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: err  " + t.getMessage());
                success[0] = false;
            }
        });
*/

        Log.d(TAG, "doActualWork: done ");

        return success[0];
    }

    private class ResetDraftsEvent {
    }

    public interface UploadClipLister {
        void onSuccess();

        void onFailure();
    }
}
