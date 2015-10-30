package com.android.magcomm.albums.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.android.magcomm.albums.R;
import com.android.magcomm.albums.ui.fragment.LocalFragment;

/**
 * Created by lenovo on 15-10-27.
 */
public class AlbumsActivity extends FragmentActivity implements LocalFragment.BackHandlerInterface {
    private LocalFragment mLocalFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmnets_container);
        FragmentManager fm = getSupportFragmentManager();
        goLocal(fm);
    }

    void goLocal(FragmentManager fm) {
        mLocalFragment = new LocalFragment();
        if (fm.findFragmentByTag(LocalFragment.TAG) == null) {
            fm.beginTransaction()
                    .add(R.id.container, mLocalFragment, LocalFragment.TAG)
                    .commit();
        }
    }

    void goAlbums(FragmentManager fm) {

    }

    @Override
    public void setSelectedFragment(LocalFragment backHandledFragment) {
        this.mLocalFragment = backHandledFragment;
    }

    @Override
    public void onBackPressed() {
        if (mLocalFragment == null || !mLocalFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
