package com.android.magcomm.albums.util;

import android.app.Activity;
import android.content.Intent;

import com.android.magcomm.albums.ui.activity.AlbumsActivity;

/**
 * Created by Aspsine on 2015/3/20.
 */
public class IntentUtils {

    public static final void intentToAlbumsActivity(Activity activity) {
        Intent intent = new Intent(activity, AlbumsActivity.class);
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(android.support.v7.appcompat.R.anim.abc_fade_in, android.support.v7.appcompat.R.anim.abc_fade_out);
    }
}
