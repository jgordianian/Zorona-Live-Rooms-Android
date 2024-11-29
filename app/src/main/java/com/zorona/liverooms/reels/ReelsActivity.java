package com.app.liverooms.reels;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.app.liverooms.R;
import com.app.liverooms.activity.BaseActivity;
import com.app.liverooms.bottomsheets.BottomSheetReport_g;
import com.app.liverooms.comments.CommentLikeListActivity;
import com.app.liverooms.modelclass.ReliteRoot;
import com.app.liverooms.retrofit.CommenApiCalling;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.user.guestUser.GuestActivity;
import com.app.liverooms.viewModel.ReelsViewModel;
import com.app.liverooms.viewModel.ViewModelFactory;
import com.app.liverooms.databinding.ActivityReelsBinding;
import com.app.liverooms.databinding.ItemReelsBinding;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReelsActivity extends BaseActivity implements Player.EventListener {
    private static final String TAG = "ReelsActiviry";
    ActivityReelsBinding binding;
    private SimpleExoPlayer player;
    private ItemReelsBinding playerBinding;
    private int lastPosition = -1;
    private Animation animation;
    private ReelsViewModel viewModel;
    private int position;
    private List<ReliteRoot.VideoItem> reels = new ArrayList<>();
    private CommenApiCalling commenApiCalling;
    private int start = 0;
    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reels);
        animation = AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.slow_rotate);
        new PagerSnapHelper().attachToRecyclerView(binding.rvReels);

        commenApiCalling = new CommenApiCalling(this);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new ReelsViewModel()).createFor()).get(ReelsViewModel.class);
        viewModel.init(this);
        binding.setViewModel(viewModel);

        Intent intent = getIntent();
        position = intent.getIntExtra(Const.POSITION, 0);
        String data = intent.getStringExtra(Const.DATA);
        userId = intent.getStringExtra(Const.USERID);

        if (data != null && !data.isEmpty()) {
            reels = new Gson().fromJson(data, new TypeToken<ArrayList<ReliteRoot.VideoItem>>() {
            }.getType());
            viewModel.start = reels.size() - Const.LIMIT;  // isloadmore always increment limit in start feild

            viewModel.reelsAdapter.addData(reels);
            binding.rvReels.scrollToPosition(position);
            viewModel.reelsAdapter.playVideoAt(position);
            if (userId == null || userId.isEmpty()) {
                userId = reels.get(0).getUserId();
            }
        }


        initListner();
    }


    private void initListner() {
        Log.d(TAG, "initVIew: ll " + lastPosition);
        binding.swipeRefresh.autoLoadMore();

        binding.swipeRefresh.setOnRefreshListener((refreshLayout) -> {
            viewModel.getReliteData(false, userId, false, true);

        });
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
            viewModel.getReliteData(true, userId, false, true);
        });
        viewModel.isLoadCompleted.observe(this, aBoolean -> {
            binding.swipeRefresh.finishRefresh();
            binding.swipeRefresh.finishLoadMore();
            viewModel.isFirstTimeLoading.set(false);
            viewModel.isLoadMoreLoading.set(false);
        });

        binding.rvReels.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (binding.rvReels.getLayoutManager() instanceof LinearLayoutManager) {
                        int position = ((LinearLayoutManager) binding.rvReels.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        if (position != -1 && lastPosition != position) {
                          /*  if (reelsAdapter.getList().get(position) != null) {

                            } else {
                                if (player != null) {
                                    player.setPlayWhenReady(false);
                                    player.stop();
                                    player.release();
                                    player = null;
                                    lastPosition = position;
                                }
                            }*/

                            if (binding.rvReels.getLayoutManager() != null) {
                                View view = binding.rvReels.getLayoutManager().findViewByPosition(position);
                                if (view != null) {
                                    lastPosition = position;
                                    ItemReelsBinding binding1 = DataBindingUtil.bind(view);
                                    if (binding1 != null) {
                                        binding1.lytSound.startAnimation(animation);
                                        //new GlobalApi().increaseView(binding1.getModel().getPostId());
                                        playVideo(viewModel.reelsAdapter.getList().get(position).getVideo(), binding1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
        binding.rvReels.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int position = ((LinearLayoutManager) binding.rvReels.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                    if (!(position <= -1) && lastPosition != position) {
                        if (binding.rvReels.getLayoutManager() != null) {
                            View view = binding.rvReels.getLayoutManager().findViewByPosition(position);
                            if (view != null) {
                                lastPosition = position;
                                ItemReelsBinding binding1 = DataBindingUtil.bind(view);
                                if (binding1 != null) {
                                    binding1.lytSound.startAnimation(animation);
                                    //new GlobalApi().increaseView(binding1.getModel().getPostId());
                                    playVideo(viewModel.reelsAdapter.getList().get(position).getVideo(), binding1);
                                }
                            }
                        }
                    }
                }
            }
        });

        viewModel.reelsAdapter.setOnReelsVideoAdapterListner(new ReelsAdapter.OnReelsVideoAdapterListner() {
            @Override
            public void onItemClick(ItemReelsBinding reelsBinding, int pos, int type) {
                lastPosition = pos;
                reelsBinding.lytSound.startAnimation(animation);
                playVideo(viewModel.reelsAdapter.getList().get(pos).getVideo(), reelsBinding);
            }

            @Override
            public void onClickCommentList(ReliteRoot.VideoItem relite) {
                startActivity(new Intent(ReelsActivity.this, CommentLikeListActivity.class)
                        .putExtra(Const.TYPE, CommentLikeListActivity.COMMENTS)
                        .putExtra(Const.RELITEDATA, new Gson().toJson(relite)));
                doTransition(Const.BOTTOM_TO_UP);


            }

            @Override
            public void onClickLikeList(ReliteRoot.VideoItem relite) {
                startActivity(new Intent(ReelsActivity.this, CommentLikeListActivity.class)
                        .putExtra(Const.TYPE, CommentLikeListActivity.LIKES)
                        .putExtra(Const.RELITEDATA, new Gson().toJson(relite)));
                doTransition(Const.BOTTOM_TO_UP);


            }

            @Override
            public void onHashTagClick(String hashTag) {

            }

            @Override
            public void onMentionClick(String userName) {
                startActivity(new Intent(ReelsActivity.this, GuestActivity.class).putExtra(Const.USERNAME, userName));
            }

            @Override
            public void onDoubleClick(ReliteRoot.VideoItem model, MotionEvent event, ItemReelsBinding binding) {
                Log.d(TAG, "onDoubleClick: ");
                showHeart(event, binding);
                if (!model.isLike()) {
                    binding.likebtn.performClick();
                }
            }

            @Override
            public void onClickLike(ItemReelsBinding reelsBinding, int pos) {
                commenApiCalling.toggleLikeRelite(viewModel.reelsAdapter.getList().get(pos).getId(), isLiked -> {


                    ReliteRoot.VideoItem model = viewModel.reelsAdapter.getList().get(pos);
                    int like;
                    if (isLiked) {
                        like = model.getLikeCount() + 1;
                    } else {
                        like = model.getLikeCount() - 1;
                    }
                    reelsBinding.likebtn.setLiked(isLiked);

                    reelsBinding.tvLikeCount.setText(String.valueOf(like));

                    model.setLike(isLiked);
                    model.setLikeCount(like);
                    viewModel.reelsAdapter.notifyItemChanged(pos, model);
                    // restart issue because above line
                });

            }

            @Override
            public void onClickReport(ReliteRoot.VideoItem reel) {
                openReportSheet(reel.getUserId());
            }

            @Override
            public void onClickUser(ReliteRoot.VideoItem reel) {
                // startActivity(new Intent(ReelsActivity.this, GuestActivity.class).putExtra(Const.USER_STR, new Gson().toJson(reel.getUser())));
            }

            @Override
            public void onClickShare(ReliteRoot.VideoItem reel) {

            }
        });

    }

    private void openReportSheet(String userId) {

        new BottomSheetReport_g(this, userId, () -> {
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


    private void openFramgnet(BottomSheetDialogFragment bottomSheetDialogFragment) {
        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getClass().getSimpleName());
    }

    private void playVideo(String videoUrl, ItemReelsBinding binding) {
        if (player != null) {
            player.removeListener(this);
            player.setPlayWhenReady(false);
            player.release();
        }
        Log.d(TAG, "playVideo: ");
        playerBinding = binding;
        player = new SimpleExoPlayer.Builder(ReelsActivity.this).build();
        //    SimpleCache simpleCache = MyApp.simpleCache;
      /*  CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(simpleCache, new DefaultHttpDataSourceFactory(Util.getUserAgent(ReelsActivity.this, "TejTok"))
                , CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
*/
        //    ProgressiveMediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(Uri.parse(videoUrl));
        binding.playerView.setPlayer(player);
        player.setPlayWhenReady(true);
        player.seekTo(0, 0);
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.addListener(this);
        binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        MediaSource mediaSource = buildMediaSource(Uri.parse(videoUrl));
        player.prepare(mediaSource, true, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(ReelsActivity.this, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_BUFFERING) {
            binding.pd.setVisibility(View.VISIBLE);
        } else if (playbackState == Player.STATE_READY) {
            binding.pd.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        if (player != null) {
            player.setPlayWhenReady(true);
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
        }
        super.onDestroy();
    }

    public void showHeart(MotionEvent e, ItemReelsBinding binding) {

        int x = (int) e.getX() - 200;
        int y = (int) e.getY() - 200;
        Log.i(TAG, "showHeart: " + x + "------" + y);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        final ImageView iv = new ImageView(this);
        lp.setMargins(x, y, 0, 0);
        iv.setLayoutParams(lp);
        Random r = new Random();
        int i1 = r.nextInt(30 + 30) - 30;
        iv.setRotation(i1);
        iv.setImageResource(R.drawable.ic_heart_gradient);
        if (binding.rtl.getChildCount() > 0) {
            binding.rtl.removeAllViews();
        }
        binding.rtl.addView(iv);
        Animation fadeoutani = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        fadeoutani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.rtl.removeView(iv);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv.startAnimation(fadeoutani);

    }


}