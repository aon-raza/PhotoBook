package com.sar.photobook;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface IMyService {

    @GET("photos")
    Observable<Object> getAlbum();

}
