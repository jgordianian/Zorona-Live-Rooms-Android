package com.app.liverooms.liveStreamming;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HomeViewPagerAdapter extends FragmentPagerAdapter {


    private final String[] categories;

    public HomeViewPagerAdapter(FragmentManager fm, String[] categories) {
        super(fm);
        this.categories = categories;
    }

    @Override
    public Fragment getItem(int position) {
        return new LiveListFragment(categories[position]);
    }

    @Override
    public int getCount() {
        return categories.length;
    }
}
