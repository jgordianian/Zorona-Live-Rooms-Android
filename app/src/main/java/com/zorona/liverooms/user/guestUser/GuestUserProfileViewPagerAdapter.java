package com.app.liverooms.user.guestUser;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.liverooms.modelclass.GuestProfileRoot;

public class GuestUserProfileViewPagerAdapter extends FragmentPagerAdapter {


    private GuestProfileRoot.User userDummy;

    public GuestUserProfileViewPagerAdapter(FragmentManager fm, GuestProfileRoot.User userDummy) {
        super(fm);
        this.userDummy = userDummy;
    }

    @Override
    public Fragment getItem(int position) {
        if (position==0) {
            return new GuestUserPostsFragment(userDummy);
        } else {
            return new GuestUserReelsFragment(userDummy);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
