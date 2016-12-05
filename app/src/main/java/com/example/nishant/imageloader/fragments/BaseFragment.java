package com.example.nishant.imageloader.fragments;

import android.app.Fragment;
import android.content.Context;

import com.example.nishant.imageloader.network.MVNetworkClient;

/**
 * Created by nishant on 12/4/2016.
 */

public class BaseFragment extends Fragment {

    private MVNetworkClient client;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        client = MVNetworkClient.getInstance();
    }

    protected MVNetworkClient getClient() {
        return client;
    }
}
