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

package com.carver.paul.dotavision.Ui.HeroesDetected;

import android.util.Log;

import com.carver.paul.dotavision.BuildConfig;
import com.carver.paul.dotavision.Models.DataManager;
import com.carver.paul.dotavision.Models.HeroImageAndPosition;
import com.carver.paul.dotavision.Models.HeroInfo;
import com.carver.paul.dotavision.Models.SimilarityListAndPosition;
import com.carver.paul.dotavision.Ui.HeroesDetected.HeroesDetectedItem.HeroDetectedItemPresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * This class shows the heroes which have been found in the image.
 *
 * For each of them we show:
 *
 *   1) The image of the hero we found in the photo.
 *
 *   2) The name of the hero (this is editable by the user to change the hero identified)
 *
 *   3) A horizontal RecyclerView showing all the images of the heroes in the game, in order of how
 *   similar we think they are to the image of the hero in the photo. The user can scroll through
 *   these to select a different hero.
 *
 *   These is a HeroDetectedItem for each hero.
 */
public class HeroesDetectedPresenter {
    private static final String TAG = "HeroesDetectedPresenter";

    private HeroesDetectedFragment mView;
    private DataManager mDataManger;
    private List<HeroDetectedItemPresenter> mHeroDetectedItemPresenters = new ArrayList<>();
    private List<String> mAllHeroNames;
    private Subscriber<SimilarityListAndPosition> mHeroRecognitionSubscriberRx;

    public HeroesDetectedPresenter(HeroesDetectedFragment view) {
        mView = view;
    }

    public void reset() {
        mView.removeAllViews();
        mHeroDetectedItemPresenters.clear();
        setupSubscriber();
    }

    public void setDataManger(DataManager dataManger) {
        mDataManger = dataManger;
    }

    public Subscriber<SimilarityListAndPosition> getHeroRecognitionSubscriberRx() {
        return mHeroRecognitionSubscriberRx;
    }

    public void showHeroImages(List<HeroImageAndPosition> heroImages) {
        mHeroDetectedItemPresenters = mView.createHeroDetectedViews(heroImages.size());

        ensureHeroNamesInitialised();

        for (int i = 0; i < mHeroDetectedItemPresenters.size() && i < heroImages.size(); i++) {
            mHeroDetectedItemPresenters.get(i).setImage(this, heroImages.get(i));
        }
    }

    public void sendUpdatedHeroList() {
        List<HeroInfo> heroInfoList = new ArrayList<>();
        for (HeroDetectedItemPresenter hero : mHeroDetectedItemPresenters) {
            HeroInfo heroInfo = findHeroWithName(hero.getName(), mDataManger.getHeroInfo());
            if (!heroInfoList.contains(heroInfo))
                heroInfoList.add(heroInfo);
        }

        mDataManger.sendUpdatedHeroList(heroInfoList);
    }

    public void hideKeyboard() {
        mView.hideKeyboard();
    }

    protected void onDestroy() {
        unsubscribeSubscriber();
    }

    public String getHeroRealName(String heroImageName) {
        HeroInfo heroInfo = findHeroWithName(heroImageName, mDataManger.getHeroInfo());
        return heroInfo.name;
    }

    public String getHeroImageName(String heroRealName) {
        HeroInfo heroInfo = findHeroWithName(heroRealName, mDataManger.getHeroInfo());
        return heroInfo.imageName;
    }

    private void setupSubscriber() {
        unsubscribeSubscriber();

        mHeroRecognitionSubscriberRx = new Subscriber<SimilarityListAndPosition>() {

            // Show the ability cards for the heroes we have now identified
            @Override
            public void onCompleted() {
                sendUpdatedHeroList();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "mHeroRecognitionSubscriberRx. Unhandled error: " + e.toString());
            }

            // When a hero has been identified, show the images of similar heroes and the name of
            // the hero we think it is by completing the set up of the HeroDetectedItemPresenter
            @Override
            public void onNext(SimilarityListAndPosition similarityListAndPosition) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Adding "
                            + similarityListAndPosition.getSimilarityList().get(0).hero.name);
                }
                ;

                completeHeroDetectedSetup(similarityListAndPosition);
            }
        };
    }

    // When a hero has been identified, this is used to show the images of similar heroes and the
    // name of the hero we think it is by completing the set up of the HeroDetectedItemPresenter
    private void completeHeroDetectedSetup(SimilarityListAndPosition similarityListAndPosition) {
        HeroDetectedItemPresenter heroDetected =
                findPresenterAtPosition(similarityListAndPosition.getPosition());

        heroDetected.setSimilarityListAndName(
                similarityListAndPosition.getSimilarityList(),
                getHeroRealName(similarityListAndPosition.getSimilarityList().get(0).hero.name),
                mAllHeroNames);
    }

    private void unsubscribeSubscriber() {
        //TODO-beauty: test if we actually need to unsubscribe from the observer, and if
        // unsubscribing like this does clear it from memory
        if (mHeroRecognitionSubscriberRx != null) {
            mHeroRecognitionSubscriberRx.unsubscribe();
            mHeroRecognitionSubscriberRx = null;
        }
    }

    private void ensureHeroNamesInitialised() {
        if (mAllHeroNames == null) {
            if (mDataManger == null || mDataManger.getHeroInfo() == null) {
                throw new RuntimeException("Attempting to access hero info but mDataManager not " +
                        "ready.");
            }
            mAllHeroNames = getHeroNames(mDataManger.getHeroInfo());
        }
    }

    private HeroDetectedItemPresenter findPresenterAtPosition(int positionInPhoto) {
        for(HeroDetectedItemPresenter heroPresenter : mHeroDetectedItemPresenters) {
            if(heroPresenter.getPositionInPhoto() == positionInPhoto) {
                return heroPresenter;
            }
        }

        throw new RuntimeException("Can't find presenter for hero in photo");
    }

    private static List<String> getHeroNames(List<HeroInfo> heroInfoFromXml) {
        List<String> names = new ArrayList<>();
        for(HeroInfo heroInfo : heroInfoFromXml) {
            names.add(heroInfo.name);
        }
        return names;
    }

    // TODO-beauty: replace FindHeroWithName to use the drawable id int instead of strings
    private static HeroInfo findHeroWithName(String name, List<HeroInfo> heroInfos) {
        if (heroInfos == null)
            throw new RuntimeException("Called findHeroWithName when heroInfo is not " +
                    "initialised.");

        for (HeroInfo heroInfo : heroInfos) {
            if (heroInfo.hasName(name)) {
                return heroInfo;
            }
        }

        return null;
    }
}
