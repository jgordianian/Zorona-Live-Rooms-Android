package com.app.liverooms.reels;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.app.liverooms.BuildConfig;
import com.app.liverooms.activity.BaseFragment;
import com.app.liverooms.bottomsheets.BottomSheetReport_g;
import com.app.liverooms.comments.CommentLikeListActivity;
import com.app.liverooms.liveStreamming.LiveFragmentMain;
import com.app.liverooms.modelclass.ReliteRoot;
import com.app.liverooms.retrofit.CommenApiCalling;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.user.guestUser.GuestActivity;
import com.app.liverooms.videocall.Ont_to_oneFragmentMain;
import com.app.liverooms.viewModel.ReelsViewModel;
import com.app.liverooms.viewModel.ViewModelFactory;
import com.app.liverooms.MainApplication;
import com.app.liverooms.R;
import com.app.liverooms.databinding.FragmentVideoListBinding;
import com.app.liverooms.databinding.ItemReelsBinding;
import com.app.liverooms.home.HomeFragment;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Random;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;


public class VideoListFragment extends BaseFragment implements Player.EventListener {


    private static final String TAG = "videolistfragment";

    FragmentVideoListBinding binding;
    private SimpleExoPlayer player;
    private ItemReelsBinding playerBinding;
    private int lastPosition = -1;
    private Animation animation;
    private ReelsViewModel viewModel;
    private CommenApiCalling commenApiCalling;

    public VideoListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video_list, container, false);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new ReelsViewModel()).createFor()).get(ReelsViewModel.class);
        viewModel.init(getActivity());
        binding.setViewModel(viewModel);
        commenApiCalling = new CommenApiCalling(getActivity());
        initVIew();
        viewModel.getReliteData(false, sessionManager.getUser().getId(), true, false);
        initListner();
        return binding.getRoot();
    }

    private void initListner() {


        binding.swipeRefresh.autoLoadMore();
        binding.swipeRefresh.setOnRefreshListener(refreshLayout -> viewModel.getReliteData(false, sessionManager.getUser().getId(), true, false));
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
            viewModel.getReliteData(true, sessionManager.getUser().getId(), true, false);
        });
        viewModel.isLoadCompleted.observe(getActivity(), aBoolean -> {
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

                Log.d(TAG, "onItemClick: " + type);
                if (type == 1) {
                    lastPosition = pos;
                    playVideo(viewModel.reelsAdapter.getList().get(pos).getVideo(), reelsBinding);
                } else {
                    if (player != null) {
                        if (player.isPlaying()) {
                            player.setPlayWhenReady(false);

                        } else {
                            player.setPlayWhenReady(true);
                        }
                        showMediaButton();
                    }

                }
               /* lastPosition = pos;
                reelsBinding.imgSound.startAnimation(animation);
                playVideo(viewModel.reelsAdapter.getList().get(pos).getVideo(), reelsBinding);*/
            }

            @Override
            public void onClickCommentList(ReliteRoot.VideoItem relite) {
                startActivity(new Intent(getActivity(), CommentLikeListActivity.class)
                        .putExtra(Const.TYPE, CommentLikeListActivity.COMMENTS)
                        .putExtra(Const.RELITEDATA, new Gson().toJson(relite)));
                doTransition(Const.BOTTOM_TO_UP);


            }

            @Override
            public void onClickLikeList(ReliteRoot.VideoItem relite) {
                startActivity(new Intent(getActivity(), CommentLikeListActivity.class)
                        .putExtra(Const.TYPE, CommentLikeListActivity.LIKES)
                        .putExtra(Const.RELITEDATA, new Gson().toJson(relite)));
                doTransition(Const.BOTTOM_TO_UP);

            }

            @Override
            public void onHashTagClick(String hashTag) {

            }

            @Override
            public void onMentionClick(String userName) {
                startActivity(new Intent(getActivity(), GuestActivity.class).putExtra(Const.USERNAME, userName));
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
                //  startActivity(new Intent(getActivity(), GuestActivity.class).putExtra(Const.USER_STR, new Gson().toJson(reel.getUser())));
            }

            @Override
            public void onClickShare(ReliteRoot.VideoItem reel) {
                shareRlite(reel);
            }
        });
    }

    private void openReportSheet(String userId) {

        new BottomSheetReport_g(getActivity(), userId, () -> Toast.makeText(getActivity(), "We will take immediately action for this user Thank You", Toast.LENGTH_SHORT).show());
    }

    private void shareRlite(ReliteRoot.VideoItem reel) {
        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setTitle(reel.getCaption())
                .setContentDescription("By : " + reel.getName())
                .setContentImageUrl(BuildConfig.BASE_URL + reel.getScreenshot())
                .setContentMetadata(new ContentMetadata().addCustomMetadata("type", "RELITE").addCustomMetadata(Const.DATA, new Gson().toJson(reel)));

        LinkProperties lp = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .setCampaign("content 123 launch")
                .setStage("new user")

                .addControlParameter("", "")
                .addControlParameter("", Long.toString(Calendar.getInstance().getTimeInMillis()));

        buo.generateShortUrl(getActivity(), lp, (url, error) -> {
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

    private void callFragment(Fragment fragment) {
        if (getParentFragment() != null) {
            ((HomeFragment) getParentFragment()).openFragmet(fragment);
        }
    }

    private void initVIew() {

        binding.tvLive.setOnClickListener(v -> callFragment(new LiveFragmentMain()));
        binding.tvVideo.setOnClickListener(v -> callFragment(new VideoListFragment()));
        binding.tvOnetoOne.setOnClickListener(v -> callFragment(new Ont_to_oneFragmentMain()));

        animation = AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.slow_rotate);
        binding.rvReels.setAdapter(viewModel.reelsAdapter);


        new PagerSnapHelper().attachToRecyclerView(binding.rvReels);

        Log.d(TAG, "initVIew: ll " + lastPosition);


    }

    private void showMediaButton() {
        if (player != null) {
            if (player.getPlayWhenReady()) {
                binding.imgMedia.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.exo_icon_play));
            } else {
                binding.imgMedia.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.exo_icon_pause));
            }
            binding.imgMedia.setVisibility(View.VISIBLE);
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> binding.imgMedia.setVisibility(View.GONE), 1000);
    }

    public void showHeart(MotionEvent e, ItemReelsBinding binding) {

        int x = (int) e.getX() - 200;
        int y = (int) e.getY() - 200;
        Log.i(TAG, "showHeart: " + x + "------" + y);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        final ImageView iv = new ImageView(getActivity());
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
        Animation fadeoutani = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
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


    private void playVideo(String videoUrl, ItemReelsBinding binding) {
        if (player != null) {
            player.removeListener(this);
            player.setPlayWhenReady(false);
            player.release();
        }
        Log.d(TAG, "playVideo:URL  " + videoUrl);
        playerBinding = binding;
        player = new SimpleExoPlayer.Builder(getActivity()).build();
        SimpleCache simpleCache = MainApplication.simpleCache;
        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(simpleCache, new DefaultHttpDataSourceFactory(Util.getUserAgent(getActivity(), "TejTok"))
                , CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
        ProgressiveMediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(Uri.parse(videoUrl));
        binding.playerView.setPlayer(player);
        player.setPlayWhenReady(true);
        player.seekTo(0, 0);
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.addListener(this);
        binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        // MediaSource mediaSource = buildMediaSource(Uri.parse(videoUrl));
        player.prepare(progressiveMediaSource, true, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(getActivity(), "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_BUFFERING) {
            //  binding.pd.setVisibility(View.VISIBLE);
            if (playerBinding != null) {
                binding.buffering.setVisibility(View.VISIBLE);
            }
        } else if (playbackState == Player.STATE_READY) {
            //   binding.pd.setVisibility(View.GONE);
            if (playerBinding != null) {
                binding.buffering.setVisibility(View.GONE);
            }
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
}