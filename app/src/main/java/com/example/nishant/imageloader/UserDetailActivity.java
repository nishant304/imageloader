package com.example.nishant.imageloader;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.example.nishant.imageloader.network.MVNetworkClient;
import com.example.nishant.imageloader.network.ResponseListener;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nishant on 12/4/2016.
 */

public class UserDetailActivity extends BaseActivity {

    @BindView(R.id.ivProfile)
    public ImageView userImageView;

    public static final String PROFILE_URL = "profile_url";
    private static String url;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail);
        ButterKnife.bind(this);
        url = getIntent().getExtras().getString(PROFILE_URL);
        MVNetworkClient.getInstance().addImageRequest(userImageView, new ProfileImageLoadListener(Bitmap.class, userImageView));
    }

    private static class ProfileImageLoadListener extends ResponseListener<Bitmap> {

        private final WeakReference<ImageView> imageView;

        public ProfileImageLoadListener(Class<Bitmap> bitmapClass, ImageView imageView) {
            super(bitmapClass, url);
            this.imageView = new WeakReference<>(imageView);
        }

        @Override
        public void onResponse(List<Bitmap> t) {

        }

        @Override
        public void onResponse(Bitmap bitmap) {
            ImageView imageView = this.imageView.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }

        @Override
        public void onError(String message) {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        MVNetworkClient.getInstance().cancel(url);
    }
}
