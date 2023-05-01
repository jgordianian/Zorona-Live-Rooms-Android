package com.zorona.liverooms.posts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.zorona.liverooms.BuildConfig;
import com.zorona.liverooms.R;
import com.zorona.liverooms.activity.BaseFragment;
import com.zorona.liverooms.comments.CommentLikeListActivity;
import com.zorona.liverooms.modelclass.PostRoot;
import com.zorona.liverooms.retrofit.CommenApiCalling;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.user.guestUser.GuestActivity;
import com.zorona.liverooms.viewModel.FeedListViewModel;
import com.zorona.liverooms.viewModel.ViewModelFactory;
import com.zorona.liverooms.databinding.FragmentFeedListBinding;
import com.zorona.liverooms.databinding.ItemFeedBinding;
import com.google.gson.Gson;

import java.util.Calendar;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;

public class FeedListFragment extends BaseFragment implements FeedAdapter.OnPostClickListner {


    private static final String TAG = "feedlistfragment";
    FragmentFeedListBinding binding;
    CommenApiCalling commenApiCalling;
    private FeedListViewModel viewModel;
    private String type;

    public FeedListFragment() {
        // Required empty public constructor
    }

    public FeedListFragment(String type) {

        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed_list, container, false);
        commenApiCalling = new CommenApiCalling(getActivity());
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new FeedListViewModel()).createFor()).get(FeedListViewModel.class);
        viewModel.init(getActivity(), type);
        binding.setViewModel(viewModel);
        viewModel.feedAdapter.setOnPostClickListner(this);
        viewModel.getPostData(false, sessionManager.getUser().getId());
        initView();
        initListner();
        return binding.getRoot();
    }

    private void initView() {
      /* */

    }

    private void initListner() {
        binding.swipeRefresh.setOnRefreshListener(refreshLayout -> viewModel.getPostData(false, sessionManager.getUser().getId()));
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
            viewModel.getPostData(true, sessionManager.getUser().getId());
        });
        viewModel.isLoadCompleted.observe(getActivity(), aBoolean -> {
            binding.swipeRefresh.finishRefresh();
            binding.swipeRefresh.finishLoadMore();
            viewModel.isFirstTimeLoading.set(false);
            viewModel.isLoadMoreLoading.set(false);
        });

    }


    @Override
    public void onLikeClick(PostRoot.PostItem postDummy, int position, ItemFeedBinding binding) {

        commenApiCalling.toggleLikePost(postDummy.getId(), isLiked -> {

            binding.likeButton.setLiked(isLiked);
            int like;
            if (isLiked) {
                like = postDummy.getLike() + 1;
            } else {
                like = postDummy.getLike() - 1;
            }
            postDummy.setLike(like);
            postDummy.setLike(isLiked);
            binding.tvLikes.setText(String.valueOf(like));
            viewModel.feedAdapter.notifyItemChanged(position, postDummy);
        });
    }

    @Override
    public void onCommentListClick(PostRoot.PostItem postDummy) {
        startActivity(new Intent(getActivity(), CommentLikeListActivity.class)
                .putExtra(Const.TYPE, CommentLikeListActivity.COMMENTS)
                .putExtra(Const.POSTDATA, new Gson().toJson(postDummy)));
        doTransition(Const.BOTTOM_TO_UP);

    }

    @Override
    public void onLikeListClick(PostRoot.PostItem postDummy) {
        startActivity(new Intent(getActivity(), CommentLikeListActivity.class)
                .putExtra(Const.TYPE, CommentLikeListActivity.LIKES)
                .putExtra(Const.POSTDATA, new Gson().toJson(postDummy)));
        doTransition(Const.BOTTOM_TO_UP);

    }


    @Override
    public void onShareClick(PostRoot.PostItem postDummy) {

        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setTitle(postDummy.getCaption())
                .setContentDescription("By : " + postDummy.getName())
                .setContentImageUrl(BuildConfig.BASE_URL + postDummy.getPost())
                .setContentMetadata(new ContentMetadata().addCustomMetadata("type", "POST").addCustomMetadata(Const.DATA, new Gson().toJson(postDummy)));

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

    @Override
    public void onMentionClick(String userName) {
        startActivity(new Intent(getActivity(), GuestActivity.class).putExtra(Const.USERNAME, userName));
    }


}