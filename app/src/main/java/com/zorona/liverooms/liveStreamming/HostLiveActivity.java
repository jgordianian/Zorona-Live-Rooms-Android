package com.zorona.liverooms.liveStreamming;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zorona.liverooms.BuildConfig;
import com.zorona.liverooms.MainApplication;
import com.zorona.liverooms.R;
import com.zorona.liverooms.agora.stats.LocalStatsData;
import com.zorona.liverooms.agora.stats.RemoteStatsData;
import com.zorona.liverooms.agora.stats.StatsData;
import com.zorona.liverooms.agora.ui.VideoGridContainer;
import com.zorona.liverooms.modelclass.LiveUserRoot;
import com.zorona.liverooms.utils.Filters.FilterRoot;
import com.zorona.liverooms.utils.Filters.FilterUtils;
import com.zorona.liverooms.RayziUtils;
import com.zorona.liverooms.SessionManager;
import com.zorona.liverooms.agora.AgoraBaseActivity;
import com.zorona.liverooms.bottomsheets.UserProfileBottomSheet;
import com.zorona.liverooms.databinding.ActivityHostLiveBinding;
import com.zorona.liverooms.emoji.EmojiBottomsheetFragment;
import com.zorona.liverooms.modelclass.GiftRoot;
import com.zorona.liverooms.modelclass.GuestProfileRoot;
import com.zorona.liverooms.modelclass.LiveStramComment;
import com.zorona.liverooms.modelclass.LiveStreamRoot;
import com.zorona.liverooms.modelclass.UserRoot;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.viewModel.EmojiSheetViewModel;
import com.zorona.liverooms.viewModel.HostLiveViewModel;
import com.zorona.liverooms.viewModel.ViewModelFactory;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class HostLiveActivity extends AgoraBaseActivity {
    public static final String TAG = "hostliveactivity";
    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
    };
    ActivityHostLiveBinding binding;
    SessionManager sessionManager;
    EmojiBottomsheetFragment emojiBottomsheetFragment;
    UserProfileBottomSheet userProfileBottomSheet;
    JSONArray blockedUsersList = new JSONArray();
    private HostLiveViewModel viewModel;
    //private VideoGridContainer mVideoGridContainer;
    private EmojiSheetViewModel giftViewModel;
    private int userCount = 0; // Keep track of the number of users in the channel
    private LiveUserRoot.UsersItem host;
    boolean isGiftPlaying = false;
    private Queue<GiftRoot.GiftItem> giftQueue = new LinkedList<>();
    List<Integer> activeSeats = new ArrayList<>();
    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }


    private Emitter.Listener simpleFilterListner = args -> {
        if (args[0] != null) {
            runOnUiThread(() -> {

                String filtertype = null;

                filtertype = args[0].toString();
                FilterRoot filterRoot = new Gson().fromJson(filtertype, FilterRoot.class);
                if (filterRoot != null) {
                    if (filterRoot.getTitle().equalsIgnoreCase("None")) {
                        Log.d(TAG, "initLister: null");
                        binding.imgFilter.setImageDrawable(null);
                    } else {
                        Log.d(TAG, "initLister: ffff");
                        Glide.with(binding.imgFilter).load(FilterUtils.getDraw(filterRoot.getTitle())).into(binding.imgFilter);
                        //  Glide.with(this).asGif().load(selectedFilter.getFilter()).into(binding.imgFilter);
                    }

                }

            });

        }


    };


    private Emitter.Listener animatedFilterListner = args -> {
        if (args[0] != null) {
            runOnUiThread(() -> {

                String filtertype = null;

                filtertype = args[0].toString();
                FilterRoot filterRoot = new Gson().fromJson(filtertype, FilterRoot.class);
                if (filterRoot != null) {
                    if (filterRoot.getTitle().equalsIgnoreCase("None")) {
                        binding.imgFilter2.setImageDrawable(null);
                    } else {
                        Glide.with(binding.imgFilter2).load(FilterUtils.getDraw(filterRoot.getTitle()))
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                .into(binding.imgFilter2);
                    }
                }

            });

        }
    };

    // Update the UI to display the user count whenever a user joins or leaves the channel
    private void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView userCountTextView = findViewById(R.id.userCountTextView);
                userCountTextView.setText(String.format("%d online users", userCount));
            }
        });
    }


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

    private Emitter.Listener giftListner = args -> {
        // Gift sent successfully
        // Call playNextGift() to play the next gift in the queue
        playNextGift();
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
                            }, 16000);
                            makeSound();
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

        });
    };


    private LiveStreamRoot.LiveUser liveUser;
    private Emitter.Listener viewListner = data -> {
        runOnUiThread(() -> {
            Object args = data[0];
            Log.d(TAG, "viewListner : " + args.toString());

            try {

                JSONArray jsonArray = new JSONArray(args.toString());
                viewModel.liveViewUserAdapter.addData(jsonArray);
               // binding.tvViewUserCount.setText(String.valueOf(jsonArray.length()));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_live);

        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
            // Initialize Agora Engine
        }

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

        viewModel.initLister();

        initView();

       //switchAudioRouteToSpeaker();

        joinChannel();
        //setupAudioProfile();
       // enableMicrophone();
        startBroadcast();

        initLister();
        getSocket().on(Socket.EVENT_CONNECT, args -> runOnUiThread(() -> {

            getSocket().on(Const.EVENT_SIMPLEFILTER, simpleFilterListner);
            getSocket().on(Const.EVENT_ANIMFILTER, animatedFilterListner);
            getSocket().on(Const.EVENT_GIF, giftListner);
            getSocket().on(Const.EVENT_COMMENT, commentListner);
            getSocket().on(Const.EVENT_GIFT, giftListner);
            getSocket().on(Const.EVENT_VIEW, viewListner);

        }));



      /*  SVGAImageView imageView = binding.svgaImage;r
        SVGAParser parser = new SVGAParser(this);
        try {
            parser.decodeFromURL(new URL("https://github.com/yyued/SVGA-Samples/blob/master/posche.svga?raw=true"), new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                    SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
                    imageView.setImageDrawable(drawable);
                    imageView.startAnimation();
                }

                @Override
                public void onError() {

                }
            }, new SVGAParser.PlayCallback() {
                @Override
                public void onPlay(@NonNull List<? extends File> list) {

                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/

    }

    @Override

    public void onBackPressed() {
        endLive();
    }

    private void endLive() {

       // removeRtcVideo(0, true);
        //
        //  mVideoGridContainer.removeUserVideo(0, true);

        startActivity(new Intent(this, LiveSummaryActivity.class).putExtra(Const.DATA, liveUser.getLiveStreamingId()));
        finish();

    }

    LinearLayout lytSeats = findViewById(R.id.lytSeats);
    ImageView seat1 = findViewById(R.id.seat1);
    ImageView seat2 = findViewById(R.id.seat2);
// Get references to other seats as needed



    private void joinChannel() {
        try {
            rtcEngine().setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            rtcEngine().enableAudio();

            Log.d("TAG", "joinChannel:tkn " + liveUser.getToken());
            Log.d("TAG", "joinChannel:chanel " + liveUser.getChannel());
            rtcEngine().joinChannel(liveUser.getToken(), liveUser.getChannel(), "", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//start agora.io broadcast

    private void startBroadcast() {
        Log.d(TAG, "startBroadcast: ");
        try {
            rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            rtcEngine().enableAudio();
            rtcEngine().setEnableSpeakerphone(true);

            // When the user starts speaking or based on your logic, update the activeSeats list
            activeSeats.clear(); // Clear the list to start fresh
            activeSeats.add(R.id.seat1); // Add seat IDs that should be highlighted
            activeSeats.add(R.id.seat2);
// Add more seat IDs as needed

// Iterate over the seat views and apply the highlight effect
            for (int i = 0; i < lytSeats.getChildCount(); i++) {
                View seat = lytSeats.getChildAt(i);
                if (activeSeats.contains(seat.getId())) {
                    // Apply the highlight effect to the seat
                    seat.setBackgroundColor(Color.YELLOW);
                } else {
                    // Remove the highlight effect from the seat
                    seat.setBackgroundColor(Color.TRANSPARENT);
                }
            }

            // Add code to create seats with mute/unmute functionality
            createSeatsWithMuteUnmute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSeatsWithMuteUnmute() {
        LinearLayout layout = findViewById(R.id.lytSeats); // Assuming you have a LinearLayout with id "seatsLayout" in your layout XML

        for (int i = 0; i < 8; i++) {
            LinearLayout seatLayout = new LinearLayout(this);
            seatLayout.setOrientation(LinearLayout.VERTICAL);
            seatLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView seatNumber = new TextView(this);
            seatNumber.setText("Seat " + (i + 1));
            seatNumber.setGravity(Gravity.CENTER);

            Button muteButton = new Button(this);
            muteButton.setText("Mute");
            muteButton.setTag(i); // Set the tag to identify the seat

            // Set click listener for mute/unmute functionality
            muteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int seatIndex = (int) v.getTag();
                    toggleMute(seatIndex);
                }
            });

            seatLayout.addView(seatNumber);
            seatLayout.addView(muteButton);

            layout.addView(seatLayout);
        }
    }

    private void toggleMute(int seatIndex) {
        // Assuming you have an array of mute states for each seat
        boolean[] muteStates = new boolean[8];
        muteStates[seatIndex] = !muteStates[seatIndex]; // Toggle the mute state

        // Assuming you have a list of Agora UIDs corresponding to each seat
        int[] agoraUids = new int[8];
        int uid = agoraUids[seatIndex]; // Get the Agora UID for the seat

        // Mute/unmute based on the seat's mute state
        if (muteStates[seatIndex]) {
            rtcEngine().muteRemoteAudioStream(uid, true); // Mute remote audio stream
        } else {
            rtcEngine().muteRemoteAudioStream(uid, false); // Unmute remote audio stream
        }
    }

    private void initView() {
        //  binding.tvDiamonds.setText(String.valueOf(sessionManager.getUser().getDiamond()));
        binding.tvRcoins.setText(String.valueOf(sessionManager.getUser().getRCoin()));


        //   mVideoGridContainer = binding.liveVideoGridLayout;
        //  mVideoGridContainer.setStatsManager(statsManager());
        emojiBottomsheetFragment = new EmojiBottomsheetFragment();
        userProfileBottomSheet = new UserProfileBottomSheet(this);


    }

    private void initLister() {
        viewModel.isShowFilterSheet.observe(this, aBoolean -> {
            Log.d(TAG, "initLister:filter sheet  " + aBoolean);
            if (aBoolean) {
                binding.lytFilters.setVisibility(View.VISIBLE);
            } else {
                binding.lytFilters.setVisibility(View.GONE);
            }
        });
        viewModel.selectedFilter.observe(this, selectedFilter -> {
            if (selectedFilter.getTitle().equalsIgnoreCase("None")) {
                Log.d(TAG, "initLister: null");
                binding.imgFilter.setImageDrawable(null);
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
                binding.imgFilter.setImageDrawable(null);
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

                // Enqueue the gift item
                giftQueue.add(giftItem);

                // Check if a gift is currently playing
                if (!isGiftPlaying) {
                    // If no gift is playing, start playing the next gift in the queue
                    playNextGift();
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

    private void playNextGift() {
        // Check if the gift queue is empty
        if (giftQueue.isEmpty()) {
            // No more gifts in the queue, set isGiftPlaying to false and return
            isGiftPlaying = false;
            return;
        }

        // Get the next gift from the queue
        GiftRoot.GiftItem nextGift = giftQueue.poll();

        // Set isGiftPlaying to true
        isGiftPlaying = true;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", sessionManager.getUser().getId());
            jsonObject.put("coin", nextGift.getCoin() * nextGift.getCount());
            jsonObject.put("gift", new Gson().toJson(nextGift));
            jsonObject.put("userName", sessionManager.getUser().getName());
            getSocket().emit(Const.EVENT_LIVEUSER_GIFT, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BIND_VOICE_INTERACTION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

    }

        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            return;
        }*

    }



    public void onClickFilter(View view) {
        viewModel.isShowFilterSheet.setValue(true);
        binding.rvFilters.setAdapter(viewModel.filterAdapter_tt);

    }



    /*public void onSwitchCameraClicked(View view) {
        rtcEngine().switchCamera();
    }*/


    @Override
    public void onErr(int err) {
        Log.d(TAG, "onErr: " + err);
    }

    @Override
    public void onConnectionLost() {
        Log.d(TAG, "onConnectionLost: ");
    }

  /*  @Override
        public void onVideoStopped() {
        Log.d(TAG, "onVideoStopped: ");
    }*/

    public void onClickGifIcon(View view) {
        viewModel.isShowFilterSheet.setValue(true);
        binding.rvFilters.setAdapter(viewModel.filterAdapter2);
    }

    public void onClickStickerIcon(View view) {
        viewModel.isShowFilterSheet.setValue(true);
        binding.rvFilters.setAdapter(viewModel.stickerAdapter);
    }

    public void onClickEmojiIcon(View view) {
    }

 /*   public void onLocalAudioMuteClicked(View view) {
        viewModel.isMuted = !viewModel.isMuted;
        rtcEngine().muteLocalAudioStream(viewModel.isMuted);
        if (viewModel.isMuted) {
            binding.btnMute.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mute));

            Toast.makeText(this, "Mic muted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Mic unmuted", Toast.LENGTH_SHORT).show();
            binding.btnMute.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.unmute));
        }
    }*/

    public void onclickGiftIcon(View view) {
        emojiBottomsheetFragment.show(getSupportFragmentManager(), "emojifragfmetn");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        statsManager().clearAllData();
    }

    public void onClickSendComment(View view) {
        String comment = binding.etComment.getText().toString();
        if (!comment.isEmpty()) {
            binding.etComment.setText("");
            LiveStramComment liveStramComment = new LiveStramComment(liveUser.getLiveStreamingId(), comment, sessionManager.getUser(), false);
            getSocket().emit(Const.EVENT_COMMENT, new Gson().toJson(liveStramComment));

            Log.d(TAG, "onClickSendComment: " + liveStramComment.toString());


//            try {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
//                jsonObject.put("comment", new Gson().toJson(liveStramComment));
//                getSocket().emit(Const.EVENT_COMMENT, jsonObject);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
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

 /*   @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                renderRemoteUser(uid);
            }
        });
    }*/

    private void renderRemoteUser(int uid) {
        // SurfaceView surface = prepareRtcVideo(uid, false);
        // mVideoGridContainer.addUserVideoSurface(uid, surface, false);
    }

    private void removeRemoteUser(int uid) {
        // removeRtcVideo(uid, false);
        //  mVideoGridContainer.removeUserVideo(uid, false);
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
        userCount++; // Decrement the user count when a user leaves the channel
        updateUI(); // Update the UI to display the new user count
    }

    @Override
    public void onLastmileQuality(int quality) {

    }

    @Override
    public void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result) {

    }

    private Handler uiHandler = new Handler();
    private Runnable updateUiRunnable = new Runnable() {
        @Override
        public void run() {
// TODO: Update the UI to reflect changes in the live room
            uiHandler.postDelayed(this, 1000); // Schedule the next update after 1 second
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        uiHandler.postDelayed(updateUiRunnable, 1000); // Schedule the first UI update after 1 second
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiHandler.removeCallbacks(updateUiRunnable); // Stop updating the UI when the activity is paused
    }

  /*  @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        VideoEncoderConfiguration.VideoDimensions mVideoDimension = VideoEncoderConfiguration.VD_960x720;
        data.setWidth(mVideoDimension.width);
        data.setHeight(mVideoDimension.height);
        data.setFramerate(stats.sentFrameRate);
    }*/

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setLastMileDelay(stats.lastmileDelay);
        //data.setVideoSendBitrate(stats.txVideoKBitRate);
        //data.setVideoRecvBitrate(stats.rxVideoKBitRate);
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

  /*  @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setWidth(stats.width);
        data.setHeight(stats.height);
        data.setFramerate(stats.rendererOutputFrameRate);
        data.setVideoDelay(stats.delay);
    }*/

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
    public void finish() {
        super.finish();
        statsManager().clearAllData();
    }

    ///   filter  gift sticker emoji
}