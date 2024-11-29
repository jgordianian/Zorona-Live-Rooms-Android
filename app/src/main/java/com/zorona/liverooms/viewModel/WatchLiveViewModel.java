package com.app.liverooms.viewModel;

import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCaptureConfig;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.liverooms.liveStreamming.LiveStramCommentAdapter;
import com.app.liverooms.liveStreamming.LiveViewUserAdapter;
import com.app.liverooms.modelclass.UserRoot;

import org.json.JSONObject;

public class WatchLiveViewModel extends ViewModel {
    public CameraX.LensFacing lensFacing = CameraX.LensFacing.FRONT;
    public PreviewConfig.Builder builder;
    public PreviewConfig previewConfig;
    public Preview preview;
    public VideoCaptureConfig.Builder builder1;
    public VideoCaptureConfig videoCaptureConfig;
    public VideoCapture videoCapture;


    public LiveViewUserAdapter liveViewUserAdapter = new LiveViewUserAdapter();
    public LiveStramCommentAdapter liveStramCommentAdapter = new LiveStramCommentAdapter();
    public MutableLiveData<UserRoot.User> clickedComment = new MutableLiveData<>();
    public MutableLiveData<JSONObject> clickedUser = new MutableLiveData<>();
    public boolean isMuted;
    

    public void initLister() {
        liveStramCommentAdapter.setOnCommentClickListner((UserRoot.User userDummy) -> {
            clickedComment.setValue(userDummy);
        });
        liveViewUserAdapter.setOnLiveUserAdapterClickLisnter((JSONObject userDummy) -> clickedUser.setValue(userDummy));

    }

}
