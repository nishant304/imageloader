package com.example.nishant.imageloader.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nishant.imageloader.R;

import butterknife.ButterKnife;

/**
 * Created by nishant on 12/4/2016.
 */

public class UserScreenFragment extends BaseFragment {
    private static final String PROFILE_URL = "profile_url";

    public static UserScreenFragment getFragment(String user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PROFILE_URL, user);
        UserScreenFragment userScreenFragment = new UserScreenFragment();
        userScreenFragment.setArguments(bundle);
        return userScreenFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_detail, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

}
