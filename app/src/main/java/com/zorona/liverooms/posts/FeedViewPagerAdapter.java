package com.zorona.liverooms.posts;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FeedViewPagerAdapter extends FragmentPagerAdapter {


    private String[] types;

    public FeedViewPagerAdapter(FragmentManager fm, String[] types) {
        super(fm);
        this.types = types;
    }

    @Override
    public Fragment getItem(int position) {

        return new FeedListFragment(types[position]);
    }

    @Override
    public int getCount() {
        return types.length;
    }
}
