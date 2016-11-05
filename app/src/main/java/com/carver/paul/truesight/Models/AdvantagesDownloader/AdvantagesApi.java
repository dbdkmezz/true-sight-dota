/**
 * True Sight for Dota 2
 * Copyright (C) 2016 Paul Broadbent
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */

package com.carver.paul.truesight.Models.AdvantagesDownloader;

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

    @GET("/advantages/{name}")
    Observable<AdvantageData> getSingeAdvantage(@Path("name") String name);
}