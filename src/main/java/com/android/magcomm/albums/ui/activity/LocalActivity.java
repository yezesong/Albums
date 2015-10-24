package com.android.magcomm.albums.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.android.magcomm.albums.R;
import com.android.magcomm.albums.ui.fragment.LocalPhotoFragment;
import com.android.magcomm.albums.ui.fragment.SplashFragment;
import com.android.magcomm.albums.util.SharedPrefUtils;

/**
 * Created by lenovo on 15-10-23.
 */
public class LocalActivity extends FragmentActivity {
    private static final String TAG = LocalActivity.class.getSimpleName();

    private LocalPhotoFragment mLocalFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guider);
        FragmentManager fm = getSupportFragmentManager();
        localFragment(fm);
    }

    void localFragment(FragmentManager fm) {
        mLocalFragment = new LocalPhotoFragment();

        if (fm.findFragmentByTag(mLocalFragment.TAG) == null) {
            fm.beginTransaction()
                    .add(R.id.container, mLocalFragment, mLocalFragment.TAG)
                    .commit();
        }
    }
}
