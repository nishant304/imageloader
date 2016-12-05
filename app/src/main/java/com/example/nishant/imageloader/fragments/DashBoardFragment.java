package com.example.nishant.imageloader.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nishant.imageloader.R;
import com.example.nishant.imageloader.adapter.DashBoardAdapter;
import com.example.nishant.imageloader.models.MasterResponse;
import com.example.nishant.imageloader.network.ResponseListener;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nishant on 12/4/2016.
 */

public class DashBoardFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rvDashBoard)
    public RecyclerView mRecyclerView;

    @BindView(R.id.swipeRefresh)
    public SwipeRefreshLayout swipeRefreshLayout;

    private List<MasterResponse> list;
    private DashBoardAdapter dashBoardAdapter;
    private DashBoardAdapter.ILoadUserDetailListener iLoadUserDetailListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DashBoardAdapter.ILoadUserDetailListener) {
            iLoadUserDetailListener = (DashBoardAdapter.ILoadUserDetailListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);
        ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(this);
        setUpView();
        return view;
    }

    private void setUpView() {
        dashBoardAdapter = new DashBoardAdapter(getActivity(), mRecyclerView, iLoadUserDetailListener);
        mRecyclerView.setAdapter(dashBoardAdapter);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        getClient().addStringRequest(new DashBoardResponseListener(dashBoardAdapter));
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    private static class DashBoardResponseListener extends ResponseListener<MasterResponse> {

        private static String url = "http://pastebin.com/raw/wgkJgazE";
        private final WeakReference<DashBoardAdapter> dashBoardWeak;


        public DashBoardResponseListener(DashBoardAdapter dashBoardAdapter) {
            super(MasterResponse.class, url);
            dashBoardWeak = new WeakReference<>(dashBoardAdapter);
        }

        @Override
        public void onResponse(List<MasterResponse> t) {
            DashBoardAdapter dashBoardAdapter = dashBoardWeak.get();
            if (dashBoardAdapter != null) {
                dashBoardAdapter.addItems(t);
            }
        }

        @Override
        public void onResponse(MasterResponse masterResponse) {

        }

        @Override
        public void onError(String message) {

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getClient().cancel(DashBoardResponseListener.url);
    }

}
