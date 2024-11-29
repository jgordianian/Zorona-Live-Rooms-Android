package com.app.liverooms.chat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.app.liverooms.BuildConfig;
import com.app.liverooms.R;
import com.app.liverooms.activity.BaseActivity;
import com.app.liverooms.bottomsheets.BottomSheetMessageDetails;
import com.app.liverooms.bottomsheets.BottomSheetReport_g;
import com.app.liverooms.modelclass.ChatItem;
import com.app.liverooms.modelclass.ChatUserListRoot;
import com.app.liverooms.modelclass.GuestProfileRoot;
import com.app.liverooms.modelclass.UploadImageRoot;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.retrofit.RetrofitBuilder;
import com.app.liverooms.retrofit.UserApiCall;
import com.app.liverooms.user.guestUser.GuestActivity;
import com.app.liverooms.videocall.CallRequestActivity;
import com.app.liverooms.viewModel.ChatViewModel;
import com.app.liverooms.viewModel.ViewModelFactory;
import com.app.liverooms.SessionManager;
import com.app.liverooms.databinding.ActivityChatBinding;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatActivity extends BaseActivity {
    public static final int RESULT_LOAD_IMAGE = 201;
    private static final String TAG = "chatactivity";
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "cropimage";
    public static String otherUserId = "";
    public static boolean isOPEN = false;
    ActivityChatBinding binding;
    SessionManager sessionManager;
    boolean isSend = false;
    String destinationUri = SAMPLE_CROPPED_IMAGE_NAME + ".png";
    private ChatViewModel viewModel;
    private GuestProfileRoot.User guestUser;
    private Socket socket;
    private Emitter.Listener chatListner = args -> {
        Log.d(TAG, "chetlister : " + args[0]);
        if (args[0] != null) {
            runOnUiThread(() -> {
                ChatItem chatUserItem = new Gson().fromJson(args[0].toString(), ChatItem.class);
                if (chatUserItem != null) {
                    Log.d(TAG, "chetlister : " + chatUserItem.getMessage());

                    if (!isSend) {
                        viewModel.chatAdapter.addSingleChat(chatUserItem);
                        scrollAdapterLogic();
                        isSend = true;
                    }
                } else {
                    Log.d(TAG, "lister : chet obj null");
                }
            });
        }
    };
    private String picturePath;
    private Uri selectedImage;

    @Override
    protected void onStart() {
        super.onStart();
        isOPEN = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        sessionManager = new SessionManager(this);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new ChatViewModel()).createFor()).get(ChatViewModel.class);

        binding.setViewmodel(viewModel);
        viewModel.chatAdapter.initLocalUserImage(sessionManager.getUser().getImage());
        viewModel.chatAdapter.initLocalUserId(sessionManager.getUser().getId());

        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.rvChat.getLayoutManager();
        //  layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        Intent intent = getIntent();


        String chatRootStr = intent.getStringExtra(Const.CHATROOM);
        if (chatRootStr != null && !chatRootStr.isEmpty()) {
            ChatUserListRoot.ChatUserItem chatRoot = new Gson().fromJson(chatRootStr, ChatUserListRoot.ChatUserItem.class);
            otherUserId = chatRoot.getUserId();
            userApiCall.getGuestProfile(chatRoot.getUserId(), new UserApiCall.OnGuestUserApiListner() {
                @Override
                public void onUserGetted(GuestProfileRoot.User user) {
                    guestUser = user;
                    binding.imgUser.setUserImage(guestUser.getImage(), guestUser.isVIP());
                }

                @Override
                public void onFailure() {

                }
            });
            binding.imgUser.setUserImage(chatRoot.getImage());
            binding.tvUserNamew.setText(chatRoot.getName());
            viewModel.chatAdapter.initGuestUserImage(chatRoot.getImage());

            viewModel.chatTopic = chatRoot.getTopic();

            initSocketIO();
            initListner();
            viewModel.getOldChat(false);

        }

        String userStr = intent.getStringExtra(Const.USER);
        if (userStr != null && !userStr.isEmpty()) {
            guestUser = new Gson().fromJson(userStr, GuestProfileRoot.User.class);

            binding.imgUser.setUserImage(guestUser.getImage(), guestUser.isVIP());
            binding.tvUserNamew.setText(guestUser.getName());

            userApiCall.createChatTopic(sessionManager.getUser().getId(), guestUser.getUserId(), topic -> {
                viewModel.chatTopic = topic;

                initSocketIO();
                initListner();
                viewModel.getOldChat(false);
            });

        }


    }

    private void initSocketIO() {
        IO.Options options = IO.Options.builder()
                // IO factory options
                .setForceNew(false)
                .setMultiplex(true)

                // low-level engine options
                .setTransports(new String[]{Polling.NAME, WebSocket.NAME})
                .setUpgrade(true)
                .setRememberUpgrade(false)
                .setPath("/socket.io/")
                .setQuery("chatRoom=" + viewModel.chatTopic + "")
                .setExtraHeaders(null)

                // Manager options
                .setReconnection(true)
                .setReconnectionAttempts(Integer.MAX_VALUE)
                .setReconnectionDelay(1_000)
                .setReconnectionDelayMax(5_000)
                .setRandomizationFactor(0.5)
                .setTimeout(20_000)

                // Socket options
                .setAuth(null)
                .build();

        URI uri = URI.create(BuildConfig.BASE_URL);
        socket = IO.socket(uri, options);
        Log.d(TAG, "onCreate: " + socket.id());
        socket.connect();
        socket.on("connection", args -> Log.d(TAG, "call: "));

        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d(TAG, "call: connect" + args.length);
            socket.on(Const.EVENT_CHAT, chatListner);

        });
    }

    private void initListner() {
        binding.etChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                Log.d("TAG", "afterTextChanged: " + charSequence.toString());
                viewModel.sendBtnEnable.postValue(!charSequence.toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.btnScroll.setOnClickListener(v -> {
            binding.rvChat.scrollToPosition(viewModel.chatAdapter.getItemCount() - 1);
            binding.btnScroll.setVisibility(View.GONE);
        });
        viewModel.sendBtnEnable.observe(this, aBoolean -> {
            binding.tvSend.setEnabled(aBoolean);
            if (!aBoolean) {
                binding.tvSend.setTextColor(ContextCompat.getColor(ChatActivity.this, R.color.text_gray));
            } else {
                binding.tvSend.setTextColor(ContextCompat.getColor(ChatActivity.this, R.color.pink));
            }
        });
        binding.tvSend.setOnClickListener(v -> {
            String message = binding.etChat.getText().toString().trim();

            if (message.isEmpty()) {
                Toast.makeText(this, "Enter valid Message", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("senderId", sessionManager.getUser().getId());
                jsonObject.put("messageType", "message");
                jsonObject.put("topic", viewModel.chatTopic);
                jsonObject.put("message", message);
                Log.d(TAG, "initListner: send chat " + jsonObject);
                socket.emit(Const.EVENT_CHAT, jsonObject);
                binding.etChat.setText("");

                isSend = false;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        viewModel.chatAdapter.setOnChatItemClickLister((chatDummy, position) -> {
            new BottomSheetMessageDetails(this, chatDummy, () -> {
                viewModel.deleteChat(chatDummy, position);
            });
        });


        binding.rvChat.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Log.d(TAG, "onScrolled: can scroll-1   " + binding.rvChat.canScrollVertically(-1));
                if (!binding.rvChat.canScrollVertically(-1)) {
                    LinearLayoutManager manager = (LinearLayoutManager) binding.rvChat.getLayoutManager();
                    Log.d("TAG", "onScrollStateChanged: ");
                    int visibleItemcount = manager.getChildCount();
                    int totalitem = manager.getItemCount();
                    int firstvisibleitempos = manager.findFirstCompletelyVisibleItemPosition();
                    Log.d("TAG", "onScrollStateChanged:firstvisible    " + firstvisibleitempos);
                    Log.d("TAG", "onScrollStateChanged:188 " + totalitem);
                    if (!viewModel.isLoding && (visibleItemcount + firstvisibleitempos >= totalitem) && firstvisibleitempos >= 0) {

                        viewModel.isLoding = true;
                        binding.rvChat.clearFocus();
                        viewModel.getOldChat(true);

                    }
                }
                if (!binding.rvChat.canScrollVertically(1)) {
                    binding.btnScroll.setVisibility(View.GONE);
                }
            }
        });

        viewModel.chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                Log.d(TAG, "onScrolled: can scroll-1   " + binding.rvChat.canScrollVertically(-1));
                Log.d(TAG, "onScrolled: can scroll 1   " + binding.rvChat.canScrollVertically(1));
                if (!binding.rvChat.canScrollVertically(1)) {
                    binding.rvChat.scrollToPosition(0);
                }
            }

        });


    }

    private void scrollAdapterLogic() {

        if (binding.rvChat.canScrollVertically(1)) {
            binding.btnScroll.setVisibility(View.VISIBLE);
        } else {
            binding.rvChat.scrollToPosition(0);
        }
    }

    public void onClickVideoCall(View view) {
        if (guestUser == null) return;
        startActivity(new Intent(this, CallRequestActivity.class).putExtra(Const.USER, new Gson().toJson(guestUser)));
    }

    public void onClickUser(View view) {
        if (guestUser != null) {
            startActivity(new Intent(this, GuestActivity.class).putExtra(Const.USERID, guestUser.getUserId()));
        }
    }

    public void onClickCamara(View view) {
        choosePhoto();
    }

    public void openGallery(Context context) {
        try {
            startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), RESULT_LOAD_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
                choosePhoto();
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
        }
    }

    private void choosePhoto() {

        if (checkPermission()) {
            openGallery(this);
        } else {
            requestPermission();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {


            selectedImage = data.getData();


            startCropActivity(data.getData());

            Glide.with(this)
                    .load(selectedImage)
                    .placeholder(R.drawable.ic_user_place).error(R.drawable.ic_user_place)
                    .into(binding.imageview);
//            binding.imageview.setAdjustViewBounds(true);

            picturePath = getRealPathFromURI(selectedImage);

            isSend = false;
//            uploadImage();

        } else if (requestCode == 69 && resultCode == -1) {
            handleCropResult(data);
        }
        if (resultCode == 96) {
            handleCropError(data);
        }


    }

    private void startCropActivity(@androidx.annotation.NonNull Uri uri) {
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME + ".png"))).useSourceImageAspectRatio();
        UCrop.Options options = new UCrop.Options();
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.pink));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.pink));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.blacklight));
        options.setToolbarColor(ContextCompat.getColor(this, R.color.blacklight));
        options.setCropFrameColor(ContextCompat.getColor(this, R.color.blackpure));
        options.setDimmedLayerColor(ContextCompat.getColor(this, R.color.blackpure));
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        uCrop.withOptions(options);
        uCrop.start(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleCropResult(@androidx.annotation.NonNull Intent result) {
        Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {

            selectedImage = resultUri;

            Glide.with(this)
                    .load(selectedImage)
                    .placeholder(R.drawable.ic_user_place).error(R.drawable.ic_user_place)
                    .into(binding.imageview);
            binding.imageview.setAdjustViewBounds(true);
            picturePath = getRealPathFromURI(selectedImage);

            uploadImage();

        } else {
            Toast.makeText(this, "toast_cannot_retrieve_cropped_image", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCropError(@androidx.annotation.NonNull Intent result) {
        Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e("TAG", "handleCropError: ", cropError);
            Toast.makeText(this, cropError.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, "toast_unexpected_error", Toast.LENGTH_SHORT).show();
    }

    private void uploadImage() {
        binding.lytImage.setVisibility(View.VISIBLE);
        if (picturePath != null) {
            File file = new File(picturePath);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            RequestBody messageTypebody = RequestBody.create(MediaType.parse("text/plain"), "image");
            RequestBody topicbody = RequestBody.create(MediaType.parse("text/plain"), viewModel.chatTopic);
            RequestBody senderIdbody = RequestBody.create(MediaType.parse("text/plain"), sessionManager.getUser().getId());

            HashMap<String, RequestBody> map = new HashMap<>();

            map.put("messageType", messageTypebody);
            map.put("topic", topicbody);
            map.put("senderId", senderIdbody);

            Call<UploadImageRoot> call = RetrofitBuilder.create().uploadChatImage(map, body);
            call.enqueue(new Callback<UploadImageRoot>() {
                @Override
                public void onResponse(Call<UploadImageRoot> call, Response<UploadImageRoot> response) {
                    if (response.code() == 200) {
                        if (response.body().isStatus()) {
                            binding.lytImage.setVisibility(View.GONE);
                            try {
                                JSONObject jsonObject = new JSONObject();

                                jsonObject.put("senderId", sessionManager.getUser().getId());
                                jsonObject.put("messageType", "image");
                                jsonObject.put("topic", viewModel.chatTopic);
                                jsonObject.put("message", "image");
                                jsonObject.put("date", response.body().getChat().getDate());
                                jsonObject.put("image", response.body().getChat().getImage());
                                Log.d(TAG, "initListner: send chat " + jsonObject);
                                socket.emit(Const.EVENT_CHAT, jsonObject);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<UploadImageRoot> call, Throwable t) {

                }
            });
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void onClickReport(View view) {
        if (guestUser == null) return;
        new BottomSheetReport_g(this, guestUser.getUserId(), () -> {
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