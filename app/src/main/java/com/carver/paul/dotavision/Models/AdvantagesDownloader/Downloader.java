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

package com.carver.paul.dotavision.Models.AdvantagesDownloader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.carver.paul.dotavision.Models.HeroAndAdvantages;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;
import rx.Observable;
import rx.functions.Func1;

public class Downloader {
    private static final String SERVICE_ENDPOINT = "https://test-truesight.rhcloud.com/";
    private static final String TAG = "AdvantagesDownloader";

    @RxLogObservable
    static public Observable<List<HeroAndAdvantages>> getObservable(List<String> heroesInPhoto,
                                                                    boolean networkAvailable) {
        if(networkAvailable == false) {
            return Observable.error(new Throwable("No network available"));
        }

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(SERVICE_ENDPOINT)
                .build();

        AdvantagesApi advantages = restAdapter.create(AdvantagesApi.class);

        if(heroesInPhoto.size() != 5) {
            throw new RuntimeException("Wrong number of heroes. Need 5");
        }

        heroesInPhoto = removeEmptyNames(heroesInPhoto);

        return advantages.getAdvantages(heroesInPhoto.get(0), heroesInPhoto.get(1), heroesInPhoto.get(2),
                heroesInPhoto.get(3), heroesInPhoto.get(4))
                .map(new Func1<AdvantageData, List<HeroAndAdvantages>>() {
                    @Override
                    public List<HeroAndAdvantages> call(AdvantageData advantageData) {
                        List<HeroAndAdvantages> newList = new ArrayList<>();
                        for(AdvantagesDatum datum : advantageData.getData()) {
                            newList.add(new HeroAndAdvantages(datum));
                        }
                        return newList;
                    }
                });
    }

    static private List<String> removeEmptyNames(List<String> heroesInPhoto) {
        List<String> newList = new ArrayList<>();
        for(String s : heroesInPhoto) {
            if(s.equals("")) {
                newList.add("none");
            } else {
                newList.add(s);
            }
        }
        return newList;
    }
}
