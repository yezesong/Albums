package com.android.magcomm.albums.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.android.magcomm.albums.R;
import com.android.magcomm.albums.util.ViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lenovo on 15-10-26.
 */

public class LocalAdapter extends CommonAdapter<String> {

    private boolean sCanSelect;

    public void setsCanSelect(Boolean select) {
        this.sCanSelect = select;
    }

    public boolean getSelect() {
        return sCanSelect;
    }

    /**
     * 用户选择的图片，存储为图片的完整路径
     */
    public static List<String> mSelectedImage = new LinkedList<String>();

    /**
     * 文件夹路径
     */
    private String mDirPath;

    public LocalAdapter(Context context, List<String> mDatas, int itemLayoutId,
                        String dirPath) {
        super(context, mDatas, itemLayoutId);
        this.mDirPath = dirPath;
    }

    @Override
    public void convert(final ViewHolder helper, final String item) {

        helper.setImageButtonResource(R.id.id_item_select,
                R.drawable.picture_unselected);
        //设置no_pic
        //helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
        //设置no_selected
        //设置图片
        //helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);
        //asynLoadBitmap(helper, item);//异步去加载图片
        setImageBitmap(helper, new Handler(), item);
        final SimpleDraweeView imageView = helper.getView(R.id.id_item_image);
        final ImageButton select = helper.getView(R.id.id_item_select);

        //imageView.clearColorFilter();
        Log.i("LocalFragment", "getSelect() " + getSelect());
        if (getSelect()) {
            select.setVisibility(View.VISIBLE);
            //设置ImageView的点击事件
            imageView.setOnClickListener(new View.OnClickListener() {
                //选择，则将图片变暗，反之则反之
                @Override
                public void onClick(View v) {
                    // 已经选择过该图片
                    if (mSelectedImage.contains(mDirPath + "/" + item)) {
                        mSelectedImage.remove(mDirPath + "/" + item);
                        select.setImageResource(R.drawable.picture_unselected);
                        //imageView.setColorFilter(null);
                        //imageView.clearColorFilter();
                    } else {
                        mSelectedImage.add(mDirPath + "/" + item);
                        select.setImageResource(R.drawable.pictures_selected);
                        //imageView.setColorFilter(Color.parseColor("#77000000"));
                    }

                    if (mSelectedImage.size() == 0) {
                        mIEnableView.setViewEnable(false);
                    } else {
                        mIEnableView.setViewEnable(true);
                    }
                }
            });
            /**
             * 已经选择过的图片，显示出选择过的效果
             */
            if (mSelectedImage.contains(mDirPath + "/" + item)) {
                select.setImageResource(R.drawable.pictures_selected);
                //imageView.setColorFilter(Color.parseColor("#77000000"));
            }
        } else {
            //imageView.setColorFilter(null);
            select.setVisibility(View.GONE);
            //预览照片
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("yzs000", "onClick is called and v'id = " + v.getId());
                }
            });
        }
    }

    private void setImageBitmap(final ViewHolder holder, Handler handler, final String item) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                holder.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);
            }
        });
    }

    /*private class LoadTask extends AsyncTask<Integer, Void, String> {

        private ViewHolder mHolder;
        private Handler mHandler;
        private String mItem;

        public LoadTask(ViewHolder holder, Handler handler, String item) {
            this.mHolder = holder;
            this.mHandler = handler;
            this.mItem = item;
        }

        @Override
        protected String doInBackground(Integer... params) {

            return mItem;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            setImageBitmap(mHolder, mHandler, true, aVoid);
        }
    }*/

    private IEnableView mIEnableView;

    public void setmIEnableView(IEnableView enableView) {
        this.mIEnableView = enableView;
    }

    public interface IEnableView {
        public void setViewEnable(boolean enable);
    }
}