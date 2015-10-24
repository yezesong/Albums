package com.android.magcomm.albums.ui.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.magcomm.albums.AlbumsApplication;
import com.android.magcomm.albums.R;
import com.android.magcomm.albums.adapter.NormalAlbumAdapter;
import com.android.magcomm.albums.data.LocalImages;
import com.android.magcomm.albums.ui.BaseProgressQueryHandler;
import com.android.magcomm.albums.util.ContantsUtils;

import cn.bingoogolapple.refreshlayout.BGAMoocStyleRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

/**
 * Created by lenovo on 15-10-23.
 */
public class LocalPhotoFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate, AdapterView.OnItemClickListener {
    public static final String TAG = LocalPhotoFragment.class.getSimpleName();
    private BGARefreshLayout mRefreshLayout;
    private AlbumsApplication mApp;
    private GridView mDataGv;
    private boolean mNeedQuery = false;
    private boolean mIsInActivity = false;
    private ThreadListQueryHandler mQueryHandler;
    private Handler mHandler;
    private NormalAlbumAdapter mAdapter;

    private final NormalAlbumAdapter.OnContentChangedListener mContentChangedListener = new NormalAlbumAdapter.OnContentChangedListener() {
        @Override
        public void onContentChanged(NormalAlbumAdapter adapter) {
            if (mIsInActivity) {
                mNeedQuery = true;
                startAsyncQuery();
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mApp = AlbumsApplication.getInstance();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_local);
        mAdapter = new NormalAlbumAdapter(getActivity(), null, true);
        mRefreshLayout = getViewById(R.id.fragment_gridview_refresh);
        mDataGv = getViewById(R.id.fragment_gridview_data);
        mHandler = new Handler();
        mQueryHandler = new ThreadListQueryHandler(getActivity().getContentResolver());
    }

    @Override
    protected void setListener() {
        //mRefreshLayout.setDelegate(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        //mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(this, true));
        //mRefreshLayout.setRefreshViewHolder(new BGAMoocStyleRefreshViewHolder(mApp, true));
        //mRefreshLayout.setRefreshViewHolder(new BGAStickinessRefreshViewHolder(this, true));
        mDataGv.setAdapter(mAdapter);
    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume is called ...");
        if (mAdapter != null) {
            //mAdapter.uncheckAll();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mIsInActivity = false;
        if (mQueryHandler != null) {
            Log.d(TAG, "cancel undone queries in onStop");
            mQueryHandler.cancelOperation(ContantsUtils.THREAD_LIST_QUERY_TOKEN);
            mNeedQuery = false;
        }

        if (mAdapter != null) {
            Log.d(TAG, "remove OnContentChangedListener");
            mAdapter.setOnContentChangedListener(null);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mDataGv != null) {
            Log.d(TAG, "set onContentChanged listener");
            mAdapter.setOnContentChangedListener(mContentChangedListener);
            //mDataGv.setCheckVisible(false);
        }
        mIsInActivity = true;
        startAsyncQuery();
    }

    private void startAsyncQuery() {
        try {
            mNeedQuery = false;
            LocalImages.startQueryForAll(mQueryHandler, ContantsUtils.THREAD_LIST_QUERY_TOKEN);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        if (mQueryHandler != null) {
            mQueryHandler.removeCallbacksAndMessages(null);
            mQueryHandler.cancelOperation(ContantsUtils.THREAD_LIST_QUERY_TOKEN);
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

    private final class ThreadListQueryHandler extends BaseProgressQueryHandler {
        public ThreadListQueryHandler(ContentResolver contentResolver) {
            super(contentResolver);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            Log.d(TAG, "onQueryComplete mNeedQuery = " + mNeedQuery + " mIsInActivity = " + mIsInActivity);
            if (cursor == null) {
                if (mNeedQuery && mIsInActivity) {
                    Log.d(TAG, "onQueryComplete cursor == null startAsyncQuery");
                    startAsyncQuery();
                }
                return;
            }
            switch (token) {
                case ContantsUtils.THREAD_LIST_QUERY_TOKEN:
                    if (mAdapter.getOnContentChangedListener() == null) {
                        cursor.close();
                        return;
                    }
                    Log.d(TAG, "onQueryComplete cursor count is " + cursor.getCount());

                    mAdapter.changeCursor(cursor);
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    }, 60000);
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
                case ContantsUtils.DELETE_CONVERSATION_TOKEN:
                    //dismissProgressDialog();
                    break;
            }
        }
    }
}
