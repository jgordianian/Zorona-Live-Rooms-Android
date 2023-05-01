package com.zorona.liverooms.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.zorona.liverooms.BuildConfig;
import com.zorona.liverooms.agora.rtc.EventHandler;
import com.zorona.liverooms.modelclass.AdsRoot;
import com.zorona.liverooms.modelclass.RestResponse;
import com.zorona.liverooms.modelclass.StickerRoot;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.retrofit.RetrofitBuilder;
import com.zorona.liverooms.retrofit.UserApiCall;
import com.zorona.liverooms.videocall.CallIncomeActivity;
import com.zorona.liverooms.MainApplication;
import com.zorona.liverooms.NetWorkChangeReceiver;
import com.zorona.liverooms.R;
import com.zorona.liverooms.RayziUtils;
import com.zorona.liverooms.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseActivity extends AppCompatActivity {
    public static final String TAG = "baseactivirty";
    public static boolean STATUS_VIDEO_CALL = false;

    protected SessionManager sessionManager;
    protected UserApiCall userApiCall;
    private String myAgoraToken;
    private NetWorkChangeReceiver netWorkChangeReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        sessionManager = new SessionManager(this);
        userApiCall = new UserApiCall(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            decorView.setOnApplyWindowInsetsListener((v, insets) -> {
                WindowInsets defaultInsets = v.onApplyWindowInsets(insets);
                return defaultInsets.replaceSystemWindowInsets(
                        defaultInsets.getSystemWindowInsetLeft(),
                        0,
                        defaultInsets.getSystemWindowInsetRight(),
                        defaultInsets.getSystemWindowInsetBottom());
            });

        }

        ViewCompat.requestApplyInsets(decorView);


        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_no_internet, null);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.gravity = Gravity.BOTTOM;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        final WindowManager mWindowManager = getWindow().getWindowManager();
        getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //   mWindowManager.addView(layout, params);
        getWindow().addContentView(layout, params);
    }

    public void doTransition(int type) {
        if (type == Const.BOTTOM_TO_UP) {

            overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_none);
        } else if (type == Const.UP_TO_BOTTOM) {
            overridePendingTransition(R.anim.exit_none, R.anim.enter_from_up);

        }

    }

    public void getAdsKeys() {
        Call<AdsRoot> call = RetrofitBuilder.create().getAds();
        call.enqueue(new Callback<AdsRoot>() {
            @Override
            public void onResponse(Call<AdsRoot> call, Response<AdsRoot> response) {
                if (response.code() == 200 && response.body().isStatus()) {
                    if (response.body().getAdvertisement() != null) {
                        sessionManager.saveAds(response.body().getAdvertisement());
                    }
                }
            }

            @Override
            public void onFailure(Call<AdsRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public void getStrickers() {
        Call<StickerRoot> call = RetrofitBuilder.create().getStickers();
        call.enqueue(new Callback<StickerRoot>() {
            @Override
            public void onResponse(Call<StickerRoot> call, Response<StickerRoot> response) {
                if (response.code() == 200 && response.body().isStatus()) {
                    if (!response.body().getSticker().isEmpty()) {
                        RayziUtils.setstickers(response.body().getSticker());
                    }
                }
            }

            @Override
            public void onFailure(Call<StickerRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public Socket getGlobalSocket() {
        Log.d(TAG, "getGlobalSocket: start");
        IO.Options options = IO.Options.builder()
                // IO factory options
                .setForceNew(false)
                .setMultiplex(true)

                // low-level engine options
                .setTransports(new String[]{Polling.NAME, WebSocket.NAME})
                .setUpgrade(true)
                .setRememberUpgrade(false)
                .setPath("/socket.io/")
                .setQuery("globalRoom=12021")
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
            runOnUiThread(() -> {
                if (args != null) {
                    Log.d(TAG, "connected: globelSoket");

                    callSocket.on(Const.EVENT_CALL_REQUEST, args1 -> {
                        if (args1 != null) {
                            Log.d(TAG, "EVENT_CALL_REQUEST  : " + args1.toString());
                            try {

                                JSONObject jsonObject = new JSONObject(args1[0].toString());
                                String userId1 = jsonObject.getString(Const.USERID1);
                                String userId2 = jsonObject.getString(Const.USERID2);
                                String user2Name = jsonObject.getString(Const.USER2_NAME);
                                String user2Image = jsonObject.getString(Const.USER2_IMAGE);
                                String callRoomId = jsonObject.getString(Const.CALL_ROOM_ID);
                                if (userId1.equals(sessionManager.getUser().getId())) {
                                    Log.d(TAG, "getGlobalSocket: is In CALl   " + BaseActivity.STATUS_VIDEO_CALL);
                                    if (!BaseActivity.STATUS_VIDEO_CALL) {
                                        BaseActivity.STATUS_VIDEO_CALL = true;
                                        Log.d(TAG, "getGlobalSocket:call Object " + jsonObject);
                                        startActivity(new Intent(BaseActivity.this, CallIncomeActivity.class).putExtra(Const.DATA, jsonObject.toString()));
                                    } else {
                                        // logic here   he is in other call
                                    }
                                }
                            } catch (JSONException e) {
                                Log.d(TAG, "getGlobalSocket: err " + e.toString());
                                e.printStackTrace();
                            }


                        }
                    });
                }
            });
        });
        return callSocket;
    }


    public void makeOnlineUser() {
        if (sessionManager.getBooleanValue(Const.ISLOGIN)) {
            try {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("userId", sessionManager.getUser().getId());
                Call<RestResponse> call = RetrofitBuilder.create().makeOnlineUser(jsonObject);
                call.enqueue(new Callback<RestResponse>() {
                    @Override
                    public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                        if (response.code() == 200) {
                            if (response.body().isStatus()) {
                                Log.d(TAG, "onResponse: user online now");
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<RestResponse> call, Throwable t) {
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    protected MainApplication application() {
        return (MainApplication) getApplication();
    }

    protected void registerRtcEventHandler(EventHandler handler) {
        application().registerEventHandler(handler);
    }

    protected void removeRtcEventHandler(EventHandler handler) {
        application().removeEventHandler(handler);
    }

    public void onClickBack(View view) {
        onBackPressed();
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
        Log.d(TAG, "showHideInternet: " + isOnline);
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
