package com.android.magcomm.albums;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.magcomm.albums.data.LocalImages;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by lenovo on 15-10-24.
 */
public class AlbumsLayout extends FrameLayout {
    private SimpleDraweeView mDraweeView;
    private TextView mDescText;

    public AlbumsLayout(Context context) {
        super(context, null);
    }

    public AlbumsLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public AlbumsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDraweeView = (SimpleDraweeView) findViewById(R.id.iv_item_staggered_icon);
        mDescText = (TextView) findViewById(R.id.tv_item_staggered_desc);
    }

    public void bindView(Cursor cursor) {
        int imageId = cursor.getInt(LocalImages.ID_INDEX);
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().
                appendPath(Long.toString(imageId)).build();
        String descText = cursor.getString(LocalImages.NAME_INDEX);
        mDraweeView.setImageURI(imageUri);
        mDescText.setText(descText);
    }
}
