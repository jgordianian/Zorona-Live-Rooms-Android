package com.app.liverooms.videocall;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.app.liverooms.BuildConfig;
import com.app.liverooms.activity.BaseActivity;

import java.net.URI;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.client.SocketOptionBuilder;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;

public class VideoCallBaseActivity extends BaseActivity {


    private static final String TAG = "callBaseact";
    private Socket callSocket;

    @Override
    protected void onStart() {
        super.onStart();
        BaseActivity.STATUS_VIDEO_CALL = true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseActivity.STATUS_VIDEO_CALL = true;

    }


    public Socket getCallSocket(String callId, boolean isVideoCallPage) {
        SocketOptionBuilder optionsBuilder = IO.Options.builder()
                // IO factory options
                .setForceNew(false)
                .setMultiplex(true)

                // low-level engine options
                .setTransports(new String[]{Polling.NAME, WebSocket.NAME})
                .setUpgrade(true)
                .setRememberUpgrade(false)
                .setPath("/socket.io/")

                .setExtraHeaders(null)
                // Manager options
                .setReconnection(true)
                .setReconnectionAttempts(Integer.MAX_VALUE)
                .setReconnectionDelay(1_000)
                .setReconnectionDelayMax(5_000)
                .setRandomizationFactor(0.5)
                .setTimeout(20_000)
                // Socket options
                .setAuth(null);
        if (isVideoCallPage) {
            Log.d(TAG, "getCallSocket: isvieocall");
            optionsBuilder.setQuery("videoCallRoom=" + callId + "");
        } else {
            Log.d(TAG, "getCallSocket: callroom");
            optionsBuilder.setQuery("callRoom=" + callId + "");
        }
        IO.Options options = optionsBuilder.build();

        URI uri = URI.create(BuildConfig.BASE_URL);
        callSocket = IO.socket(uri, options);
        callSocket.connect();

        return callSocket;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (callSocket != null) {
            callSocket.disconnect();
        }
    }
}
