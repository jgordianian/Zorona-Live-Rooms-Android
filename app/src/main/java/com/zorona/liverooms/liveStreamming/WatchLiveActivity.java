package com.app.liverooms.liveStreamming;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.gson.JsonSyntaxException;
import com.app.liverooms.BuildConfig;
import com.app.liverooms.R;
import com.app.liverooms.RayziUtils;
import com.app.liverooms.SessionManager;
import com.app.liverooms.activity.MainActivity;
import com.app.liverooms.agora.AgoraBaseActivity;
import com.app.liverooms.agora.stats.LocalStatsData;
import com.app.liverooms.agora.stats.RemoteStatsData;
import com.app.liverooms.agora.stats.StatsData;
import com.app.liverooms.agora.token.RtcTokenBuilder;
import com.app.liverooms.bottomsheets.BottomSheetReport_g;
import com.app.liverooms.bottomsheets.UserProfileBottomSheet;
import com.app.liverooms.databinding.ActivityWatchLiveBinding;
import com.app.liverooms.emoji.EmojiBottomsheetFragment;
import com.app.liverooms.modelclass.GiftRoot;
import com.app.liverooms.modelclass.GuestProfileRoot;
import com.app.liverooms.modelclass.LiveStramComment;
import com.app.liverooms.modelclass.LiveUserRoot;
import com.app.liverooms.modelclass.UserRoot;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.utils.Filters.FilterRoot;
import com.app.liverooms.utils.Filters.FilterUtils;
import com.app.liverooms.viewModel.EmojiSheetViewModel;
import com.app.liverooms.viewModel.ViewModelFactory;
import com.app.liverooms.viewModel.WatchLiveViewModel;
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

import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.internal.RtcEngineImpl;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/*public class WatchLiveActivity extends AgoraBaseActivity {

    private static final String TAG = "watchliveact";
    ActivityWatchLiveBinding binding;
    Handler handler = new Handler();
    SessionManager sessionManager;
    String token = "";
    EmojiBottomsheetFragment emojiBottomsheetFragment;
    private WatchLiveViewModel viewModel;

    private int userCount = 0; // Keep track of the number of users in the channel

    private LiveUserRoot.UsersItem host;

    private static final int PERMISSION_REQ_CODE = 123;

    private EmojiSheetViewModel giftViewModel;

    private boolean[] seatsOccupied = new boolean[8];

    // Add a list to track occupied seats
    private Map<String, Integer> occupiedSeatsMap = new HashMap<>();
    private boolean[] seatsMuted = new boolean[8];

    // Define a threshold for audio level to consider a user as speaking
    private static final int SPEAKING_THRESHOLD = 15;

    private Button[] seatButtons = new Button[8]; // Assuming there are 8 seats

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
   /* private Emitter.Listener giftListner = args -> {

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
                            }, 13000);
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
    };*//*

    // Define a queue to store incoming gifts
    Queue<GiftRoot.GiftItem> giftQueue = new LinkedList<>();
    JSONObject jsonObject; // Declare jsonObject as a class-level variable

    private Emitter.Listener giftListner = args -> {
        runOnUiThread(() -> {
            if (args[0] != null) {
                Log.d(TAG, "giftListener: " + args.toString());
                String data = args[0].toString();
                try {
                    jsonObject = new JSONObject(data); // Assign the jsonObject here
                    if (jsonObject.has("gift")) {
                        Log.d(TAG, "json gift: " + jsonObject.toString());
                        GiftRoot.GiftItem giftData = null;
                        try {
                            giftData = new Gson().fromJson(jsonObject.get("gift").toString(), GiftRoot.GiftItem.class);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        if (giftData != null) {
                            // Add the gift to the queue
                            giftQueue.offer(giftData);

                            // Check if the queue is empty and no gift is currently playing
                            if (giftQueue.size() == 1) {
                                playNextGiftFromQueue();
                            }
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
                //Log.d(TAG, "host string: " + args[2].toString();
                try {
                    jsonObject = new JSONObject(args[2].toString()); // Assign the jsonObject here
                    UserRoot.User host = null;
                    try {
                        host = new Gson().fromJson(jsonObject.toString(), UserRoot.User.class);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                    if (host != null) {
                        Log.d(TAG, "getted host: " + host.toString());
                        if (sessionManager.getUser().getId().equals(host.getId())) {
                            sessionManager.saveUser(host);
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
    private Emitter.Listener viewListner = data -> {
        runOnUiThread(() -> {
            Object args = data[0];
            Log.d(TAG, "viewListner : " + args.toString());

            try {

                JSONArray jsonArray = new JSONArray(args.toString());
                viewModel.liveViewUserAdapter.addData(jsonArray);
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



        Intent intent = getIntent();
        String userStr = intent.getStringExtra(Const.DATA);
        if (userStr != null && !userStr.isEmpty()) {
            host = new Gson().fromJson(userStr, LiveUserRoot.UsersItem.class);
            token = host.getToken();

            initSoketIo(host.getLiveStreamingId(), false);

           /* Glide.with(this).load(host.getImage())
                    .apply(MainApplication.requestOptions)
                    .circleCrop().into(binding.imgProfile);*/
      /*      binding.tvCountry.setText(String.valueOf(host.getCountry()));
            if (host.getCountry() == null || host.getCountry().isEmpty()) {
                binding.tvCountry.setVisibility(View.GONE);
            }

            binding.tvRcoins.setText(String.valueOf(host.getRCoin()));
            binding.tvName.setText(host.getName());
            binding.tvUserId.setText(host.getUsername());


            // init agora cred


           // joinChannel();
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


        initSeatButtons();


    }

    @Override
    public void onUserMuteAudio(int uid, boolean muted) {

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

    // Function to play the next gift from the queue
    private void playNextGiftFromQueue() {
        try {
            if (!giftQueue.isEmpty()) {
                GiftRoot.GiftItem nextGift = giftQueue.poll();
                if (nextGift != null) {
                    // Play the gift here
                    playGift(nextGift);

                    // After playing the gift, check if there are more gifts in the queue
                    if (!giftQueue.isEmpty()) {
                        // Delay for some time (e.g., 5 seconds) before playing the next gift
                        new Handler(Looper.myLooper()).postDelayed(this::playNextGiftFromQueue, 5000);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle the JSONException here, e.g., log the error or take appropriate action
        }
    }

    // Function to play a gift
    private void playGift(GiftRoot.GiftItem giftData) throws JSONException {
        if (jsonObject != null) { // Check if jsonObject is not null
            Log.d(TAG, "sent a gift: " + BuildConfig.BASE_URL + giftData.getImage());

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

    // Call this function to start playing gifts
    private void startGiftQueue() throws JSONException {
        playNextGiftFromQueue();
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

            rtcEngine().setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            //rtcEngine().enableVideo(); // Enable video if needed
            rtcEngine().enableAudio(); // Enable audio
            rtcEngine().setEnableSpeakerphone(true);

            //  configVideo();
            Log.d("TAG", "joinChannel: " + host.getChannel());
            rtcEngine().joinChannel(token, host.getChannel(), "", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Initialize seat buttons and set click listeners
  /*  private void initSeatButtons() {
        for (int i = 0; i < 8; i++) {
            int buttonId = getResources().getIdentifier("seat" + (i + 1) + "Button", "id", getPackageName());
            seatButtons[i] = findViewById(buttonId);
            final int seatIndex = i;

            // Handle long tap to join a seat or leave a joined seat
            seatButtons[i].setOnLongClickListener(v -> {
                if (seatsOccupied[seatIndex]) {
                    // The seat is already joined, so leave it
                    onLeaveSeat(seatIndex);
                } else {
                    // Implement logic to check if the user is already seated in another seat
                    if (isUserSeated()) {
                        // Show a Toast message indicating they can only join one seat at a time
                        Toast.makeText(this, "You can join only one seat at a time.", Toast.LENGTH_SHORT).show();
                    } else {
                        // User long-tapped an unoccupied seat, join the seat
                        onJoinSeat(seatIndex);
                    }
                }
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

    // Check if the user is already seated in a seat
    private boolean isUserSeated() {
        for (boolean occupied : seatsOccupied) {
            if (occupied) {
                return true;
            }
        }
        return false;
    }

    // Helper method to join a seat and associate a user with it
    private void onJoinSeat(int seatIndex) {
        if (!seatsOccupied[seatIndex]) {
            // Check if the seat is not already occupied
            int userUid = 12345; // Replace with your logic to get the user's UID

            // Join the Agora channel with the user's UID as the seat identifier
            joinChannelWithSeat(userUid, seatIndex);

            seatsOccupied[seatIndex] = true;
            occupiedSeats.add(seatIndex); // Add to the list of occupied seats

            // Change the seat icon to a mic icon (replace with your drawable resource)
            seatButtons[seatIndex].setBackgroundResource(R.drawable.roommic);

            // Show a Toast message indicating successful seat joining
            Toast.makeText(this, "Joined seat " + (seatIndex + 1), Toast.LENGTH_SHORT).show();
        }
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

    // Helper method to leave a seat and disassociate a user from it
    private void onLeaveSeat(int seatIndex) {
        if (seatsOccupied[seatIndex]) {
            // Check if the seat is occupied before leaving it
            int userUid = 12345; // Replace with your logic to get the user's UID

            // Leave the Agora channel and release the seat
            leaveChannelWithSeat(userUid, seatIndex);

            // Update UI to indicate that the user has left the seat
            seatsOccupied[seatIndex] = false;
            occupiedSeats.remove(Integer.valueOf(seatIndex)); // Remove from the list of occupied seats

            // Reset the button's background to the default seat icon (replace with your drawable resource)
            seatButtons[seatIndex].setBackgroundResource(R.drawable.seat);

            // Show a Toast message to indicate leaving the seat
            Toast.makeText(this, "Left seat " + (seatIndex + 1), Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to join the Agora channel with a user's UID and associate them with a seat
    private void joinChannelWithSeat(int userUid, int seatIndex) {
        // Implement logic to join the Agora channel with the user's UID as the seat identifier
        // You should use Agora's SDK methods to join the channel and set the seat identifier.
        // Here's a simplified example:
        String channelName = "your_channel_name"; // Replace with your channel name
        rtcEngine().joinChannel(null, channelName, null, userUid);

        // Associate the seat with the user's UID
        occupiedSeatsMap.put(seatIndex, userUid);
    }

    // Helper method to leave the Agora channel and release a seat
    private void leaveChannelWithSeat(int userUid, int seatIndex) {
        // Implement logic to leave the Agora channel and release the seat
        // You should use Agora's SDK methods to leave the channel.
        // Here's a simplified example:
        rtcEngine().leaveChannel();

        // Release the seat by removing the association
        occupiedSeatsMap.remove(seatIndex);
    }

    // Implement the onAudioVolumeIndication callback
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
            for (AudioVolumeInfo info : speakers) {
                if (info.volume > SPEAKING_THRESHOLD) {
                    // User is speaking, find the corresponding seat and highlight it
                    int seatIndex = findSeatIndexByUid(info.uid);
                    if (seatIndex != -1) {
                        highlightSeat(seatIndex);
                    }
                }
            }
        }
    };

    // Helper method to find the seat index by user ID (uid)
    private int findSeatIndexByUid(int uid) {
        for (int i = 0; i < occupiedSeats.size(); i++) {
            if (occupiedSeats.get(i) == uid) {
                return i;
            }
        }
        return -1; // User not found in occupied seats
    }

    // Helper method to highlight the seat by changing its background
    private void highlightSeat(int seatIndex) {
        // Change the button background or appearance to represent speaking (replace with your logic)
        seatButtons[seatIndex].setBackgroundResource(R.drawable.speaking_roommic);
    }*//*

    // Initialize seat buttons and set click listeners
    private void initSeatButtons() {
        for (int i = 0; i < 8; i++) {
            int buttonId = getResources().getIdentifier("seat" + (i + 1) + "Button", "id", getPackageName());
            seatButtons[i] = findViewById(buttonId);
            final int seatIndex = i;

            // Handle long tap to join a seat or leave a joined seat
            seatButtons[i].setOnLongClickListener(v -> {
                if (seatsOccupied[seatIndex]) {
                    // The seat is already joined, so leave it
                    leaveChannelWithSeat(seatIndex);
                    // Update the seat state to unoccupied
                    seatsOccupied[seatIndex] = false;
                    // Update the UI to reflect the seat state
                    updateSeatUI(seatIndex);
                } else {
                    // Implement logic to check if the user is already seated in another seat
                    if (isUserSeated()) {
                        // Show a Toast message indicating they can only join one seat at a time
                        Toast.makeText(this, "You can join only one seat at a time.", Toast.LENGTH_SHORT).show();
                    } else {
                        // User long-tapped an unoccupied seat, join the seat
                        joinChannelWithSeat(seatIndex);
                        // Update the seat state to occupied
                        seatsOccupied[seatIndex] = true;
                        // Update the UI to reflect the seat state
                        updateSeatUI(seatIndex);
                    }
                }
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
    private void joinChannelWithSeat(int seatIndex) {
        // Check if the seat is already occupied by another user
        if (occupiedSeatsMap.containsValue(seatIndex)) {
            // Seat is already occupied, show a message or take appropriate action
            Toast.makeText(this, "Seat " + (seatIndex + 1) + " is already occupied.", Toast.LENGTH_SHORT).show();
        } else {
            // Join the Agora channel with the user's name as the seat identifier
            // Replace this with your Agora SDK logic to join the channel
            // For example: rtcEngine().joinChannel(null, channelName, null, userUid);

            if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
                token = null; // default, no token
            }

            rtcEngine().setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);

            // rtcEngine().enableVideo(); // Enable video if needed
            rtcEngine().enableAudio(); // Enable audio
            rtcEngine().setEnableSpeakerphone(true);

            // Join the channel
            Log.d("TAG", "joinChannel: " + host.getChannel());
            Log.d("TAG", "joinChannel:tkn " + host.getToken());
            Log.d("TAG", "joinChannel:agoraUID " + agoraUID);
            // Now, you can use userIdString when joining the Agora channel
            // rtcEngine().joinChannel(liveUser.getToken(), liveUser.getChannel(), String.valueOf(agoraUID), agoraUID); // Omit the last argument or provide an empty string
            //rtcEngine().joinChannel(token, host.getChannel(), String.valueOf(agoraUID), agoraUID);
            rtcEngine().joinChannel(token, host.getChannel(), String.valueOf(agoraUID), agoraUID);

            // Enable the user's audio so they can hear others
            rtcEngine().muteLocalAudioStream(false);

            // Associate the user's name with the seat index
            occupiedSeatsMap.put(String.valueOf(agoraUID), seatIndex);
        }
    }

    // Helper method to leave a seat and disassociate a user from it
    private void leaveChannelWithSeat(int seatIndex) {
        // Check if the user is associated with the specified seat
        if (occupiedSeatsMap.containsKey(String.valueOf(agoraUID)) && occupiedSeatsMap.get(String.valueOf(agoraUID)) == seatIndex) {
            // Leave the Agora channel and release the seat
            // Replace this with your Agora SDK logic to leave the channel
            // For example: rtcEngine().leaveChannel();

            // Disable the user's audio when they leave the seat
            rtcEngine().muteLocalAudioStream(true);

            // Disassociate the user's name from the seat index
            occupiedSeatsMap.remove(String.valueOf(agoraUID));
        }
    }

    // Implement the onAudioVolumeIndication callback
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
            for (AudioVolumeInfo info : speakers) {
                if (info.volume > SPEAKING_THRESHOLD) {
                    // User is speaking, find the corresponding seat by username and highlight it
                    int seatIndex = findSeatIndexByUid(info.uid);
                    if (seatIndex != -1) {
                        highlightSeat(seatIndex);
                    }
                }
            }
        }
    };

    // Helper method to find the seat index by uid
    private int findSeatIndexByUid(int uid) {
        for (Map.Entry<String, Integer> entry : occupiedSeatsMap.entrySet()) {
            if (entry.getValue() == uid) {
                return entry.getValue();
            }
        }
        return -1; // User not found in occupied seats
    }

    // Helper method to highlight the seat by changing its background
    private void highlightSeat(int seatIndex) {
        // Change the button background or appearance to represent speaking (replace with your logic)
        seatButtons[seatIndex].setBackgroundResource(R.drawable.speaking_roommic);
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
            endLive();

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


  /*  public void onLocalAudioMuteClicked(View view) {
        viewModel.isMuted = !viewModel.isMuted;
        rtcEngine().muteLocalAudioStream(viewModel.isMuted);
        if (viewModel.isMuted) {
            binding.btnMute.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mute));

            Toast.makeText(this, "Mic muted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Mic unmuted", Toast.LENGTH_SHORT).show();
            binding.btnMute.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.unmute));
        }
    }*//*
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


                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("senderUserId", sessionManager.getUser().getId());
                    jsonObject.put("receiverUserId", host.getLiveUserId());
                    jsonObject.put("liveStreamingId", host.getLiveStreamingId());
                    jsonObject.put("userName", sessionManager.getUser().getName());
                    jsonObject.put("coin", giftItem.getCoin() * giftItem.getCount());
                    jsonObject.put("gift", new Gson().toJson(giftItem));
                    getSocket().emit(Const.EVENT_NORMALUSER_GIFT, jsonObject);

                    // Add the gift to the queue
                    giftQueue.add(giftItem);

                    // Play the gift immediately if the queue was empty
                    if (giftQueue.size() == 1) {
                        playNextGiftFromQueue();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

/*
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
    }*//*

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
/*    }

    @Override
    public void onUserOffline(int uid, int reason) {
       // Log.d(TAG, "onUserOffline: " + uid + " reason" + reason);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        Log.d(TAG, "onUserJoined: " + uid + "  elapsed" + elapsed);
      //  userCount++; // Decrement the user count when a user leaves the channel
      //  updateUI(); // Update the UI to display the new user count
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

  /*  @Override
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
/*
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
}*/