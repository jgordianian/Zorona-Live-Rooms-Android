package com.app.liverooms.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.app.liverooms.BuildConfig;
import com.app.liverooms.FaceBookLoginManager;
import com.app.liverooms.GoogleLoginManager;
import com.app.liverooms.R;
import com.app.liverooms.modelclass.UserRoot;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.retrofit.RetrofitBuilder;
import com.app.liverooms.user.EditProfileActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivityActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "loginact";
    GoogleLoginManager googleLoginManager;

    FaceBookLoginManager faceBookLoginManager;
    private String androidId;
    private String token;

    Random random = new Random();
    Locale locale = Locale.getDefault(); // or specify a specific locale
    String userName = String.format(locale, "1%03d", random.nextInt(1000));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);

        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                return;
            }
            token = task.getResult();

            initMain();

        });


        try {
            faceBookLoginManager = new FaceBookLoginManager(this, facebookObject -> {
                Log.d(TAG, "onLoginSuccess: facebook  " + facebookObject.toString());
                JsonObject jsonObject = new JsonObject();
                try {
                    jsonObject.addProperty("email", facebookObject.getString("email"));
                    jsonObject.addProperty("name", facebookObject.getString("name"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //  jsonObject.addProperty("gender", "");

                try {
                    jsonObject.addProperty("image", "https://graph.facebook.com/" + facebookObject.getString("id") + "/picture?type=large");
                } catch (JSONException e) {
                    jsonObject.addProperty("image", "");
                    e.printStackTrace();
                }

                jsonObject.addProperty("loginType", 0);
                //jsonObject.addProperty("username", "");
                sendData(jsonObject);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void initMain() {
        googleLoginManager = new GoogleLoginManager(this, new GoogleLoginManager.OnGoogleLoginListner() {
            @Override
            public void onLoginSuccess(GoogleLoginManager.GoogleUser googleUser) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", googleUser.getName());
                // jsonObject.addProperty("gender", "");
                if (googleUser.getImage() != null && !googleUser.getImage().isEmpty()) {
                    jsonObject.addProperty("image", googleUser.getImage());
                }
                jsonObject.addProperty("email", googleUser.getEmail());
                jsonObject.addProperty("loginType", 0);
                //  jsonObject.addProperty("username", "");
                jsonObject.addProperty("username", userName);
                sendData(jsonObject);


            }

            @Override
            public void onFailure(String err) {
                Log.d(TAG, "onFailure: " + err.toString());

            }
        });
    }

    private void sendData(JsonObject jsonObject) {
        jsonObject.addProperty("age", 18);
        jsonObject.addProperty("country", sessionManager.getStringValue(Const.COUNTRY));
        jsonObject.addProperty("ip", sessionManager.getStringValue(Const.IPADDRESS));
        jsonObject.addProperty("identity", androidId);
        jsonObject.addProperty("fcmToken", token);

        Call<UserRoot> call = RetrofitBuilder.create().createUser(jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code()==200) {
                    if (response.body().isStatus() && response.body().getUser()!=null) {
                        sessionManager.saveUser(response.body().getUser());

                        checkData();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void checkData() {
        UserRoot.User user = sessionManager.getUser();
        if (user.getUsername().isEmpty() ||
                user.getGender().isEmpty()
        ) {
            startActivity(new Intent(this, EditProfileActivity.class));

        } else {
            sessionManager.saveBooleanValue(Const.ISLOGIN, true);
            startActivity(new Intent(this, MainActivity.class));
        }
    }


    public void onClickGoogle(View view) {


        googleLoginManager.onLogin();
       /* new BottomSheetGender_g(LoginType.quick, this, new BottomSheetGender_g.OnGenderSelectListner() {
            @Override
            public void onSelect(String g, String name) {

                if (g.isEmpty()) {
                    Toast.makeText(LoginActivityActivity.this, "Select Gender First", Toast.LENGTH_SHORT).show();
                    return;
                }


            }
        });*/

    }

    private int getAgeFromBYear(int birthYear) {
        Log.d(TAG, "onCreate: byear " + birthYear);
        Log.d(TAG, "onCreate: " + Calendar.getInstance().get(Calendar.YEAR));

        return Calendar.getInstance().get(Calendar.YEAR) - birthYear;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (requestCode==RC_SIGN_IN) {
            if (resultCode==Activity.RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                googleLoginManager.handleSignInResult(task);
            } else {
                Log.w(TAG, "failed, user denied OR no network OR jks SHA1 not configure yet at play console android project");
            }
        } else {
            faceBookLoginManager.callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private String getProfileUrl(String imageUrl, String gender) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            return imageUrl;
        }
        if (gender.equalsIgnoreCase(Const.FEMALE)) {
            imageUrl = BuildConfig.BASE_URL + "storage/female.png";
        } else if (gender.equalsIgnoreCase("MALE")) {
            imageUrl = BuildConfig.BASE_URL + "storage/male.png";

        } else return "";
        return imageUrl;
    }

    public void onClickFacebook(View view) {
//        try {
//            faceBookLoginManager.onClickLogin();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void onClickQuick(View view) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "");
        jsonObject.addProperty("gender", "");
        jsonObject.addProperty("image", "");
        jsonObject.addProperty("email", androidId);
        jsonObject.addProperty("loginType", 2);
        jsonObject.addProperty("username", userName);
        sendData(jsonObject);

    }

    public void onClickPrivacy(View view) {
        WebActivity.open(this, "Privacy Policy", sessionManager.getSetting().getPrivacyPolicyLink());
    }

    public enum LoginType {
        google, facebook, quick, mobile;
    }
}