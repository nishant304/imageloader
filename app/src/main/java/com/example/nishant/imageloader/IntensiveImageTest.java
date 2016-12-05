package com.example.nishant.imageloader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.example.nishant.imageloader.adapter.ImageListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nishant on 12/4/2016.
 */

public class IntensiveImageTest extends BaseActivity {

    @BindView(R.id.lvTest)
    public ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intensive_image_layout);
        ButterKnife.bind(this);

        ImageListAdapter imageListAdapter = new ImageListAdapter(this);
        listView.setAdapter(imageListAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listView.setAdapter(null);
    }
}
