package com.zorona.liverooms.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.zorona.liverooms.R;
import com.zorona.liverooms.activity.BaseFragment;
import com.zorona.liverooms.liveStreamming.LiveFragmentMain;
import com.zorona.liverooms.databinding.FragmentHomeBinding;


public class HomeFragment extends BaseFragment {

    FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        openFragmet(new LiveFragmentMain());

        return binding.getRoot();
    }

    public void openFragmet(Fragment fragment) {
        getChildFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frameHome, fragment).commit();
    }


}