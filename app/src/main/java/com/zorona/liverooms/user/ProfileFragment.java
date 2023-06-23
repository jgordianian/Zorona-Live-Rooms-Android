package com.zorona.liverooms.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.zorona.liverooms.R;
import com.zorona.liverooms.activity.BaseFragment;
import com.zorona.liverooms.activity.SettingActivity;
import com.zorona.liverooms.modelclass.UserRoot;
import com.zorona.liverooms.posts.FeedGridActivity;
import com.zorona.liverooms.reels.VideoListGridActivity;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.retrofit.UserApiCall;
import com.zorona.liverooms.user.complain.ComplainListActivity;
import com.zorona.liverooms.user.complain.CreateComplainActivity;
import com.zorona.liverooms.user.freeCoins.FreeDimondsActivity;
import com.zorona.liverooms.user.vip.VipPlanActivity;
import com.zorona.liverooms.user.wallet.MyWalletActivity;
import com.zorona.liverooms.viewModel.ViewModelFactory;
import com.zorona.liverooms.SessionManager;
import com.zorona.liverooms.databinding.FragmentProfileBinding;
import com.google.gson.Gson;


public class ProfileFragment extends BaseFragment {


    FragmentProfileBinding binding;
    SessionManager sessionManager;
    private UserRoot.User user;
    private UserApiCall userApiCall;
    private ProfileViewModel viewModel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new ProfileViewModel()).createFor()).get(ProfileViewModel.class);
        binding.setViewModel(viewModel);

        return binding.getRoot();

    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        sessionManager = new SessionManager(getActivity());
        userApiCall = new UserApiCall(getActivity());

        viewModel.isLoading.set(true);
        user = sessionManager.getUser();
        userApiCall.getUser(user -> {
            sessionManager.saveUser(user);
            ProfileFragment.this.user = user;
            initView();

            viewModel.isLoading.set(false);
        });
        initListner();
    }

    private void initListner() {
        binding.btnSetting.setOnClickListener(v -> startActivity(new Intent(getActivity(), SettingActivity.class)));
        binding.lytMyPost.setOnClickListener(v -> startActivity(new Intent(getActivity(), FeedGridActivity.class).putExtra(Const.DATA, new Gson().toJson(user))));
        binding.lytMyVideos.setOnClickListener(v -> startActivity(new Intent(getActivity(), VideoListGridActivity.class).putExtra(Const.DATA, new Gson().toJson(user))));
        binding.lytFollowing.setOnClickListener(v -> startActivity(new Intent(getActivity(), FollowrsListActivity.class).putExtra(Const.TYPE, 1).putExtra(Const.USERID, user.getId())));
        binding.lytFollowrs.setOnClickListener(v -> startActivity(new Intent(getActivity(), FollowrsListActivity.class).putExtra(Const.TYPE, 2).putExtra(Const.USERID, user.getId())));
        binding.btnEditProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfileActivity.class)));
        binding.tvLevel.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyLevelListActivity.class)));
        binding.lytVIP.setOnClickListener(v -> startActivity(new Intent(getActivity(), VipPlanActivity.class)));
        binding.lytWallet.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyWalletActivity.class)));
        binding.lytFreeDimonds.setOnClickListener(v -> startActivity(new Intent(getActivity(), FreeDimondsActivity.class)));
        binding.lytSupport.setOnClickListener(v -> startActivity(new Intent(getActivity(), CreateComplainActivity.class)));
        binding.lytComplains.setOnClickListener(v -> startActivity(new Intent(getActivity(), ComplainListActivity.class)));
    }

    private void initView() {
    if (getActivity() == null) return;

        binding.imgUser.setUserImage(user.getImage(), user.isIsVIP());
        binding.tvName.setText(user.getName());
        binding.tvAge.setText(String.valueOf(user.getAge()));
        // binding.tvBio.setText(user.getBio());
        // binding.tvCountry.setText(user.getCountry());
        binding.tvFollowrs.setText(String.valueOf(user.getFollowers()));
        binding.tvLevel.setText(user.getLevel().getName());
        binding.tvFollowing.setText(String.valueOf(user.getFollowing()));

        binding.tvUserName.setText(user.getUsername());

        if (user.getGender().equalsIgnoreCase(Const.MALE)) {
            binding.imgGender.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.male));
        } else {
            binding.imgGender.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.female));
        }
    }

    public class ProfileViewModel extends ViewModel {
        public ObservableBoolean isLoading = new ObservableBoolean(true);
    }
}