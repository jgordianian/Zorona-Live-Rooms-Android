package com.app.liverooms.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

//import com.app.liverooms.liveStreamming.WatchLiveActivity;
import com.app.liverooms.liveStreamming.HostLiveActivity;
import com.app.liverooms.user.guestUser.GuestActivity;
import com.app.liverooms.MainApplication;
import com.app.liverooms.R;
import com.app.liverooms.activity.SpleshActivity;
import com.app.liverooms.chat.ChatActivity;
import com.app.liverooms.posts.FeedListActivity;
import com.app.liverooms.reels.ReelsActivity;
import com.app.liverooms.retrofit.Const;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class FirebaseMessage extends FirebaseMessagingService {
    public static final String TAG = "mmmmmm";




    @NonNull
    @Override
    protected Intent getStartCommandIntent(@NonNull Intent intent) {
        Log.d(TAG, "getStartCommandIntent: ");
        return super.getStartCommandIntent(intent);
    }

    boolean isLocal = false;

    @Override
    public boolean handleIntentOnMainThread(@NonNull Intent intent) {
        Log.d(TAG, "handleIntentOnMainThread: " + intent.toString());
        return super.handleIntentOnMainThread(intent);

    }

    public FirebaseMessage() {
        super();
    }


    @Override
    public void handleIntent(@NonNull Intent intent) {
//        super.handleIntent(intent);
        Log.d(TAG, "handleIntent: " + intent.toString());
        Log.d(TAG, "handleIntent: " + intent.getExtras().toString());
        if (intent.getExtras() != null) {
            for (String key : intent.getExtras().keySet()) {
                Object value = intent.getExtras().get(key);
                Log.d("Activity onResume", "Key: " + key + " Value: " + value);
            }
        }
        try {
            if (intent.getExtras() != null) {
                RemoteMessage.Builder builder = new RemoteMessage.Builder("MessagingService");
                for (String key : intent.getExtras().keySet()) {
                    builder.addData(key, intent.getExtras().get(key).toString());
                }

                onMessageReceived(builder.build());

            } else {
//                super.handleIntent(intent);
            }
        } catch (Exception e) {
//            super.handleIntent(intent);
        }
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived2: sdsdds");
        if (remoteMessage.getData().get("type").equals("CALL")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                if (MainApplication.isAppOpen) return;

                Intent serviceIntent = new Intent(getApplicationContext(), HeadsUpNotificationService.class);
                Bundle mBundle = new Bundle();
                mBundle.putString(HeadsUpNotificationService.TITLE, remoteMessage.getNotification().getTitle());
                mBundle.putString(HeadsUpNotificationService.DES, remoteMessage.getNotification().getBody());
                mBundle.putString(HeadsUpNotificationService.TYPE, remoteMessage.getData().get("type"));
                mBundle.putString(HeadsUpNotificationService.CALL_FROM, remoteMessage.getData().get("callFrom"));
                mBundle.putString(HeadsUpNotificationService.DATA, remoteMessage.getData().get("data"));
                serviceIntent.putExtras(mBundle);
                ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);

//
//                String channelId="0123";
//                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
//                        .setContentTitle(remoteMessage.getNotification().getTitle())
//                        .setContentText(remoteMessage.getNotification().getBody())
//                        .setAutoCancel(true)
//                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                        .setColor(getResources().getColor(R.color.pink))
//                        .setLights(Color.RED, 1000, 300)
//                        .setDefaults(Notification.DEFAULT_VIBRATE)
//                        .setSmallIcon(R.drawable.ic_msg_icon);
//
//                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//                    String name = "Channel_001";
//                    String description = "Channel Description";
//                    int importance = NotificationManager.IMPORTANCE_HIGH;
//
//                    NotificationChannel channel = new NotificationChannel(channelId, name, importance);
//                    channel.setDescription(description);
//                    channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//                    notificationManager.createNotificationChannel(channel);
//                }
//                notificationManager.notify(0, notificationBuilder.build());

                Log.d(TAG, "onMessageReceived: handle type custom ");
            }
        } else {
            Log.d(TAG, "onMessageReceived: handle type system ");
            if (remoteMessage.getNotification() != null) {

                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

                Map<String, String> messageData = remoteMessage.getData();
                Log.d(TAG, "onMessageReceived: " + messageData);

                String type = messageData.get("type");
                String data = messageData.get("data");


                Intent intent = null;
                if (type.equals("MESSAGE")) {
                    Log.d(TAG, "onMessageReceived: sdsds" + data);
                    intent = new Intent(this, ChatActivity.class);
                    intent.putExtra(Const.CHATROOM, data);  // data==chatroom
                } else if (type.equals("USER")) {
                    intent = new Intent(this, GuestActivity.class);
                    intent.putExtra(Const.USERID, data);   //data==userid
                } else if (type.equals("POST")) {
                    intent = new Intent(this, FeedListActivity.class);
                    intent.putExtra(Const.DATA, data);    //data== list[feed]
                } else if (type.equals("RELITE")) {
                    intent = new Intent(this, ReelsActivity.class);
                    intent.putExtra(Const.DATA, data);    //data==list[relite]
                } else if (type.equals("LIVE")) {
                    intent = new Intent(this, HostLiveActivity.class);
                    intent.putExtra(Const.DATA, data);    //data== LiveUserRoot.UsersItem
                } else {
                    intent = new Intent(this, SpleshActivity.class);
                    // intent.putExtra(Const.CHATROOM,data);
                }

                JSONObject dataObj = null;
                try {
                    dataObj = new JSONObject(remoteMessage.getData().get("data"));

                    if (ChatActivity.isOPEN && ChatActivity.otherUserId.equals(dataObj.getString("userId"))) {
                        return;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                String channelId = "01";
                NotificationCompat.Builder notificationBuilder;
                if (remoteMessage.getNotification().getImageUrl() != null) {
                    String imageUri = remoteMessage.getNotification().getImageUrl().toString();
                    Bitmap bitmap = getBitmapfromUrl(imageUri);

                    notificationBuilder = new NotificationCompat.Builder(this, channelId)
                            .setContentTitle(remoteMessage.getNotification().getTitle())
                            .setContentText(remoteMessage.getNotification().getBody())
                            .setAutoCancel(true)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setContentIntent(pendingIntent)
                            .setLargeIcon(icon)
                            .setStyle(new NotificationCompat.BigPictureStyle()
                                    .bigPicture(bitmap))
                            .setColor(getResources().getColor(R.color.pink))
                            .setLights(Color.RED, 1000, 300)
                            .setDefaults(Notification.DEFAULT_VIBRATE)
                            .setSmallIcon(R.drawable.ic_msg_icon);

                } else {
                    notificationBuilder = new NotificationCompat.Builder(this, channelId)
                            .setContentTitle(remoteMessage.getNotification().getTitle())
                            .setContentText(remoteMessage.getNotification().getBody())
                            .setAutoCancel(true)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setContentIntent(pendingIntent)
                            .setLargeIcon(icon)
                            .setColor(getResources().getColor(R.color.pink))
                            .setLights(Color.RED, 1000, 300)
                            .setDefaults(Notification.DEFAULT_VIBRATE)
                            .setSmallIcon(R.drawable.ic_msg_icon);


                }
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    String name = "Channel_001";
                    String description = "Channel Description";
                    int importance = NotificationManager.IMPORTANCE_HIGH;

                    NotificationChannel channel = new NotificationChannel(channelId, name, importance);
                    channel.setDescription(description);
                    channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                    notificationManager.createNotificationChannel(channel);
                }
                notificationManager.notify(0, notificationBuilder.build());


            }

        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


        }
    }

/*
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "remote msg : " + remoteMessage.getData().toString());

     */
/*   try {
            if ( remoteMessage.getData().get("isLocal").equals("true")) {
                isLocal=true;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (!isLocal){
            return;
        }*//*


        if (remoteMessage.getData().get("type").equals("CALL")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                Intent serviceIntent = new Intent(getApplicationContext(), HeadsUpNotificationService.class);
                Bundle mBundle = new Bundle();
                mBundle.putString(HeadsUpNotificationService.TITLE, remoteMessage.getNotification().getTitle());
                mBundle.putString(HeadsUpNotificationService.DES, remoteMessage.getNotification().getBody());
                mBundle.putString(HeadsUpNotificationService.TYPE, remoteMessage.getData().get("type"));
                mBundle.putString(HeadsUpNotificationService.CALL_FROM, remoteMessage.getData().get("callFrom"));
                mBundle.putString(HeadsUpNotificationService.DATA, remoteMessage.getData().get("data"));
                serviceIntent.putExtras(mBundle);
                ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);


                Log.d(TAG, "onMessageReceived: handle type custom ");
            }
        } else {
            Log.d(TAG, "onMessageReceived: handle type system ");
            if (remoteMessage.getNotification() != null) {

                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

                Map<String, String> messageData = remoteMessage.getData();
                Log.d(TAG, "onMessageReceived: " + messageData);

                String type = messageData.get("type");
                String data = messageData.get("data");
                Intent intent = null;
                if (type.equals("MESSAGE")) {
                    Log.d(TAG, "onMessageReceived: sdsds"+data);
                    intent = new Intent(this, ChatActivity.class);
                    intent.putExtra(Const.CHATROOM, data);  // data==chatroom
                } else if (type.equals("USER")) {
                    intent = new Intent(this, GuestActivity.class);
                    intent.putExtra(Const.USERID, data);   //data==userid
                } else if (type.equals("POST")) {
                    intent = new Intent(this, FeedListActivity.class);
                    intent.putExtra(Const.DATA, data);    //data== list[feed]
                } else if (type.equals("RELITE")) {
                    intent = new Intent(this, ReelsActivity.class);
                    intent.putExtra(Const.DATA, data);    //data==list[relite]
                } else if (type.equals("LIVE")) {
                    intent = new Intent(this, WatchLiveActivity.class);
                    intent.putExtra(Const.DATA, data);    //data== LiveUserRoot.UsersItem
                } else {
                    intent = new Intent(this, SpleshActivity.class);
                    // intent.putExtra(Const.CHATROOM,data);
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


                String channelId = "01";
                NotificationCompat.Builder notificationBuilder;
                if (remoteMessage.getNotification().getImageUrl() != null) {
                    String imageUri = remoteMessage.getNotification().getImageUrl().toString();
                    Bitmap bitmap = getBitmapfromUrl(imageUri);

                    notificationBuilder = new NotificationCompat.Builder(this, channelId)
                            .setContentTitle(remoteMessage.getNotification().getTitle())
                            .setContentText(remoteMessage.getNotification().getBody())
                            .setAutoCancel(true)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setContentIntent(pendingIntent)
                            .setLargeIcon(icon)
                            .setStyle(new NotificationCompat.BigPictureStyle()
                                    .bigPicture(bitmap))
                            .setColor(getResources().getColor(R.color.pink))
                            .setLights(Color.RED, 1000, 300)
                            .setDefaults(Notification.DEFAULT_VIBRATE)
                            .setSmallIcon(R.drawable.ic_msg_icon);

                } else {
                    notificationBuilder = new NotificationCompat.Builder(this, channelId)
                            .setContentTitle(remoteMessage.getNotification().getTitle())
                            .setContentText(remoteMessage.getNotification().getBody())
                            .setAutoCancel(true)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setContentIntent(pendingIntent)
                            .setLargeIcon(icon)
                            .setColor(getResources().getColor(R.color.pink))
                            .setLights(Color.RED, 1000, 300)
                            .setDefaults(Notification.DEFAULT_VIBRATE)
                            .setSmallIcon(R.drawable.ic_msg_icon);


                }
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    String name = "Channel_001";
                    String description = "Channel Description";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;

                    NotificationChannel channel = new NotificationChannel(channelId, name, importance);
                    channel.setDescription(description);
                    notificationManager.createNotificationChannel(channel);
                }
                notificationManager.notify(0, notificationBuilder.build());


            }

        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


        }

        // Check if message contains a notification payload.

    }
*/

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
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

        }
    }
}
