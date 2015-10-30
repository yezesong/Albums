package com.android.magcomm.albums.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.magcomm.albums.R;

public class ImageTextButton extends ViewGroup {

    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private static final int TOP = 3;
    private static final int BOTTOM = 4;

    private static final int DEFAULT_PADDING = 10;

    String mTextStr;
    Drawable mIconDraw;

    ImageView mIconView;
    TextView mTextView;

    int mIconViewWidth, mIconViewHeight;
    int mTextViewWidth, mTextViewHeight;
    int mPosition;
    int mPadding;

    float mTextSize;
    ColorStateList mTextColor;

    public ImageTextButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageTextButton);
        mTextStr = a.getString(R.styleable.ImageTextButton_image_button_text);
        mIconDraw = a.getDrawable(R.styleable.ImageTextButton_image_button_icon);
        mPosition = a.getInt(R.styleable.ImageTextButton_image_button_positon, TOP);
        mPadding = a.getInt(R.styleable.ImageTextButton_image_button_padding, 3);
        mTextSize = a.getDimension(R.styleable.ImageTextButton_image_button_textSize, -1);
        mTextColor = a.getColorStateList(R.styleable.ImageTextButton_image_button_textColor);
        a.recycle();
        init(context);
    }


    private void init(Context context) {
        mIconView = new ImageView(context);
        mIconView.setImageDrawable(mIconDraw);
        addView(mIconView);
        mTextView = new TextView(context);
        mTextView.setText(mTextStr);

        if (mTextSize != -1) {
            mTextView.setTextSize(mTextSize);
        }

        if (mTextColor != null) {
            mTextView.setTextColor(mTextColor);
        }
        addView(mTextView);
        this.setClickable(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = widthMeasureSpec;
        int height = heightMeasureSpec;

        final int measureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        mIconView.measure(measureSpec, measureSpec);
        mIconViewWidth = mIconView.getMeasuredWidth();
        mIconViewHeight = mIconView.getMeasuredHeight();

        mTextView.measure(measureSpec, measureSpec);
        mTextViewWidth = mTextView.getMeasuredWidth();
        mTextViewHeight = mTextView.getMeasuredHeight();

        int modeH = MeasureSpec.getMode(heightMeasureSpec);
        int modeW = MeasureSpec.getMode(widthMeasureSpec);
        switch (mPosition) {
            case LEFT:
            case RIGHT:
                if (modeW == MeasureSpec.AT_MOST) {
                    width = getPaddingLeft() + getPaddingRight() + mIconViewWidth + mIconViewHeight + mPadding + DEFAULT_PADDING * 2;
                }
                if (modeH == MeasureSpec.AT_MOST) {
                    int H = Math.max(mTextViewHeight, mIconViewHeight);
                    height = getPaddingTop() + getPaddingBottom() + H + DEFAULT_PADDING * 2;
                }
                break;
            case TOP:
            case BOTTOM:
                if (modeW == MeasureSpec.AT_MOST) {
                    int W = Math.max(mTextViewWidth, mIconViewWidth);
                    width = getPaddingLeft() + getPaddingRight() + W + DEFAULT_PADDING * 2;
                }
                if (modeH == MeasureSpec.AT_MOST) {
                    height = getPaddingTop() + getPaddingBottom() + mIconViewHeight + mTextViewHeight + mPadding + DEFAULT_PADDING * 2;
                }
                break;

        }
        setMeasuredDimension(width, height);

    }

    public void setText(int resId) {
        mTextView.setText(resId);
    }

    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    public void setTextSize(float size) {
        mTextView.setTextSize(size);
    }

    public void setTextColor(ColorStateList color) {
        mTextView.setTextColor(color);
    }

    public void setTextColor(int color) {
        mTextView.setTextColor(color);
    }

    public void setIcon(int resId) {
        mIconView.setImageResource(resId);
    }

    public void setIcon(Drawable drawable) {
        mIconView.setImageDrawable(drawable);
    }

    public void setIcon(Bitmap bitmap) {
        mIconView.setImageBitmap(bitmap);
    }

    public CharSequence getText() {
        return mTextView.getText();
    }

    public Drawable getIcon() {
        return mIconView.getDrawable();
    }

    public ImageView getIconView() {
        return mIconView;
    }

    public TextView getTextView() {
        return mTextView;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int height = b - t;
        final int width = r - l;
        int top, left;
        int W, H;

        switch (mPosition) {
            case LEFT:
                W = mIconViewWidth + mTextViewWidth + mPadding;
                top = (height - mIconViewHeight) / 2;
                left = (width - W) / 2;
                mIconView.layout(left, top, left + mIconViewWidth, top + mIconViewHeight);
                top = (height - mTextViewHeight) / 2;
                left = left + mIconViewWidth + mPadding;
                mTextView.layout(left, top, left + mTextViewWidth, top + mTextViewHeight);
                break;
            case RIGHT:
                W = mIconViewWidth + mTextViewWidth + mPadding;
                top = (height - mTextViewHeight) / 2;
                left = (width - W) / 2;
                mTextView.layout(left, top, left + mTextViewWidth, top + mTextViewHeight);

                top = (height - mIconViewHeight) / 2;
                left = left + mTextViewWidth + mPadding;
                mIconView.layout(left, top, left + mIconViewWidth, top + mIconViewHeight);

                break;
            case TOP:
                H = mIconViewHeight + mTextViewHeight + mPadding;
                top = (height - H) / 2;
                left = (width - mIconViewWidth) / 2;
                mIconView.layout(left, top, left + mIconViewWidth, top + mIconViewHeight);

                top = top + mIconViewHeight + mPadding;
                left = (width - mTextViewWidth) / 2;
                mTextView.layout(left, top, left + mTextViewWidth, top + mTextViewHeight);

                break;
            case BOTTOM:
                H = mIconViewHeight + mTextViewHeight + mPadding;
                top = (height - H) / 2;
                left = (width - mTextViewWidth) / 2;
                mTextView.layout(left, top, left + mTextViewWidth, top + mTextViewHeight);

                top = top + mTextViewHeight + mPadding;
                left = (width - mIconViewWidth) / 2;
                mIconView.layout(left, top, left + mIconViewWidth, top + mIconViewHeight);
                break;

        }

    }

}
