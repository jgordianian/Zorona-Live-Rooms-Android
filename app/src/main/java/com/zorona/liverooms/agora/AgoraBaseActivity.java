package com.zorona.liverooms.agora;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import com.zorona.liverooms.BuildConfig;
import com.zorona.liverooms.activity.BaseActivity;
import com.zorona.liverooms.agora.rtc.Constants;
import com.zorona.liverooms.agora.rtc.EngineConfig;
import com.zorona.liverooms.agora.rtc.EventHandler;
import com.zorona.liverooms.agora.stats.StatsManager;
import com.zorona.liverooms.MainApplication;
import com.zorona.liverooms.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;

import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.client.SocketOptionBuilder;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;

public abstract class AgoraBaseActivity extends BaseActivity implements EventHandler {
    private static final String TAG = "agorabaseactivity";
    SessionManager sessionManager;

    private Socket socket;
    private MediaPlayer player2;

    public Socket getSocket() {
        return socket;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        registerRtcEventHandler(this);


    }

    public void initSoketIo(String tkn, boolean ishost) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("liveRoom", tkn);
            if (ishost) {
                jsonObject.put("liveHostRoom", sessionManager.getUser().getId());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "initSoketIo: live rid " + tkn);
        SocketOptionBuilder optionsBuilder = IO.Options.builder()
                // IO factory options
                .setForceNew(false)
                .setMultiplex(true)

                // low-level engine options
                .setTransports(new String[]{Polling.NAME, WebSocket.NAME})
                .setUpgrade(true)
                .setRememberUpgrade(false)
                .setPath("/socket.io/")
                .setQuery("obj=" + jsonObject.toString() + "")
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

        Log.d(TAG, "initSoketIo: ");
       /* if (ishost) {
            optionsBuilder.setQuery("liveHostRoom=" + tkn + "");
        }*/
        //   optionsBuilder.setQuery("liveRoom=" + tkn + "");
        IO.Options options = optionsBuilder.build();

        URI uri = URI.create(BuildConfig.BASE_URL);
        socket = IO.socket(uri, options);
        Log.d("TAG", "onCreate: " + socket.id());
        socket.connect();
        Log.d("TAG", "soket: " + socket.connected());
        socket.on("connection", args -> {
            Log.d("TAG", "onCreate: socket ");
        });
        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d("TAG", "initSoketIo: connect ");
        });
    }

    public void makeSound() {
        if (player2 != null) {
            player2.release();
            player2 = null;
        }
        try {
            player2 = new MediaPlayer();
            try {
                AssetFileDescriptor afd2 = getAssets().openFd("pop.mp3");
                player2.setDataSource(afd2.getFileDescriptor(), afd2.getStartOffset(), afd2.getLength());
                player2.prepare();
                player2.start();
            } catch (IOException e) {
                Log.d(TAG, "initUI: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "initUI: errrr " + e.getMessage());
        }
    }


    protected MainApplication application() {
        return (MainApplication) getApplication();
    }

    protected StatsManager statsManager() {
        return application().statsManager();
    }

    protected RtcEngine rtcEngine() {
        return application().rtcEngine();
    }


  /*  public void configVideo() {
        VideoEncoderConfiguration configuration = new VideoEncoderConfiguration(
                Constants.VIDEO_DIMENSIONS[config().getVideoDimenIndex()],
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
        );
        configuration.mirrorMode = Constants.VIDEO_MIRROR_MODES[config().getMirrorEncodeIndex()];
        rtcEngine().setVideoEncoderConfiguration(configuration);
    }*/

 /*   protected SurfaceView prepareRtcVideo(int uid, boolean local) {
        // Render local/remote video on a SurfaceView

        SurfaceView surface = RtcEngine.CreateRendererView(getApplicationContext());
        if (local) {
            rtcEngine().setupLocalVideo(
                    new VideoCanvas(
                            surface,
                            VideoCanvas.RENDER_MODE_HIDDEN,
                            0,
                            Constants.VIDEO_MIRROR_MODES[config().getMirrorLocalIndex()]
                    )
            );
        } else {
            rtcEngine().setupRemoteVideo(
                    new VideoCanvas(
                            surface,
                            VideoCanvas.RENDER_MODE_HIDDEN,
                            uid,
                            Constants.VIDEO_MIRROR_MODES[config().getMirrorRemoteIndex()]
                    )
            );
        }
        return surface;
    }*/

    protected EngineConfig config() {
        return application().engineConfig();
    }

  /*  protected void removeRtcVideo(int uid, boolean local) {
        if (local) {
            rtcEngine().setupLocalVideo(null);
        } else {
            rtcEngine().setupRemoteVideo(new VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rtcEngine() != null) {
            rtcEngine().leaveChannel();
        }
        removeRtcEventHandler(this);
        getSocket().disconnect();
    }

}
