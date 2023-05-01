package com.zorona.liverooms.videocall;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.zorona.liverooms.modelclass.CallRequestRoot;
import com.zorona.liverooms.modelclass.GuestProfileRoot;
import com.zorona.liverooms.MainApplication;
import com.zorona.liverooms.R;
import com.zorona.liverooms.activity.BaseActivity;
import com.zorona.liverooms.databinding.ActivityCallRequestBinding;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.retrofit.RetrofitBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallRequestActivity extends VideoCallBaseActivity {

    private static final String TAG = "callReqAct";
    ActivityCallRequestBinding binding;

    private GuestProfileRoot.User guestUser;
    private Socket globalSocket;

    private Emitter.Listener callConfirmLister = args -> {
        runOnUiThread(() -> {
            if (args != null) {
                Log.d(TAG, "callConfirmLister: " + args[0].toString());
                try {
                    JSONObject jsonObject = new JSONObject(args[0].toString());
                    String userId1 = jsonObject.getString(Const.USERID1);
                    String userId2 = jsonObject.getString(Const.USERID2);
                    boolean isConfirm = jsonObject.getBoolean(Const.ISCONFIRM);
                    if (userId1.equals(guestUser.getUserId())) {
                        if (userId2.equals(sessionManager.getUser().getId())) {
                            if (isConfirm) {
                                binding.tvStatus.setText("Ringing....");
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    };
    private Emitter.Listener callAnswerLister = args -> {
        runOnUiThread(() -> {
            if (args != null) {
                Log.d(TAG, "callAnswerLister: " + args[0].toString());
                try {
                    JSONObject jsonObject = new JSONObject(args[0].toString());  // required feild  token channel
                    String userId1 = jsonObject.getString(Const.USERID1);
                    String userId2 = jsonObject.getString(Const.USERID2);
                    String token = jsonObject.getString(Const.TOKEN);
                    String callRoomId = jsonObject.getString(Const.CALL_ROOM_ID);
                    String channel = jsonObject.getString(Const.CHANNEL);
                    Log.d(TAG, "guest id : " + guestUser.getUserId());
                    Log.d(TAG, "local  id : " + sessionManager.getUser().getId());
                    boolean isAccept = jsonObject.getBoolean(Const.ISACCEPT);
                    if (userId1.equals(guestUser.getUserId())) {


                        if (userId2.equals(sessionManager.getUser().getId())) {
                            if (isAccept) {
                                Intent intent = new Intent(CallRequestActivity.this, VideoCallActivity.class);
                                intent.putExtra(Const.USERID, userId1);
                                intent.putExtra(Const.TOKEN, token);
                                intent.putExtra(Const.CHANNEL, channel);
                                intent.putExtra(Const.CALL_ROOM_ID, callRoomId);
                                intent.putExtra(Const.CALL_BY_ME, true);
                                startActivity(intent);

                            } else {
                                Toast.makeText(this, "Call Declined", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                            BaseActivity.STATUS_VIDEO_CALL = true;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    };
    private String callRoomId;
    private Socket callRoomSocket;
    private String agoraToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_request);
        BaseActivity.STATUS_VIDEO_CALL = true;
        globalSocket = ((MainApplication) getApplication()).getGlobalSoket();

        Intent intent = getIntent();
        String userData = intent.getStringExtra(Const.USER);
        if (userData != null && !userData.isEmpty()) {
            guestUser = new Gson().fromJson(userData, GuestProfileRoot.User.class);
            Log.d(TAG, "onCreate: guest user  " + guestUser.toString());

            Glide.with(this).load(guestUser.getImage())
                    .apply(MainApplication.requestOptions)
                    .circleCrop().into(binding.imgUser);
            binding.tvName.setText(guestUser.getName());

            makeCallRequest();

        }

        binding.btnDecline.setOnClickListener(v -> onBackPressed());

    }

    private void makeCallRequest() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("callerUserId", sessionManager.getUser().getId());
        jsonObject.addProperty("receiverUserId", guestUser.getUserId());
        jsonObject.addProperty("channel", guestUser.getUserId());
        Call<CallRequestRoot> call = RetrofitBuilder.create().makeCallRequest(jsonObject);
        call.enqueue(new Callback<CallRequestRoot>() {
            @Override
            public void onResponse(Call<CallRequestRoot> call, Response<CallRequestRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        callRoomId = response.body().getCallId();
                        agoraToken = response.body().getToken();

                        Log.d(TAG, "onResponse: " + callRoomId);

                        callRoomSocket = getCallSocket(callRoomId, false);
                        callRoomSocket.on(Socket.EVENT_CONNECT, args -> runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initMain();
                            }
                        }));
                    }
                }
            }

            @Override
            public void onFailure(Call<CallRequestRoot> call, Throwable t) {

                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void initMain() {
        try {
            JSONObject callReqObject = new JSONObject();
            callReqObject.put(Const.USERID1, guestUser.getUserId());
            callReqObject.put(Const.USERID2, sessionManager.getUser().getId());
            callReqObject.put(Const.USER2_NAME, sessionManager.getUser().getName());
            callReqObject.put(Const.USER2_IMAGE, sessionManager.getUser().getImage());
            callReqObject.put(Const.CALL_ROOM_ID, callRoomId);
            callReqObject.put(Const.TOKEN, agoraToken);

            Log.d(TAG, "initMain: socket connected " + globalSocket.isActive());
            Log.d(TAG, "initMain: socket connected " + globalSocket.connected());
            Log.d(TAG, "initMain:call req send  " + callReqObject);

            callRoomSocket.emit(Const.EVENT_CALL_REQUEST, callReqObject);
            binding.tvStatus.setText("Calling...");
            callRoomSocket.on(Const.EVENT_CALL_CONFIRMED, callConfirmLister);
            callRoomSocket.on(Const.EVENT_CALL_ANSWER, callAnswerLister);

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    @Override
    protected void onPause() {
        super.onPause();
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        //globalSocket.disconnect();
        callRoomSocket.disconnect();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        try {
            JSONObject callReqObject = new JSONObject();
            callReqObject.put(Const.USERID1, guestUser.getUserId());
            callReqObject.put(Const.USERID2, sessionManager.getUser().getId());
            callReqObject.put(Const.USER2_NAME, sessionManager.getUser().getName());
            callReqObject.put(Const.USER2_IMAGE, sessionManager.getUser().getImage());
            callReqObject.put(Const.CALL_ROOM_ID, callRoomId);

            Log.d(TAG, "initMain: socket connected " + globalSocket.isActive());
            Log.d(TAG, "initMain: socket connected " + globalSocket.connected());
            Log.d(TAG, "initMain:call req send  " + callReqObject);

            callRoomSocket.emit(Const.EVENT_CALL_CANCEL, callReqObject);
            //  todo ios ma pending

        } catch (JSONException e) {
            e.printStackTrace();
        }

        BaseActivity.STATUS_VIDEO_CALL = false;
        finish();
    }
}