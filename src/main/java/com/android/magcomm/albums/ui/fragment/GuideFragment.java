package com.android.magcomm.albums.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.magcomm.albums.R;
import com.android.magcomm.albums.ui.activity.GuiderActivity;
import com.android.magcomm.albums.util.SharedPrefUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGABanner.TransitionEffect;

/**
 * Created by lenovo on 15-10-21.
 */
public class GuideFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = GuideFragment.class.getSimpleName();
    private Activity sActivity;
    private View mRootView;
    private BGABanner mBanner;

    private TransitionEffect[] mEffects = new TransitionEffect[]{
            TransitionEffect.Default,
            TransitionEffect.Alpha,
            TransitionEffect.Rotate,
            TransitionEffect.Cube,
            TransitionEffect.Flip,
            TransitionEffect.Accordion,
            TransitionEffect.ZoomFade,
            TransitionEffect.Fade,
            TransitionEffect.ZoomCenter,
            TransitionEffect.ZoomStack,
            TransitionEffect.Stack,
            TransitionEffect.Depth,
            TransitionEffect.Zoom
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sActivity = getActivity();
        mRootView = inflater.inflate(R.layout.fragment_guider, container, false);

        initViews();
        return mRootView;
    }

    private void initViews() {
        mBanner = (BGABanner) mRootView.findViewById(R.id.banner_splash);
        mBanner.setTransitionEffect(mEffects[2]);//Rotate
        mBanner.setPageChangeDuration(800);// 设置page切换时长

        List<View> views = new ArrayList<>();
        views.add(getPageView(R.drawable.guide_1));
        views.add(getPageView(R.drawable.guide_2));

        View lastView = sActivity.getLayoutInflater().inflate(R.layout.view_last, null);

        lastView.findViewById(R.id.btn_last).setOnClickListener(this);
        views.add(lastView);

        mBanner.setViews(views);
    }

    private View getPageView(int resid) {
        ImageView imageView = new ImageView(sActivity);
        imageView.setImageResource(resid);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    @Override
    public void onClick(View v) {
        SharedPrefUtils.markFirstLaunch(sActivity);
        ((GuiderActivity) getActivity()).intentToAlbumsActivity();
    }

}
