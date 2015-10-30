package com.android.magcomm.albums;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;

public abstract class BasePopupWindowForListView extends PopupWindow {
    /**
     * 布局文件的最外层View
     */
    protected View mContentView;
    protected Context context;

    public BasePopupWindowForListView(View contentView, int width, int height,
                                      boolean focusable) {
        super(contentView, width, height, focusable);
        this.mContentView = contentView;
        context = contentView.getContext();

        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
        setOutsideTouchable(true);
        setTouchInterceptor(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        initViews();
    }

    public abstract void initViews();

    public View findViewById(int id) {
        return mContentView.findViewById(id);
    }

    protected static int dpToPx(Context context, int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }


    public void setmOptionButtonClick(OptionButtonClick optionButtonClick) {
        this.mOptionButtonClick = optionButtonClick;
    }

    protected OptionButtonClick mOptionButtonClick;

    public interface OptionButtonClick {
        public void optionClick(View view);
    }
}
