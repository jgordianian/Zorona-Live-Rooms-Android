package com.app.liverooms.videocall;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.app.liverooms.R;
import com.app.liverooms.modelclass.UserRoot;
import com.app.liverooms.viewModel.VideoCallViewModel;
import com.app.liverooms.viewModel.ViewModelFactory;
import com.app.liverooms.activity.BaseActivity;
import com.app.liverooms.databinding.ActivityVideoCallBinding;
import com.app.liverooms.retrofit.Const;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class VideoCallActivity extends VideoCallBaseActivity {
    private static final String TAG = "videocallact";
    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };
    ActivityVideoCallBinding binding;
    private VideoCallViewModel viewModel;
    private RtcEngine mRtcEngine;
    private boolean mCallEnd;
    private boolean mMuted;

    private FrameLayout mLocalContainer;
    private RelativeLayout mRemoteContainer;
    private VideoCanvas mLocalVideo;
    private VideoCanvas mRemoteVideo;
    private boolean isVideoDecoded = false;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        /**
         * Occurs when the local user joins a specified channel.
         * The channel name assignment is based on channelName specified in the joinChannel method.
         * If the uid is not specified when joinChannel is called, the server automatically assigns a uid.
         *
         * @param channel Channel name.
         * @param uid     User ID.
         * @param elapsed Time elapsed (ms) from the user calling joinChannel until this callback is triggered.
         */
        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(() -> new Handler().postDelayed(() -> {
                if (isVideoDecoded) {
                    Log.d(TAG, "sssss=- run: yreeeeeeehhhhh  video decoded");
                } else {
                    Toast.makeText(VideoCallActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    endCall();
                }
            }, 5000)); //todo
        }

        @Override
        public void onUserJoined(final int uid, int elapsed) {
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        /**
         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         *
         * There are two reasons for users to become offline:
         *
         *     Leave the channel: When the user/host leaves the channel, the user/host sends a
         *     goodbye message. When this message is received, the SDK determines that the
         *     user/host leaves the channel.
         *
         *     Drop offline: When no data packet of the user or host is received for a certain
         *     period of time (20 seconds for the communication profile, and more for the live
         *     broadcast profile), the SDK assumes that the user/host drops offline. A poor
         *     network connection may lead to false detections, so we recommend using the
         *     Agora RTM SDK for reliable offline detection.
         *
         * @param uid ID of the user or host who leaves the channel or goes offline.
         * @param reason Reason why the user goes offline:
         *
         *     USER_OFFLINE_QUIT(0): The user left the current channel.
         *     USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
         *     USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
         */
        @Override
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(() -> {
                onRemoteUserLeft(uid);
                endCall();
            });
        }

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(() -> {
                isVideoDecoded = true;
                Log.d(TAG, "sssss=- run: vide decode");


            });
        }

        @Override
        public void onError(int err) {
            super.onError(err);
            Log.d(TAG, "onError: " + err);
        }
    };
    private String otherUserId = "";
    private String token;
    private String channel;
    private boolean callByMe;
    private String callRoomId;
    private Socket callRoomSocket;
    Handler timerHandler = new Handler(Looper.myLooper());
    private Emitter.Listener eventCallRecive = args -> {
        runOnUiThread(() -> {
            if (args != null) {
                Log.d(TAG, "call: callrecive " + args[0].toString());
                try {
                    JSONObject jsonObject = new JSONObject(args[0].toString());
                    UserRoot.User user = new Gson().fromJson(jsonObject.toString(), UserRoot.User.class);
                    sessionManager.saveUser(user);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    };
    private int seconds = 0;
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            seconds++;
            if (callByMe && seconds % 60 == 0) {
                reduseCoin();
            }
            int p1 = seconds % 60;
            int p2 = seconds / 60;
            int p3 = p2 % 60;
            p2 = p2 / 60;

            String sec;
            String hour;
            String min;
            if (p1 < 10) {
                sec = "0" + p1;
            } else {
                sec = String.valueOf(p1);
            }
            if (p2 < 10) {
                hour = "0" + p2;
            } else {
                hour = String.valueOf(p2);
            }
            if (p3 < 10) {
                min = "0" + p3;
            } else {
                min = String.valueOf(p3);
            }
            binding.tvtimer.setText(hour + ":" + min + ":" + sec);


            timerHandler.postDelayed(this, 1000);
        }
    };


    private void reduseCoin() {

        if (sessionManager.getUser().getLevel().getAccessibleFunction().isFreeCall()) {
            Log.d(TAG, "reduseCoin: free call");
            return;
        }
        if (sessionManager.getUser().getDiamond() >= sessionManager.getSetting().getCallCharge()) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("callId", callRoomId);
                jsonObject.put("coin", sessionManager.getSetting().getCallCharge());
                Log.d(TAG, "reduseCoin: callreceive event " + jsonObject);
                callRoomSocket.emit(Const.EVENT_CALL_RECIVE, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Insufficient coins", Toast.LENGTH_SHORT).show();
            endCall();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_call);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new VideoCallViewModel()).createFor()).get(VideoCallViewModel.class);

        BaseActivity.STATUS_VIDEO_CALL = true;

        Intent intent = getIntent();
        otherUserId = intent.getStringExtra(Const.USERID);
        token = intent.getStringExtra(Const.TOKEN);
        callRoomId = intent.getStringExtra(Const.CALL_ROOM_ID);
        channel = intent.getStringExtra(Const.CHANNEL);
        callByMe = intent.getBooleanExtra(Const.CALL_BY_ME, false);
        if (otherUserId != null && !otherUserId.isEmpty()) {
            if (token != null && !token.isEmpty()) {

                initUI();
                initListner();
                if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                        checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
                    initEngineAndJoinChannel();
                }

                timerHandler.postDelayed(timerRunnable, 1000);
                callRoomSocket = getCallSocket(callRoomId, true);
                if (callByMe) {
                    reduseCoin();
                    callRoomSocket.on(Socket.EVENT_CONNECT, args -> runOnUiThread(() -> callRoomSocket.on(Const.EVENT_CALL_RECIVE, eventCallRecive)));
                }
            }
        }


    }

    private void initListner() {
        binding.btnDecline.setOnClickListener(v -> onBackPressed());
        binding.localVideoViewContainer.setOnClickListener(v -> {
            switchView(mLocalVideo);
            switchView(mRemoteVideo);
        });
        binding.btnMute.setOnClickListener(v -> {
            mMuted = !mMuted;
            mRtcEngine.muteLocalAudioStream(mMuted);
            int res = mMuted ? R.drawable.btn_mute_pressed : R.drawable.btn_unmute;
            binding.btnMute.setImageResource(res);
        });
        binding.btnSwitchCamera.setOnClickListener(v -> mRtcEngine.switchCamera());
        binding.btnDecline.setOnClickListener(v -> endCall());
    }

    private void setupRemoteVideo(int uid) {
        ViewGroup parent = mRemoteContainer;
        if (parent.indexOfChild(mLocalVideo.view) > -1) {
            parent = mLocalContainer;
        }

        if (mRemoteVideo != null) {
            return;
        }


        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(parent == mLocalContainer);
        parent.addView(view);
        mRemoteVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid);
        // Initializes the video view of a remote user.
        mRtcEngine.setupRemoteVideo(mRemoteVideo);
    }

    private void onRemoteUserLeft(int uid) {
        Log.d(TAG, "onRemoteUserLeft: ");
        if (mRemoteVideo != null && mRemoteVideo.uid == uid) {
            removeFromParent(mRemoteVideo);
            // Destroys remote view
            mRemoteVideo = null;
            endCall();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        endCall();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void initUI() {
        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);


    }


    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO +
                        "/" + Manifest.permission.CAMERA);
                finish();
                return;
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.
            initEngineAndJoinChannel();
        }
    }

    private void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initEngineAndJoinChannel() {
        // This is our usual steps for joining
        // a channel and starting a call.
        initializeEngine();
        setupVideoConfig();
        setupLocalVideo();
        joinChannel();
    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), sessionManager.getSetting().getAgoraKey(), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine.enableVideo();

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo() {
        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(true);
        mLocalContainer.addView(view);
        // Initializes the local video view.
        // RENDER_MODE_HIDDEN: Uniformly scale the video until it fills the visible boundaries. One dimension of the video may have clipped contents.
        mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        mRtcEngine.setupLocalVideo(mLocalVideo);
    }

    private void joinChannel() {
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.


        mRtcEngine.joinChannel(token, channel, "Extra Optional Data", 0);
    }

    @Override
    protected void onDestroy() {

        if (!mCallEnd) {
            leaveChannel();
        }
        BaseActivity.STATUS_VIDEO_CALL = false;
        Log.d(TAG, "reduseCoin: calldisconnect event ");
        callRoomSocket.emit(Const.EVENT_CALL_DISCONNECT, callRoomId);

        /*
          Destroys the RtcEngine instance and releases all resources used by the Agora SDK.

          This method is useful for apps that occasionally make voice or video calls,
          to free up resources for other operations when not making calls.
         */
        RtcEngine.destroy();
        timerHandler.removeCallbacks(timerRunnable);
        super.onDestroy();
    }

    private void leaveChannel() {
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();

        }
    }


    private void endCall() {
        mCallEnd = true;
        removeFromParent(mLocalVideo);
        mLocalVideo = null;
        removeFromParent(mRemoteVideo);
        mRemoteVideo = null;
        leaveChannel();
        finish();
    }

    private void showButtons(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        binding.btnMute.setVisibility(visibility);
        binding.btnSwitchCamera.setVisibility(visibility);
    }

    private ViewGroup removeFromParent(VideoCanvas canvas) {
        if (canvas != null) {
            ViewParent parent = canvas.view.getParent();
            if (parent != null) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(canvas.view);
                return group;
            }
        }
        return null;
    }

    private void switchView(VideoCanvas canvas) {
        ViewGroup parent = removeFromParent(canvas);
        if (parent == mLocalContainer) {
            if (canvas.view instanceof SurfaceView) {
                ((SurfaceView) canvas.view).setZOrderMediaOverlay(false);
            }
            mRemoteContainer.addView(canvas.view);
        } else if (parent == mRemoteContainer) {
            if (canvas.view instanceof SurfaceView) {
                ((SurfaceView) canvas.view).setZOrderMediaOverlay(true);
            }
            mLocalContainer.addView(canvas.view);
        }
    }


}