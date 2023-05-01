package com.zorona.liverooms.reels.record.songPicker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.zorona.liverooms.BuildConfig;
import com.zorona.liverooms.R;
import com.zorona.liverooms.SharedConstants;
import com.zorona.liverooms.activity.BaseActivity;
import com.zorona.liverooms.dilog.CustomDialogClass;
import com.zorona.liverooms.modelclass.SongRoot;
import com.zorona.liverooms.reels.record.RecorderActivity;
import com.zorona.liverooms.retrofit.RetrofitBuilder;
import com.zorona.liverooms.utils.IntentUtil;
import com.zorona.liverooms.utils.TempUtil;
import com.zorona.liverooms.databinding.ActivitySongPickerBinding;
import com.zorona.liverooms.reels.record.workers.FileDownloadWorker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import nl.changer.audiowife.AudioWife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongPickerActivity extends BaseActivity {

    private static final String TAG = "SongPickerActivity";


    ActivitySongPickerBinding binding;
    private int start = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode== SharedConstants.REQUEST_CODE_PICK_SONG_FILE && resultCode==RESULT_OK && data!=null) {
            try {
                closeWithSelection(null, copySongFile(data.getData()));
            } catch (Exception e) {
                Log.e(TAG, "Failed to copy song file on phone.");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    SongsAdapter songsAdapter = new SongsAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_song_picker);


        songsAdapter.setOnSongClickListner(song -> {
            downloadSelectedSong(song);
        });
        binding.rvSongs.setAdapter(songsAdapter);
        initView();
        fetchSongsData();
    }

    public void fetchSongsData() {
        binding.noData.setVisibility(View.GONE);
        Call<SongRoot> call = RetrofitBuilder.create().getSongs();
        call.enqueue(new Callback<SongRoot>() {
            @Override
            public void onResponse(Call<SongRoot> call, Response<SongRoot> response) {
                if (response.code()==200) {
                    if (response.body().isStatus() && !response.body().getSong().isEmpty()) {
                        songsAdapter.addData(response.body().getSong());
                    } else if (start == 0) {
                        binding.noData.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<SongRoot> call, Throwable t) {

            }
        });
    }

    private void initView() {
        View browse = findViewById(R.id.browse);
        browse.setOnClickListener(v ->
                IntentUtil.startChooser(
                        this, SharedConstants.REQUEST_CODE_PICK_SONG_FILE, "audio/*"));
        View sheet = findViewById(R.id.song_preview_sheet);
        BottomSheetBehavior<View> bsb = BottomSheetBehavior.from(sheet);
        bsb.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View sheet, int state) {
                Log.v(TAG, "Song preview sheet state is: " + state);
                if (state==BottomSheetBehavior.STATE_COLLAPSED) {
                    AudioWife.getInstance().release();
                }
            }

            @Override
            public void onSlide(@NonNull View sheet, float offset) {

            }


        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioWife.getInstance().release();

    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioWife.getInstance().pause();
    }

    public void downloadSelectedSong(final SongRoot.SongItem songDummy) {
        File songs = new File(getFilesDir(), "songs");
        if (!songs.exists() && !songs.mkdirs()) {
            Log.w(TAG, "Could not create directory at " + songs);
        }

        Log.d(TAG, "downloadSelectedSong: " + BuildConfig.BASE_URL + songDummy.getSong());

        String extension = songDummy.getSong().substring(songDummy.getSong().lastIndexOf(".") + 1);
        File audio = new File(songs, songDummy.getId() + "." + extension);
        if (audio.exists()) {
            playSelection(songDummy, Uri.fromFile(audio));
            return;
        }
        Log.d(TAG, "downloadSelectedSong: audio path " + audio.getPath());
        CustomDialogClass progress = new CustomDialogClass(this, R.style.customStyle);
        progress.setCancelable(false);
        progress.show();
        Data input = new Data.Builder()
                .putString(FileDownloadWorker.KEY_INPUT, BuildConfig.BASE_URL + songDummy.getSong())
                .putString(FileDownloadWorker.KEY_OUTPUT, audio.getAbsolutePath())
                .build();
        WorkRequest request = new OneTimeWorkRequest.Builder(FileDownloadWorker.class)
                .setInputData(input)
                .build();
        WorkManager wm = WorkManager.getInstance(this);
        wm.enqueue(request);
        wm.getWorkInfoByIdLiveData(request.getId())
                .observe(this, info -> {
                    Log.d(TAG, "downloadSelectedSong: " + info);
                    boolean ended = info.getState()==WorkInfo.State.CANCELLED
                            || info.getState()==WorkInfo.State.FAILED
                            || info.getState()==WorkInfo.State.SUCCEEDED;
                    if (ended) {
                        progress.dismiss();
                    }

                    if (info.getState()==WorkInfo.State.SUCCEEDED) {
                        playSelection(songDummy, Uri.fromFile(audio));
                    }
                });
    }

    private void closeWithSelection(@Nullable SongRoot.SongItem songDummy, Uri file) {
        Intent data = new Intent();
        if (songDummy!=null) {
            data.putExtra(RecorderActivity.EXTRA_SONG, songDummy);
        }

        data.putExtra(RecorderActivity.EXTRA_AUDIO, file);
        setResult(RESULT_OK, data);
        finish();
    }

    private Uri copySongFile(Uri uri) throws Exception {
        InputStream is = getContentResolver().openInputStream(uri);
        File target = TempUtil.createNewFile(this, "audio");
        OutputStream os = new FileOutputStream(target);
        IOUtils.copy(is, os);
        is.close();
        os.close();
        return Uri.fromFile(target);
    }

    private void playSelection(SongRoot.SongItem songDummy, Uri file) {
        View sheet = findViewById(R.id.song_preview_sheet);
        AudioWife.getInstance().release();
        AudioWife.getInstance()
                .init(this, file)
                .setPlayView(sheet.findViewById(R.id.play))
                .setPauseView(sheet.findViewById(R.id.pause))
                .setSeekBar(sheet.findViewById(R.id.seekbar))
                .setRuntimeView(sheet.findViewById(R.id.start))
                // .setTotalTimeView(sheet.findViewById(R.id.end))
                .play();
        TextView song2 = sheet.findViewById(R.id.song);
        song2.setText(songDummy.getTitle());
        sheet.findViewById(R.id.use)
                .setOnClickListener(v -> closeWithSelection(songDummy, file));
        BottomSheetBehavior<View> bsb = BottomSheetBehavior.from(sheet);
        bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
    }


}
