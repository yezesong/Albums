package com.android.magcomm.albums.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.android.magcomm.albums.R;

/**
 * Created by lenovo on 15-10-27.
 */
public class ImageTextView extends View implements View.OnTouchListener {
    private static final String TAG = ImageTextView.class.getSimpleName();

    private boolean mContentVisible;
    private boolean mIconVisible;
    private int mContentColor;
    private int mContentSize;
    private String mContent;
    private Bitmap mIcon;
    private int mDistance;//图标和文字间的距离
    private int mDire;
    private ImageTextViewClick mImageTextViewClick;
    /**
     * 绘制时控制文本绘制的范围
     */
    private Rect mBound;
    private Paint mPaint;

    private int mViewId;

    int mWidth = 0, mHeight = 0;


    public ImageTextView(Context context) {
        super(context, null);
    }

    public ImageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setTag(this);
        mViewId = getId();
        Log.i(TAG, "ImageTextView is called2 ,,,,");
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ImageTextView);
        //mContentColor = ta.getColorStateList(R.styleable.ImageTextView_content_color);
        mContentVisible = ta.getBoolean(R.styleable.ImageTextView_content_visible, true);

        mIconVisible = ta.getBoolean(R.styleable.ImageTextView_icon_visible, true);
        mContentColor = ta.getColor(R.styleable.ImageTextView_content_color, Color.WHITE);

        mContentSize = ta.getDimensionPixelSize(R.styleable.ImageTextView_content_size, getDefaultTextSize(context));
        mContent = ta.getString(R.styleable.ImageTextView_content);
        mIcon = drawableToBitamp(ta.getDrawable(R.styleable.ImageTextView_view_icon));
        mDistance = ta.getDimensionPixelOffset(R.styleable.ImageTextView_view_dis, getDefaultDistance(context));
        mDire = ta.getInt(R.styleable.ImageTextView_dire, 1);

        Log.i(TAG, "mDistance = " + mDistance);
        /**
         * 获得绘制文本的宽和高
         */
        mPaint = new Paint();
        mPaint.setAlpha(120);
        mPaint.setAntiAlias(true);
        if (mContentVisible) {
            mPaint.setTextSize(mContentSize);
            mPaint.setColor(mContentColor);
            mBound = new Rect();
            mPaint.getTextBounds(mContent, 0, mContent.length(), mBound);
            Log.i(TAG, "mContent = " + mContent + " and bound's size = " + mBound.height());
        }

        setEnabled(false);
        setOnTouchListener(this);
    }

    public ImageTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i(TAG, "ImageTextView is called3 ,,,,");
    }

    private int getDefaultTextSize(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.def_content_view_textsize);
    }

    private int getDefaultDistance(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.def_content_distance);
    }

    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidate();
    }

    public void setViewEnable(boolean enable) {
        setEnabled(enable);
    }


    float x2 = 0, y2 = 0;

    private Bitmap drawableToBitamp(Drawable drawable) {
        if (drawable == null) {
            return ((BitmapDrawable) getResources().getDrawable(R.drawable.image_text_icon)).getBitmap();
        }
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMeasure = MeasureSpec.getSize(widthMeasureSpec);
        int heightMeasure = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heighMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthMeasure;
        } else {
            switch (mDire) {
                case 1:
                case 3:
                    if (mContentVisible) {
                        mWidth = mIcon.getWidth() + mDistance + getPaddingRight() + getPaddingLeft() + mBound.width();
                    } else {
                        mWidth = mIcon.getWidth() + getPaddingRight() + getPaddingLeft();
                    }
                    break;
                case 2:
                case 4:
                    if (mContentVisible) {
                        mWidth = Math.max((mIcon.getWidth() + getPaddingLeft() + getPaddingRight()),
                                (mBound.width() + getPaddingLeft() + getPaddingRight()));
                    } else {
                        mWidth = mIcon.getWidth() + getPaddingLeft() + getPaddingRight();
                    }
                    break;
            }
        }

        if (heighMode == MeasureSpec.EXACTLY) {
            mHeight = heightMeasure;
        } else {
            switch (mDire) {
                case 1:
                case 3:
                    if (mContentVisible) {
                        mHeight = Math.max((mIcon.getHeight() + getPaddingTop() + getPaddingBottom()),
                                (mBound.height() + getPaddingTop() + getPaddingBottom()));
                    } else {
                        mHeight = mIcon.getHeight() + getPaddingTop() + getPaddingBottom();
                    }
                    break;
                case 2:
                case 4:
                    if (mContentVisible) {
                        mHeight = getPaddingTop() + mIcon.getHeight() + mDistance + mBound.height() + getPaddingBottom();
                    } else {
                        mHeight = getPaddingTop() + mIcon.getHeight() + getPaddingBottom();
                    }
                    break;
            }
        }
        Log.i(TAG, "onMeasure is called and widthMeasure = " + widthMeasure + " and heightMeasure = " + heightMeasure);

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        switch (mDire) {
            case 1://LEFT
                if (!mIconVisible) {
                    canvas.drawText(mContent, 0, (mHeight + mBound.height()) / 2 - 4, mPaint);
                } else {
                    canvas.drawBitmap(mIcon, 5, (mHeight - mIcon.getHeight()) / 2, mPaint);
                    canvas.drawText(mContent, mIcon.getWidth() - 3, (mHeight + mBound.height()) / 2 - 4, mPaint);
                }
                break;
            case 2://TOP
                if (!mIconVisible) {
                    canvas.drawText(mContent, (mWidth - mBound.width()) / 2, (mHeight + mBound.height()) / 2 - 4, mPaint);
                } else {
                    if (!mContentVisible) {
                        canvas.drawBitmap(mIcon, (mWidth - mIcon.getWidth()) / 2, (mHeight - mIcon.getHeight()) / 2, mPaint);
                    } else {
                        canvas.drawBitmap(mIcon, (mWidth - mIcon.getWidth()) / 2, 0, mPaint);
                        canvas.drawText(mContent, (mWidth - mBound.width()) / 2, mIcon.getHeight() + mDistance, mPaint);
                    }
                }
                break;
            case 3://RIGHT
                if (!mIconVisible) {
                    canvas.drawText(mContent, mWidth - mBound.width() - 25, (mHeight + mBound.height()) / 2 - 4, mPaint);
                } else {
                    canvas.drawText(mContent, 0, (mHeight + mBound.height()) / 2 - 4, mPaint);
                    canvas.drawBitmap(mIcon, mBound.width(), (mHeight - mIcon.getHeight()) / 2, mPaint);
                }
                break;
            case 4://BOTTOM
                break;
        }
    }

    public void setInterface(ImageTextViewClick imageTextViewClick) {
        mImageTextViewClick = imageTextViewClick;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPaint.setAlpha(120);
                break;
            case MotionEvent.ACTION_MOVE:
                x2 = event.getX();
                y2 = event.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if ((5 <= x2 && mWidth >= x2) && (0 <= y2 && mHeight >= y2)) {
                    if (mImageTextViewClick != null) {
                        mImageTextViewClick.doClick(mViewId);
                    }
                }
                mPaint.setAlpha(240);
                //invalidate();
                break;
            //return true;
        }

        invalidate();
        return true;//super.onTouchEvent(event);
    }

    public interface ImageTextViewClick {
        void doClick(int id);
    }
}
