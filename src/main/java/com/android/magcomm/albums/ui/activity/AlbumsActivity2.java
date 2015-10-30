package com.android.magcomm.albums.ui.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.magcomm.albums.BaseProgressQueryHandler;
import com.android.magcomm.albums.R;
import com.android.magcomm.albums.adapter.LocalAdapter;
import com.android.magcomm.albums.bean.ImageFloder;
import com.android.magcomm.albums.data.LocalImages;
import com.android.magcomm.albums.ui.ListImageDirPopupWindow;
import com.android.magcomm.albums.util.ContantsUtils;
import com.android.magcomm.albums.util.DensityUtil;
import com.android.magcomm.albums.view.ImageTextView;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by lenovo on 15-10-21.
 */
public class AlbumsActivity2 extends Activity implements ListImageDirPopupWindow.OnImageDirSelected, ImageTextView.ImageTextViewClick, View.OnClickListener {
    private static final String TAG = AlbumsActivity2.class.getSimpleName();
    private boolean mNeedQuery = false;
    private boolean mIsInActivity = false;
    private GridView mGridView;
    private ImageTextView mBackView;
    //private LinearLayout mBottomLy;
    private LocalAdapter mAdapter;
    private int mScreenHeight;
    /**
     * 所有的图片
     */
    private List<String> mImgs;

    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;
    private ListImageDirPopupWindow mListImageDirPopupWindow;
    private int mTotalCount = 0;
    /**
     * 图片数量最多的文件夹
     */
    private File mImgDir;
    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();
    /*
    * 临时的辅助类，用于防止同一个文件夹的多次扫描
    */
    private HashSet<String> mDirPaths;
    private ThreadListQueryHandler mQueryHandler;

    private LocalHandler mHandler = new LocalHandler(this);

    @Override
    public void selected(ImageFloder floder) {
        mImgDir = new File(floder.getDir());
        mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg"))
                    return true;
                return false;
            }
        }));
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new LocalAdapter(getApplicationContext(), mImgs,
                R.layout.item_staggered, mImgDir.getAbsolutePath());
        mGridView.setAdapter(mAdapter);
        // mAdapter.notifyDataSetChanged();
        //mImageCount.setText(floder.getCount() + "张");
        //mChooseDir.setText(floder.getName());
        mListImageDirPopupWindow.dismiss();

    }

    @Override
    public void doClick(int id) {
        Log.i("yzs005", "doClick is called and id = " + id);
    }

    @Override
    public void onClick(View v) {

    }

    static class LocalHandler extends Handler {
        WeakReference<AlbumsActivity2> mActivity;

        LocalHandler(AlbumsActivity2 activity) {
            mActivity = new WeakReference<AlbumsActivity2>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            AlbumsActivity2 theActivity = mActivity.get();
            theActivity.setDataToView();
            // 初始化展示文件夹的popupWindw
            theActivity.initListDirPopupWindw();
        }
    }

    /**
     * 为View绑定数据
     */
    private void setDataToView() {
        if (mImgDir == null) {
            Toast.makeText(getApplicationContext(), R.string.no_photos,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mImgs = Arrays.asList(mImgDir.list());
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new LocalAdapter(getApplicationContext(), mImgs,
                R.layout.item_staggered, mImgDir.getAbsolutePath());
        mGridView.setAdapter(mAdapter);
        //mImageCount.setText(totalCount + "张");
    }

    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw() {
        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                LinearLayout.LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.6),
                mImageFloders, LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.list_dir, null));

        mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }

    private View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = LayoutInflater.from(this).inflate(R.layout.fragment_local, null);

        setContentView(mRootView);
        mScreenHeight = DensityUtil.getScreenHeight(this);
        initView();
        mQueryHandler = new ThreadListQueryHandler(getContentResolver());
    }

    /**
     * 初始化View
     */
    private void initView() {
        mGridView = (GridView) findViewById(R.id.local_grid);
        mBackView = (ImageTextView) findViewById(R.id.id_back);
        mBackView.setInterface(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mDirPaths == null) {
            mDirPaths = new HashSet<String>();
        }
        startAsyncQuery();
        mIsInActivity = true;
    }


    private void startAsyncQuery() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, R.string.no_mounted, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mNeedQuery = false;
            LocalImages.startQuery(mQueryHandler, ContantsUtils.LIST_QUERY_TOKEN);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        startAsyncQuery();

        setIntent(intent);
        processExtraData();
    }

    private void processExtraData() {
        Intent intent = getIntent();
    }

    private final class ThreadListQueryHandler extends BaseProgressQueryHandler {
        public ThreadListQueryHandler(ContentResolver contentResolver) {
            super(contentResolver);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, final Cursor cursor) {
            Log.d(TAG, "onQueryComplete mNeedQuery = " + mNeedQuery + " mIsInActivity = " + mIsInActivity);
            if (cursor == null) {
                setProgressBarIndeterminateVisibility(false);
                if (mNeedQuery && mIsInActivity) {
                    Log.d(TAG, "onQueryComplete cursor == null startAsyncQuery");
                    startAsyncQuery();
                }
                return;
            }
            switch (token) {
                case ContantsUtils.LIST_QUERY_TOKEN:
                    Log.d(TAG, "onQueryComplete cursor count is " + cursor.getCount());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            String firstImage = null;

                            while (cursor.moveToNext()) {
                                // 获取图片的路径
                                String path = cursor.getString(LocalImages.DATA_INDEX);

                                //Log.e(TAG, "localimage's path = " + path);
                                // 拿到第一张图片的路径
                                if (firstImage == null) {
                                    firstImage = path;
                                }
                                // 获取该图片的父路径名
                                File parentFile = new File(path).getParentFile();
                                if (parentFile == null) {
                                    continue;
                                }
                                String dirPath = parentFile.getAbsolutePath();
                                ImageFloder imageFloder = null;
                                // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                                if (mDirPaths.contains(dirPath)) {
                                    continue;
                                } else {
                                    mDirPaths.add(dirPath);
                                    // 初始化imageFloder
                                    imageFloder = new ImageFloder();
                                    imageFloder.setDir(dirPath);
                                    imageFloder.setFirstImagePath(path);
                                }

                                if (parentFile == null) {
                                    continue;
                                }

                                int picSize = parentFile.list(new FilenameFilter() {
                                    @Override
                                    public boolean accept(File dir, String filename) {
                                        if (filename.endsWith(".jpg")
                                                || filename.endsWith(".png")
                                                || filename.endsWith(".jpeg"))
                                            return true;
                                        return false;
                                    }
                                }).length;
                                mTotalCount += picSize;

                                imageFloder.setCount(picSize);
                                mImageFloders.add(imageFloder);

                                if (picSize > mPicsSize) {
                                    mPicsSize = picSize;
                                    mImgDir = parentFile;
                                }
                            }
                            //mCursor.close();

                            // 扫描完成，辅助的HashSet也就可以释放内存了
                            mDirPaths = null;

                            // 通知Handler扫描图片完成
                            mHandler.sendEmptyMessage(0x110);

                        }
                    }).start();
                    break;

                default:
                    Log.e(TAG, "onQueryComplete called with unknown token " + token);
            }

            if (mNeedQuery && mIsInActivity) {
                startAsyncQuery();
            }
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {

            switch (token) {
                case ContantsUtils.LIST_DELETE_TOKEN:

                    dismissProgressDialog();
                    break;
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsInActivity = false;
        if (mQueryHandler != null) {
            Log.d(TAG, "cancel undone queries in onStop");
            mQueryHandler.cancelOperation(ContantsUtils.LIST_QUERY_TOKEN);
            mQueryHandler.cancelOperation(ContantsUtils.LIST_DELETE_TOKEN);
            mNeedQuery = false;
        }
    }

    @Override
    protected void onDestroy() {
        if (mQueryHandler != null) {
            mQueryHandler.removeCallbacksAndMessages(null);
            mQueryHandler.cancelOperation(ContantsUtils.LIST_QUERY_TOKEN);
            mQueryHandler.cancelOperation(ContantsUtils.LIST_DELETE_TOKEN);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        // TODO Auto-generated method stub
        /*if (mOptionDialog != null && mOptionDialog.isShowing()) {
            mOptionDialog.dismiss();
            mOptionDialog = null;
        } else {
            showOptionDialog();
        }*/

        mListImageDirPopupWindow
                .setAnimationStyle(R.style.anim_popup_dir);
        mListImageDirPopupWindow.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
        //mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = .3f;
        getWindow().setAttributes(lp);

        return super.onMenuOpened(featureId, menu);
    }

    public void doSelect(View view) {
        if (!mAdapter.getSelect()) {
            mAdapter.setsCanSelect(true);
        } else {
            mAdapter.setsCanSelect(false);
        }
        //mAdapter.notifyDataSetChanged();
    }
}
