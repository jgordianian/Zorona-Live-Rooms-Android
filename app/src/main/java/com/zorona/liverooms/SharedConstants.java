package com.app.liverooms;

import java.util.concurrent.TimeUnit;

final public class SharedConstants {

    public static final int DEFAULT_PAGE_SIZE = 15;

    public static final long MAX_DURATION = TimeUnit.SECONDS.toMillis(30);
    public static final long MIN_DURATION = TimeUnit.SECONDS.toMillis(5);
    public static final int MAX_RESOLUTION = 960;

    public static final int NOTIFICATION_DOWNLOAD = 60600 + 1;
    public static final int NOTIFICATION_GENERATE_PREVIEW = 60600 + 2;
    public static final int NOTIFICATION_SAVE_TO_GALLERY = 60600 + 3;
    public static final int NOTIFICATION_UPLOAD = 60600 + 4;
    public static final int NOTIFICATION_UPLOAD_FAILED = 60600 + 5;
    public static final int NOTIFICATION_WATERMARK = 60600 + 6;

    public static final String PREF_ADS_CONFIG = "ads_config";
    public static final String PREF_ADS_SYNCED_AT = "ads_synced_at";
    public static final String PREF_CLIPS_SEEN_UNTIL = "clip_seen_until";
    public static final String PREF_DEVICE_ID = "device_id";
    public static final String PREF_FCM_TOKEN = "fcm_token";
    public static final String PREF_FCM_TOKEN_SYNCED_AT = "fcm_token_synced_at";
    public static final String PREF_INTRO_SHOWN = "intro_shown";
    public static final String PREF_LANGUAGES_OFFERED = "languages_offered";
    public static final String PREF_LAUNCH_COUNT = "launch_count";
    public static final String PREF_PREFERRED_LANGUAGES = "preferred_languages";
    public static final String PREF_PROMOTIONS_SEEN_UNTIL = "promotions_seen_until";
    public static final String PREF_REVIEW_PROMPTED_AT = "review_prompted_at";
    public static final String PREF_SERVER_TOKEN = "server_token";

    public static final int REQUEST_CODE_LOGIN_EMAIL = 60600 + 2;
    public static final int REQUEST_CODE_LOGIN_GOOGLE = 60600 + 1;
    public static final int REQUEST_CODE_LOGIN_PHONE = 60600 + 2;
    public static final int REQUEST_CODE_PICK_DOCUMENT = 60600 + 3;
    public static final int REQUEST_CODE_PICK_LOCATION = 60600 + 4;
    public static final int REQUEST_CODE_PICK_SONG = 60600 + 5;
    public static final int REQUEST_CODE_PICK_SONG_FILE = 60600 + 6;
    public static final int REQUEST_CODE_PICK_STICKER = 60600 + 7;
    public static final int REQUEST_CODE_PICK_VIDEO = 60600 + 8;
    public static final int REQUEST_CODE_PERMISSIONS_DOWNLOAD = 60600 + 9;
    public static final int REQUEST_CODE_PERMISSIONS_SCAN = 60600 + 10;
    public static final int REQUEST_CODE_PERMISSIONS_UPLOAD = 60600 + 11;
    public static final int REQUEST_CODE_UPDATE_APP = 60600 + 12;
    public static final int REQUEST_CODE_UPLOAD_CLIP = 60600 + 13;

    public static final long SYNC_ADS_INTERVAL = TimeUnit.HOURS.toMillis(12);
    public static final long SYNC_FCM_INTERVAL = TimeUnit.MINUTES.toMillis(15);

    public static final long TIMEOUT_CONNECT = TimeUnit.SECONDS.toMillis(30);
    public static final long TIMEOUT_READ = TimeUnit.MINUTES.toMillis(1);
    public static final long TIMEOUT_WRITE = TimeUnit.MINUTES.toMillis(10);
}
