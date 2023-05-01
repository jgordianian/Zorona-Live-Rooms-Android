package com.zorona.liverooms;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FaceBookLoginManager {
    private static final String TAG = "facebookloginmanager";
    public final CallbackManager callbackManager;
    private final Activity context;

    public FaceBookLoginManager(Activity context, OnFacebookLogin onFacebookLogin) {
        this.context = context;



        callbackManager = CallbackManager.Factory.create();
        FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), (object, response) -> {
                            if (object!=null) {
                                Log.d(TAG, "onSuccess: resp   " + response);
                                onFacebookLogin.onLoginSuccess(object);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
                Log.d("TAG", "onSuccess: ");

                // setFacebookData(loginResult);
            }

            @Override
            public void onCancel() {
                Log.d("TAG", "");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("TAG", "onError: " + error.getLocalizedMessage());
            }
        };
        LoginManager.getInstance().registerCallback(callbackManager, mFacebookCallback);
    }

    public void onClickLogin() {
        LoginManager.getInstance().logInWithReadPermissions(
                context,
                Arrays.asList("email", "public_profile")
        );
    }

    private void setFacebookData(LoginResult loginResult) {

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender,birthday,age_range");

        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                (object, response) -> {
                    // Application code
                    try {
                        Log.i("Response json ", object.toString());
                        Log.i("Response", response.toString());

                        String email = response.getJSONObject().getString("email");
                        String firstName = response.getJSONObject().getString("first_name");
                        String lastName = response.getJSONObject().getString("last_name");


                        Profile profile = Profile.getCurrentProfile();
                        String id = profile.getId();
                        String link = profile.getLinkUri().toString();
                        Log.i("Link", link);
                        if (Profile.getCurrentProfile()!=null) {
                            Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                        }

                        Log.i("Login" + "Email", email);
                        Log.i("Login" + "FirstName", firstName);
                        Log.i("Login" + "LastName", lastName);

                   /*     try {
                            String gender = response.getJSONObject().getString("gender");
                            Log.i("Login" + "Gender", gender);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            String gender = response.getJSONObject().getString("birthday");
                            Log.i("Login" + "birthday", gender);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
*/

/*
                        new GraphRequest(
                                AccessToken.getCurrentAccessToken(),
                                //      "/{person-id}/",
                                "/" + id + "/",
                                parameters,
                                HttpMethod.GET,
                                new GraphRequest.Callback() {

                                    public void onCompleted(GraphResponse response) {
                                        Log.d(TAG, "onCompleted: grapyth   " + response.toString());
                                        Log.d(TAG, "onCompleted: grapyth   " + response.getRawResponse());
                                        try {
                                            String gender = response.getJSONObject().getString("gender");
                                            Log.i("Login" + "Gender", gender);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            String gender = response.getJSONObject().getString("birthday");
                                            Log.i("Login" + "birthday", gender);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        */
                        /* handle the result *//*

                                    }

                                }

                        ).executeAsync();
*/

                    } catch (JSONException e) {
                        Log.d(TAG, "onCompleted:err  " + e.toString());
                        e.printStackTrace();
                    }
                });
        request.setParameters(parameters);
        request.executeAsync();
    }


    public interface OnFacebookLogin {
        void onLoginSuccess(JSONObject jsonObject);
    }
}
