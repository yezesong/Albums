package com.android.magcomm.albums.data;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.android.magcomm.albums.model.AlbumsModel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by lenovo on 15-10-23.
 */
public class LocalImages {
    private static final String TAG = LocalImages.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static boolean sLoadingThreads;

    private int mImageId;
    private String mImageName;
    private String mImageDire;
    private long mImageSize;
    private String mImageType;
    private Uri mImageUri;

    public static final int ID_INDEX = 0;
    public static final int NAME_INDEX = 1;
    public static final int DATA_INDEX = 2;
    public static final int SIZE_INDEX = 3;
    public static final int TYPE_INDEX = 4;

    private static final String where = "mime_type in ('image/jpeg','image/png') ";
    private static final String sortOrder = MediaStore.Images.Media.DATA;

    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE
    };

    private static void fillFromCursor(LocalImages localImages,
                                       Cursor c) {
        synchronized (localImages) {
            localImages.mImageId = c.getInt(ID_INDEX);
            localImages.mImageName = c.getString(NAME_INDEX);
            localImages.mImageDire = c.getString(DATA_INDEX);
            localImages.mImageSize = c.getLong(SIZE_INDEX);
            localImages.mImageType = c.getString(TYPE_INDEX);
            localImages.mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().
                    appendPath(Long.toString(localImages.mImageId)).build();
        }
    }

    public static LocalImages from(Cursor cursor) {
        LocalImages image = Cache.get(ID_INDEX);
        if (image != null) {
            fillFromCursor(image, cursor);
            return image;
        } else {
            LocalImages conv = new LocalImages(cursor);
            try {
                Cache.put(conv);
            } catch (IllegalStateException e) {
                if (!Cache.replace(conv)) {
                    Log.e(TAG, "LocalImages.from cache.replace failed on " + conv);
                }
            }
            return conv;
        }

    }

    private LocalImages(Cursor cursor) {
        fillFromCursor(this, cursor);
    }

    public static void init(final Context context) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                cacheAllThreads(context);
            }
        }, "LocalImages.init");
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    private static void cacheAllThreads(Context context) {

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }

        synchronized (Cache.getInstance()) {
            if (sLoadingThreads) {
                return;
            }
            sLoadingThreads = true;
        }

        // Keep track of what threads are now on disk so we
        // can discard anything removed from the cache.
        HashSet<Integer> threadsOnDisk = new HashSet<Integer>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES,
                where, null, sortOrder);
        // Query for all localImages.
        try {
            if (cursor != null) {
                if (DEBUG)
                    Log.i(TAG, " cacheAllThreads is called and cursor's count = " + cursor.getCount());

                while (cursor.moveToNext()) {

                    int imageId = cursor.getInt(ID_INDEX);
                    threadsOnDisk.add(imageId);

                    // Try to find this image ID in the cache.
                    LocalImages image;
                    synchronized (Cache.getInstance()) {
                        image = Cache.get(imageId);
                    }

                    if (image == null) {
                        // Make a new LocalImages and put it in
                        // the cache if necessary.
                        image = new LocalImages(cursor);
                        try {
                            synchronized (Cache.getInstance()) {
                                Cache.put(image);
                            }
                        } catch (IllegalStateException e) {
                            Log.e(TAG, "Tried to add duplicate LocalImages to Cache" +
                                    " for imageId : " + imageId + " new image: " + image);
                            if (!Cache.replace(image)) {
                                Log.e(TAG, "cacheAllThreads cache.replace failed on " + image);
                            }
                        }
                    } else {
                        fillFromCursor(image, cursor);
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            synchronized (Cache.getInstance()) {
                sLoadingThreads = false;
            }
        }

        Cache.keepOnly(threadsOnDisk);
        if (DEBUG)
            Cache.dumpCache();
    }

    /**
     * Are we in the process of loading and caching all the threads?.
     */
    public static boolean loadingThreads() {
        synchronized (Cache.getInstance()) {
            return sLoadingThreads;
        }
    }

    /**
     * Private cache for the use of the various forms of Conversation.get.
     */
    private static class Cache {
        private static Cache sInstance = new Cache();

        static Cache getInstance() {
            return sInstance;
        }

        private final HashSet<LocalImages> mCache;

        private Cache() {
            mCache = new HashSet<LocalImages>(10);
        }

        /**
         * Return the LocalImages with the imageId  or
         * null if it's not in cache.
         */
        static LocalImages get(int imageId) {
            synchronized (sInstance) {
                for (LocalImages image : sInstance.mCache) {
                    if (image.mImageId == imageId) {
                        return image;
                    }
                }
            }
            return null;
        }

        static void put(LocalImages image) {
            synchronized (sInstance) {
                if (sInstance.mCache.contains(image)) {
                    if (DEBUG) {
                        dumpCache();
                    }
                    throw new IllegalStateException("cache already contains " + image +
                            " and imageId is = : " + image.mImageId);
                }
                sInstance.mCache.add(image);
            }
        }

        static void dumpCache() {
            synchronized (sInstance) {
                Log.i(TAG, "LocalImages dumpCache: ");
                for (LocalImages c : sInstance.mCache) {
                    //do nothing
                }
            }
        }

        /**
         * Remove all LocalImages from the cache that are not in
         * the provided set of image IDs.
         */
        static void keepOnly(Set<Integer> imageId) {
            synchronized (sInstance) {
                Iterator<LocalImages> iter = sInstance.mCache.iterator();
                while (iter.hasNext()) {
                    LocalImages images = iter.next();
                    if (!imageId.contains(images)) {
                        iter.remove();
                    }
                }
            }
            if (DEBUG) {
                Log.d(TAG, "after keepOnly");
                dumpCache();
            }
        }

        static boolean replace(LocalImages images) {
            synchronized (sInstance) {
                if (!sInstance.mCache.contains(images)) {
                    if (DEBUG) {
                        dumpCache();
                    }
                    return false;
                }

                sInstance.mCache.remove(images);
                sInstance.mCache.add(images);
                return true;
            }
        }

        static void remove(int imageId) {
            synchronized (sInstance) {
                if (DEBUG) {
                    Log.d(TAG, "remove imageId: " + imageId);
                    dumpCache();
                }
                for (LocalImages image : sInstance.mCache) {
                    if (image.mImageId == imageId) {
                        sInstance.mCache.remove(image);
                        return;
                    }
                }
            }
        }
    }

    public static void startQueryForAll(AsyncQueryHandler handler, int token) {
        handler.cancelOperation(token);
        startQuery(handler, token, null);
    }

    public static void startQuery(AsyncQueryHandler handler, int token, String selection) {
        handler.cancelOperation(token);

        handler.startQuery(token, null, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                STORE_IMAGES, selection, null, sortOrder);
    }
}
