package com.zorona.liverooms.user;

import static android.provider.MediaStore.MediaColumns.DATA;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.zorona.liverooms.BuildConfig;
import com.zorona.liverooms.activity.BaseActivity;
import com.zorona.liverooms.activity.MainActivity;
import com.zorona.liverooms.modelclass.RestResponse;
import com.zorona.liverooms.modelclass.UserRoot;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.retrofit.RetrofitBuilder;
import com.zorona.liverooms.MainApplication;
import com.zorona.liverooms.R;
import com.zorona.liverooms.databinding.ActivityEditProfileBinding;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends BaseActivity {
    private static final int GALLERY_CODE = 1001;
    private static final int PERMISSION_REQUEST_CODE = 111;
    private static final String TAG = "Editprofileact";
    ActivityEditProfileBinding binding;
    boolean isValidUserName = false;
    String nameS, usernameS;
    private String gender = "";
    private String picturePath = "";
    private UserRoot.User userDummy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        binding.pd1.setVisibility(View.GONE);
        userDummy = sessionManager.getUser();
        Glide.with(this).load(userDummy.getImage())
                .apply(MainApplication.requestOptions)
                .circleCrop().into(binding.imgUser);
        binding.etName.setText(userDummy.getName());
        binding.etBio.setText(userDummy.getBio());
        binding.etAge.setText(String.valueOf(userDummy.getAge()));
        if (String.valueOf(userDummy.getAge()).equals("0")) {
            binding.etAge.setText("18");
        }
        binding.etUserName.setText(userDummy.getUsername());

        binding.lytMale.setOnClickListener(v -> onMaleClick());
        binding.lytFemale.setOnClickListener(v -> onFeMaleClick());
        binding.radioFemale.setOnClickListener(v -> onFeMaleClick());
        binding.radioMale.setOnClickListener(v -> onMaleClick());

        if (userDummy.getGender().equalsIgnoreCase(Const.MALE)) {
            binding.lytMale.performClick();
        } else if (userDummy.getGender().equalsIgnoreCase(Const.FEMALE)) {
            binding.tvFemale.performClick();
        } else {
            binding.lytMale.performClick();
        }


        gender = Const.MALE;

        isValidUserName = !userDummy.getUsername().isEmpty();
        Log.d(TAG, "checkDetails: " + isValidUserName + "  " + gender);
        if (userDummy != null && userDummy.getUsername() != null && !userDummy.getUsername().isEmpty()) {
            binding.etUserName.setText(userDummy.getUsername());
            isValidUserName = true;
            // binding.etUserName.setEnabled(false);
        }

        if (userDummy.getUsername() != null && !userDummy.getUsername().isEmpty()) {

            binding.etUserName.setText(userDummy.getUsername());
            isValidUserName = true;
            //  binding.etUserName.setEnabled(false);
        }
        binding.imgUser.setOnClickListener(v -> choosePhoto());
        binding.btnPencil.setOnClickListener(v -> choosePhoto());

        binding.etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkValidation(s.toString());
                usernameS = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (!usernameS.isEmpty()) {
//                  checkDetails();
//                }

            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameS = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    binding.etAge.setError("Enter Correct Age");
                    return;
                }
                int age = Integer.parseInt(s.toString());
                if (age < 0 || age > 105) {
                    binding.etAge.setError("Enter Correct Age");

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.tvSubmit.setOnClickListener(v -> {
            String name = binding.etName.getText().toString();
            String userName = binding.etUserName.getText().toString();
            String bio = binding.etBio.getText().toString();


            if (name.isEmpty()) {
                Toast.makeText(this, "Enter your name first", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userName.isEmpty()) {
                Toast.makeText(this, "Enter Username first", Toast.LENGTH_SHORT).show();
                return;
            }
            if (gender.isEmpty()) {
                Toast.makeText(this, "Select your gender", Toast.LENGTH_SHORT).show();
                return;
            }


            int age = Integer.parseInt(binding.etAge.getText().toString());
            if (age < 0 || age > 105) {
                Toast.makeText(this, "Enter Correct Age", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, RequestBody> map = new HashMap<>();

            MultipartBody.Part body = null;
            if (picturePath != null && !picturePath.isEmpty()) {
                File file = new File(picturePath);
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);
                body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            }


            RequestBody bodyUserid = RequestBody.create(MediaType.parse("text/plain"), sessionManager.getUser().getId());
            RequestBody bodyName = RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody bodyGender = RequestBody.create(MediaType.parse("text/plain"), gender);
            // RequestBody bodyEmail = RequestBody.create(MediaType.parse("text/plain"), userDummy.getEmail());
            RequestBody bodyUserName = RequestBody.create(MediaType.parse("text/plain"), userName);
            RequestBody bodyBio = RequestBody.create(MediaType.parse("text/plain"), bio);

            RequestBody bodyAge = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(age));


            map.put("name", bodyName);
            map.put("username", bodyUserName);
            map.put("bio", bodyBio);
            map.put("userId", bodyUserid);
            map.put("gender", bodyGender);
            map.put("age", bodyAge);

            binding.loder.setVisibility(View.VISIBLE);
            Call<UserRoot> call = RetrofitBuilder.create().updateUser(map, body);
            call.enqueue(new Callback<UserRoot>() {
                @Override
                public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                    if (response.code() == 200) {
                        if (response.body().isStatus()) {
                            sessionManager.saveUser(response.body().getUser());
                            sessionManager.saveBooleanValue(Const.ISLOGIN, true);
                            startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                    binding.loder.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<UserRoot> call, Throwable t) {

                }
            });


        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private String getProfileUrl(String imageUrl, String gender) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            return imageUrl;
        }
        if (gender.equalsIgnoreCase(Const.FEMALE)) {
            imageUrl = BuildConfig.BASE_URL + "storage/female.png";
        } else if (gender.equalsIgnoreCase(Const.MALE)) {
            imageUrl = BuildConfig.BASE_URL + "storage/male.png";

        } else return "";
        return imageUrl;
    }


    private void checkDetails() {

//        if (!isValidUserName || gender.isEmpty()) {
//            binding.tvSubmit.setTextColor(ContextCompat.getColor(this, R.color.graylight));
//            binding.tvSubmit.setEnabled(false);
//        } else {
//            binding.tvSubmit.setTextColor(ContextCompat.getColor(this, R.color.pink));
//            binding.tvSubmit.setEnabled(true);
//        }
    }

    private void checkValidation(String toString) {

        binding.pd1.setVisibility(View.VISIBLE);

        Call<RestResponse> call = RetrofitBuilder.create().checkUserName(toString, sessionManager.getUser().getId());
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code()==200) {
                    if (!response.body().isStatus()) {
                        binding.etUserName.setError("Username already taken");
                        isValidUserName = false;
                    } else {

                        isValidUserName = true;
                    }
                    Log.d(TAG, "checkDetails: " + isValidUserName + "  " + gender);

                    checkDetails();
                }
                binding.pd1.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {

            }
        });
    }


    private void choosePhoto() {
        if (checkPermission()) {
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, GALLERY_CODE);
        } else {
            requestPermission();
        }
    }


    private int getAgeFromBYear(int birthYear) {
        Log.d(TAG, "onCreate: " + Calendar.getInstance().get(Calendar.YEAR));

        return Calendar.getInstance().get(Calendar.YEAR) - birthYear;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result==PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
                choosePhoto();
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_CODE && resultCode==RESULT_OK && null!=data) {

            Uri selectedImage = data.getData();

            Glide.with(this)
                    .load(selectedImage)
                    .circleCrop()
                    .into(binding.imgUser);
            String[] filePathColumn = {DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

        }
    }

    private void onFeMaleClick() {
        gender = Const.FEMALE;
        binding.tvFemale.setTextColor(ContextCompat.getColor(this, R.color.pink));
        binding.tvMale.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.radioMale.setChecked(false);
        binding.radioFemale.setChecked(true);

        checkDetails();
    }

    private void onMaleClick() {
        gender = Const.MALE;
        binding.tvMale.setTextColor(ContextCompat.getColor(this, R.color.pink));
        binding.tvFemale.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.radioMale.setChecked(true);
        binding.radioFemale.setChecked(false);
        checkDetails();
    }
}