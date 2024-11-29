
package com.app.liverooms.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.app.liverooms.BuildConfig;
import com.app.liverooms.agora.token.RtcTokenBuilder;
import com.app.liverooms.liveStreamming.GotoLiveActivity;
import com.app.liverooms.liveStreamming.HostLiveActivity;
//import com.app.liverooms.liveStreamming.WatchLiveActivity;
import com.app.liverooms.modelclass.LiveStreamRoot;
import com.app.liverooms.modelclass.LiveUserRoot;
import com.app.liverooms.modelclass.PostRoot;
import com.app.liverooms.modelclass.ReliteRoot;
import com.app.liverooms.reels.record.RecorderActivity;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.retrofit.RetrofitBuilder;
import com.app.liverooms.user.guestUser.GuestActivity;
import com.app.liverooms.MainApplication;
import com.app.liverooms.NetWorkChangeReceiver;
import com.app.liverooms.R;
import com.app.liverooms.chat.MessageFragment;
import com.app.liverooms.databinding.ActivityMainBinding;
import com.app.liverooms.databinding.BottomSheetChoicesBinding;
import com.app.liverooms.home.HomeFragment;
import com.app.liverooms.popups.PopupBuilder;
import com.app.liverooms.popups.PrivacyPopup_g;
import com.app.liverooms.posts.FeedFragmentMain;
import com.app.liverooms.posts.FeedListActivity;
import com.app.liverooms.posts.UploadPostActivity;
import com.app.liverooms.reels.ReelsActivity;
import com.app.liverooms.user.ProfileFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;
import retrofit2.Call;

public class MainActivity extends BaseActivity {

    public static final String IS_GUEST = "is_guest";

    ActivityMainBinding binding;
    private BottomSheetDialog bottomSheetDialog;
    private NetWorkChangeReceiver netWorkChangeReceiver;
    boolean isPrivate = true;

    @Override
    protected void onStart() {
        super.onStart();
        MainApplication.isAppOpen = true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        if (!sessionManager.getBooleanValue(Const.POLICY_ACCEPTED)) {
            new PrivacyPopup_g(this, new PrivacyPopup_g.OnSubmitClickListnear() {
                @Override
                public void onAccept() {
                    sessionManager.saveBooleanValue(Const.POLICY_ACCEPTED, true);
                    initMain();
                }

                @Override
                public void onDeny() {
                    finishAffinity();
                }
            });
        } else {
            initMain();
        }

        getStrickers();
        getAdsKeys();
    }


    private void initMain() {
        startReceiver();
        handleBranchData();
//        getGlobalSocket();
        createUserStatusSocket();
        makeOnlineUser();

        setDefaultBottomBar();
        setUpFragment(new HomeFragment(), binding.animHome);
        initBottomBar();
        binding.imgBall.setOnClickListener(v -> {
            bottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme);
            bottomSheetDialog.setOnShowListener(dialog -> {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            });

            BottomSheetChoicesBinding bottomSheetChoicesBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottom_sheet_choices, null, false);
            bottomSheetDialog.setContentView(bottomSheetChoicesBinding.getRoot());
            bottomSheetDialog.show();
            bottomSheetChoicesBinding.imgClose.setOnClickListener(v1 -> bottomSheetDialog.dismiss());
            bottomSheetChoicesBinding.lytLive.setOnClickListener(v1 -> {
                bottomSheetDialog.dismiss();
                if (!sessionManager.getUser().getLevel().getAccessibleFunction().isLiveStreaming()) {
                    Log.d(TAG, "initMain: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
                    new PopupBuilder(this).showSimplePopup("You are not able to go live at your level", "Dismiss", () -> {
                    });
                    return;
                }

                startActivity(new Intent(this, GotoLiveActivity.class));
            });

            bottomSheetChoicesBinding.lytVideos.setOnClickListener(v1 -> {
                bottomSheetDialog.dismiss();
                if (!sessionManager.getUser().getLevel().getAccessibleFunction().isUploadVideo()) {
                    new PopupBuilder(this).showSimplePopup("You are not able to create relite at your level", "Dismiss", () -> {
                    });
                    return;
                }

                startActivity(new Intent(this, RecorderActivity.class));
            });

            bottomSheetChoicesBinding.lytMoments.setOnClickListener(v1 -> {
                bottomSheetDialog.dismiss();
                if (!sessionManager.getUser().getLevel().getAccessibleFunction().isUploadPost()) {
                    new PopupBuilder(this).showSimplePopup("You are not able to upload post at your level", "Dismiss", () -> {
                    });
                    return;
                }
                startActivity(new Intent(this, UploadPostActivity.class));
            });


        });
    }

    private void createUserStatusSocket() {
        IO.Options options = IO.Options.builder()
                // IO factory options
                .setForceNew(false)
                .setMultiplex(true)

                // low-level engine options
                .setTransports(new String[]{Polling.NAME, WebSocket.NAME})
                .setUpgrade(true)
                .setRememberUpgrade(false)
                .setPath("/socket.io/")
                .setQuery("userRoom=" + sessionManager.getUser().getId() + "")
                .setExtraHeaders(null)

                // Manager options
                .setReconnection(true)
                .setReconnectionAttempts(Integer.MAX_VALUE)
                .setReconnectionDelay(1_000)
                .setReconnectionDelayMax(5_000)
                .setRandomizationFactor(0.5)
                .setTimeout(20_000)

                // Socket options
                .setAuth(null)
                .build();

        URI uri = URI.create(BuildConfig.BASE_URL);
        Socket callSocket = IO.socket(uri, options);
        callSocket.connect();

        callSocket.on(Socket.EVENT_CONNECT, args -> {
            Log.e("TAG", "createUserStatusSocket: connected");
        });

    }

    private void handleBranchData() {
        Intent intent = getIntent();
        String branchData = intent.getStringExtra(Const.DATA);
        String type = intent.getStringExtra(Const.TYPE);
        if (branchData != null && !branchData.isEmpty()) {
            if (type.equals("POST")) {
                PostRoot.PostItem post = new Gson().fromJson(branchData, PostRoot.PostItem.class);
                List<PostRoot.PostItem> list = new ArrayList<>();
                list.add(post);
                startActivity(new Intent(this, FeedListActivity.class)

                        .putExtra(Const.POSITION, 0)
                        .putExtra(Const.DATA, new Gson().toJson(list)));

            } else if (type.equals("RELITE")) {
                ReliteRoot.VideoItem post = new Gson().fromJson(branchData, ReliteRoot.VideoItem.class);
                List<ReliteRoot.VideoItem> list = new ArrayList<>();
                list.add(post);
                startActivity(new Intent(this, ReelsActivity.class).putExtra(Const.POSITION, 0).putExtra(Const.DATA, new Gson().toJson(list)));
            } else if (type.equals("PROFILE")) {
                String userId = branchData;
                startActivity(new Intent(this, GuestActivity.class).putExtra(Const.USERID, userId));

            } else if (type.equals("LIVE")) {
              //  RtcTokenBuilder mainClass = new RtcTokenBuilder();
               // int agoraUID = mainClass.getAgoraUID();
                LiveUserRoot.UsersItem usersItem = new Gson().fromJson(branchData, LiveUserRoot.UsersItem.class);
                Log.d("TAG", "handleBranchData: live  " + usersItem.toString());
                // Starting HostLiveActivity for guests
                Intent start = new Intent(this, HostLiveActivity.class);
                intent.putExtra(Const.DATA, new Gson().toJson(usersItem));
                intent.putExtra(Const.IS_GUEST, true); // Set to true if the user is a guest
                startActivity(start);
            }
        }
    }

    private void setDefaultBottomBar() {
        binding.animHome.setImageTintList(ContextCompat.getColorStateList(this, R.color.gray_icon));
        binding.animDiscover.setImageTintList(ContextCompat.getColorStateList(this, R.color.gray_icon));
        binding.animChat.setImageTintList(ContextCompat.getColorStateList(this, R.color.gray_icon));
        binding.animProfile.setImageTintList(ContextCompat.getColorStateList(this, R.color.gray_icon));
    }

    private void setUpFragment(Fragment fragment, ImageView animHome) {
        setDefaultBottomBar();
        animHome.setImageTintList(ContextCompat.getColorStateList(this, R.color.offwhite));
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
    }

    private void initBottomBar() {
        binding.lytHome.setOnClickListener(v -> {
            setUpFragment(new HomeFragment(), binding.animHome);

        });
        binding.lytDiscover.setOnClickListener(v -> {
            setUpFragment(new FeedFragmentMain(), binding.animDiscover);

        });
        /*binding.lytChat.setOnClickListener(v -> {
            setUpFragment(new MessageFragment(), binding.animChat);

        });*/
        binding.lytProfile.setOnClickListener(v -> {
            setUpFragment(new ProfileFragment(), binding.animProfile);
        });
    }

    @Override
    protected void onDestroy() {
        unregisterNetworkChanges();
        MainApplication.isAppOpen = false;
        super.onDestroy();
    }

    protected void startReceiver() {
        netWorkChangeReceiver = new NetWorkChangeReceiver(this::showHideInternet);
        registerNetworkBroadcastForNougat();
    }

    private void registerNetworkBroadcastForNougat() {
        registerReceiver(netWorkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(netWorkChangeReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

    private void showHideInternet(Boolean isOnline) {
        Log.d("TAG", "showHideInternet: " + isOnline);
        final TextView tvInternetStatus = findViewById(R.id.tv_internet_status);

        if (isOnline) {
            if (tvInternetStatus != null && tvInternetStatus.getVisibility() == View.VISIBLE && tvInternetStatus.getText().toString().equalsIgnoreCase(getString(R.string.no_internet_connection))) {
                tvInternetStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                tvInternetStatus.setText(R.string.back_online);
                new Handler().postDelayed(() -> slideToTop(tvInternetStatus), 200);
            }
        } else {
            if (tvInternetStatus != null) {
                tvInternetStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                tvInternetStatus.setText(R.string.no_internet_connection);
                if (tvInternetStatus.getVisibility() == View.GONE) {
                    slideToBottom(tvInternetStatus);
                }
            }
        }
    }

    private void slideToTop(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.enter_up);

       /* TranslateAnimation animation = new TranslateAnimation(0f, 0f,  0f,view.getHeight());
        animation.setDuration(1000);
        view.startAnimation(animation);*/
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });
        view.startAnimation(animation);
    }

    private void slideToBottom(final View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.enter_down);

       /* TranslateAnimation animation = new TranslateAnimation(0f, 0f,  0f,view.getHeight());
        animation.setDuration(1000);
        view.startAnimation(animation);*/
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });
        view.startAnimation(animation);
    }
}