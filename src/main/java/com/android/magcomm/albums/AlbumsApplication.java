package com.android.magcomm.albums;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.appcompat.BuildConfig;

import com.android.magcomm.albums.data.LocalImages;
import com.android.magcomm.albums.engine.Constants;
import com.android.magcomm.albums.engine.Engine;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by lenovo on 15-10-21.
 */
public class AlbumsApplication extends Application {
    private static AlbumsApplication sInstance;
    private Engine mEngine;

    @Override
    public void onCreate() {
        //setStrictMode();
        super.onCreate();
        sInstance = this;
        mEngine = new Retrofit.Builder()
                .baseUrl("http://7xk9dj.com1.z0.glb.clouddn.com/")
                        //https://raw.githubusercontent.com/bingoogolapple/BGABanner-Android/server/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(Engine.class);

        Fresco.initialize(this);
        initImageLoader(this);
    }

    /**
     * issue: retrofit Memory leak in StrickMode
     * https://github.com/square/retrofit/issues/801
     */
    private void setStrictMode() {
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.enableDefaults();
        }
    }

    private void initImageLoader(final Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(Constants.Config.IMAGE_CACHE_SIZE) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
//                .writeDebugLogs() // Remove for release app
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static AlbumsApplication getInstance() {
        return sInstance;
    }

    public Engine getEngine() {
        return mEngine;
    }
}
