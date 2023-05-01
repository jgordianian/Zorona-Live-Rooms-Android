package com.zorona.liverooms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import kotlin.jvm.JvmOverloads;

public class NetWorkChangeReceiver extends BroadcastReceiver {
    private OnNetworkStatusChange onNetworkStatusChange;

    public NetWorkChangeReceiver() {
    }

    @JvmOverloads
    public NetWorkChangeReceiver(OnNetworkStatusChange onNetworkStatusChange) {
        this.onNetworkStatusChange = onNetworkStatusChange;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo activeNetInfo2 = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
            boolean isConnected2 = activeNetInfo2 != null && activeNetInfo2.isConnectedOrConnecting();
            if (isConnected || isConnected2)
                Log.i("NET", "Connected" + isConnected);
            else
                Log.i("NET", "Not Connected" + isConnected);

            if (onNetworkStatusChange != null) {
                onNetworkStatusChange.isOnline(isConnected || isConnected2);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public interface OnNetworkStatusChange {
        void isOnline(Boolean isOnline);
    }
}
