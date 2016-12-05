package com.example.nishant.imageloader.utils;

import android.content.Context;

/**
 * Created by nishant on 12/4/2016.
 */

public final class ConnectivityManager {

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return false;
    }
}
