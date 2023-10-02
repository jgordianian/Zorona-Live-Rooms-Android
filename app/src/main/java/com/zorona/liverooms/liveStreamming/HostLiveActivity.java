package com.zorona.liverooms.liveStreamming;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.zorona.liverooms.BuildConfig;
import com.zorona.liverooms.MainApplication;
import com.zorona.liverooms.R;
import com.zorona.liverooms.RayziUtils;
import com.zorona.liverooms.SessionManager;
import com.zorona.liverooms.agora.AgoraBaseActivity;
import com.zorona.liverooms.agora.stats.LocalStatsData;
import com.zorona.liverooms.agora.stats.RemoteStatsData;
import com.zorona.liverooms.agora.stats.StatsData;
import com.zorona.liverooms.bottomsheets.UserProfileBottomSheet;
import com.zorona.liverooms.databinding.ActivityHostLiveBinding;
import com.zorona.liverooms.emoji.EmojiBottomsheetFragment;
import com.zorona.liverooms.modelclass.GiftRoot;
import com.zorona.liverooms.modelclass.GuestProfileRoot;
import com.zorona.liverooms.modelclass.LiveStramComment;
import com.zorona.liverooms.modelclass.LiveStreamRoot;
import com.zorona.liverooms.modelclass.LiveUserRoot;
import com.zorona.liverooms.modelclass.UserRoot;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.retrofit.UserApiCall;
import com.zorona.liverooms.viewModel.EmojiSheetViewModel;
import com.zorona.liverooms.viewModel.HostLiveViewModel;
import com.zorona.liverooms.viewModel.ViewModelFactory;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.Constants;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

// Add these imports at the top of your Java file

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import io.agora.rtm.RtmClient;

import io.agora.rtm.RtmChannel;

import com.zorona.liverooms.agora.token.RtcTokenBuilder;

public class HostLiveActivity extends AgoraBaseActivity {
    public static final String TAG = "hostliveactivity";
    ActivityHostLiveBinding binding;
    SessionManager sessionManager;
    String token = "";
    EmojiBottomsheetFragment emojiBottomsheetFragment;
    UserProfileBottomSheet userProfileBottomSheet;
    JSONArray blockedUsersList = new JSONArray();
    private HostLiveViewModel viewModel;
    private EmojiSheetViewModel giftViewModel;
    private static final int PERMISSION_REQ_CODE = 123;

    private boolean[] seatsOccupied = new boolean[8];

    // Add a list to track occupied seats
   // private Map<Integer, Integer> occupiedSeatsMap = new HashMap<>();

    // Define the HashMap using SeatKey as keys
    private Map<SeatKey, Integer> occupiedSeatsMap = new HashMap<>();
    private boolean[] seatsMuted = new boolean[8];

    private Button[] seatButtons = new Button[8]; // Assuming there are 8 seats

    private LiveUserRoot.UsersItem host;

    private boolean isGuest;

    private int userCount = 0; // Keep track of the number of users in the channel

    private final Handler handler = new Handler();
    private static final int OCCUPY_SEAT_INTERVAL_MS = 800; // 1/8 seconds in milliseconds

    private UserRoot.User user;
    private UserApiCall userApiCall;

    RtcTokenBuilder mainClass = new RtcTokenBuilder();
    int agoraUID = mainClass.getAgoraUID();

    // Initialize Agora RTM
    RtmClient rtmClient;
    RtmChannel rtmChannel;


    private Emitter.Listener gifListner = args -> {

    };

    private Emitter.Listener commentListner = args -> {
        if (args[0] != null) {
            runOnUiThread(() -> {

                Log.d(TAG, "commentlister : " + args[0]);

                String data = args[0].toString();
                if (!data.isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(data.toString());

                        LiveStramComment liveStramComment = new Gson().fromJson(jsonObject.toString(), LiveStramComment.class);

                        if (liveStramComment != null) {
                            viewModel.liveStramCommentAdapter.addSingleComment(liveStramComment);
                            binding.rvComments.smoothScrollToPosition(viewModel.liveStramCommentAdapter.getItemCount() - 1);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    };

    private LiveStreamRoot.LiveUser liveUser;
    private Emitter.Listener viewListner = data -> {
        runOnUiThread(() -> {
            Object args = data[0];
            Log.d(TAG, "viewListner : " + args.toString());

            try {

                JSONArray jsonArray = new JSONArray(args.toString());
                viewModel.liveViewUserAdapter.addData(jsonArray);
                //  binding.tvViewUserCount.setText(String.valueOf(jsonArray.length()));
                Log.d(TAG, "views2 : " + jsonArray);
                //  binding.tvNoOneJoined.setVisibility(viewModel.liveViewUserAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);

            } catch (JSONException e) {
                Log.d(TAG, "207: ");
                e.printStackTrace();
            }

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("blocked", blockedUsersList);
                getSocket().emit(Const.EVENT_BLOCK, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

    };

    private Emitter.Listener giftListner = args -> {
        runOnUiThread(() -> {
            if (args[0] != null) {


                Log.d(TAG, "giftloister : " + args.toString());
                String data = args[0].toString();
                try {
                    JSONObject jsonObject = new JSONObject(data.toString());
                    if (jsonObject.get("gift") != null) {
                        Log.d(TAG, "json gift : " + jsonObject.toString());
                        GiftRoot.GiftItem giftData = new Gson().fromJson(jsonObject.get("gift").toString(), GiftRoot.GiftItem.class);
                        if (giftData != null) {

                            Log.d(TAG, "sent a gift    :  " + BuildConfig.BASE_URL + giftData.getImage());

                            Glide.with(binding.imgGift).load(BuildConfig.BASE_URL + giftData.getImage())
                                    .into(binding.imgGift);
                            Glide.with(binding.imgGiftCount).load(RayziUtils.getImageFromNumber(giftData.getCount()))
                                    .into(binding.imgGiftCount);


                            String name = jsonObject.getString("userName").toString();
                            binding.tvGiftUserName.setText(name + " Sent a gift");

                            binding.lytGift.setVisibility(View.VISIBLE);
                            binding.tvGiftUserName.setVisibility(View.VISIBLE);
                            new Handler(Looper.myLooper()).postDelayed(() -> {
                                binding.lytGift.setVisibility(View.GONE);
                                binding.tvGiftUserName.setVisibility(View.GONE);
                                binding.tvGiftUserName.setText("");
                                binding.imgGift.setImageDrawable(null);
                                binding.imgGiftCount.setImageDrawable(null);
                            }, 13000);
                            // makeSound();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            // Determine if the user is a host or guest based on the 'isGuest' flag
            if (isGuest) {
                // This user is a guest, perform guest-specific actions
                if (args[1] != null) {  // gift sender user
                    Log.d(TAG, "user string   : " + args[1].toString());
                    try {
                        JSONObject jsonObject = new JSONObject(args[1].toString());
                        UserRoot.User user = new Gson().fromJson(jsonObject.toString(), UserRoot.User.class);
                        if (user != null) {
                            Log.d(TAG, ":getted user    " + user.toString());
                            if (user.getId().equals(sessionManager.getUser().getId())) {
                                sessionManager.saveUser(user);
                                giftViewModel.localUserCoin.setValue(user.getDiamond());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                if (args[2] != null) {   // host
                    Log.d(TAG, "host string   : " + args[2].toString());
                    try {
                        JSONObject jsonObject = new JSONObject(args[2].toString());
                        UserRoot.User host = new Gson().fromJson(jsonObject.toString(), UserRoot.User.class);
                        if (host != null) {
                            Log.d(TAG, ":getted host    " + host.toString());

                            binding.tvRcoins.setText(String.valueOf(host.getRCoin()));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                if (args[2] != null) {   // host
                    Log.d(TAG, "host string   : " + args[2].toString());
                    try {
                        JSONObject jsonObject = new JSONObject(args[2].toString());
                        UserRoot.User host = new Gson().fromJson(jsonObject.toString(), UserRoot.User.class);
                        if (host != null) {
                            Log.d(TAG, ":getted host    " + host.toString());
                            if (sessionManager.getUser().getId().equals(host.getId())) {
                                sessionManager.saveUser(host);
                                // binding.tvDiamonds.setText(String.valueOf(host.getDiamond()));
                                binding.tvRcoins.setText(String.valueOf(host.getRCoin()));
                                giftViewModel.localUserCoin.setValue(host.getDiamond());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    };

    private Emitter.Listener blockedUsersListner = args -> {
        Log.d(TAG, "blockedUsersListner: " + args[0].toString());
        runOnUiThread(() -> {
            if (args[0] != null) {
                Object data = args[0];
                try {
                    JSONObject jsonObject = new JSONObject(data.toString());
                    JSONArray blockedList = jsonObject.getJSONArray("blocked");
                    for (int i = 0; i < blockedList.length(); i++) {
                        Log.d(TAG, "block user : " + blockedList.get(i).toString());
                        if (blockedList.get(i).toString().equals(sessionManager.getUser().getId())) {
                            Toast.makeText(HostLiveActivity.this, "You are blocked by host", Toast.LENGTH_SHORT).show();
                            if(isGuest){
                                new Handler(Looper.myLooper()).postDelayed(() -> endLiveGuest(), 500);
                            }else {
                                new Handler(Looper.myLooper()).postDelayed(() -> endLive(), 500);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    };

    // Update the UI to display the user count
    private void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView userCountTextView = findViewById(R.id.userCountTextView);
                userCountTextView.setText(String.format("%d online", userCount));
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_live);

        // Retrieve the 'IS_GUEST' flag from the intent
        isGuest = getIntent().getBooleanExtra(Const.IS_GUEST, false);



        // Determine if the user is a host or guest based on the 'isGuest' flag
        if (isGuest) {

            giftViewModel = ViewModelProviders.of(this, new ViewModelFactory(new EmojiSheetViewModel()).createFor()).get(EmojiSheetViewModel.class);
            viewModel = ViewModelProviders.of(this, new ViewModelFactory(new HostLiveViewModel()).createFor()).get(HostLiveViewModel.class);
            sessionManager = new SessionManager(this);
            binding.setViewModel(viewModel);
            viewModel.initLister();
            giftViewModel.getGiftCategory();

            Intent intent = getIntent();
            String userStr = intent.getStringExtra(Const.DATA);
            if (userStr != null && !userStr.isEmpty()) {
                host = new Gson().fromJson(userStr, LiveUserRoot.UsersItem.class);
                token = host.getToken();

                initSoketIo(host.getLiveStreamingId(), false);

                Glide.with(this).load(host.getImage())
                        .apply(MainApplication.requestOptions)
                        .circleCrop().into(binding.imgProfile);
                binding.tvCountry.setText(String.valueOf(host.getCountry()));
                if (host.getCountry() == null || host.getCountry().isEmpty()) {
                    binding.tvCountry.setVisibility(View.GONE);
                }

                binding.tvRcoins.setText(String.valueOf(host.getRCoin()));
                binding.tvName.setText(host.getName());


                // init agora cred


                initView();
                joinChannel();

                initLister();


                binding.rvComments.scrollToPosition(viewModel.liveStramCommentAdapter.getItemCount() - 1);

                getSocket().on(Socket.EVENT_CONNECT, args -> runOnUiThread(() -> {
                    getSocket().on(Const.EVENT_GIF, gifListner);
                    getSocket().on(Const.EVENT_COMMENT, commentListner);
                    getSocket().on(Const.EVENT_GIFT, giftListner);
                    getSocket().on(Const.EVENT_VIEW, viewListner);

                    getSocket().on(Const.EVENT_BLOCK, blockedUsersListner);
                    Log.d(TAG, "onCreate: live send");
                    //  addLessView(true);
                }));

                // Add listeners for seat occupancy updates
                getSocket().on("occupiedSeatsMapUpdated", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        // Handle the updated occupiedSeatsMap here
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject seatIndex = (JSONObject) args[0];
                                // Update your UI based on the data received
                                // data will contain the updated seat occupancy information
                            }
                        });
                    }
                });

            }

        } else {

            giftViewModel = ViewModelProviders.of(this, new ViewModelFactory(new EmojiSheetViewModel()).createFor()).get(EmojiSheetViewModel.class);
            viewModel = ViewModelProviders.of(this, new ViewModelFactory(new HostLiveViewModel()).createFor()).get(HostLiveViewModel.class);
            sessionManager = new SessionManager(this);
            binding.setViewModel(viewModel);

            giftViewModel.getGiftCategory();

            Intent intent = getIntent();
            if (intent != null) {
                String data = intent.getStringExtra(Const.DATA);
                String privacy = intent.getStringExtra(Const.PRIVACY);
                binding.tvPrivacy.setText(privacy);
                if (privacy.equalsIgnoreCase("Private")) {
                    binding.imgPrivacyk.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.lock));
                }
                if (data != null && !data.isEmpty()) {
                    liveUser = new Gson().fromJson(data, LiveStreamRoot.LiveUser.class);

                    Log.d(TAG, "onCreate: live room id " + liveUser.getLiveStreamingId());
                    initSoketIo(liveUser.getLiveStreamingId(), true);
                }
            }

            Glide.with(this).load(liveUser.getImage())
                    .apply(MainApplication.requestOptions)
                    .circleCrop().into(binding.imgProfile);
            binding.tvCountry.setText(String.valueOf(liveUser.getCountry()));
            if (liveUser.getCountry() == null || liveUser.getCountry().isEmpty()) {
                binding.tvCountry.setVisibility(View.GONE);
            }

            viewModel.initLister();
            initView();

            joinChannel();

            initLister();
            getSocket().on(Socket.EVENT_CONNECT, args -> runOnUiThread(() -> {


                getSocket().on(Const.EVENT_GIF, gifListner);
                getSocket().on(Const.EVENT_COMMENT, commentListner);
                getSocket().on(Const.EVENT_GIFT, giftListner);
                getSocket().on(Const.EVENT_VIEW, viewListner);

            }));

            // Add listeners for seat occupancy updates
            getSocket().on("occupiedSeatsMapUpdated", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    // Handle the updated occupiedSeatsMap here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject seatIndex = (JSONObject) args[0];
                            // Update your UI based on the data received
                            // data will contain the updated seat occupancy information
                        }
                    });
                }
            });
        }

        // Call the initSeatButtons method and pass the channel
        initSeatButtons();

    }

    // Function to join the Agora channel
    private void joinChannel() {
        try {
            // Join the Agora channel with the user's name as the seat identifier
            // Replace this with your Agora SDK logic to join the channel
            // For example: rtcEngine().joinChannel(null, channelName, null, userUid);

            rtcEngine().setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            rtcEngine().enableAudio(); // Enable audio
            rtcEngine().muteLocalAudioStream(true);
            rtcEngine().muteAllRemoteAudioStreams(false);

            rtcEngine().enableAudioVolumeIndication(200, 3, false); // Set up the callback

            // rtcEngine().setEnableSpeakerphone(true);

            if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
                token = null; // default, no token
            }




            if (isGuest) {
                // Join the channel
                Log.d("TAG", "joinChannel: " + host.getChannel());
                Log.d("TAG", "joinChannel:agoraUID " + agoraUID);
                // Now, you can use userIdString when joining the Agora channel
                // rtcEngine().joinChannel(liveUser.getToken(), liveUser.getChannel(), String.valueOf(agoraUID), agoraUID); // Omit the last argument or provide an empty string
                //rtcEngine().joinChannel(token, host.getChannel(), String.valueOf(agoraUID), agoraUID);
                rtcEngine().joinChannel(token, host.getChannel(), String.valueOf(agoraUID), agoraUID);
            } else {
                // Join the channel
                Log.d("TAG", "joinChannel:tkn " + liveUser.getToken());
                Log.d("TAG", "joinChannel:chanel " + liveUser.getChannel());
                Log.d("TAG", "joinChannel:agoraUID " + agoraUID);
                rtcEngine().joinChannel(liveUser.getToken(), liveUser.getChannel(), String.valueOf(agoraUID), agoraUID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (isGuest) {
            endLiveGuest();
            handler.removeCallbacksAndMessages(null);
        } else {
            endLive();
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void endLiveGuest() {
        addLessView(false);
        try {
            //   removeRtcVideo(0, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //  mVideoGridContainer.removeUserVideo(0, true);
        getSocket().disconnect();
        finish();

    }

    private void addLessView(boolean isAdd) {
        if (isGuest){
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("liveStreamingId", host.getLiveStreamingId());
                    jsonObject.put("liveUserMongoId", host.getId());
                    jsonObject.put("userId", sessionManager.getUser().getId());
                    jsonObject.put("isVIP", sessionManager.getUser().isIsVIP());
                    jsonObject.put("image", sessionManager.getUser().getImage());
                    if (isAdd) {
                        getSocket().emit(Const.EVENT_ADDVIEW, jsonObject);
                    } else {
                        getSocket().emit(Const.EVENT_LESSVIEW, jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
                jsonObject.put("liveUserMongoId", host.getId());
                jsonObject.put("userId", sessionManager.getUser().getId());
                jsonObject.put("isVIP", sessionManager.getUser().isIsVIP());
                jsonObject.put("image", sessionManager.getUser().getImage());
                if (isAdd) {
                    getSocket().emit(Const.EVENT_ADDVIEW, jsonObject);
                } else {
                    getSocket().emit(Const.EVENT_LESSVIEW, jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void endLive() {

        startActivity(new Intent(this, LiveSummaryActivity.class).putExtra(Const.DATA, liveUser.getLiveStreamingId()));
        finish();

    }

    private void initSeatButtons() {
        for (int i = 0; i < 8; i++) {
            int buttonId = getResources().getIdentifier("seat" + (i + 1) + "Button", "id", getPackageName());
            seatButtons[i] = findViewById(buttonId);
            final int seatIndex = i;

            // Handle long tap to join a seat or leave a joined seat
            seatButtons[i].setOnLongClickListener(v -> {
                if (seatsOccupied[seatIndex]) {
                    // The seat is already joined, so leave it
                    // Check seat occupancy with the backend before leaving
                    if(isGuest){
                        checkSeatAvailability(seatIndex, false, sessionManager.getUser().getUsername(), host.getLiveStreamingId()); // Replace "your_username_here" with the actual username
                    }else {
                        checkSeatAvailability(seatIndex, false, liveUser.getUsername(), liveUser.getLiveStreamingId());
                    }// Replace "your_username_here" with the actual username
                    // Clear the association of the user with this seat
                    seatsOccupied[seatIndex] = false;
                    if(isGuest){
                        SeatKey seatKey = new SeatKey(host.getLiveStreamingId(), seatIndex, agoraUID);
                        occupiedSeatsMap.remove(seatKey, agoraUID);
                    }else{
                        SeatKey seatKey = new SeatKey(liveUser.getLiveStreamingId(), seatIndex, agoraUID);
                        occupiedSeatsMap.remove(seatKey, agoraUID);
                    }
                } else {
                    // Implement logic to check if the user is already seated in another seat
                    if (isUserSeated()) {
                        // Show a Toast message indicating they can only join one seat at a time
                        Toast.makeText(this, "You can join only one seat at a time.", Toast.LENGTH_SHORT).show();
                    } else {
                        // User long-tapped an unoccupied seat, join the seat
                        // Check seat occupancy with the backend before joining
                        if(isGuest) {
                            checkSeatAvailability(seatIndex, true, sessionManager.getUser().getUsername(), host.getLiveStreamingId());// Replace "your_username_here" with the actual username
                        }
                        else {
                            checkSeatAvailability(seatIndex, true, liveUser.getUsername(), liveUser.getLiveStreamingId());// Replace "your_username_here" with the actual username
                        }
                    }
                }
                // Update the UI to reflect the seat state
                updateSeatUI(seatIndex);
                return true;
            });

            // Handle tap to mute/unmute if the seat is occupied
            seatButtons[i].setOnClickListener(v -> {
                // Implement logic to mute/unmute the user in this seat
                if (seatsOccupied[seatIndex]) {
                    onSeatTap(seatIndex);
                }
            });
        }
    }

    // Helper method to update the UI of a seat based on its state
    private void updateSeatUI(int seatIndex) {
        Button seatButton = seatButtons[seatIndex];
        if (seatsOccupied[seatIndex]) {
            // Change the button background or appearance to represent a joined seat
            seatButton.setBackgroundResource(R.drawable.roommic);
        } else {
            // Change the button background or appearance to represent an unoccupied seat
            seatButton.setBackgroundResource(R.drawable.seat);
        }
    }

    // Helper method to update the UI of a seat based on its state
    private void highlightSeat(int seatIndex) {
        Button seatButton = seatButtons[seatIndex];
        Log.d("Speaker", "Speaking Index: " + seatIndex);
            // Highlight the seat with a yellow round circle background
            seatButton.setBackgroundResource(R.drawable.yellow_round_circle);
    }

    public interface ApiService {
        // Define an endpoint to check if a seat is occupied with userName and channel
        @GET("/seat/{seatIndex}/{channel}")
        Call<Boolean> isSeatOccupiedWithuserName(@Path("seatIndex") int seatIndex,  @Path("channel") String channel);

        // Define an endpoint to mark a seat as occupied with userName and channel
        @POST("/seat/{seatIndex}/{userName}/{channel}/occupy")
        Call<Void> occupySeatWithuserName(@Path("seatIndex") int seatIndex, @Path("userName") String userName, @Path("channel") String channel);

        // Define an endpoint to mark a seat as unoccupied with userName and channel
        @POST("/seat/{seatIndex}/{userName}/{channel}/vacate")
        Call<Void> vacateSeatWithuserName(@Path("seatIndex") int seatIndex, @Path("userName") String userName, @Path("channel") String channel);
    }

    private void checkSeatAvailability(int seatIndex, boolean isJoining, String userName, String channel) {
        // Create an OkHttpClient with HttpLoggingInterceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor) // Add the logging interceptor
                .build();
        // Create an HTTP request to check seat occupancy with your Node.js backend
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL) // Replace with your backend URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Define your API endpoint for checking seat occupancy with userName
        Call<Boolean> call = apiService.isSeatOccupiedWithuserName(seatIndex, channel);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    boolean isOccupied = response.body();
                    if (isOccupied && isJoining) {
                        // The seat is already occupied, show a message or take appropriate action
                        Log.d("SeatStatus", "Seat " + (seatIndex + 1) + " is already occupied.");
                        Toast.makeText(HostLiveActivity.this, "Seat " + (seatIndex + 1) + " is already occupied.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Seat is available, join or leave as appropriate
                        if (isJoining) {
                            if(isGuest) {
                                joinSeat(seatIndex, sessionManager.getUser().getUsername(), host.getLiveStreamingId());
                                scheduleOccupyingTask(seatIndex, sessionManager.getUser().getUsername(), host.getLiveStreamingId());
                            }else{
                                joinSeat(seatIndex, liveUser.getUsername(), liveUser.getLiveStreamingId());
                                scheduleOccupyingTask(seatIndex, liveUser.getUsername(), liveUser.getLiveStreamingId());
                            }
                            seatsOccupied[seatIndex] = true;
                            // Mark the seat as occupied in the backend
                            if(isGuest) {
                                markSeatAsOccupied(seatIndex, sessionManager.getUser().getUsername(), host.getLiveStreamingId());
                            }else {
                                markSeatAsOccupied(seatIndex, liveUser.getUsername(), liveUser.getLiveStreamingId());
                            }
                        } else {
                            if(isGuest){
                                leaveSeat(seatIndex, sessionManager.getUser().getUsername(), host.getLiveStreamingId());
                            }else{
                                leaveSeat(seatIndex, liveUser.getUsername(), liveUser.getLiveStreamingId());
                            }
                            // Clear the association of the user with this seat
                            seatsOccupied[seatIndex] = false;
                            if (isGuest){
                                SeatKey seatKey = new SeatKey(host.getLiveStreamingId(), seatIndex, agoraUID);
                                occupiedSeatsMap.remove(seatKey, agoraUID);
                            }else{
                                SeatKey seatKey = new SeatKey(liveUser.getLiveStreamingId(), seatIndex, agoraUID);
                                occupiedSeatsMap.remove(seatKey, agoraUID);
                            }
                            // Mark the seat as unoccupied in the backend
                            if(isGuest) {
                                markSeatAsUnoccupied(seatIndex, sessionManager.getUser().getUsername(),host.getLiveStreamingId());
                            }else{
                                markSeatAsUnoccupied(seatIndex, liveUser.getUsername(), liveUser.getLiveStreamingId());
                            }
                            Log.d("SeatStatus", "User left seat " + (seatIndex + 1));
                        }
                    }
                } else {
                    // Handle an unsuccessful response from the backend
                    // You may want to retry or show an error message
                    Log.e("SeatStatus", "Failed to check seat occupancy. Response code: " + response.code());

                    // Log the response body if needed
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("SeatStatus", "Error body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // Update the UI to reflect the seat state
                updateSeatUI(seatIndex);
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                // Handle a network failure or other errors here
                Log.e("SeatStatus", "Failed to check seat occupancy. Error: " + t.getMessage());
            }
        });
    }

    // Helper method to schedule a task for occupying the seat every 4 seconds
    private void scheduleOccupyingTask(final int seatIndex, final String userName, String channel) {
        Runnable occupyingTask = new Runnable() {
            @Override
            public void run() {
                // Check if the user is still seated
                if (seatsOccupied[seatIndex]) {
                    // Mark the seat as occupied again
                    if(isGuest) {
                        markSeatAsOccupied(seatIndex, sessionManager.getUser().getUsername(), host.getLiveStreamingId());
                    }else {
                        markSeatAsOccupied(seatIndex, liveUser.getUsername(), liveUser.getLiveStreamingId());
                    }

                    // Schedule the task again after 4 seconds
                    handler.postDelayed(this, OCCUPY_SEAT_INTERVAL_MS);
                }
            }
        };

        // Schedule the initial task to occupy the seat after 4 seconds
        handler.postDelayed(occupyingTask, OCCUPY_SEAT_INTERVAL_MS);
    }

    // Helper method to mark a seat as occupied in the backend with userName
    private void markSeatAsOccupied(int seatIndex, String userName, String channel) {
        // Create an OkHttpClient with HttpLoggingInterceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor) // Add the logging interceptor
                .build();
        // Create an HTTP request to mark the seat as occupied with your Node.js backend
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL) // Replace with your backend URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Define your API endpoint for marking the seat as occupied with userName
        Call<Void> call = apiService.occupySeatWithuserName(seatIndex, userName, channel);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Handle a successful response from the backend
                    // You can update your UI or perform other actions here
                } else {
                    // Handle an unsuccessful response from the backend
                    // You may want to retry or show an error message
                    Log.e("SeatStatus", "Failed to mark seat as occupied. Response code: " + response.code());

                    // Log the response body if needed
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("SeatStatus", "Error body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle a network failure or other errors here
                Log.e("SeatStatus", "Failed to mark seat as occupied. Error: " + t.getMessage());
            }
        });
    }

    // Helper method to mark a seat as unoccupied in the backend with userName
    private void markSeatAsUnoccupied(int seatIndex, String userName, String channel) {
        // Create an OkHttpClient with HttpLoggingInterceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor) // Add the logging interceptor
                .build();
        // Create an HTTP request to mark the seat as unoccupied with your Node.js backend
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL) // Replace with your backend URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Define your API endpoint for marking the seat as unoccupied with userName
        Call<Void> call = apiService.vacateSeatWithuserName(seatIndex, userName, channel);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                } else {
                    // Handle an unsuccessful response from the backend
                    // You may want to retry or show an error message
                    Log.e("SeatStatus", "Failed to mark seat as unoccupied. Response code: " + response.code());

                    // Log the response body if needed
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("SeatStatus", "Error body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle a network failure or other errors here
                Log.e("SeatStatus", "Failed to mark seat as unoccupied. Error: " + t.getMessage());
            }
        });
    }

    // Check if the user is already seated in a seat
    private boolean isUserSeated() {
        for (boolean occupied : seatsOccupied) {
            if (occupied) {
                return true;
            }
        }
        return false;
    }

    // Handle tap on a joined seat to mute/unmute the user
    private void onSeatTap(int seatIndex) {
        // Toggle mute/unmute for the user in this seat
        seatsMuted[seatIndex] = !seatsMuted[seatIndex];

        // Implement logic to mute/unmute the user's audio using Agora SDK
        rtcEngine().muteLocalAudioStream(seatsMuted[seatIndex]);

        // Update UI to indicate mute/unmute state
        Button seatButton = seatButtons[seatIndex];
        if (seatsMuted[seatIndex]) {
            // Change the button background or appearance to represent muted mic (replace with your logic)
            seatButton.setBackgroundResource(R.drawable.ic_mic_muted);
            Toast.makeText(this, "Seat " + (seatIndex + 1) + " Muted", Toast.LENGTH_SHORT).show();
        } else {
            // Change the button background or appearance to represent mic (replace with your logic)
            seatButton.setBackgroundResource(R.drawable.roommic);
            Toast.makeText(this, "Seat " + (seatIndex + 1) + " Unmuted", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to join a seat and associate a user with it
    private void joinSeat(int seatIndex, String username, String channel) {
        // Check if the seat is already occupied by another user
        if (occupiedSeatsMap.containsValue(seatIndex)) {
            // Seat is already occupied, show a message or take appropriate action
            Toast.makeText(this, "Seat " + (seatIndex + 1) + " is already occupied.", Toast.LENGTH_SHORT).show();
        } else {
            // Join the Agora channel with the user's name as the seat identifier
            // Replace this with your Agora SDK logic to join the channel
            // For example: rtcEngine().joinChannel(null, channelName, null, userUid);

            getSocket().emit("occupySeat", seatIndex);

            rtcEngine().setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            rtcEngine().enableAudio(); // Enable audio
            // Disable the user's audio when they leave the seat
            rtcEngine().muteLocalAudioStream(false);
            rtcEngine().muteAllRemoteAudioStreams(false);
          // rtcEngine().setEnableSpeakerphone(true);

            Toast.makeText(this, "Joined Seat: " + (seatIndex + 1) + "", Toast.LENGTH_SHORT).show();


        }

        // Associate the user's name (username) with the seat index
        if(isGuest){
            SeatKey seatKey = new SeatKey(host.getLiveStreamingId(), seatIndex, agoraUID);
            occupiedSeatsMap.put(seatKey, agoraUID); // Use the username as the key
        }else{
            SeatKey seatKey = new SeatKey(liveUser.getLiveStreamingId(), seatIndex, agoraUID);
            occupiedSeatsMap.put(seatKey, agoraUID); // Use the username as the key
        }

    }

    // Helper method to leave a seat and disassociate a user from it
    private void leaveSeat(int seatIndex, String userName, String channel) {
        // Check if the user is associated with the specified seat
     //   if (occupiedSeatsMap.containsKey(String.valueOf(agoraUID)) && occupiedSeatsMap.get(String.valueOf(agoraUID)) == seatIndex) {
            // Leave the Agora channel and release the seat
            // Replace this with your Agora SDK logic to leave the channel
            // For example: rtcEngine().leaveChannel();

            getSocket().emit("vacateSeat", seatIndex);

            // Disable the user's audio when they leave the seat
            rtcEngine().muteLocalAudioStream(true);
            rtcEngine().muteAllRemoteAudioStreams(false);
            Toast.makeText(this, "Left Seat: " + (seatIndex + 1), Toast.LENGTH_SHORT).show();

        // Associate the user's name (username) with the seat index
        if(isGuest){
            SeatKey seatKey = new SeatKey(host.getLiveStreamingId(), seatIndex, agoraUID);
            occupiedSeatsMap.remove(seatKey, agoraUID); // Use the username as the key
            // Remove the scheduled occupying task for this seat
            handler.removeCallbacksAndMessages(null);
        }else{
            SeatKey seatKey = new SeatKey(liveUser.getLiveStreamingId(), seatIndex, agoraUID);
            occupiedSeatsMap.remove(seatKey, agoraUID); // Use the username as the key
            // Remove the scheduled occupying task for this seat
            handler.removeCallbacksAndMessages(null);
        }
    }


    private void initView() {
        //  binding.tvDiamonds.setText(String.valueOf(sessionManager.getUser().getDiamond()));
        binding.tvRcoins.setText(String.valueOf(sessionManager.getUser().getRCoin()));


        //mVideoGridContainer = binding.liveVideoGridLayout;
        // mVideoGridContainer.setStatsManager(statsManager());
        emojiBottomsheetFragment = new EmojiBottomsheetFragment();
        userProfileBottomSheet = new UserProfileBottomSheet(this);


    }

    private void initLister() {

        viewModel.isShowFilterSheet.observe(this, aBoolean -> {
            Log.d(TAG, "initLister:filter sheet  " + aBoolean);
            if (aBoolean) {
                //  binding.lytFilters.setVisibility(View.VISIBLE);
            } else {
                // binding.lytFilters.setVisibility(View.GONE);
            }
        });
        viewModel.selectedFilter.observe(this, selectedFilter -> {
            if (selectedFilter.getTitle().equalsIgnoreCase("None")) {
                Log.d(TAG, "initLister: null");
                //  binding.imgFilter.setImageDrawable(null);
            } else {
                Log.d(TAG, "initLister: ffff");
//                  Glide.with(this).asGif().load(FilterUtils.getDraw(selectedFilter.getTitle())).into(binding.imgFilter);
            }
            getSocket().emit(Const.EVENT_ANIMFILTER, new Gson().toJson(selectedFilter));
            Log.d(HostLiveActivity.TAG + " ", "onBindViewHolder: 11===========" + selectedFilter.getTitle());
        });
        viewModel.selectedFilter2.observe(this, selectedFilter -> {
            if (selectedFilter.getTitle().equalsIgnoreCase("None")) {
                Log.d(TAG, "initLister: null");
                // binding.imgFilter.setImageDrawable(null);
            } else {
                Log.d(TAG, "initLister: ffff");

                //  Glide.with(this).asGif().load(selectedFilter.getFilter()).into(binding.imgFilter);
            }
            getSocket().emit(Const.EVENT_SIMPLEFILTER, new Gson().toJson(selectedFilter));
            Log.d(HostLiveActivity.TAG + " ", "onBindViewHolder: 11===========" + selectedFilter.getTitle());
        });
        viewModel.selectedSticker.observe(this, selectedSticker -> {
            binding.imgSticker.setImageURI(selectedSticker.getSticker());

            binding.imgSticker.setVisibility(View.VISIBLE);
            new Handler(Looper.myLooper()).postDelayed(() -> binding.imgSticker.setVisibility(View.GONE), 2000);
            getSocket().emit(Const.EVENT_GIF, new Gson().toJson(selectedSticker));

        });
        viewModel.clickedComment.observe(this, user -> {
            getUser(user.getId());

        });
        viewModel.clickedUser.observe(this, user -> {
            try {
                getUser(user.get("userId").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        binding.btnClose.setOnClickListener(v -> endLive());
        giftViewModel.finelGift.observe(this, giftItem -> {
            if (giftItem != null) {

                int totalCoin = giftItem.getCoin() * giftItem.getCount();
                if (sessionManager.getUser().getDiamond() < totalCoin) {
                    Toast.makeText(HostLiveActivity.this, "You not have enough diamonds to send gift", Toast.LENGTH_SHORT).show();
                    return;
                }


                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userId", sessionManager.getUser().getId());
                    jsonObject.put("coin", giftItem.getCoin() * giftItem.getCount());
                    jsonObject.put("gift", new Gson().toJson(giftItem));
                    jsonObject.put("userName", sessionManager.getUser().getName());
                    getSocket().emit(Const.EVENT_LIVEUSER_GIFT, jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        userProfileBottomSheet.setOnUserTapListner(user -> {  // for block user
            blockedUsersList.put(user.getUserId());
            Log.d(TAG, "initLister: blocked " + blockedUsersList.toString());

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("blocked", blockedUsersList);
                getSocket().emit(Const.EVENT_BLOCK, jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        });


    }

    private void getUser(String userId) {
        getSocket().on(Const.EVENT_GET_USER, args1 -> {
            runOnUiThread(() -> {
                if (args1[0] != null) {
                    String data = args1[0].toString();
                    Log.d(TAG, "initLister: usr sty1 " + data);
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = parser.parse(data);
                    Log.d(TAG, "initLister: usr sty2 " + mJson);
                    Gson gson = new Gson();
                    GuestProfileRoot.User userData = gson.fromJson(mJson, GuestProfileRoot.User.class);
                    Log.d(TAG, "initLister: user  " + userData.toString());
                    if (userData != null) {
                        userProfileBottomSheet.show(true, userData, "");

                    }
                }
            });


            getSocket().off(Const.EVENT_GET_USER);
        });
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fromUserId", sessionManager.getUser().getId());
            jsonObject.put("toUserId", userId);
            Log.d(TAG, "getUser:request  " + jsonObject);
            getSocket().emit(Const.EVENT_GET_USER, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErr(int err) {
        Log.d(TAG, "onErr: " + err);
    }

    @Override
    public void onConnectionLost() {
        Log.d(TAG, "onConnectionLost: ");
    }

   /* @Override
    public void onVideoStopped() {
        Log.d(TAG, "onVideoStopped: ");
    }*/


    public void onClickEmojiIcon(View view) {
    }

    public void onclickGiftIcon(View view) {
        emojiBottomsheetFragment.show(getSupportFragmentManager(), "emojifragfmetn");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        statsManager().clearAllData();
    }

    public void onClickSendComment(View view) {
        if (isGuest) {
            String comment = binding.etComment.getText().toString();
            if (!comment.isEmpty()) {
                binding.etComment.setText("");
                LiveStramComment liveStramComment = new LiveStramComment(liveUser.getLiveStreamingId(), comment, sessionManager.getUser(), false);
                getSocket().emit(Const.EVENT_COMMENT, new Gson().toJson(liveStramComment));
//            try {
//                JSONObject jsonObject = new JSONObject();
//               jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
//                jsonObject.put("comment", new Gson().toJson(liveStramComment));
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            }
        } else {
            String comment = binding.etComment.getText().toString();
            if (!comment.isEmpty()) {
                binding.etComment.setText("");
                LiveStramComment liveStramComment = new LiveStramComment(liveUser.getLiveStreamingId(), comment, sessionManager.getUser(), false);
                getSocket().emit(Const.EVENT_COMMENT, new Gson().toJson(liveStramComment));

                Log.d(TAG, "onClickSendComment: " + liveStramComment.toString());
            }
        }
    }


    public void onclickShare(View view) {
        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setTitle("Watch My Live Video")
                .setContentDescription("By : " + sessionManager.getUser().getName())
                .setContentImageUrl(sessionManager.getUser().getImage())
                .setContentMetadata(new ContentMetadata().addCustomMetadata("type", "LIVE").addCustomMetadata(Const.DATA, new Gson().toJson(liveUser)));

        LinkProperties lp = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .setCampaign("content 123 launch")
                .setStage("new user")

                .addControlParameter("", "")
                .addControlParameter("", Long.toString(Calendar.getInstance().getTimeInMillis()));

        buo.generateShortUrl(this, lp, (url, error) -> {
            Log.d(TAG, "initListnear: branch url" + url);
            try {
                Log.d(TAG, "initListnear: share");
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareMessage = url;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                Log.d(TAG, "initListnear: " + e.getMessage());
                //e.toString();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

    }

    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
        Log.d(TAG, "onLeaveChannel: stts " + stats);
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.d(TAG, "onJoinChannelSuccess: chanel " + channel + " uid" + uid + "  elapsed " + elapsed);
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        Log.d(TAG, "onUserOffline: " + uid + " reason" + reason);

        userCount--; // Decrement the user count when a user leaves the channel
        updateUI(); // Update the UI to display the new user count

    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        Log.d(TAG, "onUserJoined: " + uid + "  elapsed" + elapsed);

        userCount++; // Increment the user count when a user joins the channel
        updateUI(); // Update the UI to display the new user count


    }

    @Override
    public void onLastmileQuality(int quality) {

    }

    @Override
    public void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result) {

    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setLastMileDelay(stats.lastmileDelay);
        data.setAudioSendBitrate(stats.txAudioKBitRate);
        data.setAudioRecvBitrate(stats.rxAudioKBitRate);
        data.setCpuApp(stats.cpuAppUsage);
        data.setCpuTotal(stats.cpuAppUsage);
        data.setSendLoss(stats.txPacketLossRate);
        data.setRecvLoss(stats.rxPacketLossRate);
    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (!statsManager().isEnabled()) return;

        StatsData data = statsManager().getStatsData(uid);
        if (data == null) return;

        data.setSendQuality(statsManager().qualityToString(txQuality));
        data.setRecvQuality(statsManager().qualityToString(rxQuality));
    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setAudioNetDelay(stats.networkTransportDelay);
        data.setAudioNetJitter(stats.jitterBufferDelay);
        data.setAudioLoss(stats.audioLossRate);
        data.setAudioQuality(statsManager().qualityToString(stats.quality));
    }

    @Override
    public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {
        // Create a set to keep track of seat indices that should be highlighted
        Set<Integer> speakingSeats = new HashSet<>();

        for (IRtcEngineEventHandler.AudioVolumeInfo info : speakers) {
            if (isGuest) {
                int seatIndex = findSeatIndexByUidAndChannel(info.uid, host.getLiveStreamingId());
                if (info.volume > 10 && !seatsMuted[seatIndex]) {
                    speakingSeats.add(seatIndex);
                }
            } else {
                int seatIndex = findSeatIndexByUidAndChannel(info.uid, liveUser.getLiveStreamingId());
                if (info.volume > 10 && !seatsMuted[seatIndex]) {
                    speakingSeats.add(seatIndex);
                }
            }
        }

        // Now, loop through the seat buttons and update their background based on speaking state
        for (int seatIndex = 0; seatIndex < seatButtons.length; seatIndex++) {
            if (speakingSeats.contains(seatIndex)) {
                highlightSeat(seatIndex);
            } else {
                updateSeatUI(seatIndex);
            }
        }
    }



    // Helper method to find the seat index by UID and channel
    private int findSeatIndexByUidAndChannel(int uid, String channel) {
        for (Map.Entry<SeatKey, Integer> entry : occupiedSeatsMap.entrySet()) {
            SeatKey seatKey = entry.getKey();
            uid = entry.getValue(); // Assuming the value is the user ID
            Log.d("OccupiedSeatsMap", "Seat Index: " + seatKey.getSeatIndex() + ", UID: " + uid + ", Channel: " + seatKey.getChannel());
            if (seatKey.getagoraUID() == uid && seatKey.getChannel().equals(channel)) {
                return seatKey.getSeatIndex(); // Return the seat index associated with the UID and channel
            }
        }
        return -1; // User not found in occupied seats or the user's channel doesn't match
    }

    @Override
    public void finish() {
        super.finish();
        statsManager().clearAllData();
    }

    ///   filter  gift sticker emoji
}