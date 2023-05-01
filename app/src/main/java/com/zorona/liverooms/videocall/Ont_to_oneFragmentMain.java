package com.zorona.liverooms.videocall;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCaptureConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.zorona.liverooms.R;
import com.zorona.liverooms.liveStreamming.LiveFragmentMain;
import com.zorona.liverooms.activity.BaseFragment;
import com.zorona.liverooms.databinding.FragmentOntToOneMainBinding;
import com.zorona.liverooms.home.HomeFragment;
import com.zorona.liverooms.reels.VideoListFragment;
import com.zorona.liverooms.utils.AutoFitPreviewBuilder;


public class Ont_to_oneFragmentMain extends BaseFragment {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 7;
    private static final String TAG = "camarafeag";


    Dialog dialog;
    FragmentOntToOneMainBinding binding;
    public CameraX.LensFacing lensFacing = CameraX.LensFacing.FRONT;
    private PreviewConfig.Builder builder;
    private PreviewConfig previewConfig;
    private Preview preview;
    private VideoCaptureConfig.Builder builder1;
    private VideoCaptureConfig videoCaptureConfig;

    public Ont_to_oneFragmentMain() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ont_to_one_main, container, false);
        initView();
        return binding.getRoot();
    }

    private VideoCapture videoCapture;

    @SuppressLint("RestrictedApi")
    private void initView() {
        binding.tvLive.setOnClickListener(v -> callFragment(new LiveFragmentMain()));
        binding.tvVideo.setOnClickListener(v -> callFragment(new VideoListFragment()));
        binding.tvOnetoOne.setOnClickListener(v -> callFragment(new Ont_to_oneFragmentMain()));


      //  initCamera();

        binding.viewFinder.setOnClickListener(v -> startActivity(new Intent(getActivity(), RandomMatchActivity.class)));

    }

    @Override
    public void onResume() {
        super.onResume();
        CameraX.unbindAll();
        new Handler(Looper.getMainLooper()).postDelayed(this::initCamera, 1000);

        try {
            //                      -initCamera();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CameraX.unbindAll();
    }

    @SuppressLint("RestrictedApi")
    private void initCamera() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA},
                    1);
        } else {
            try {
                binding.viewFinder.setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                TextureView viewFinder = binding.viewFinder;
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

                CameraX.bindToLifecycle(getActivity(), preview, videoCapture);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        initCamera();
    }

    private void callFragment(Fragment fragment) {
        if (getParentFragment() != null) {
            ((HomeFragment) getParentFragment()).openFragmet(fragment);
        }
    }
}