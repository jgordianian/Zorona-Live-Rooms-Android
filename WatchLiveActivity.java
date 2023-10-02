package com.zorona.liverooms.liveStreamming;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import com.zorona.liverooms.RayziUtils;
import com.zorona.liverooms.SessionManager;
import com.zorona.liverooms.agora.AgoraBaseActivity;
import com.zorona.liverooms.agora.stats.LocalStatsData;
import com.zorona.liverooms.agora.stats.RemoteStatsData;
import com.zorona.liverooms.agora.stats.StatsData;
import com.zorona.liverooms.agora.ui.VideoGridContainer;
import com.zorona.liverooms.bottomsheets.BottomSheetReport_g;
import com.zorona.liverooms.bottomsheets.UserProfileBottomSheet;
import com.zorona.liverooms.databinding.ActivityWatchLiveBinding;
import com.zorona.liverooms.emoji.EmojiBottomsheetFragment;
import com.zorona.liverooms.modelclass.GiftRoot;
import com.zorona.liverooms.modelclass.GuestProfileRoot;
import com.zorona.liverooms.modelclass.LiveStramComment;
import com.zorona.liverooms.modelclass.LiveUserRoot;
import com.zorona.liverooms.modelclass.StickerRoot;
import com.zorona.liverooms.modelclass.UserRoot;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.utils.Filters.FilterRoot;
import com.zorona.liverooms.utils.Filters.FilterUtils;
import com.zorona.liverooms.viewModel.EmojiSheetViewModel;
import com.zorona.liverooms.viewModel.ViewModelFactory;
import com.zorona.liverooms.viewModel.WatchLiveViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.video.VideoEncoderConfiguration;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class WatchLiveActivity extends AgoraBaseActivity {

    private static final String TAG = "watchliveact";
    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
    };
    ActivityWatchLiveBinding binding;
    private CircleImageView mic1,mic2,mic3,mic4,mic5,mic6,mic7,mic8;
    Handler handler = new Handler();
    SessionManager sessionManager;
    String token = "";
    EmojiBottomsheetFragment emojiBottomsheetFragment;
    private WatchLiveViewModel viewModel;

    private Queue<GiftRoot.GiftItem> giftQueue = new LinkedList<>();

    boolean isGiftPlaying = false;
    private boolean isAudioEnabled=false;

    private int userCount = 0; // Keep track of the number of users in the channel

    private LiveUserRoot.UsersItem host;

    private CircleImageView[] micImages;
    private boolean[] isAudioEnabledMic;

    private int currentOccupiedSeat = -1;
    private VideoGridContainer mVideoGridContainer;
    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    private EmojiSheetViewModel giftViewModel;
    private Emitter.Listener simpleFilterListner = args -> {
        if (args[0] != null) {
            runOnUiThread(() -> {

                String filtertype = null;

                filtertype = args[0].toString();
                FilterRoot filterRoot = new Gson().fromJson(filtertype, FilterRoot.class);
                if (filterRoot != null) {
                    if (filterRoot.getTitle().equalsIgnoreCase("None")) {
                        binding.imgFilter.setImageDrawable(null);
                    } else {
                        Glide.with(binding.imgFilter).load(FilterUtils.getDraw(filterRoot.getTitle())).into(binding.imgFilter);
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
                        Glide.with(binding.imgFilter2).load(FilterUtils.getDraw(filterRoot.getTitle())).into(binding.imgFilter2);
                    }
                }

            });

        }
    };


    // Update the UI to display the user count
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
                    LiveStramComment liveStramComment = new Gson().fromJson(data.toString(), LiveStramComment.class);
                    if (liveStramComment != null) {
                        viewModel.liveStramCommentAdapter.addSingleComment(liveStramComment);
                        binding.rvComments.smoothScrollToPosition(viewModel.liveStramCommentAdapter.getItemCount() - 1);
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

            Log.d(TAG, "giftloister : " + args);
            if (args[0] != null) {
                String data = args[0].toString();
                try {
                    JSONObject jsonObject = new JSONObject(data.toString());
                    if (jsonObject.get("gift") != null) {
                        GiftRoot.GiftItem giftData = new Gson().fromJson(jsonObject.get("gift").toString(), GiftRoot.GiftItem.class);
                        if (giftData != null) {

                            Glide.with(binding.imgGift).load(BuildConfig.BASE_URL + giftData.getImage())
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
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


        });


    };


    private Emitter.Listener viewListner = data -> {
        runOnUiThread(() -> {
            Object args = data[0];
            Log.d(TAG, "viewListner : " + args.toString());

            try {

                JSONArray jsonArray = new JSONArray(args.toString());
                viewModel.liveViewUserAdapter.addData(jsonArray);
                // binding.tvViewUserCount.setText(String.valueOf(jsonArray.length()));
                Log.d(TAG, "views2 : " + jsonArray);
            } catch (JSONException e) {
                Log.d(TAG, "207: ");
                e.printStackTrace();
            }
        });


    };

    private UserProfileBottomSheet userProfileBottomSheet;

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
                            Toast.makeText(WatchLiveActivity.this, "You are blocked by host", Toast.LENGTH_SHORT).show();
                            new Handler(Looper.myLooper()).postDelayed(() -> endLive(), 500);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    };
    // private boolean isVideoDecoded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_watch_live);
        giftViewModel = ViewModelProviders.of(this, new ViewModelFactory(new EmojiSheetViewModel()).createFor()).get(EmojiSheetViewModel.class);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new WatchLiveViewModel()).createFor()).get(WatchLiveViewModel.class);
        sessionManager = new SessionManager(this);
        binding.setViewModel(viewModel);
        viewModel.initLister();
        giftViewModel.getGiftCategory();
        micImages = new CircleImageView[8];
        isAudioEnabledMic = new boolean[8];
        micImages = new CircleImageView[8];
        isAudioEnabledMic = new boolean[8];

        // Initialize your micImages array here, e.g., micImages[0] = findViewById(R.id.mic1);
        // Initialize isAudioEnabledMic array to true for all mics
        micImages[0]=findViewById(R.id.mic1);
        micImages[1]=findViewById(R.id.mic2);
        micImages[2]=findViewById(R.id.mic3);
        micImages[3]=findViewById(R.id.mic4);
        micImages[4]=findViewById(R.id.mic5);
        micImages[5]=findViewById(R.id.mic6);
        micImages[6]=findViewById(R.id.mic7);
        micImages[7]=findViewById(R.id.mic8);


        setupMicClickListeners();
    }

    private void setupMicClickListeners() {
        for (int i = 0; i < micImages.length; i++) {
            final int micIndex = i;
            micImages[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentOccupiedSeat != -1 && currentOccupiedSeat != micIndex) {
                        // If a seat is already occupied and it's not the same seat
                        // Inform the user they need to leave the occupied seat first
                        // You can show a message, toast, or handle it as per your design
                        return;
                    }

                    isAudioEnabledMic[micIndex] = !isAudioEnabledMic[micIndex]; // Toggle audio status

                    if (isAudioEnabledMic[micIndex]) {
                        rtcEngine().enableAudio(); // Enable audio
                        micImages[micIndex].setImageResource(R.drawable.ic_user_place); // Change to enabled image
                    } else {
                        rtcEngine().disableAudio(); // Disable audio
                        micImages[micIndex].setImageResource(R.drawable.roommic); // Change to disabled image
                    }

                    if (isAudioEnabledMic[micIndex]) {
                        currentOccupiedSeat = micIndex;
                    } else {
                        currentOccupiedSeat = -1; // User left the seat
                    }
                }
            });
        }


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
            binding.tvUserId.setText(host.getUsername());


            // init agora cred

            // switchAudioRouteToSpeaker();


            joinChannel();
            //     setupAudioProfile();
            //  enableMicrophone();
            startBroadcast();
            initView();

            initLister();


            binding.rvComments.scrollToPosition(viewModel.liveStramCommentAdapter.getItemCount() - 1);

            getSocket().on(Socket.EVENT_CONNECT, args -> runOnUiThread(() -> {
                getSocket().on(Const.EVENT_SIMPLEFILTER, simpleFilterListner);
                getSocket().on(Const.EVENT_ANIMFILTER, animatedFilterListner);
                getSocket().on(Const.EVENT_GIF, giftListner);
                getSocket().on(Const.EVENT_COMMENT, commentListner);
                getSocket().on(Const.EVENT_GIFT, giftListner);
                getSocket().on(Const.EVENT_VIEW, viewListner);

                getSocket().on(Const.EVENT_BLOCK, blockedUsersListner);
                Log.d(TAG, "onCreate: live send");
                //  addLessView(true);
            }));

        }


    }


    private void addLessView(boolean isAdd) {
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

    private void joinChannel() {
        // Initialize token, extra info here before joining channel
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name and uid that
        // you use to generate this token.
        try {


            if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
                token = null; // default, no token
            }

            // Sets the channel profile of the Agora RtcEngine.
            // The Agora RtcEngine differentiates channel profiles and applies different optimization algorithms accordingly. For example, it prioritizes smoothness and low latency for a video call, and prioritizes video quality for a video broadcast.
            rtcEngine().setChannelProfile(io.agora.rtc2.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            // rtcEngine().enableVideo();
            // rtcEngine().enableAudio();
            //  rtcEngine().muteLocalAudioStream(true);
            rtcEngine().setEnableSpeakerphone(true);

            //  configVideo();
            Log.d("TAG", "joinChannel: " + config().getChannelName());
            rtcEngine().joinChannel(token, host.getChannel(), "", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Start broadcast

    private void startBroadcast() {
        Log.d(TAG, "startBroadcast: ");
        try {
            rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            //rtcEngine().enableAudio();
            rtcEngine().setEnableSpeakerphone(true);
            // SurfaceView surface = prepareRtcVideo(0, false);
            // mVideoGridContainer.addUserVideoSurface(0, surface, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initView() {
        //  mVideoGridContainer = binding.liveVideoGridLayout;
        //  mVideoGridContainer.setStatsManager(statsManager());
        emojiBottomsheetFragment = new EmojiBottomsheetFragment();
        userProfileBottomSheet = new UserProfileBottomSheet(this);
        if (rtcEngine() == null) {
            Log.d(TAG, "initView: rtc engine null");
            return;
        }
        //  rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);

    }

    @Override
    public void onBackPressed() {
        if (userCount == 0) {
            endLive();
        }
        else {

        }
    }

    private void endLive() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();


        statsManager().clearAllData();
    }


    public void onLocalAudioMuteClicked(View view) {
        viewModel.isMuted = !viewModel.isMuted;
        rtcEngine().muteLocalAudioStream(viewModel.isMuted);
        if (viewModel.isMuted) {
            binding.btnMute.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mute));

            Toast.makeText(this, "Mic muted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Mic unmuted", Toast.LENGTH_SHORT).show();
            binding.btnMute.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.unmute));
        }
    }
    private void initLister() {
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
        binding.lytHost.setOnClickListener(v -> getUser(host.getLiveUserId()));
        giftViewModel.finelGift.observe(this, giftItem -> {
            if (giftItem != null) {
                int totalCoin = giftItem.getCoin() * giftItem.getCount();
                if (sessionManager.getUser().getDiamond() < totalCoin) {
                    Toast.makeText(WatchLiveActivity.this, "You not have enough diamonds to send gift", Toast.LENGTH_SHORT).show();
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
                    jsonObject.put("senderUserId", sessionManager.getUser().getId());
                    jsonObject.put("receiverUserId", host.getLiveUserId());
                    jsonObject.put("liveStreamingId", host.getLiveStreamingId());
                    jsonObject.put("userName", sessionManager.getUser().getName());
                    jsonObject.put("coin", giftItem.getCoin() * giftItem.getCount());
                    jsonObject.put("gift", new Gson().toJson(giftItem));
                    getSocket().emit(Const.EVENT_NORMALUSER_GIFT, jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

                    if (userData != null) {
                        if (userData.getUserId().equals(host.getLiveUserId())) {
                            userProfileBottomSheet.show(false, userData, host.getLiveStreamingId());
                        } else {
                            userProfileBottomSheet.show(false, userData, "");
                        }
                    }
                }
            });
            getSocket().off(Const.EVENT_GET_USER);
        });
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fromUserId", sessionManager.getUser().getId());
            jsonObject.put("toUserId", userId);
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

  /*  @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

    }*/

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void onClickBack(View view) {
        onBackPressed();
    }

    public void onClickSendComment(View view) {
        String comment = binding.etComment.getText().toString();
        if (!comment.isEmpty()) {
            binding.etComment.setText("");
            LiveStramComment liveStramComment = new LiveStramComment(host.getLiveStreamingId(), comment, sessionManager.getUser(), false);
            getSocket().emit(Const.EVENT_COMMENT, new Gson().toJson(liveStramComment));
//            try {
//                JSONObject jsonObject = new JSONObject();
//               jsonObject.put("liveStreamingId", host.getLiveStreamingId());
//                jsonObject.put("comment", new Gson().toJson(liveStramComment));
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }

    public void onclickShare(View view) {


        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setTitle("Watch Live Video")
                .setContentDescription("By : " + host.getName())
                .setContentImageUrl(host.getImage())
                .setContentMetadata(new ContentMetadata().addCustomMetadata("type", "LIVE").addCustomMetadata(Const.DATA, new Gson().toJson(host)));

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



    public void onclickGiftIcon(View view) {
        emojiBottomsheetFragment.show(getSupportFragmentManager(), "emojifragfmetn");
    }

  /*  @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        runOnUiThread(() -> {
            isVideoDecoded = true;
            renderRemoteUser(uid);
            addLessView(true);
        });
    }*/

    private void renderRemoteUser(int uid) {
        Log.d(TAG, "renderRemoteUser: ");
        //   SurfaceView surface = prepareRtcVideo(uid, false);
        //   mVideoGridContainer.addUserVideoSurface(uid, surface, false);
        LiveStramComment liveStramComment = new LiveStramComment(host.getLiveStreamingId(), "", sessionManager.getUser(), true);
        getSocket().emit(Const.EVENT_COMMENT, new Gson().toJson(liveStramComment));
//        try {
//
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("liveStreamingId", host.getLiveStreamingId());
//            jsonObject.put("comment", new Gson().toJson(liveStramComment));
//            getSocket().emit(Const.EVENT_COMMENT, jsonObject);
//
//            addLessView(true);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private void removeRemoteUser(int uid) {
        //   removeRtcVideo(uid, false);
        //  mVideoGridContainer.removeUserVideo(uid, false);
    }

    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
        Log.d(TAG, "onLeaveChannel: stts" + stats);
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.d(TAG, "onJoinChannelSuccess: ");

      /*  new Handler().postDelayed(() -> {
            if (isVideoDecoded) {
                Log.d(TAG, "sssss=- run: yreeeeeeehhhhh  video decoded");
            } else {
                Toast.makeText(WatchLiveActivity.this, "Somwthing went wrong", Toast.LENGTH_SHORT).show();
                endLive();
            }
        }, 5000);*/
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        Log.d(TAG, "onUserOffline: " + uid + " reason" + reason);
        userCount--; // Decrement the user count when a user leaves the channel
        updateUI(); // Update the UI to display the new user count
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeRemoteUser(uid);
                endLive();
            }
        });
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

   /* @Override
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
        //   data.setVideoSendBitrate(stats.txVideoKBitRate);
        //   data.setVideoRecvBitrate(stats.rxVideoKBitRate);
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
    public void onClickReport(View view) {
        new BottomSheetReport_g(this, host.getLiveUserId(), () -> {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout,
                    (ViewGroup) findViewById(R.id.customtoastlyt));


            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();

        });
    }
}