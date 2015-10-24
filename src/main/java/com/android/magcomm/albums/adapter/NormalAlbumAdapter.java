package com.android.magcomm.albums.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.magcomm.albums.AlbumsLayout;
import com.android.magcomm.albums.R;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by lenovo on 15-10-24.
 */
public class NormalAlbumAdapter extends CursorAdapter {
    private Cursor mCursor;
    private OnContentChangedListener mOnContentChangedListener;

    public NormalAlbumAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.mCursor = c;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_staggered, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (!(view instanceof AlbumsLayout)) {
            return;
        }
        AlbumsLayout layout = (AlbumsLayout) view;
        layout.bindView(cursor);
    }

    @Override
    protected void onContentChanged() {
        if (mCursor != null && !mCursor.isClosed()) {
            if (mOnContentChangedListener != null) {
                mOnContentChangedListener.onContentChanged(this);
            }
        }
    }

    public interface OnContentChangedListener {
        void onContentChanged(NormalAlbumAdapter adapter);
    }

    public void setOnContentChangedListener(OnContentChangedListener listener) {
        mOnContentChangedListener = listener;
    }

    public OnContentChangedListener getOnContentChangedListener() {
        return mOnContentChangedListener;
    }

}
