package com.android.magcomm.albums.ui.activity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.magcomm.albums.AlbumsApplication;
import com.android.magcomm.albums.engine.Engine;

import java.util.zip.Inflater;

/**
 * Created by lenovo on 15-10-21.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected String TAG;
    protected AlbumsApplication mApp;
    protected Engine mEngine;
    protected ProgressDialog mLoadingDialog;

    abstract int getLayoutId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());

        TAG = getClass().getSimpleName();
        mApp = AlbumsApplication.getInstance();
        mEngine = mApp.getEngine();

        mLoadingDialog = new ProgressDialog(this);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setMessage("数据加载中...");

        setListener();
        processLogic(savedInstanceState);
        //toShowImage();
    }

    /**
     * 给View控件添加事件监听器
     */
    protected abstract void setListener();

    /**
     * 处理业务逻辑，状态恢复等操作
     *
     * @param savedInstanceState
     */
    protected abstract void processLogic(Bundle savedInstanceState);

    /*
     * 当界面显示完整到时候开始加载照片显示
     */
    protected abstract void toShowImage();

    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(@IdRes int id) {
        return (VT) findViewById(id);
    }
}
