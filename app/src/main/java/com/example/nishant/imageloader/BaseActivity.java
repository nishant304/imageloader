package com.example.nishant.imageloader;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by nishant on 12/4/2016.
 */

public class BaseActivity extends AppCompatActivity {

    protected void addFragment(int id, Fragment fragment, String TAG) {
        if (findViewById(id) == null) {
            throw new RuntimeException("id not found, or view is not set");
        }
        if (fragment == null) {
            throw new RuntimeException("fragment is null");
        }

        getFragmentManager()
                .beginTransaction()
                .add(id, fragment, TAG)
                .addToBackStack(TAG)
                .commit();
    }

    protected void replaceFragment(int id, Fragment fragment, String TAG) {
        if (findViewById(id) == null) {
            throw new RuntimeException("id not found, or view is not set");
        }
        if (fragment == null) {
            throw new RuntimeException("fragment is null");
        }

        getFragmentManager().beginTransaction().replace(id, fragment, TAG).commit();
    }

}
