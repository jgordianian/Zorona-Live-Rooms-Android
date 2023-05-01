package com.zorona.liverooms.notification;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.StrictMode;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.zorona.liverooms.MainApplication;
import com.zorona.liverooms.R;
import com.zorona.liverooms.retrofit.Const;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HeadsUpNotificationService extends Service implements MediaPlayer.OnPreparedListener {


    public static final String TITLE = "title";
    public static final String TYPE = "type";
    public static final String DES = "decription";
    public static final String CALL_FROM = "callfrom";
    public static final String DATA = "data";
    public static final String IMAGE = "image";
    private static final String TAG = FirebaseMessage.TAG + " service";
    private static final String T_CALL = "CALL";

    MediaPlayer mediaPlayer;
    Vibrator mvibrator;
    AudioManager audioManager;
    AudioAttributes playbackAttributes;
    AudioManager.OnAudioFocusChangeListener afChangeListener;
    String type = "";
    private String CHANNEL_ID = MainApplication.getAppContext().getString(R.string.app_name) + "CallChannel2";
    private String CHANNEL_NAME = MainApplication.getAppContext().getString(R.string.app_name) + "Call Channel2";
    private Handler handler;
    private boolean status = false;
    private boolean vstatus = false;
    private String image = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {

        Bitmap output;
        Rect srcRect, dstRect;
        float r;
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        if (width > height) {
            output = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888);
            int left = (width - height) / 2;
            int right = left + height;
            srcRect = new Rect(left, 0, right, height);
            dstRect = new Rect(0, 0, height, height);
            r = height / 2;
        } else {
            output = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
            int top = (height - width) / 2;
            int bottom = top + width;
            srcRect = new Rect(0, top, width, bottom);
            dstRect = new Rect(0, 0, width, width);
            r = width / 2;
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

        bitmap.recycle();

        return output;
    }

    public static Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return image;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
       /* try {

            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            Log.d(TAG, "getBitmapfromUrl: " + imageUrl);
            return bitmap;

        } catch (Exception e) {
            Log.d(TAG, "getBitmapfromUrl: " + e);
            e.printStackTrace();
            return null;

        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();// release your media player here audioManager.abandonAudioFocus(afChangeListener);
        releaseMediaPlayer();
        releaseVibration();
    }

    public void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Uri ringUri = Settings.System.DEFAULT_RINGTONE_URI;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Call Notifications2");
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                channel.setSound(ringUri,
                        new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .setLegacyStreamType(AudioManager.STREAM_RING)
                                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION).build());
                Objects.requireNonNull(MainApplication.getAppContext().getSystemService(NotificationManager.class)).createNotificationChannel(channel);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void releaseVibration() {
        try {
            if (mvibrator != null) {
                if (mvibrator.hasVibrator()) {
                    mvibrator.cancel();
                }
                mvibrator = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseMediaPlayer() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
                mediaPlayer = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String name = "", decription = "";
        int NOTIFICATION_ID = 120;
        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            if (audioManager != null) {
                switch (audioManager.getRingerMode()) {
                    case AudioManager.RINGER_MODE_NORMAL:
                        status = true;
                        break;
                    case AudioManager.RINGER_MODE_SILENT:
                        status = false;
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        status = false;
                        vstatus = true;
                        Log.e("Service!!", "vibrate mode");
                        break;
                }
            }

            if (status) {
                Runnable delayedStopRunnable = () -> releaseMediaPlayer();

                afChangeListener = focusChange -> {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        // Permanent loss of audio focus
                        // Pause playback immediately
                        //mediaController.getTransportControls().pause();
                        if (mediaPlayer != null) {
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                            }
                        }
                        // Wait 30 seconds before stopping playback
                        handler.postDelayed(delayedStopRunnable,
                                TimeUnit.SECONDS.toMillis(30));
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        // Pause playback
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        // Lower the volume, keep playing
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        // Your app has been granted audio focus again
                        // Raise volume to normal, restart playback if necessary
                    }
                };
                KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);


                mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
                mediaPlayer.setLooping(true);
                mediaPlayer.setAudioStreamType(AudioManager.ADJUST_RAISE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    handler = new Handler();


                    playbackAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build();

                    AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(playbackAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(afChangeListener, handler)
                            .build();
                    int res = audioManager.requestAudioFocus(focusRequest);
                    if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        if (!keyguardManager.isDeviceLocked()) {

                            mediaPlayer.start();
                        }

                    }
                } else {

                    // Request audio focus for playback
                    int result = audioManager.requestAudioFocus(afChangeListener,
                            // Use the music stream.
                            AudioManager.STREAM_RING,
                            // Request permanent focus.
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        if (!keyguardManager.isDeviceLocked()) {
                            // Start playback
                            mediaPlayer.start();
                        }
                    }

                }

            } else if (vstatus) {
                mvibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Start without a delay
                // Each element then alternates between vibrate, sleep, vibrate, sleep...
                long[] pattern = {0, 250, 200, 250, 150, 150, 75,
                        150, 75, 150};

                // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
                mvibrator.vibrate(pattern, 0);
                Log.e("Service!!", "vibrate mode start");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle bundle = null;
        String dataString = null;
        if (intent != null && intent.getExtras() != null) {

            bundle = intent.getExtras();
            name = bundle.getString(TITLE);
            image = bundle.getString(IMAGE);
            decription = bundle.getString(DES);
            type = bundle.getString(TYPE);
            dataString = bundle.getString(DATA);
            Log.d(TAG, "onStartCommand: data   " + dataString);


        }
        NotificationCompat.Builder notificationBuilder = null;

        if (type.isEmpty()) return START_NOT_STICKY;

        if (type.equals(T_CALL)) {
            try {
                Intent receiveCallAction = new Intent(MainApplication.getAppContext(), CallNotificationActionReceiver.class);
                receiveCallAction.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.CALL_RECEIVE_ACTION");
                receiveCallAction.putExtra("ACTION_TYPE", "RECEIVE_CALL");
                receiveCallAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
                receiveCallAction.putExtra(Const.DATA, dataString);
                receiveCallAction.setAction("RECEIVE_CALL");

                Intent cancelCallAction = new Intent(MainApplication.getAppContext(), CallNotificationActionReceiver.class);
                cancelCallAction.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.CALL_CANCEL_ACTION");
                cancelCallAction.putExtra("ACTION_TYPE", "CANCEL_CALL");
                cancelCallAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
                cancelCallAction.putExtra(Const.DATA, dataString);
                cancelCallAction.setAction("CANCEL_CALL");

                Intent callDialogAction = new Intent(MainApplication.getAppContext(), CallNotificationActionReceiver.class);
                callDialogAction.putExtra("ACTION_TYPE", "DIALOG_CALL");
                callDialogAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
                callDialogAction.putExtra(Const.DATA, dataString);
                callDialogAction.setAction("DIALOG_CALL");

                PendingIntent receiveCallPendingIntent = PendingIntent.getBroadcast(MainApplication.getAppContext(), 1200, receiveCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent cancelCallPendingIntent = PendingIntent.getBroadcast(MainApplication.getAppContext(), 1201, cancelCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent callDialogPendingIntent = PendingIntent.getBroadcast(MainApplication.getAppContext(), 1202, callDialogAction, PendingIntent.FLAG_UPDATE_CURRENT);

                createChannel();
                if (bundle != null) {

                    RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_call);
                    notificationLayout.setTextViewText(R.id.notification_title, decription);
                    notificationLayout.setOnClickPendingIntent(R.id.btnDecline, cancelCallPendingIntent);
                    notificationLayout.setOnClickPendingIntent(R.id.btnAccept, receiveCallPendingIntent);
                    Log.d(TAG, "onStartCommand: image " + image);

                    if (image != null && !image.isEmpty()) {
                        //   notificationLayout.setImageViewUri(R.id.imgUser,Uri.parse(image));

                        try {
                            URL url = new URL(image);
                            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            notificationLayout.setImageViewBitmap(R.id.imgUser, getCircleBitmap(image));
                        } catch (IOException e) {
                            System.out.println(e);
                        }
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //  new BitmapLoder().execute(image);
                                //notificationLayout.setImageViewBitmap(R.id.imgUser, getCircleBitmap());
                            }
                        }, 500);
                    }


                    Uri ringUri = Settings.System.DEFAULT_RINGTONE_URI;
                    notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.bell_16)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                            .setCategory(NotificationCompat.CATEGORY_ALARM)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                            .setCustomContentView(notificationLayout)
                            .setVibrate(new long[0])
                            .setSound(ringUri)
                            .setFullScreenIntent(callDialogPendingIntent, true);

                }
                Notification incomingCallNotification = null;
                if (notificationBuilder != null) {
                    incomingCallNotification = notificationBuilder.build();
                }

                startForeground(NOTIFICATION_ID, incomingCallNotification);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new Handler().postDelayed(() -> stopSelf(), 20000);

        return START_STICKY;
    }

    public class BitmapLoder extends AsyncTask<String, String, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return image;
            } catch (IOException e) {
                System.out.println(e);
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }
    }

}