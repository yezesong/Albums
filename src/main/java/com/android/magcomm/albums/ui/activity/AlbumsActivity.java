package com.android.magcomm.albums.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.android.magcomm.albums.R;
import com.android.magcomm.albums.adapter.StaggeredRecyclerViewAdapter;
import com.android.magcomm.albums.model.AlbumsModel;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by lenovo on 15-10-21.
 */
public class AlbumsActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate,
        BGAOnRVItemClickListener {

    private static final String DCIM =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();

    private static final String DIRECTORY = DCIM + "/Camera";

    private RecyclerView mDataRv;
    private BGARefreshLayout mRefreshLayout;
    private StaggeredRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void searchAllImages(String dire) {
        //Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().
        //       appendPath(Long.toString(id)).build();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_albums;
    }

    @Override
    protected void setListener() {
        mRefreshLayout = getViewById(R.id.recyclerview_refresh);
        mDataRv = getViewById(R.id.recyclerview_data);
        Log.i(TAG, "setListener is called and mRefreshLayout = " + mRefreshLayout);
        mRefreshLayout.setDelegate(this);

        mAdapter = new StaggeredRecyclerViewAdapter(mDataRv);
        mAdapter.setOnRVItemClickListener(this);
        //mAdapter.setOnRVItemLongClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(mApp, true));
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mDataRv.setLayoutManager(layoutManager);
        mDataRv.setAdapter(mAdapter);
    }

    private void showLocalPhotos() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        showLocalPhotos();
    }

    @Override
    protected void toShowImage() {
        /*final List<AlbumsModel> list = new ArrayList<>();

        AlbumsModel model1 = new AlbumsModel();
        model1.setUri("http://7xnkdb.com1.z0.glb.clouddn.com/001.jpg");
        model1.setDesc("20151022");

        AlbumsModel model2 = new AlbumsModel();
        model2.setUri("http://7xnkdb.com1.z0.glb.clouddn.com/002.jpg");
        model2.setDesc("20151022");

        AlbumsModel model3 = new AlbumsModel();
        model3.setUri("http://7xnkdb.com1.z0.glb.clouddn.com/003.jpg");
        model3.setDesc("20151022");

        AlbumsModel model4 = new AlbumsModel();
        model4.setUri("http://7xnkdb.com1.z0.glb.clouddn.com/004.jpg");
        model4.setDesc("20151022");

        AlbumsModel model5 = new AlbumsModel();
        model5.setUri("http://7xnkdb.com1.z0.glb.clouddn.com/005.jpg");
        model5.setDesc("20151022");

        list.add(model1);
        list.add(model2);
        list.add(model3);
        list.add(model4);
        list.add(model5);*/

        mEngine.loadDefaultStaggeredData().enqueue(new Callback<List<AlbumsModel>>() {


            @Override
            public void onResponse(Response<List<AlbumsModel>> response, Retrofit retrofit) {
                //mAdapter.setDatas(list);
                mAdapter.setDatas(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(TAG, "onFailure....");
            }
        });
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        Log.i(TAG, " 您刚点击的是: " + mAdapter.getItem(position));
    }
}
