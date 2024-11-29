package com.app.liverooms.liveStreamming;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.app.liverooms.R;
import com.app.liverooms.home.adapter.BannerAdapter;
import com.app.liverooms.activity.BaseFragment;
import com.app.liverooms.adapter.DotAdaptr;
import com.app.liverooms.databinding.FragmentLiveBinding;
import com.app.liverooms.home.HomeFragment;
import com.app.liverooms.modelclass.BannerRoot;
import com.app.liverooms.reels.VideoListFragment;
import com.app.liverooms.retrofit.RetrofitBuilder;
import com.app.liverooms.videocall.Ont_to_oneFragmentMain;
import com.app.liverooms.viewModel.ViewModelFactory;
import com.google.android.material.tabs.TabLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveFragmentMain extends BaseFragment {


    FragmentLiveBinding binding;
    BannerAdapter bannerAdapter = new BannerAdapter();
    private LiveFragmentViewModel viewModel;
    private String[] categories = new String[]{"All", "Popular", "Following"};

    public LiveFragmentMain() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_live, container, false);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new LiveFragmentViewModel()).createFor()).get(LiveFragmentViewModel.class);
        binding.setViewModel(viewModel);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        binding.tvLive.setOnClickListener(v -> callFragment(new LiveFragmentMain()));
        binding.tvVideo.setOnClickListener(v -> callFragment(new VideoListFragment()));
        binding.tvOnetoOne.setOnClickListener(v -> callFragment(new Ont_to_oneFragmentMain()));


        initBanner();


        binding.viewPager.setAdapter(new HomeViewPagerAdapter(getChildFragmentManager(), categories));
        binding.tablayout1.setupWithViewPager(binding.viewPager);
        binding.tablayout1.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //ll

                View v = tab.getCustomView();
                if (v != null) {
                    TextView tv = (TextView) v.findViewById(R.id.tvTab);
                    tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                    tv.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.pink));
                    tv.setTextSize(16);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //ll
                View v = tab.getCustomView();
                if (v != null) {
                    TextView tv = (TextView) v.findViewById(R.id.tvTab);
                    tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_gray));
                    tv.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.grayinsta));
                    tv.setTextSize(14);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//ll
            }
        });
        settab(categories);

    }

    private void initBanner() {

        viewModel.isLoading.set(true);
        Call<BannerRoot> call = RetrofitBuilder.create().getBanner("SIMPLE");
        call.enqueue(new Callback<BannerRoot>() {
            @Override
            public void onResponse(Call<BannerRoot> call, Response<BannerRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getBanner().isEmpty()) {
                        bannerAdapter.addData(response.body().getBanner());
                        binding.rvBanner.setAdapter(bannerAdapter);
                        new PagerSnapHelper().attachToRecyclerView(binding.rvBanner);
                        if (bannerAdapter.getItemCount() >= 2) {
                            setupLogicAutoSlider();
                        }
                    }

                }
                viewModel.isLoading.set(false);
            }

            @Override
            public void onFailure(Call<BannerRoot> call, Throwable t) {

            }
        });
    }


    private void setupLogicAutoSlider() {
        DotAdaptr dotAdapter = new DotAdaptr(bannerAdapter.getItemCount(), R.color.pink);
        binding.rvDots.setAdapter(dotAdapter);
        binding.rvBanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager myLayoutManager = (LinearLayoutManager) binding.rvBanner.getLayoutManager();
                int scrollPosition = myLayoutManager.findFirstVisibleItemPosition();
                dotAdapter.changeDot(scrollPosition);
            }
        });
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int pos = 0;
            boolean flag = true;

            @Override
            public void run() {
                if (pos == bannerAdapter.getItemCount() - 1) {
                    flag = false;
                } else if (pos == 0) {
                    flag = true;
                }
                if (flag) {
                    pos++;
                } else {
                    pos--;
                }
                binding.rvBanner.smoothScrollToPosition(pos);
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable, 2000);
    }

    private void callFragment(Fragment fragment) {
        if (getParentFragment() != null) {
            ((HomeFragment) getParentFragment()).openFragmet(fragment);
        }
    }


    private void settab(String[] contry) {
        binding.tablayout1.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tablayout1.removeAllTabs();
        for (int i = 0; i < contry.length; i++) {
            binding.tablayout1.addTab(binding.tablayout1.newTab().setCustomView(createCustomView(i, contry[i])));
        }
        TabLayout tabLayout = binding.tablayout1;
        int betweenSpace = 0;

        final ViewGroup test = (ViewGroup) (tabLayout.getChildAt(0));//tabs is your Tablayout
        int tabLen = test.getChildCount();

        for (int i = 0; i < tabLen; i++) {
            View v = test.getChildAt(i);
            v.setPadding(10, 0, 10, 0);
        }


    }

    private View createCustomView(int i, String s) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tabhorizontol, null);

        TextView tv = (TextView) v.findViewById(R.id.tvTab);

        tv.setText(s);
        if (i == 0) {
            tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

        } else {
            tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_gray));
            tv.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.grayinsta));

        }
        return v;

    }


    public class LiveFragmentViewModel extends ViewModel {
        public ObservableBoolean isLoading = new ObservableBoolean(true);
    }
}