package com.android.magcomm.albums.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by lenovo on 15-10-24.
 */
public class ContantsUtils {
    public static final int LIST_QUERY_TOKEN = 1701;
    public static final int LIST_DELETE_TOKEN = 1702;

    public static class NewProgressDialog extends ProgressDialog {
        private boolean mIsDismiss = false;

        public NewProgressDialog(Context context) {
            super(context);
        }

        public void dismiss() {
            if (isDismiss()) {
                super.dismiss();
            }
        }

        public synchronized void setDismiss(boolean isDismiss) {
            this.mIsDismiss = isDismiss;
        }

        public synchronized boolean isDismiss() {
            return mIsDismiss;
        }
    }
}
