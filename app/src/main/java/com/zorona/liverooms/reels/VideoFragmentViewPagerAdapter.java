package com.zorona.liverooms.reels;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class VideoFragmentViewPagerAdapter extends FragmentPagerAdapter {


    public VideoFragmentViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new VideoListFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }
}
