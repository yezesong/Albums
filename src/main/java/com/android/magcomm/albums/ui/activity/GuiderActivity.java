package com.android.magcomm.albums.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;

import com.android.magcomm.albums.R;
import com.android.magcomm.albums.animation.AnimationEndListener;
import com.android.magcomm.albums.ui.fragment.GuideFragment;
import com.android.magcomm.albums.ui.fragment.SplashFragment;
import com.android.magcomm.albums.util.IntentUtils;
import com.android.magcomm.albums.util.SharedPrefUtils;

/**
 * Created by lenovo on 15-10-21.
 */
public class GuiderActivity extends FragmentActivity {
    private SplashFragment mSplashFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            /*
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            */
        }
        setContentView(R.layout.activity_guider);
        FragmentManager fm = getSupportFragmentManager();

        if (SharedPrefUtils.isFirstLaunch(this)) {
            guide(fm);
        } else {
            splash(fm);
        }
    }

    void guide(FragmentManager fm) {
        if (fm.findFragmentByTag(GuideFragment.TAG) == null) {
            fm.beginTransaction()
                    .add(R.id.container, new GuideFragment(), GuideFragment.TAG)
                    .commit();
        }
    }

    void splash(FragmentManager fm) {
        mSplashFragment = new SplashFragment();

        if (fm.findFragmentByTag(SplashFragment.TAG) == null) {
            fm.beginTransaction()
                    .add(R.id.container, mSplashFragment, SplashFragment.TAG)
                    .commit();
        }
    }

    private final Animation.AnimationListener animListener = new AnimationEndListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            mSplashFragment.ivSplash.setVisibility(View.GONE);
            intentToAlbumsActivity();
        }
    };

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if(fragment != null && fragment instanceof SplashFragment){
            mSplashFragment.mIvSplashAnim.setAnimationListener(animListener);
        }
    }

    public void intentToAlbumsActivity() {
        IntentUtils.intentToAlbumsActivity(this);
    }
}
