package com.zorona.liverooms;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.zorona.liverooms.modelclass.AdsRoot;
import com.zorona.liverooms.modelclass.GuestProfileRoot;
import com.zorona.liverooms.modelclass.SettingRoot;
import com.zorona.liverooms.modelclass.UserRoot;
import com.zorona.liverooms.reels.record.UploadActivity;
import com.zorona.liverooms.retrofit.Const;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SessionManager {
    private static final String USER_STR = "local_user";
    private static final String SETTING = "setting";
    private static final String SEARCHHISTORY = "searchhistry";
    private static final String ADS = "ads";
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this.pref = context.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        this.editor = this.pref.edit();
    }

    public static String getUserId(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        if (sessionManager.getBooleanValue(Const.ISLOGIN)) {
            return sessionManager.getUser().getId();
        }
        return "";
    }


    public void saveBooleanValue(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBooleanValue(String key) {
        return pref.getBoolean(key, false);
    }

    public void saveStringValue(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getStringValue(String key) {
        return pref.getString(key, "");
    }


    public void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        return pref.getInt(key, 0);
    }

    public void saveUser(UserRoot.User userDummy) {
        editor.putString(USER_STR, new Gson().toJson(userDummy));
        editor.apply();


    }

    public UserRoot.User getUser() {
        String userString = pref.getString(USER_STR, "");
        if (userString != null && !userString.isEmpty()) {
            return new Gson().fromJson(userString, UserRoot.User.class);
        }
        return null;
    }

    // workmanager ma big data transfer nathi thato so local ma save kari ne tya get karvama aave 6
    public void saveLocalVideo(UploadActivity.LocalVideo userDummy) {
        editor.putString("localvid", new Gson().toJson(userDummy));
        editor.apply();


    }

    public UploadActivity.LocalVideo getLocalVideo() {
        String userString = pref.getString("localvid", "");
        if (userString != null && !userString.isEmpty()) {
            return new Gson().fromJson(userString, UploadActivity.LocalVideo.class);
        }
        return null;
    }

    public void saveSetting(SettingRoot.Setting setting) {
        editor.putString(SETTING, new Gson().toJson(setting));
        editor.apply();
    }

    public SettingRoot.Setting getSetting() {
        String userString = pref.getString(SETTING, "");
        if (userString != null && !userString.isEmpty()) {
            return new Gson().fromJson(userString, SettingRoot.Setting.class);
        }
        return null;
    }


    public void addToSearchHistory(GuestProfileRoot.User newUser) {
        List<GuestProfileRoot.User> fav = getSearchHistory();

        for (GuestProfileRoot.User user : fav) {
            if (user.getUserId().equals(newUser.getUserId())) {
                fav.remove(user);
            }

        }

        fav.add(newUser);
        editor.putString(SEARCHHISTORY, new Gson().toJson(fav));
        editor.apply();
    }

    public void removefromSearchHistory(GuestProfileRoot.User newUser) {
        List<GuestProfileRoot.User> fav = getSearchHistory();
        for (int i = 0; i < fav.size(); i++) {
            if (fav.get(i).getUserId().equals(newUser.getUserId())) {
                fav.remove(i);
            }
        }
        
       /* for (GuestProfileRoot.User user: fav) {
            if (user.getUserId().equals(newUser.getUserId())){
                fav.remove(user);
            }

        }*/
        editor.putString(SEARCHHISTORY, new Gson().toJson(fav));
        editor.apply();
    }

    public List<GuestProfileRoot.User> getSearchHistory() {
        String userString = pref.getString(SEARCHHISTORY, "");
        if (!userString.isEmpty()) {
            List<GuestProfileRoot.User> list = new Gson().fromJson(userString, new TypeToken<ArrayList<GuestProfileRoot.User>>() {
            }.getType());
            Collections.reverse(list);
            return list;
        }
        return new ArrayList<>();
    }

    public void removeAllSearchHistory() {
        ArrayList<GuestProfileRoot.User> fav = new ArrayList<>();
        editor.putString(SEARCHHISTORY, new Gson().toJson(fav));
        editor.apply();

    }

    public void saveAds(AdsRoot.Advertisement setting) {
        editor.putString(ADS, new Gson().toJson(setting));
        editor.apply();
    }

    public AdsRoot.Advertisement getAds() {
        String userString = pref.getString(ADS, "");
        if (userString != null && !userString.isEmpty()) {
            return new Gson().fromJson(userString, AdsRoot.Advertisement.class);
        }
        return null;
    }

}
