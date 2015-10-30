package com.android.magcomm.albums.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.magcomm.albums.BasePopupWindowForListView;
import com.android.magcomm.albums.R;

/**
 * Created by lenovo on 15-10-28.
 */
public class OptionPopuWindowLeft extends BasePopupWindowForListView {

    private Button mShareOneKey;
    private Button mShareAlbums;
    private Button mCancel;

    public OptionPopuWindowLeft(View contentView, int width, int height) {
        super(contentView, width, height, true);
    }

    @Override
    public void initViews() {
        mShareAlbums = (Button) findViewById(R.id.menu_share_album);
        mShareOneKey = (Button) findViewById(R.id.menu_share_onkey);
        mCancel = (Button) findViewById(R.id.menu_cancel);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mShareAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

}
