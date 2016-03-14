/**
 * True Sight for Dota 2
 * Copyright (C) 2016 Paul Broadbent
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */

package com.carver.paul.dotavision.Models.AdvantagesDownloader;

import android.util.Log;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Downloader {
    private static final String SERVICE_ENDPOINT = "https://test-truesight.rhcloud.com/";
    private static final String TAG = "DownloadAdvantages";

    static protected void getAdvantages(List<String> heroesInPhoto) {
        RestAdapter restAdapter = new RestAdapter
                .Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(SERVICE_ENDPOINT).build();

        AdvantagesApi advantages = restAdapter.create(AdvantagesApi.class);

        if(heroesInPhoto.size() != 5) {
            throw new RuntimeException("Wrong number of heroes. Need 5");
        }

        advantages.getAdvantages(heroesInPhoto.get(0), heroesInPhoto.get(1), heroesInPhoto.get(2),
                heroesInPhoto.get(3), heroesInPhoto.get(4), new Callback<AdvantageData>() {
                    @Override
                    public void success(AdvantageData gitmodel, Response response) {
                        Log.d(TAG, "Yay!");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "Fail!");
                    }
                });
    }
}
