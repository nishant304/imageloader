package com.example.nishant.imageloader.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.nishant.imageloader.network.pool.CachePoolManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by nishant on 11/30/2016.
 * a reusable Image request runnable class
 */

public class ImageRequest extends AbstractRequest<Bitmap> {

    private static final String TAG = ImageRequest.class.getSimpleName();
    private final int reqHeight;
    private final int reqWidth;
    private int numOfRetry = 3;
    private BitmapFactory.Options options;

    public ImageRequest(String url, int reqWidth, int reqHeight, IResourceLoadListener imageLoadListener) {
        super(url, imageLoadListener);
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }

    @Override
    protected void loadResourceInBackGround(String url) {

        if (isInterrupted()) {
            return;
        }

        // cache miss, now try to download and decode
        for (int i = 0; i < numOfRetry; i++) {
            Bitmap bitmap = loadBitMap(url);
            if (bitmap != null) {
                notifySuccess(bitmap);
                return;
            }
        }
        notifyFailure("unable to fetch bitmap");
    }

    private Bitmap loadBitMap(String url) {

        if (isInterrupted()) {
            return null;
        }

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            decodeStream(url, options);
        } catch (OutOfMemoryError e) {
            System.gc();
            return null;
        } catch (Exception e) {
            return null;
        }

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inScaled = true;
        options.inDensity = options.outWidth;
        options.inMutable = true;
        options.inTargetDensity = reqWidth * options.inSampleSize;
        Bitmap reusableBitMap = CachePoolManager.getInstance().getBitmapFromReusableSet(options);
        if (reusableBitMap != null) {
            options.inBitmap = reusableBitMap;
            Log.d(TAG, "reusing bitmap");
        }

        try {
            return Bitmap.createScaledBitmap(decodeStream(url, options), reqWidth, reqHeight, false);
        } catch (IOException e) {
            return null;
        }
    }

    private Bitmap decodeStream(String requestUrl, BitmapFactory.Options options) throws IOException {
        URL url = new URL(requestUrl);
        InputStream imageStream = null;
        try {
            imageStream = url.openStream();
            return BitmapFactory.decodeStream(imageStream, null, options);
        } finally {
            try {
                if (imageStream != null) {
                    imageStream.close();
                }
            } catch (IOException e) {
                Log.d(TAG, "unable to close input stream");
            }
        }
    }

    private boolean isInterrupted() {
        if (interrupted) {
            numOfRetry = 0;
        }
        return interrupted;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize = inSampleSize << 1;
            }
        }

        return inSampleSize;
    }

    @Override
    public void cancel() {
        super.cancel();
    }

}
