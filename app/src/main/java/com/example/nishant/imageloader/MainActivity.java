package com.example.nishant.imageloader;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.View;

import com.example.nishant.imageloader.adapter.DashBoardAdapter;
import com.example.nishant.imageloader.fragments.DashBoardFragment;
import com.example.nishant.imageloader.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements DashBoardAdapter.ILoadUserDetailListener, View.OnClickListener {

    private String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.fbMain)
    public FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            addFragment(R.id.fragmentContainer, new DashBoardFragment(), TAG);
        }

        floatingActionButton.setOnClickListener(this);
    }

    @Override
    public void loadUser(User user) {
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra(UserDetailActivity.PROFILE_URL, user.getProfileImage().getLarge());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, IntensiveImageTest.class);
        startActivity(intent);
    }
}
