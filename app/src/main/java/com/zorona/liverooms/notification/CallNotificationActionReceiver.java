package com.app.liverooms.notification;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.app.liverooms.MainApplication;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.videocall.CallIncomeActivity;

public class CallNotificationActionReceiver extends BroadcastReceiver {


    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        if (intent != null && intent.getExtras() != null) {

            String action = "";
            action = intent.getStringExtra("ACTION_TYPE");

            if (action != null && !action.equalsIgnoreCase("")) {
                performClickAction(context, action, intent);
            }

            // Close the notification after the click action is performed.
            Intent iclose = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(iclose);
            context.stopService(new Intent(context, HeadsUpNotificationService.class));

        }


    }

    private void performClickAction(Context context, String action, Intent i) {
        Log.d("TAG", "performClickAction: " + action);
        if (action.equalsIgnoreCase("RECEIVE_CALL")) {
            Log.d("TAG", "performClickAction: " + checkAppPermissions());
            if (checkAppPermissions()) {

                Intent intent = new Intent(MainApplication.getAppContext(), CallIncomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Const.DATA, i.getStringExtra(Const.DATA));
                intent.putExtra(Const.ISACCEPT_CLICK, true);
                mContext.startActivity(intent);



               /* Intent intentCallReceive = new Intent(mContext, VideoCallActivity.class);

                intentCallReceive.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intentCallReceive);*/
            } else {


            }
        } else if (action.equalsIgnoreCase("DIALOG_CALL")) {

            // show ringing activity when phone is locked
            Intent intent = new Intent(MainApplication.getAppContext(), CallIncomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Const.DATA, i.getStringExtra(Const.DATA));

            mContext.startActivity(intent);
        } else {
            context.stopService(new Intent(context, HeadsUpNotificationService.class));
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }
        NotificationManager nMgr = (NotificationManager) mContext.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        nMgr.cancel(i.getIntExtra("NOTIFICATION_ID", 0));
        nMgr.cancelAll();
    }

    private Boolean checkAppPermissions() {
        return hasCameraPermissions() && hasAudioPermissions();
    }

    private boolean hasAudioPermissions() {
        return (ContextCompat.checkSelfPermission(MainApplication.getAppContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(MainApplication.getAppContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(MainApplication.getAppContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasCameraPermissions() {
        return (ContextCompat.checkSelfPermission(MainApplication.getAppContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }
}