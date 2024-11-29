package com.app.liverooms.user.wallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.app.liverooms.R;
import com.app.liverooms.activity.BaseActivity;
import com.app.liverooms.databinding.ActivityMyWalletBinding;
import com.google.android.material.tabs.TabLayout;

public class MyWalletActivity extends BaseActivity {
    ActivityMyWalletBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_wallet);


        binding.viewPager.setAdapter(new WalletViewPagerAdapter(getSupportFragmentManager()));
        binding.viewPager.setOffscreenPageLimit(3);
        binding.tablayout1.setupWithViewPager(binding.viewPager);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position==0) {
                    binding.lytRoot.setBackground(ContextCompat.getDrawable(MyWalletActivity.this, R.drawable.bg_gredent_yellow));

                } else if (position==1) {
                    binding.lytRoot.setBackground(ContextCompat.getDrawable(MyWalletActivity.this, R.drawable.bg_gredent_purple));
                } else {
                    binding.lytRoot.setBackground(ContextCompat.getDrawable(MyWalletActivity.this, R.color.blackpure));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        binding.tablayout1.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //ll

                View v = tab.getCustomView();
                if (v!=null) {
                    TextView tv = (TextView) v.findViewById(R.id.tvTab);
                    tv.setTextColor(ContextCompat.getColor(MyWalletActivity.this, R.color.pink));

                    tv.setTextSize(16);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //ll
                View v = tab.getCustomView();
                if (v != null) {
                    TextView tv = (TextView) v.findViewById(R.id.tvTab);
                    tv.setTextColor(ContextCompat.getColor(MyWalletActivity.this, R.color.white));

                    tv.setTextSize(14);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//ll
            }
        });
        settab(new String[]{"Recharge", "Income", "Record"});
    }

    private void settab(String[] contry) {
        binding.tablayout1.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tablayout1.removeAllTabs();
        for (int i = 0; i < contry.length; i++) {
            binding.tablayout1.addTab(binding.tablayout1.newTab().setCustomView(createCustomView(i, contry[i])));
        }

    }

    private View createCustomView(int i, String s) {

        View v = LayoutInflater.from(this).inflate(R.layout.custom_tabhorizontol_plan, null);

        TextView tv = (TextView) v.findViewById(R.id.tvTab);

        tv.setText(s);
        if (i == 0) {
            tv.setTextColor(ContextCompat.getColor(this, R.color.white));
            tv.setTextSize(16);
        } else {
            tv.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
        }
        return v;

    }
}