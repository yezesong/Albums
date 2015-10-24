package com.android.magcomm.albums.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.android.magcomm.albums.R;
import com.android.magcomm.albums.model.AlbumsModel;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;

public class StaggeredRecyclerViewAdapter extends BGARecyclerViewAdapter<AlbumsModel> {
    private ImageLoader mImageLoader;

    public StaggeredRecyclerViewAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_staggered);
        mImageLoader = ImageLoader.getInstance();
    }

    @Override
    public void fillData(BGAViewHolderHelper viewHolderHelper, int position, AlbumsModel model) {
        viewHolderHelper.setText(R.id.tv_item_staggered_desc, model.imageDesc);
        mImageLoader.displayImage(model.imageUrl,  (SimpleDraweeView)viewHolderHelper.getView(R.id.iv_item_staggered_icon));
        //((SimpleDraweeView)viewHolderHelper.getView(R.id.iv_item_staggered_icon)).setImageURI(Uri.parse(model.imageUrl));
    }
}