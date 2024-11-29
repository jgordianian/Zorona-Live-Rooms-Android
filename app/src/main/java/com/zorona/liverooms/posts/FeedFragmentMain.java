package com.app.liverooms.posts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.app.liverooms.R;
import com.app.liverooms.activity.BaseFragment;
import com.app.liverooms.retrofit.Const;
import com.app.liverooms.databinding.FragmentFeedMainBinding;
import com.app.liverooms.user.SearchActivity;
import com.google.android.material.tabs.TabLayout;


public class FeedFragmentMain extends BaseFragment {


    FragmentFeedMainBinding binding;
    String[] types = new String[]{Const.TYPE_POPULAR, Const.TYPE_LATEST, Const.TYPE_FOLLOWING};
    String[] types1 = new String[]{"Popular", Const.TYPE_LATEST, Const.TYPE_FOLLOWING};

    public FeedFragmentMain() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed_main, container, false);


        initTabLayout();
        initListner();
        return binding.getRoot();
    }

    private void initListner() {
        binding.lytSearch.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SearchActivity.class));
            doTransition(Const.BOTTOM_TO_UP);
        });
    }

    private void initTabLayout() {
        binding.viewPager.setAdapter(new FeedViewPagerAdapter(getChildFragmentManager(), types));
        binding.tablayout1.setupWithViewPager(binding.viewPager);
        binding.tablayout1.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //ll

                View v = tab.getCustomView();
                if (v != null) {
                    TextView tv = (TextView) v.findViewById(R.id.tvTab);
                    tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

                    View indicator = (View) v.findViewById(R.id.indicator);
                    indicator.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //ll
                View v = tab.getCustomView();
                if (v != null) {
                    TextView tv = (TextView) v.findViewById(R.id.tvTab);
                    tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.graylight));

                    View indicator = (View) v.findViewById(R.id.indicator);
                    indicator.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//ll
            }
        });
        settab(types1);
    }


    private void settab(String[] contry) {
        binding.tablayout1.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tablayout1.removeAllTabs();
        for (int i = 0; i < contry.length; i++) {
            binding.tablayout1.addTab(binding.tablayout1.newTab().setCustomView(createCustomView(i, contry[i])));
        }

    }

    private View createCustomView(int i, String s) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tabhorizontol2, null);
        TextView tv = (TextView) v.findViewById(R.id.tvTab);
        tv.setText(s);
        tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        View indicator = (View) v.findViewById(R.id.indicator);
        if (i == 0) {
            indicator.setVisibility(View.VISIBLE);
        } else {
            indicator.setVisibility(View.GONE);
        }
        return v;

    }

}