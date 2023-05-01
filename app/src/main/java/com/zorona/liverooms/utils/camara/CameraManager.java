package com.zorona.liverooms.utils.camara;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class CameraManager {

    public static Context mContext;
    Camera mCamera;
    Camera.PictureCallback mPicture;
    boolean cameraFront = false;
    CameraPreview mPreview;

    public CameraManager(Context ctx) {
        mContext = ctx;
    }

    public static int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public static int getCameraOrientation() {

        //Camera.Parameters parameters = mCamera.getParameters();

        Camera.CameraInfo camInfo =
                new Camera.CameraInfo();
        Camera.getCameraInfo(CameraManager.findFrontFacingCamera(), camInfo);


        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:

        }

        int result;
        result = (camInfo.orientation + degrees) % 360;
        return (360 - result) % 360;
    }

    public boolean hasCamera() {
        //check if the device has camera
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

}
