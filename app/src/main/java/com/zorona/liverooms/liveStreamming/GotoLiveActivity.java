package com.zorona.liverooms.liveStreamming;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCaptureConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.zorona.liverooms.R;
import com.zorona.liverooms.activity.BaseActivity;
import com.zorona.liverooms.agora.token.RtcTokenBuilder;
import com.zorona.liverooms.databinding.ActivityGotoLiveBinding;
import com.zorona.liverooms.modelclass.LiveStreamRoot;
import com.zorona.liverooms.modelclass.LiveUserRoot;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.retrofit.RetrofitBuilder;
import com.zorona.liverooms.utils.AutoFitPreviewBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GotoLiveActivity extends BaseActivity {
    ActivityGotoLiveBinding binding;
    int front = 1, back = 2;
    int CAMARA = front;
    boolean isPrivate = false;

    public CameraX.LensFacing lensFacing = CameraX.LensFacing.FRONT;
    private PreviewConfig.Builder builder;
    private PreviewConfig previewConfig;
    private Preview preview;
    private VideoCaptureConfig.Builder builder1;
    private VideoCaptureConfig videoCaptureConfig;
    private VideoCapture videoCapture;

    private static final int PERMISSION_REQ_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_goto_live);
        initAudioRecording();
        initListner();


    }

    private void initListner() {
//        binding.btnSwitchCamara.setOnClickListener(v -> {
//            if (CAMARA == front) {
//                CAMARA = back;
//                lensFacing = CameraX.LensFacing.BACK;
//            } else {
//                CAMARA = front;
//                lensFacing = CameraX.LensFacing.FRONT;
//            }
//            CameraX.unbindAll();
//            initCamera();
//        });
        binding.lytPrivacy.setOnClickListener(v -> {
            isPrivate = !isPrivate;

            if (isPrivate) {
                binding.imgLock.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.lock));
                binding.tvPrivacy.setText("Private");

            } else {
                binding.imgLock.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.unlock));
                binding.tvPrivacy.setText("Public");
            }
        });
        binding.btnClose.setOnClickListener(v -> onBackPressed());
        binding.btnLive.setOnClickListener(v -> {
            try {

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("userId", sessionManager.getUser().getId());

                jsonObject.addProperty("isPublic", !isPrivate);
                jsonObject.addProperty("channel", sessionManager.getUser().getId());
                RtcTokenBuilder mainClass = new RtcTokenBuilder();
                int agoraUID = mainClass.getAgoraUID();
                jsonObject.addProperty("agoraUID", 0);  // just for unique host int id


                binding.loder.setVisibility(View.VISIBLE);
                binding.btnLive.setEnabled(false);
                Call<LiveStreamRoot> call = RetrofitBuilder.create().makeliveUser(jsonObject);
                call.enqueue(new Callback<LiveStreamRoot>() {
                    @Override
                    public void onResponse(Call<LiveStreamRoot> call, Response<LiveStreamRoot> response) {
                        if (response.code() == 200) {
                            if (response.body().isStatus()) {
                                Intent intent = new Intent(GotoLiveActivity.this, HostLiveActivity.class);
                                intent.putExtra(Const.DATA, new Gson().toJson(response.body().getLiveUser()));
                                intent.putExtra(Const.PRIVACY, isPrivate ? "Private" : "Public");
                                intent.putExtra(Const.IS_GUEST, false); // Set to true if the user is a guest
                                startActivity(intent);
                                finish();
                            }
                        }
                        binding.loder.setVisibility(View.GONE);
                        binding.btnLive.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Call<LiveStreamRoot> call, Throwable t) {
                        Log.d(TAG, "onFailure: >>>>>>>>>>>>>  " +t.getMessage());
                        binding.loder.setVisibility(View.GONE);
                        binding.btnLive.setEnabled(true);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }

    @SuppressLint("RestrictedApi")
    private void initAudioRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

        } else {

//
//            TextureView viewFinder = binding.viewFinder;
//            AspectRatio ratio = AspectRatio.RATIO_4_3;
//            builder = new PreviewConfig.Builder();
//            previewConfig = builder.setTargetAspectRatio(ratio)
//                    .setLensFacing(lensFacing)
//                    .setTargetRotation(Surface.ROTATION_90)
//                    .build();
//            preview = AutoFitPreviewBuilder.Companion.build(previewConfig, viewFinder);
//            builder1 = new VideoCaptureConfig.Builder();
//            videoCaptureConfig = builder1.setTargetAspectRatio(ratio)
//                    .setLensFacing(lensFacing)
//                    .setVideoFrameRate(24)
//                    .setTargetRotation(Surface.ROTATION_0)
//                    .build();
//            videoCapture = new VideoCapture(videoCaptureConfig);
//            CameraX.bindToLifecycle(this, preview, videoCapture);
//
//            initListner();
    /*        TextureView viewFinder = binding.viewFinder;
            AspectRatio ratio = AspectRatio.RATIO_4_3;
            builder = new PreviewConfig.Builder();
            previewConfig = builder.setTargetAspectRatio(ratio)
                    .setLensFacing(lensFacing)
                    .setTargetRotation(Surface.ROTATION_90)
                    .build();
            preview = AutoFitPreviewBuilder.Companion.build(previewConfig, viewFinder);
            builder1 = new VideoCaptureConfig.Builder();
            videoCaptureConfig = builder1.setTargetAspectRatio(ratio)
                    .setLensFacing(lensFacing)
                    .setVideoFrameRate(24)
                    .setTargetRotation(Surface.ROTATION_0)
                    .build();
            videoCapture = new VideoCapture(videoCaptureConfig);
            CameraX.bindToLifecycle(this, preview, videoCapture);*/

            initListner();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            return;
        }

        initAudioRecording();

    }
}
