package com.android.magcomm.albums.ui.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.magcomm.albums.AlbumsApplication;
import com.android.magcomm.albums.R;
import com.android.magcomm.albums.engine.Engine;
import com.android.magcomm.albums.model.BannerModel;

import java.util.List;
import java.util.Random;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by lenovo on 15-10-22.
 */
public class SplashFragment extends Fragment {
    public static final String TAG = SplashFragment.class.getSimpleName();
    private Engine mEngine;
    public ImageView ivSplash;
    public Animation mIvSplashAnim;

    @Override
    public void onAttach(Activity activity) {
        mIvSplashAnim = AnimationUtils.loadAnimation(activity, R.anim.splash);
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEngine = AlbumsApplication.getInstance().getEngine();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivSplash = (ImageView) view.findViewById(R.id.splash);

        ivSplash.startAnimation(mIvSplashAnim);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reflash();
    }

    private void reflash() {

        ivSplash.setBackgroundResource(R.drawable.bg_splash);

        /*mEngine.threeItem().enqueue(new Callback<BannerModel>() {
            @Override
            public void onResponse(Response<BannerModel> response, Retrofit retrofit) {
                //if (response.body() == null) {
                //    return;
                //}

               *//* List<BannerModel> albumsModels = response.body();
                int length = albumsModels.size();
                Random random = new Random();
                int index = random.nextInt(length);
                Log.i(TAG, "random index is " + index);*//*
                //SimpleDraweeView simpleDraweeView = (SimpleDraweeView) ivSplash;
                //simpleDraweeView.setImageURI(Uri.parse(bannerModel.imgs.get(0)));
                //ivSplash.setImageURI(Uri.parse(response.body().imgs.get(0)));

                ivSplash.setBackgroundResource(R.drawable.bg_splash);
            }

            @Override
            public void onFailure(Throwable t) {
                ivSplash.setBackgroundResource(R.drawable.bg_splash);
            }
        });*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mIvSplashAnim = null;
    }

}
