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

package com.carver.paul.dotavision.Models;

import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.util.Log;

import com.carver.paul.dotavision.AbilityInfo.AbilityInfoPresenter;
import com.carver.paul.dotavision.BuildConfig;
import com.carver.paul.dotavision.ImageRecognition.HeroAndSimilarity;
import com.carver.paul.dotavision.ImageRecognition.LoadedHeroImage;
import com.carver.paul.dotavision.ImageRecognition.RecognitionModel;
import com.carver.paul.dotavision.ImageRecognition.SimilarityTest;
import com.carver.paul.dotavision.ImageRecognition.LoadHeroXml;
import com.carver.paul.dotavision.Presenters.HeroesDetectedPresenter;
import com.carver.paul.dotavision.Presenters.MainActivityPresenter;
import com.carver.paul.dotavision.R;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Single;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;

public class DataManager {
    static final String TAG = "DataManager";

    private MainActivityPresenter mMainActivityPresenter;
    private HeroesDetectedPresenter mHeroesDetectedPresenter;
    private AbilityInfoPresenter mAbilityInfoPresenter;

    private List<HeroFromPhotoWithCurrentlySelected> mHeroesInPhoto;

    private AsyncSubject<List<HeroInfo>> mXmlInfoRx;
    private AsyncSubject<SimilarityTest> mSimilarityTestRx;
    private Subscriber<HeroFromPhoto> mHeroRecognitionSubscriberRx;

    public DataManager(final MainActivityPresenter mainActivityPresenter) {
        mMainActivityPresenter = mainActivityPresenter;
        mHeroesInPhoto = new ArrayList<>();

        StartXmlLoading();
        StartSimilarityTestLoading();
    }

    /**
     * When doing the work recognising the heroes in a photo there are four methods in MainActivity
     * that should be called. These are named recognition1_ through to recognition4_.
     *
     * This method does the image recognition work in a background thread, and when necessary calls
     * the appropriate methods on MainActivity to show the progress to the user.
     * @param
     */

    public void registerPresenters(final HeroesDetectedPresenter heroesDetectedPresenter,
            final AbilityInfoPresenter abilityInfoPresenter) {
        mHeroesDetectedPresenter = heroesDetectedPresenter;
        mHeroesDetectedPresenter.setDataManger(this);

        mAbilityInfoPresenter = abilityInfoPresenter;
    }

    public boolean presentersRegistered() {
        return (mHeroesDetectedPresenter != null && mAbilityInfoPresenter != null);
    }

    public void identifyHeroesInPhoto(final Bitmap photo) {
        if(!presentersRegistered()) {
            throw new RuntimeException("Attempting to identify heroes before registering " +
                    "presenters.");
        }

        mHeroesInPhoto.clear();
        prepareHeroRecognitionSubscriber();

        // Asks the main activity to show the "detecting heroes" loading screen
        mMainActivityPresenter.startHeroRecognitionLoadingAnimations(photo);
        mHeroesDetectedPresenter.reset();
        mAbilityInfoPresenter.reset();

        //TODO-now: re-write BIG KEY comment for DataManager
        /**
         * This is where the magic happens! Recognising the heroes in the photos goes through the
         * following steps:
         *
         *   1) zip the AsyncSubjects for loading the xml file and the similarityTest (the pictures
         *   against which we compare what we see in the photograph) to ensure both have loaded
         *   before going any further.
         *
         *   2) doOnNext: call prepareToShowResults, which gets the UI ready to start showing the
         *   results of the image processing.
         *
         *   3) flatMapIterable: turn the list of unidentified heroes in the photo into chain of
         *   Observables so that each can be processed in turn.
         *
         *   4) map: identify each hero from the photo.
         *
         *   5) As each is hero is identified it will be sent to the mHeroRecognitionSubscriberRx
         *   subscriber (which will in turn call the appropriate MainActivity methods to show the
         *   results to the user as they are available).
         */
        Observable.zip(mXmlInfoRx, mSimilarityTestRx, new Func2<List<HeroInfo>, SimilarityTest,
                List<HeroFromPhoto>>() {
            @Override
            public List<HeroFromPhoto> call(List<HeroInfo> heroInfoList,
                                            SimilarityTest similarityTest) {
                return RecognitionModel.findFiveHeroesInPhoto(photo);
            }
        })
                .doOnNext(new Action1<List<HeroFromPhoto>>() {
                    @Override
                    public void call(List<HeroFromPhoto> unidentifiedHeroes) {
                        prepareToShowResults(unidentifiedHeroes);
                    }
                })
                .flatMapIterable(new Func1<List<HeroFromPhoto>, Iterable<HeroFromPhoto>>() {
                    @Override
                    public Iterable<HeroFromPhoto> call(List<HeroFromPhoto> heroFromPhotos) {
                        return heroFromPhotos;
                    }
                })
                .map(new Func1<HeroFromPhoto, HeroFromPhoto>() {
                    @Override
                    public HeroFromPhoto call(HeroFromPhoto unidentifiedHero) {
                        return RecognitionModel.identifyHeroFromPhoto(unidentifiedHero,
                                mSimilarityTestRx.getValue());
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mHeroRecognitionSubscriberRx);
    }

    /**
     * This needs to be called by MainActivity.onDestory to ensure all subscribers no longer
     * subscribe to any observers, otherwise there could be memory leaks. (I think!)
     */
    public void onDestroy() {
        ensureAllSubscribersUnsubscribed();
    }

    public void receiveHeroChangedReport(int posInPhotoOfChangedHero,
                                         int posInSimilarityListOfNewSelection) {
        HeroFromPhotoWithCurrentlySelected hero = findHeroWithPhotoPos(posInPhotoOfChangedHero);
        hero.setSelectedHero(posInSimilarityListOfNewSelection);

        HeroInfo heroInfo = findHeroWithName(
                hero.getHeroSelected().name,
                mXmlInfoRx.getValue());

        mHeroesDetectedPresenter.changeHero(posInPhotoOfChangedHero, heroInfo.name,
                posInSimilarityListOfNewSelection);
        showHeroAbilities();
    }

    public void receiveHeroChangedReport(int posInPhotoOfChangedHero,
                                         String newHeroRealName) {
        HeroFromPhotoWithCurrentlySelected hero = findHeroWithPhotoPos(posInPhotoOfChangedHero);

        String heroImageName = getHeroImageName(newHeroRealName);
        hero.setSelectedHero(heroImageName);

        mHeroesDetectedPresenter.changeHero(posInPhotoOfChangedHero, newHeroRealName,
                hero.getPosInSimilarityListOfSelectedHero());
        showHeroAbilities();
    }

    /**
     *Create an observable to load the xml file in the background. mXmlInfoRx is subscribed to
     * it and will complete when the file has been loaded.
     */
    private void StartXmlLoading() {
        mXmlInfoRx = AsyncSubject.create();

        Observable.create(new Observable.OnSubscribe<List<HeroInfo>>() {
            @Override
            public void call(Subscriber<? super List<HeroInfo>> observer) {
                XmlResourceParser parser = mMainActivityPresenter.getContext().getResources()
                        .getXml(R.xml.hero_info_from_web);
                List<HeroInfo> heroInfoList = new ArrayList<>();
                LoadHeroXml.Load(parser, heroInfoList);
                observer.onNext(heroInfoList);
                observer.onCompleted();
            }
        })
//TODO-beauty: check that the XML parsing should be done on the io thread. Is all the io work done
// at the start instantly?
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mXmlInfoRx);
    }

    /**
     * Create an observable to Similarity Test (all the hero images used for detection) in the
     * background. mSimilarityTestRx is subscribed to it and will complete when the file has been
     * loaded.
     */
    private void StartSimilarityTestLoading() {
        mSimilarityTestRx = AsyncSubject.create();

        Observable.create(new Observable.OnSubscribe<SimilarityTest>() {
            @Override
            public void call(Subscriber<? super SimilarityTest> subscriber) {
                subscriber.onNext(new SimilarityTest(mMainActivityPresenter.getContext()));
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mSimilarityTestRx);
    }

    /**
     * Sets up mHeroRecognitionSubscriberRx
     */
    private void prepareHeroRecognitionSubscriber() {
        ensureAllSubscribersUnsubscribed();
        // Set up the subscriber
        mHeroRecognitionSubscriberRx = new Subscriber<HeroFromPhoto>() {

            // Finish off showing the results of the image processing.
            @Override
            public void onCompleted() {
                showHeroAbilities();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "mHeroRecognitionSubscriberRx. Unhandled error: " + e.toString());
            }

            //TODO-now: fix all comments in DataManager
            // For each hero identified in the photo, get MainActivity to show it
            @Override
            public void onNext(HeroFromPhoto hero) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Adding " + hero.getSimilarityList().get(0).hero.name);
                };
                mHeroesInPhoto.add(new HeroFromPhotoWithCurrentlySelected(hero));
                mHeroesDetectedPresenter.heroIdentified(hero.getPositionInPhoto(),
                        getHeroRealName(hero.getSimilarityList().get(0).hero.name));
            }
        };
    }

    private HeroFromPhotoWithCurrentlySelected findHeroWithPhotoPos(int posInPhoto) {
        for (HeroFromPhotoWithCurrentlySelected hero : mHeroesInPhoto) {
            if (hero.getHero().getPositionInPhoto() == posInPhoto) {
                return hero;
            }
        }
        return null;
    }

    private String getHeroRealName(String heroImageName) {
        HeroInfo heroInfo = findHeroWithName(heroImageName, mXmlInfoRx.getValue());
        return heroInfo.name;
    }

    private String getHeroImageName(String heroRealName) {
        if (mXmlInfoRx.getValue() == null)
            throw new RuntimeException("Called getHeroImageName when mHeroInfoFromXml is not " +
                    "initialised.");

        for(HeroInfo heroInfo : mXmlInfoRx.getValue()) {
            if (heroInfo.hasName(heroRealName)) {
                return heroInfo.imageName;
            }
        }

        return null;
    }

    // TODO-beauty: replace FindHeroWithName to use the drawable id int instead of strings
    private static HeroInfo findHeroWithName(String name, List<HeroInfo> heroInfos) {
        if (heroInfos == null)
            throw new RuntimeException("Called findHeroWithName when mHeroInfoFromXml is not " +
                    "initialised.");

        for (HeroInfo heroInfo : heroInfos) {
            if (heroInfo.hasName(name)) {
                return heroInfo;
            }
        }

        return null;
    }

    private void ensureAllSubscribersUnsubscribed() {
        //TODO-beauty: test if we actually need to unsubscribe from the observer, and if
        // unsubscribing like this does clear it from memory
        if(mHeroRecognitionSubscriberRx != null) {
            mHeroRecognitionSubscriberRx.unsubscribe();
            mHeroRecognitionSubscriberRx = null;
        }
    }

    /**
     * This will make the MainActivity to end its animations which show the hero image is being
     * processed. It also sends the unidentified heroes up to the HeroesDetectedPresenter so that
     * the photos of them can be shown.
     *
     * This method is safe to call from a background thread. We use RxJava here to ensure that the
     * required work in the UI this work is done in the mainThread (i.e. the UI thread).
     *
     * @param unidentifiedHeroes the list of heroes found in the photo, currently no work has been
     *                           done to identify who they are, we just need to have a photo of them
     *                           at this stage.
     */
    private void prepareToShowResults(List<HeroFromPhoto> unidentifiedHeroes) {
        Single.just(unidentifiedHeroes)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<HeroFromPhoto>>() {
                    public void call(List<HeroFromPhoto> unidentifiedHeroes) {
                        mMainActivityPresenter.stopHeroRecognitionLoadingAnimations();
                        mHeroesDetectedPresenter.prepareToShowResults(unidentifiedHeroes,
                                mXmlInfoRx.getValue());
                    }
                });
    }

    private void showHeroAbilities() {
        List<HeroInfo> heroInfoList = new ArrayList<>();
        for (HeroFromPhotoWithCurrentlySelected hero : mHeroesInPhoto) {
            HeroInfo heroInfo = findHeroWithName(
                    hero.getHeroSelected().name,
                    mXmlInfoRx.getValue());
            heroInfoList.add(heroInfo);
        }

        mAbilityInfoPresenter.showHeroAbilities(heroInfoList);
    }
}

//TODO-now: sort out HeroFromPhotoWithCurrentlySelected !
class HeroFromPhotoWithCurrentlySelected {
    private final HeroFromPhoto mHero;
    private LoadedHeroImage mHeroSelected;

    HeroFromPhotoWithCurrentlySelected(HeroFromPhoto hero) {
        mHero = hero;
        setSelectedHero(0);
    }

    public LoadedHeroImage getHeroSelected() {
        return mHeroSelected;
    }

    public HeroFromPhoto getHero() {
        return mHero;
    }

    public void setSelectedHero(int posInSimilarityList) {
        mHeroSelected = mHero.getSimilarityList().get(posInSimilarityList).hero;
    }

    public void setSelectedHero(String name) {
        for (HeroAndSimilarity sHero : mHero.getSimilarityList()) {
            if (sHero.hero.name.equals(name)) {
                mHeroSelected = sHero.hero;
                return;
            }
        }

        throw new RuntimeException("Couldn't find hero with name " + name + " in similarity list.");
    }

    public int getPosInSimilarityListOfSelectedHero() {
        for(int i = 0; i < mHero.getSimilarityList().size(); i++) {
            if(mHeroSelected == mHero.getSimilarityList().get(i).hero) {
                return i;
            }
        }

        throw new RuntimeException("Couldn't find hero in similarity list.");
    }
}
