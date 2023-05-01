package com.zorona.liverooms;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.AgeRangeType;
import com.google.api.services.people.v1.model.Birthday;
import com.google.api.services.people.v1.model.Date;
import com.google.api.services.people.v1.model.Gender;
import com.google.api.services.people.v1.model.Person;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;


public class GoogleLoginManager {

    public static final int RC_SIGN_IN = 100;
    private static final String TAG = "googleloginmanager";
    private final GoogleSignInClient mGoogleSignInClient;
    private Activity context = null;
    GoogleUser googleUser;
    private WeakReference<GoogleLoginManager> weakAct = new WeakReference<>(this);
    private GoogleSignInAccount account;
    private OnGoogleLoginListner onGoogleLoginListner;

    public GoogleLoginManager(Activity context, OnGoogleLoginListner onGoogleLoginListner) {
        this.context = context;
        this.onGoogleLoginListner = onGoogleLoginListner;
        googleUser = new GoogleUser();
        Scope myScope = new Scope("https://www.googleapis.com/auth/user.birthday.read");
        Scope myScope1 = new Scope("https://www.googleapis.com/auth/user.gender.read");
        Scope myScope4 = new Scope("https://www.googleapis.com/auth/userinfo.profile");
        Scope myScope2 = new Scope(Scopes.PLUS_ME);
        Scope myScope3 = new Scope(Scopes.PROFILE); //get name and id
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              //  .requestScopes(myScope, myScope2, myScope1, myScope3, myScope4)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.


            //  new GetProfileDetails(account, weakAct, context).execute();


            googleUser.setName(account.getDisplayName());
            googleUser.setEmail(account.getEmail());
            googleUser.setImage(account.getPhotoUrl() == null ? "" : account.getPhotoUrl().toString());
            onGoogleLoginListner.onLoginSuccess(googleUser);
        } catch (ApiException e) { //cancel choose acc will come here with status code 12501 if not check RESULT_OK
            // , more status code at:
            //https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInStatusCodes
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            onGoogleLoginListner.onFailure(e.toString());
        }
    }

    public void onLogin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        context.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void printBasic() {
        account = GoogleSignIn.getLastSignedInAccount(context);
        if (account!=null) {
            Log.d(TAG, "latest sign in: "
                    + "\n\tPhoto url:" + account.getPhotoUrl()
                    + "\n\tscopes:" + account.getGrantedScopes()
                    + "\n\tEmail:" + account.getEmail()
                    + "\n\tDisplay name:" + account.getDisplayName()
                    + "\n\tFamily(last) name:" + account.getFamilyName()
                    + "\n\tGiven(first) name:" + account.getGivenName()
                    + "\n\tId:" + account.getId()
                    + "\n\tIdToken:" + account.getIdToken()
            );

            googleUser.setName(account.getDisplayName());
            googleUser.setEmail(account.getEmail());
            googleUser.setImage(account.getPhotoUrl() == null ? "" : account.getPhotoUrl().toString());

        } else {
            Log.w(TAG, "basic info is null");
            onGoogleLoginListner.onFailure("Account is null");
        }
    }

    private void saveAdvanced(Person meProfile) {
        account = GoogleSignIn.getLastSignedInAccount(context);
        if (account!=null) {



           /* if (n.size() > 0) {
                try {
                    Log.d("hole", "g name: " + n);
                    editor.putString("givenName", n.get(0).getGivenName());
                    editor.putString("familyName", n.get(0).getFamilyName());
                    editor.putString("id", n.get(0).getMetadata().getSource().getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    //this one should act as fallback priority since it got problem to get name without wait for ~1 minute
                    // ... when create new account will get empty name
                    editor.putString("id", account.getId());
                    editor.putString("givenName", account.getGivenName());
                    editor.putString("familyName", account.getFamilyName());
                }
            }*/

            List<AgeRangeType> ages = meProfile.getAgeRanges();
            Log.d(TAG, "saveAdvanced:agerngs   " + meProfile.getAgeRange());
           /* Log.d(TAG, "saveAdvanced:ages size  " + ages.size());
            if (ages!=null && ages.size() > 0) {
                String gender = ages.get(0).getAgeRange();
                Log.d(TAG, "onPostExecute age: " + gender);

            } else {

                Log.d(TAG, "onPostExecute no age if set to private ");
            }*/

            List<Gender> genders = meProfile.getGenders();
            Log.d(TAG, "saveAdvanced:gende size  " + genders.size());
            if (genders!=null && genders.size() > 0) {
                String gender = genders.get(0).getValue();
                googleUser.setGender(gender);
                Log.d(TAG, "onPostExecute gender: " + gender);

            } else {
                Log.d(TAG, "onPostExecute no gender if set to private ");
            }
            List<Birthday> birthdays = meProfile.getBirthdays();
            if (birthdays!=null && birthdays.size() > 0) {
                for (Birthday b : birthdays) { //birthday still able to get even private, unlike gender
                    Date bdate = b.getDate();
                    if (bdate!=null) {
                        String bday, bmonth, byear;
                        if (bdate.getDay()!=null) bday = bdate.getDay().toString();
                        else bday = "";
                        if (bdate.getMonth()!=null) bmonth = bdate.getMonth().toString();
                        else bmonth = "";
                        if (bdate.getYear()!=null) byear = bdate.getYear().toString();
                        else byear = "";
                        Log.d(TAG, "saveAdvanced: " + bday + " " + bmonth + " " + byear + "  ;; " + bdate);
                        googleUser.setBirthYear(byear);


                    }
                }
            } else {
                Log.w(TAG, "saveAdvanced no birthday");
            }

            onGoogleLoginListner.onLoginSuccess(googleUser);
        } else {
            Log.w(TAG, "saveAdvanced no acc");
            onGoogleLoginListner.onFailure("Advance account not found");
        }
    }

    public interface OnGoogleLoginListner {
        void onLoginSuccess(GoogleUser googleUser);

        void onFailure(String err);
    }

    public static class GetProfileDetails extends AsyncTask<Void, Void, Person> {

        private PeopleService ps;
        private int authError = -1;
        private WeakReference<GoogleLoginManager> weakAct;
        private String TAG;

        public GetProfileDetails(GoogleSignInAccount account, WeakReference<GoogleLoginManager> weakAct, Context context) {
            this.TAG = TAG;
            this.weakAct = weakAct;
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                    context, Collections.singleton(Scopes.PROFILE));
            credential.setSelectedAccount(
                    new Account(account.getEmail(), "com.google"));
            HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
            JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
            ps = new PeopleService.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName("Google Sign In Quickstart")
                    .build();
        }

        @Override
        protected Person doInBackground(Void... params) {
            Person meProfile = null;
            try {
                meProfile = ps
                        .people()
                        .get("people/me")
                        .setPersonFields("names,genders,birthdays")
                        .execute();
            } catch (UserRecoverableAuthIOException e) {
                e.printStackTrace();
                authError = 0;
            } catch (GoogleJsonResponseException e) {
                e.printStackTrace();
                authError = 1;
            } catch (IOException e) {
                e.printStackTrace();
                authError = 2;
            }
            return meProfile;
        }

        @Override
        protected void onPostExecute(Person meProfile) {
            GoogleLoginManager mainAct = weakAct.get();
            if (mainAct!=null) {
                mainAct.printBasic();
                if (authError==0) { //app has been revoke, re-authenticated required.
                    mainAct.onLogin();
                } else if (authError==1) {
                    Log.w(TAG, "People API might not enable at" +
                            " https://console.developers.google.com/apis/library/people.googleapis.com/?project=<project name>");
                } else if (authError==2) {
                    Log.w(TAG, "API io error");
                } else {
                    if (meProfile!=null) {
                        mainAct.saveAdvanced(meProfile);

                    }
                }
            }
        }
    }

    public static class GoogleUser {
        private String name, email, image, gender, bdate;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getBirthYear() {
            return bdate;
        }

        public void setBirthYear(String bdate) {
            this.bdate = bdate;
        }
    }
}
