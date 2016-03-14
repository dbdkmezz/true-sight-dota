package com.carver.paul.dotavision.Models.AdvantagesDownloader;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface AdvantagesApi {
    @GET("/advantages/{name1}/{name2}/{name3}/{name4}/{name5}")
    Observable<AdvantageData> getAdvantages(@Path("name1") String name1,
                                            @Path("name2") String name2,
                                            @Path("name3") String name3,
                                            @Path("name4") String name4,
                                            @Path("name5") String name5);
}