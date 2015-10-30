package com.android.magcomm.albums.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.magcomm.albums.BasePopupWindowForListView;
import com.android.magcomm.albums.R;

import java.util.List;

/**
 * Created by lenovo on 15-10-28.
 */
public class OptionPopuWindowRight extends BasePopupWindowForListView {

    private Button mConfireDelete;
    private Button mCancel;

    public OptionPopuWindowRight(View contentView, int width, int height) {
        super(contentView, width, height, true);
    }

    @Override
    public void initViews() {
        mConfireDelete = (Button) findViewById(R.id.menu_delete);
        mCancel = (Button) findViewById(R.id.menu_cancel);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mConfireDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOptionButtonClick != null) {
                    mOptionButtonClick.optionClick(v);
                }
            }
        });
    }
}
