package com.android.magcomm.albums.data;

import android.content.AsyncQueryHandler;
import android.net.Uri;
import android.provider.MediaStore;
import java.util.HashMap;

/**
 * Created by lenovo on 15-10-23.
 */
public class LocalImages {
    private static final String TAG = LocalImages.class.getSimpleName();
    private static final boolean DEBUG = true;

    private static HashMap<Integer, Runnable> sQueryMap = new HashMap<Integer, Runnable>();

    public static final int ID_INDEX = 0;
    public static final int NAME_INDEX = 1;
    public static final int DATA_INDEX = 2;
    public static final int SIZE_INDEX = 3;
    public static final int TYPE_INDEX = 4;

    private static final String mWhere = "mime_type in ('image/jpeg','image/png') ";
    private static final String mSortOrder = MediaStore.Images.Media.DATA;

    private static final Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE
    };

    public static void startQuery(AsyncQueryHandler handler, int token) {
        final int queryToken = token;
        final AsyncQueryHandler queryHandler = handler;

        Runnable r = sQueryMap.get(token);
        if (r != null) {
            queryHandler.removeCallbacks(r);
            sQueryMap.remove(token);
        }

        Runnable queryRunnable = new Runnable() {
            public void run() {
                queryHandler.startQuery(queryToken, null, mImageUri,
                        STORE_IMAGES, mWhere, null, mSortOrder);
            }
        };

        queryHandler.postDelayed(queryRunnable, 10);
        sQueryMap.put(token, queryRunnable);
    }
}
