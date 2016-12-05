package com.example.nishant.imageloader.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by nishant on 11/30/2016.
 */

public class StringRequest<T> extends AbstractRequest {

    private static final int numOfRetry = 3;
    private static final String TAG = StringRequest.class.getSimpleName();

    protected StringRequest(String url, IResourceLoadListener resourceLoadListener) {
        super(url, resourceLoadListener);
    }

    @Override
    protected void loadResourceInBackGround(String stringUrl) {

        if (interrupted) {
            notifyFailure("request cancelled");
            return;
        }

        for (int i = 0; i < numOfRetry; i++) {
            try {
                String response = downloadFromUrl(stringUrl);
                notifySuccess(response);
                return;
            } catch (MalformedURLException e) {
                notifyFailure("invalid url");
            } catch (IOException e) {
                try {
                    Thread.sleep(200);
                    Log.d(TAG, "retrying again");
                } catch (InterruptedException e1) {

                }
            }
        }

        notifyFailure("unable to download resource");
    }

    private String downloadFromUrl(String stringUrl) throws IOException {
        URLConnection connection = new URL(stringUrl).openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String buffer;
        while ((buffer = bufferedReader.readLine()) != null) {
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }
}
