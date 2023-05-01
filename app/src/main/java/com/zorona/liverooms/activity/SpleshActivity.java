package com.zorona.liverooms.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.zorona.liverooms.modelclass.IpAddressRoot_e;
import com.zorona.liverooms.modelclass.SettingRoot;
import com.zorona.liverooms.modelclass.UserRoot;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.retrofit.RetrofitBuilder;
import com.zorona.liverooms.retrofit.UserApiCall;
import com.zorona.liverooms.MainApplication;
import com.zorona.liverooms.R;
import com.zorona.liverooms.SessionManager;
import com.zorona.liverooms.popups.PopupBuilder;
import com.facebook.FacebookSdk;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;

import io.branch.referral.Branch;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpleshActivity extends AppCompatActivity {
    private static final String TAG = "spleshact";
    SessionManager sessionManager;
    private String branchData = "";
    private String type = "";

    @Override
    public void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();

        // Branch init
        branch.initSession((referringParams, error) -> {
            if (error == null) {
                Log.i("BRANCH SDK1", referringParams.toString());
                try {
                    boolean isLinkClicked = referringParams.getBoolean("+clicked_branch_link");
                    Log.d(TAG, "onStart:is link clicked   " + isLinkClicked);

                    if (isLinkClicked) {
                        branchData = referringParams.getString(Const.DATA);
                        type = referringParams.getString(Const.TYPE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.i("BRANCH SDK2", error.getMessage());
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splesh);

        sessionManager = new SessionManager(this);

        try {
            FacebookSdk.sdkInitialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.splesh);
        anim.setFillAfter(true);
        findViewById(R.id.tv1).setVisibility(View.VISIBLE);
        findViewById(R.id.tv1).startAnimation(anim);

        MobileAds.initialize(this, initializationStatus -> {
        });
        String s = Build.SERIAL;
        Log.d(TAG, "onCreate: serial " + s);
        //  getIp();
        FirebaseMessaging.getInstance().subscribeToTopic("CHAPI").addOnCompleteListener(task -> Log.d("TAG", "onCreate: init msg"));

        checkNetwork();
    }



    private void getIp() {
        Call<IpAddressRoot_e> call = RetrofitBuilder.getIp().getIp();
        call.enqueue(new Callback<IpAddressRoot_e>() {
            @Override
            public void onResponse(Call<IpAddressRoot_e> call, Response<IpAddressRoot_e> response) {
                if (response.code() == 200 && response.body() != null) {
                    if (response.body().getCountry() != null) {
                        Log.d("TAG", "onResponse: get ip");

                        sessionManager.saveStringValue(Const.COUNTRY, response.body().getCountry());
                        sessionManager.saveStringValue(Const.CURRENT_CITY, response.body().getCity());
                        if (response.body().getQuery() != null) {
                            sessionManager.saveStringValue(Const.IPADDRESS, response.body().getQuery());
                        }
                        getSetting();
                    } else {

                    }
                } else {

                }

            }

            @Override
            public void onFailure(Call<IpAddressRoot_e> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());

            }
        });
    }

    private void getSetting() {

        Call<SettingRoot> call = RetrofitBuilder.create().getSettings();
        call.enqueue(new Callback<SettingRoot>() {
            @Override
            public void onResponse(Call<SettingRoot> call, Response<SettingRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        sessionManager.saveSetting(response.body().getSetting());
                        ((MainApplication) getApplication()).initAgora(SpleshActivity.this);
                        Const.setCurrency(sessionManager.getSetting().getCurrency());

                        if (sessionManager.getSetting().isIsAppActive()) {
                            gotoMainPage();
                        } else {
                            new PopupBuilder(SpleshActivity.this).showSimplePopup("We are Under Maintenance", "Dismiss", () -> finishAffinity());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SettingRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });


    }

    private void gotoMainPage() {
        new Handler(Looper.myLooper()).postDelayed(() -> {
            if (sessionManager.getBooleanValue(Const.ISLOGIN)) {
                UserApiCall userApiCall = new UserApiCall(this);
                userApiCall.getUser(user -> {

                    if (user.isIsBlock()) {
                        new PopupBuilder(SpleshActivity.this).showSimplePopup("You are blocked by admin", "Dismiss", () -> finishAffinity());

                    } else {
                        checkUser(user);
                    }
                });


            } else {
                startActivity(new Intent(SpleshActivity.this, LoginActivityActivity.class));
            }
        }, 500);
    }

    private void checkUser(UserRoot.User user) {
        Log.d(TAG, "checkUser: local Id " + sessionManager.getUser().getIdentity());
        Log.d(TAG, "checkUser: remote Id " + user.getIdentity());
        if (user.getIdentity().equals(sessionManager.getUser().getIdentity())) {
            sessionManager.saveUser(user);
            startActivity(new Intent(SpleshActivity.this, MainActivity.class)
                    .putExtra(Const.DATA, branchData).putExtra(Const.TYPE, type));
        } else {
            new PopupBuilder(this).showSimplePopup("You are logged in other devices", "Dismiss", () -> {
                GoogleSignInOptions gso = new GoogleSignInOptions.
                        Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                        build();

                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
                googleSignInClient.signOut();

                Toast.makeText(this, "Log Out", Toast.LENGTH_SHORT).show();

                sessionManager.saveUser(null);
                sessionManager.saveBooleanValue(Const.ISLOGIN, false);
                startActivity(new Intent(SpleshActivity.this, LoginActivityActivity.class));
                finish();

            });
        }

    }

    private void checkNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo activeNetInfo2 = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
        boolean isConnected2 = activeNetInfo2 != null && activeNetInfo2.isConnectedOrConnecting();
        showHideInternet(isConnected || isConnected2);
    }

    private void showHideInternet(Boolean isOnline) {
        Log.d(TAG, "showHideInternet: " + isOnline);
        final TextView tvInternetStatus = findViewById(R.id.tv_internet_status);

        if (isOnline) {
            getIp();
            if (tvInternetStatus != null && tvInternetStatus.getVisibility() == View.VISIBLE && tvInternetStatus.getText().toString().equalsIgnoreCase(getString(R.string.no_internet_connection))) {
                tvInternetStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                tvInternetStatus.setText(R.string.back_online);
                new Handler().postDelayed(() -> {
                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.enter_up);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            tvInternetStatus.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    tvInternetStatus.startAnimation(animation);
                }, 200);
            }
        } else {
            if (tvInternetStatus != null) {
                tvInternetStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                tvInternetStatus.setText(R.string.no_internet_connection);
                if (tvInternetStatus.getVisibility() == View.GONE) {
                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.enter_down);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            tvInternetStatus.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    tvInternetStatus.startAnimation(animation);
                }
            }
        }
    }
}