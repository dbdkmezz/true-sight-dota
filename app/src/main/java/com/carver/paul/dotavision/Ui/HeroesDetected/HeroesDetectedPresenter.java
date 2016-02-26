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
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * This class shows the five heroes which have been found in the image.
 *
 * For each hero we use HeroesDetectedItem to show:
 *
 *   1) The image of the hero we found in the photo.
 *
 *   2) The name of the hero (this is editable by the user to change the hero identified)
 *
 *   3) A horizontal RecyclerView showing all the images of the heroes in the game, in order of how
 *   similar we think they are to the image of the hero in the photo. The user can scroll through
 *   these to select a different hero.
 */
public class HeroesDetectedPresenter {
    private static final String TAG = "HeroesDetectedPresenter";

    private final HeroesDetectedFragment mView;
    private DataManager mDataManger;
    private List<HeroDetectedItemPresenter> mHeroDetectedItemPresenters = new ArrayList<>();
    private Subscriber<SimilarityListAndPosition> mHeroRecognitionSubscriberRx;
    private Subscriber<List<HeroInfo>> mNameSetupSubscriberRx;

    public HeroesDetectedPresenter(HeroesDetectedFragment view) {
        mView = view;
    }

/*    public void clearAll() {
        for (HeroDetectedItemPresenter presenter : mHeroDetectedItemPresenters) {
            presenter.clear();
        }
    }*/

    public void reset() {
        setupSubscriber();
        for (HeroDetectedItemPresenter presenter : mHeroDetectedItemPresenters) {
            presenter.clear();
        }
        mHeroDetectedItemPresenters.clear();
        mView.removeAllViews();
    }

    public void setDataManger(DataManager dataManger) {
        mDataManger = dataManger;
    }

    public Subscriber<SimilarityListAndPosition> getHeroRecognitionSubscriberRx() {
        return mHeroRecognitionSubscriberRx;
    }

    public void showWithoutRecyclers() {
        mHeroDetectedItemPresenters = mView.createHeroDetectedViews(5, false);
        setupTextAutoCompleteAndChangeListener();
    }

    public void showHeroImages(List<HeroImageAndPosition> heroImages) {
        mHeroDetectedItemPresenters = mView.createHeroDetectedViews(heroImages.size(), true);

        for (int i = 0; i < mHeroDetectedItemPresenters.size() && i < heroImages.size(); i++) {
            mHeroDetectedItemPresenters.get(i).setPhotoImage(heroImages.get(i));
        }

        setupTextAutoCompleteAndChangeListener();
    }

    public void hideKeyboard() {
        mView.hideKeyboard();
    }

    public void sendUpdatedHeroList() {
        sendUpdatedHeroList(false);
    }

    public boolean allHeroesClear() {
        for (HeroDetectedItemPresenter hero : mHeroDetectedItemPresenters) {
            if(hero.getName() != null && hero.getName().length() > 0) {
                return false;
            }
        }
        return true;
    }

    protected void onDestroy() {
        unsubscribeSubscriber();
    }

    public String getHeroRealName(String heroImageName) {
        HeroInfo heroInfo = findHeroWithName(heroImageName, mDataManger.getHeroInfoValue());
        return heroInfo.name;
    }

    public String getHeroImageName(String heroRealName) {
        if(heroRealName.equals("")) {
            return "";
        }

        HeroInfo heroInfo = findHeroWithName(heroRealName, mDataManger.getHeroInfoValue());
        return heroInfo.imageName;
    }

    public void recyclerViewAnimationsFinished() {
        sendUpdatedHeroList(true);
    }

    /**
     * Sends the datamanager the list of all the heroes currently selected.
     * @param completelyNewList true if the list has just been loaded for the first time (i.e. is
     *                          not changing as a result of user input).
     */
    private void sendUpdatedHeroList(boolean completelyNewList) {
        List<HeroInfo> heroInfoList = new ArrayList<>();
        for (HeroDetectedItemPresenter hero : mHeroDetectedItemPresenters) {
            if(hero.getName() == null) {
                Log.d(TAG, "WARNING: Hero item name not initialised when called " +
                        "sendUpdatedHeroList, giving up attempt to send update.");
                return;
            }
            HeroInfo heroInfo = findHeroWithName(hero.getName(), mDataManger.getHeroInfoValue());
            heroInfoList.add(heroInfo);
        }

        mDataManger.sendUpdatedHeroList(heroInfoList, completelyNewList);
    }

    private void setupSubscriber() {
        unsubscribeSubscriber();

        mHeroRecognitionSubscriberRx = new Subscriber<SimilarityListAndPosition>() {

            // Show the ability cards for the heroes we have now identified
            @Override
            public void onCompleted() {
//                sendUpdatedHeroList(true);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "mHeroRecognitionSubscriberRx. Unhandled error: " + e.toString());
            }

            // When a hero has been identified, show the images of similar heroes and the name of
            // the hero we think it is by completing the set up of the HeroDetectedItemPresenter
            @Override
            public void onNext(SimilarityListAndPosition similarityListAndPosition) {
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
                similarityListAndPosition.getPosition() == 4);
    }

    private void setupTextAutoCompleteAndChangeListener() {
        // To setup autocomplete we need to know all the hero names from the xml file, but the data
        // manager may not have finished loading the xml yet. So we subscribe to observable loading
        // them and defer setting up the names until its done.

        mNameSetupSubscriberRx =  new Subscriber<List<HeroInfo>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "mNameSetupSubscriberRx. Unhandled error: " + e.toString());
            }

            @Override
            public void onNext(List<HeroInfo> heroInfo) {
                List<String> allHeroNames = getAllHeroNames(heroInfo);
                for (HeroDetectedItemPresenter presenter : mHeroDetectedItemPresenters) {
                    presenter.setupTextAutoCompleteAndChangeListener(allHeroNames);
                }
            }
        };

        mDataManger.getHeroInfoRx()
                .first()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mNameSetupSubscriberRx);
    }

    private void unsubscribeSubscriber() {
        //TODO-beauty: test if we actually need to unsubscribe from the observer, and if
        // unsubscribing like this does clear it from memory
        if (mHeroRecognitionSubscriberRx != null) {
            mHeroRecognitionSubscriberRx.unsubscribe();
            mHeroRecognitionSubscriberRx = null;
        }
        if(mNameSetupSubscriberRx != null) {
            mNameSetupSubscriberRx.unsubscribe();
            mNameSetupSubscriberRx = null;
        }
    }

/*    private void ensureHeroNamesInitialised() {
        if (mAllHeroNames == null) {
            if (mDataManger == null || mDataManger.getHeroInfo() == null) {
                throw new RuntimeException("Attempting to access hero info but mDataManager not " +
                        "ready.");
            }
            mAllHeroNames = getAllHeroNames(mDataManger.getHeroInfo());
        }
    }*/

    private HeroDetectedItemPresenter findPresenterAtPosition(int positionInPhoto) {
        for(HeroDetectedItemPresenter heroPresenter : mHeroDetectedItemPresenters) {
            if(heroPresenter.getPositionInPhoto() == positionInPhoto) {
                return heroPresenter;
            }
        }

        throw new RuntimeException("Can't find presenter for hero in photo");
    }

    /**
     * Returns a list of all the names of heroes in the game (using the data loaded in
     * heroInfoFromXml)
     * @param heroInfoFromXml
     * @return
     */
    private static List<String> getAllHeroNames(List<HeroInfo> heroInfoFromXml) {
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

        if (name == null)
            throw new RuntimeException("Called findHeroWithName when name is not " +
                    "initialised.");

        if(name.equals("")) {
            HeroInfo missingHero = new HeroInfo();
            missingHero.name = "";
            return missingHero;
        }

        for (HeroInfo heroInfo : heroInfos) {
            if (heroInfo.hasName(name)) {
                return heroInfo;
            }
        }

        return null;
    }
}
