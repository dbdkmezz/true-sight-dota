/**
 * True Sight for Dota 2
 * Copyright (C) 2016 Paul Broadbent
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */

package com.carver.paul.dotavision.Models.AdvantagesDownloader;

import android.util.Log;
import android.util.Pair;

import com.carver.paul.dotavision.Models.HeroAndAdvantages;
import com.carver.paul.dotavision.Models.SqlLoader;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import rx.Observable;
import rx.functions.Func1;

public class Downloader {
    public static final int NO_DIFFERENCES_FOUND = 100;
    public static final int MULTIPLE_DIFFERENCES_FOUND = 101;

    private static final String SERVICE_ENDPOINT = "https://test-truesight.rhcloud.com/";
    private static final String TAG = "AdvantagesDownloader";
    private static final int FULL_QUERY_TIMEOUT = 3500;
    private static final int SINGlE_QUERY_TIMEOUT = 3500;

//    @RxLogObservable(RxLogObservable.Scope.NOTHING)
    @RxLogObservable
    static public Observable<List<HeroAndAdvantages>>
    getObservable(List<String> heroesInPhoto,
                  boolean networkAvailable,
                  final Pair<List<String>, List<HeroAndAdvantages>> lastAdvantageData) {
        if (networkAvailable == false) {
            // There's no network connection so we might as well give up now
            return Observable.error(new Throwable("No network available"));
        }

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(SERVICE_ENDPOINT)
                .build();

        AdvantagesApi advantagesApi = restAdapter.create(AdvantagesApi.class);

        if (heroesInPhoto.size() != 5) {
            throw new RuntimeException("Wrong number of heroes. Need 5");
        }

        if (lastAdvantageData != null) {
            final int differencePos = findSingleDifference(heroesInPhoto, lastAdvantageData.first);
            if (differencePos == NO_DIFFERENCES_FOUND) {
                // If the heroes are the same as those we processed last time then we can just send
                // back the same results as last time
                Log.d(TAG, "No differences found.");
                return Observable.just(lastAdvantageData.second);
            }
            if (differencePos != MULTIPLE_DIFFERENCES_FOUND) {
                // If there is only one hero which has changed then we only need to ask the server
                // to give us data on that hero (this makes it easier, and quicker, for the server)
                return getSingleHeroChangedObservable(lastAdvantageData.second, heroesInPhoto,
                        differencePos, advantagesApi);
            }
        }

        List<String> testList = Arrays.asList("", "", "", "", "");
        int singleHeroPos = findSingleDifference(heroesInPhoto, testList);
        if(singleHeroPos != NO_DIFFERENCES_FOUND && singleHeroPos != MULTIPLE_DIFFERENCES_FOUND) {
            return getSingleHeroObservable(advantagesApi, singleHeroPos, heroesInPhoto);
        }

        return getFullObservable(advantagesApi, heroesInPhoto);
    }

    static public int findSingleDifference(List<String> list1, List<String> list2) {
        if(list1.size() != list2.size()) {
            throw new RuntimeException("One of the hero names lists is the wrong length. This " +
                    "should never happen!");
        }

        int differencePos = NO_DIFFERENCES_FOUND;
        for(int i = 0; i < list1.size(); i++) {
            if(!list1.get(i).equals(list2.get(i))) {
                if(differencePos == NO_DIFFERENCES_FOUND) {
                    differencePos = i;
                } else {
                    return MULTIPLE_DIFFERENCES_FOUND;
                }
            }
        }
        return differencePos;
    }

    static private List<String> prepareNamesForSql(List<String> heroesInPhoto) {
        List<String> newList = new ArrayList<>();
        for (String s : heroesInPhoto) {
            if(s.contains("'")) {
                s = s.replace("'", "");
            }

            if (s.equals("")) {
                newList.add("none");
            } else {
                newList.add(s);
            }
        }
        return newList;
    }

    /**
     * If just one of the five heroes in the list is different from last time then we don't want to
     * make the server get advantages data on all of them. This method returns an observable that
     * just queries the server for advantages data on the new hero (the one at differencePos) and
     * then updates the earlier advantages info with that new hero.
     *
     * @param oldAdvantagesData
     * @param heroesInPhoto
     * @param differencePos
     * @param advantagesApi
     * @return
     */
    static private Observable<List<HeroAndAdvantages>> getSingleHeroChangedObservable(
            final List<HeroAndAdvantages> oldAdvantagesData, List<String> heroesInPhoto,
            final int differencePos, AdvantagesApi advantagesApi) {
        final List<HeroAndAdvantages> advantagesData =
                SqlLoader.deepCopyOfHeroes(oldAdvantagesData);

        if(heroesInPhoto.get(differencePos).equals("")) {
            for(HeroAndAdvantages hero : advantagesData) {
                hero.setAdvantage(HeroAndAdvantages.NEUTRAL_ADVANTAGE, differencePos);
            }
            return Observable.just(advantagesData);
        }

        heroesInPhoto = prepareNamesForSql(heroesInPhoto);

        return advantagesApi.getSingeAdvantage(heroesInPhoto.get(differencePos))
                .map(new Func1<AdvantageData, List<HeroAndAdvantages>>() {
                    @Override
                    public List<HeroAndAdvantages> call(AdvantageData newAdvantageData) {
                        for(HeroAndAdvantages hero : advantagesData) {
                            for(AdvantagesDatum newHero : newAdvantageData.getData()) {
                                if(hero.getName().equals(newHero.getName())) {
                                    hero.setAdvantage(newHero.getAdvantages().get(0),
                                            differencePos);
                                    break;
                                }
                            }
                        }
                        Log.d(TAG, "Downloaded data for just one difference.");
                        Collections.sort(advantagesData);
                        return advantagesData;
                    }
                })
                .timeout(SINGlE_QUERY_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    /**
     * If we only have one hero in the list of heroes then we don't need to ask the server to get
     * full advantages data, we can just ask it to get advantages data for the one hero. This
     * returns an observable which does that.
     * @param advantagesApi
     * @param singleHeroPos
     * @param heroesInPhoto
     * @return
     */
    static private Observable<List<HeroAndAdvantages>> getSingleHeroObservable(
            AdvantagesApi advantagesApi, final int singleHeroPos, List<String> heroesInPhoto) {

        return advantagesApi.getSingeAdvantage(heroesInPhoto.get(singleHeroPos))
                .map(new Func1<AdvantageData, List<HeroAndAdvantages>>() {
                    @Override
                    public List<HeroAndAdvantages> call(AdvantageData newAdvantageData) {
                        List<HeroAndAdvantages> newList = new ArrayList<>();
                        for(AdvantagesDatum hero : newAdvantageData.getData()) {
                            List<Double> fullAdvantages = Arrays.asList(
                                    HeroAndAdvantages.NEUTRAL_ADVANTAGE,
                                    HeroAndAdvantages.NEUTRAL_ADVANTAGE,
                                    HeroAndAdvantages.NEUTRAL_ADVANTAGE,
                                    HeroAndAdvantages.NEUTRAL_ADVANTAGE,
                                    HeroAndAdvantages.NEUTRAL_ADVANTAGE);
                            fullAdvantages.set(singleHeroPos, hero.getAdvantages().get(0));
                            hero.setAdvantages(fullAdvantages);
                            newList.add(new HeroAndAdvantages(hero));
                            }
                        Log.d(TAG, "Downloaded data for a new single hero.");
                        Collections.sort(newList);
                        return newList;
                    }
                })
                .timeout(SINGlE_QUERY_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    /**
     * Returns an observable which queries the server to find advantages data on the five heroes
     * listed in heroesInPhoto.
     * @param advantagesApi
     * @param heroesInPhoto
     * @return
     */
    static private Observable<List<HeroAndAdvantages>> getFullObservable(
            AdvantagesApi advantagesApi, List<String> heroesInPhoto) {
        heroesInPhoto = prepareNamesForSql(heroesInPhoto);

        return advantagesApi.getAdvantages(heroesInPhoto.get(0), heroesInPhoto.get(1),
                heroesInPhoto.get(2), heroesInPhoto.get(3), heroesInPhoto.get(4))
                .map(new Func1<AdvantageData, List<HeroAndAdvantages>>() {
                    @Override
                    public List<HeroAndAdvantages> call(AdvantageData advantageData) {
                        List<HeroAndAdvantages> newList = new ArrayList<>();
                        for (AdvantagesDatum datum : advantageData.getData()) {
                            newList.add(new HeroAndAdvantages(datum));
                        }
                        Log.d(TAG, "Downloaded data for five whole heroes.");
                        Collections.sort(newList);
                        return newList;
                    }
                })
                .timeout(FULL_QUERY_TIMEOUT, TimeUnit.MILLISECONDS);
    }
}
