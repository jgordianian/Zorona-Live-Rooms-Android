package com.zorona.liverooms.user.wallet;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class WalletViewPagerAdapter extends FragmentPagerAdapter {


    public WalletViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {


        if (position == 0) {
            return new RechargeFragment();
        } else if (position == 1) {
            return new RcoinFragment();
        } else {
            return new RecordFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
