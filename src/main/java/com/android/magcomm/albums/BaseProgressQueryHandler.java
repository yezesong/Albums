package com.android.magcomm.albums;

import com.android.magcomm.albums.util.ContantsUtils;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.util.Log;


/**
 * Created by lenovo on 15-5-21.
 */
public abstract class BaseProgressQueryHandler extends AsyncQueryHandler {
    private ContantsUtils.NewProgressDialog mDialog;
    private static final String TAG = "ProgressQueryHandler";
    private int mProgress;

    public BaseProgressQueryHandler(ContentResolver resolver) {
        super(resolver);
    }

    public void setProgressDialog(ContantsUtils.NewProgressDialog dialog) {
        if (mDialog == null) {
            mDialog = dialog;
        }
    }

    public void setMax(int max) {
        if (mDialog != null) {
            mDialog.setMax(max);
        }
    }

    public void showProgressDialog() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    protected boolean progress() {
        if (mDialog != null) {
            return ++mProgress >= mDialog.getMax();
        } else {
            return false;
        }
    }

    protected void dismissProgressDialog() {
        if (mDialog == null) {
            Log.e(TAG, "mDialog is null!");
            return;
        }

        mDialog.setDismiss(true);
        try {
            mDialog.dismiss();
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "ignore IllegalArgumentException");
        }
        mDialog = null;
    }
}
