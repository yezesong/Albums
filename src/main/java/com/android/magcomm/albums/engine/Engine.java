package com.android.magcomm.albums.engine;

import com.android.magcomm.albums.model.BannerModel;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;

public interface Engine {

    @GET("3item.json")
    Call<BannerModel> threeItem();

    @GET("4item.json")
    Call<BannerModel> fourItem();

    @GET("5item.json")
    Call<BannerModel> fiveItem();

    @GET("6item.json")
    Call<BannerModel> sixItem();

}