package com.android.magcomm.albums.ui.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.magcomm.albums.BasePopupWindowForListView;
import com.android.magcomm.albums.BaseProgressQueryHandler;
import com.android.magcomm.albums.R;
import com.android.magcomm.albums.adapter.LocalAdapter;
import com.android.magcomm.albums.bean.ImageFloder;
import com.android.magcomm.albums.data.LocalImages;
import com.android.magcomm.albums.ui.OptionPopuWindowLeft;
import com.android.magcomm.albums.ui.OptionPopuWindowRight;
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

/**
 * Created by lenovo on 15-10-27.
 */
public class LocalFragment extends Fragment implements ImageTextView.ImageTextViewClick, BasePopupWindowForListView.OptionButtonClick,
        LocalAdapter.IEnableView, View.OnClickListener {
    public static final String TAG = LocalFragment.class.getSimpleName();
    private Activity sActivity;
    private View mRootView;
    private boolean mNeedQuery = false;
    private boolean mIsInActivity = false;
    private GridView mGridView;
    private ImageTextView mBackView;
    private ImageTextView mShare, mCamera, mDelete;

    private TextView mSelectView;
    private TextView mPhotoNum;
    private OptionPopuWindowRight mOptionPopuWindowRight;
    private OptionPopuWindowLeft mOptionPopuWindowLeft;

    private RadioGroup mContainer1;
    private LinearLayout mContainer2;

    private LocalAdapter mAdapter;
    private int mScreenWidth, mScreenHeight;
    /**
     * 所有的图片
     */
    private List<String> mImgs;

    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;
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
    //定义回调函数及变量
    protected BackHandlerInterface backHandlerInterface;

    @Override
    public void doClick(int id) {
        switch (id) {
            case R.id.id_back:
                break;
            case R.id.btn_share:
                break;
            case R.id.btn_camera:
                Intent intent = new Intent(); //调用照相机
                intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
                startActivity(intent);
                break;
            case R.id.btn_delete:
                onDeleteMenu();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (!mAdapter.getSelect()) {
            setBeSelect();
        } else {
            setNoSelect();
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setViewEnable(boolean enable) {
        mDelete.setViewEnable(enable);
        mShare.setViewEnable(enable);
        if (enable) {
            mDelete.setAlpha(240);
            mShare.setAlpha(240);
        } else {
            mDelete.setAlpha(120);
            mShare.setAlpha(120);
        }
    }

    public interface BackHandlerInterface {
        public void setSelectedFragment(LocalFragment backHandledFragment);
    }

    private void setBeSelect() {
        mAdapter.setsCanSelect(true);
        mBackView.setVisibility(View.INVISIBLE);
        mSelectView.setText(R.string.cancel);
        mContainer1.setVisibility(View.GONE);
        mContainer2.setVisibility(View.VISIBLE);
    }

    private void setNoSelect() {
        mAdapter.setsCanSelect(false);
        mAdapter.mSelectedImage.clear();
        setViewEnable(false);
        mBackView.setVisibility(View.VISIBLE);
        mContainer2.setVisibility(View.GONE);
        mContainer1.setVisibility(View.VISIBLE);
        mSelectView.setText(R.string.select);
    }

    @Override
    public void optionClick(View view) {

    }


    static class LocalHandler extends Handler {
        WeakReference<LocalFragment> localragment;

        LocalHandler(LocalFragment fragment) {
            localragment = new WeakReference<LocalFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            LocalFragment theFragment = localragment.get();
            theFragment.setDataToView();
            theFragment.initListRightPopupWindow();
            theFragment.initListLeftPopupWindow();
        }
    }

    /**
     * 为View绑定数据
     */
    private void setDataToView() {
        if (mImgDir == null) {
            Toast.makeText(sActivity.getApplicationContext(), R.string.no_photos,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mImgs = Arrays.asList(mImgDir.list());
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new LocalAdapter(sActivity.getApplicationContext(), mImgs,
                R.layout.item_staggered, mImgDir.getAbsolutePath());
        mAdapter.setmIEnableView(this);

        mGridView.setAdapter(mAdapter);
        mPhotoNum.setText(mTotalCount + "张");
        //mImageCount.setText(totalCount + "张");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof BackHandlerInterface)) {
            throw new ClassCastException("Hosting activity must implement BackHandlerInterface");
        } else {
            backHandlerInterface = (BackHandlerInterface) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sActivity = getActivity();
        mRootView = getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_local);
        //inflater.inflate(R.layout.fragment_local, container, false);
        mScreenWidth = DensityUtil.getScreenWidth(sActivity);
        mScreenHeight = DensityUtil.getScreenHeight(sActivity);
        Log.i(TAG, "onCreateView is called and width = " + mScreenWidth + " : and ScreenHeight = " + mScreenHeight);
        initView();
        mQueryHandler = new ThreadListQueryHandler(sActivity.getContentResolver());

        return mRootView;
    }

    private View getPersistentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, int layout) {
        if (mRootView == null) {
            // Inflate the layout for this fragment
            mRootView = inflater.inflate(layout, container, false);
        } else {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }
        return mRootView;
    }

    private void initListLeftPopupWindow() {
        View contentView = LayoutInflater.from(sActivity.getApplicationContext())
                .inflate(R.layout.option_popuwindow_left, null);

        mOptionPopuWindowLeft = new OptionPopuWindowLeft(contentView, (int) (mScreenWidth), (int) (mScreenHeight * 0.25));

        mOptionPopuWindowLeft.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = sActivity.getWindow().getAttributes();
                lp.alpha = 1.0f;
                sActivity.getWindow().setAttributes(lp);
            }
        });

        mOptionPopuWindowLeft.setmOptionButtonClick(this);
    }

    private void initListRightPopupWindow() {
        View contentView = LayoutInflater.from(sActivity.getApplicationContext())
                .inflate(R.layout.option_popuwindow_right, null);

        mOptionPopuWindowRight = new OptionPopuWindowRight(contentView, (int) (mScreenWidth), (int) (mScreenHeight * 0.25));

        mOptionPopuWindowRight.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = sActivity.getWindow().getAttributes();
                lp.alpha = 1.0f;
                sActivity.getWindow().setAttributes(lp);
            }
        });

        mOptionPopuWindowRight.setmOptionButtonClick(this);
    }

    /**
     * 初始化View
     */
    private void initView() {
        mGridView = (GridView) mRootView.findViewById(R.id.local_grid);
        mSelectView = (TextView) mRootView.findViewById(R.id.id_select);
        mPhotoNum = (TextView) mRootView.findViewById(R.id.photo_num);

        mBackView = (ImageTextView) mRootView.findViewById(R.id.id_back);

        mShare = (ImageTextView) mRootView.findViewById(R.id.btn_share);
        mCamera = (ImageTextView) mRootView.findViewById(R.id.btn_camera);
        mDelete = (ImageTextView) mRootView.findViewById(R.id.btn_delete);

        mContainer1 = (RadioGroup) mRootView.findViewById(R.id.bottom_container1);
        mContainer2 = (LinearLayout) mRootView.findViewById(R.id.bottom_container2);

        mBackView.setInterface(this);
        mShare.setInterface(this);
        mCamera.setInterface(this);
        mCamera.setAlpha(240);
        //mCamera.setClickable(true);
        mBackView.setViewEnable(true);
        mCamera.setViewEnable(true);

        mDelete.setInterface(this);
        mSelectView.setOnClickListener(this);
        /*mBackView.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mCamera.setOnClickListener(this);
        mDelete.setOnClickListener(this);*/
    }

    @Override
    public void onStart() {
        super.onStart();
        //将自己的实例传出去
        backHandlerInterface.setSelectedFragment(this);
        Log.i(TAG, "onStart is called ...");
        if (mDirPaths == null) {
            mDirPaths = new HashSet<String>();
        }
        startAsyncQuery();
        mIsInActivity = true;
    }

    private void startAsyncQuery() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(sActivity, R.string.no_mounted, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mNeedQuery = false;
            LocalImages.startQuery(mQueryHandler, ContantsUtils.LIST_QUERY_TOKEN);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    private final class ThreadListQueryHandler extends BaseProgressQueryHandler {
        public ThreadListQueryHandler(ContentResolver contentResolver) {
            super(contentResolver);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, final Cursor cursor) {
            Log.d(TAG, "onQueryComplete mNeedQuery = " + mNeedQuery + " mIsInActivity = " + mIsInActivity);
            if (cursor == null) {
                sActivity.setProgressBarIndeterminateVisibility(false);
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
                            //此处很有争议，cursor是否需要关闭
                            cursor.close();

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
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause is called ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop is called ");
        setNoSelect();
        mIsInActivity = false;
        if (mQueryHandler != null) {
            Log.d(TAG, "cancel undone queries in onStop");
            mQueryHandler.cancelOperation(ContantsUtils.LIST_QUERY_TOKEN);
            mQueryHandler.cancelOperation(ContantsUtils.LIST_DELETE_TOKEN);
            mNeedQuery = false;
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy is called ");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        if (mQueryHandler != null) {
            mQueryHandler.removeCallbacksAndMessages(null);
            mQueryHandler.cancelOperation(ContantsUtils.LIST_QUERY_TOKEN);
            mQueryHandler.cancelOperation(ContantsUtils.LIST_DELETE_TOKEN);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroyView();
        Log.i(TAG, "onDestroyView is called ");
    }

    private void onDeleteMenu() {
        mOptionPopuWindowRight
                .setAnimationStyle(R.style.anim_popup_option);
        mOptionPopuWindowRight.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = sActivity.getWindow().getAttributes();
        lp.alpha = 0.6f;
        sActivity.getWindow().setAttributes(lp);
    }

    public boolean onBackPressed() {
        if (mAdapter.getSelect()) {
            Log.i(TAG, "捕获到了back事件哦");
            setNoSelect();
            mAdapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }
}
