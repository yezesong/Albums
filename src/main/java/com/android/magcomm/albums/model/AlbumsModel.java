package com.android.magcomm.albums.model;

/**
 * Created by lenovo on 15-10-22.
 */
public class AlbumsModel {
    public String imageUrl;
    public String imageDesc;

    public void setUri(String uri){
        imageUrl = uri;
    }

    public void setDesc(String desc){
        imageDesc = desc;
    }
}
