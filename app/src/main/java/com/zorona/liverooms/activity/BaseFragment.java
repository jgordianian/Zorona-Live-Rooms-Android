package com.zorona.liverooms.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zorona.liverooms.R;
import com.zorona.liverooms.retrofit.Const;
import com.zorona.liverooms.SessionManager;

public abstract class BaseFragment extends Fragment {
    public SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getActivity());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void doTransition(int type) {

        if (getActivity() != null) {
            if (type == Const.BOTTOM_TO_UP) {

                getActivity().overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_none);
            } else if (type == Const.UP_TO_BOTTOM) {
                getActivity().overridePendingTransition(R.anim.exit_none, R.anim.enter_from_up);

            }

        }
    }
}
