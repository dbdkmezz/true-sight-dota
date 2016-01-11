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

import com.carver.paul.dotavision.Ui.AbilityInfo.AbilityInfoPresenter;
import com.carver.paul.dotavision.BuildConfig;
import com.carver.paul.dotavision.ImageRecognition.LoadedHeroImage;
import com.carver.paul.dotavision.ImageRecognition.RecognitionModel;
import com.carver.paul.dotavision.ImageRecognition.SimilarityTest;
import com.carver.paul.dotavision.ImageRecognition.LoadHeroXml;
import com.carver.paul.dotavision.Ui.HeroesDetected.HeroesDetectedPresenter;
import com.carver.paul.dotavision.Ui.MainActivityPresenter;
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

/**
 * This is the class which is responsible for processing the photo to identify the heroes in it.
 *
 * All the hard photo processing will be done in a new thread, so that it doesn't make the UI lock
 * up. Updates will be sent to the UI as they become available.
 */
public class DataManager {
    static final String TAG = "DataManager";

    private MainActivityPresenter mMainActivityPresenter;
    private HeroesDetectedPresenter mHeroesDetectedPresenter;
    private AbilityInfoPresenter mAbilityInfoPresenter;

    private List<HeroFromPhotoWithCurrentlySelected> mHeroesInPhoto;

    private AsyncSubject<List<HeroInfo>> mXmlInfoRx;
    private AsyncSubject<SimilarityTest> mSimilarityTestRx;
    private Subscriber<HeroFromPhoto> mHeroRecognitionSubscriberRx;

    /**
     * By calling StartXmlLoading and StartSimilarityTestLoading as soon as the datamanager is
     * created (hopefully when the activity is first launched) this hard work can be done in a
     * background thread immediately. These loading tasks are likely to complete before the results
     * are needed, but the use of rxJava later on means that it doesn't matter if they are not.
     * @param mainActivityPresenter
     */
    public DataManager(final MainActivityPresenter mainActivityPresenter) {
        mMainActivityPresenter = mainActivityPresenter;
        mHeroesInPhoto = new ArrayList<>();

        StartXmlLoading();
        StartSimilarityTestLoading();
    }

    /**
     * Registers the UI presenters needed to send the results of image recognition back to the UI
     * views. (In MVP presenters are the middleman between the hard work done in the Models, and
     * the Views with which the user interacts.)
     *
     * @param heroesDetectedPresenter
     * @param abilityInfoPresenter
     */
    public void registerPresenters(final HeroesDetectedPresenter heroesDetectedPresenter,
            final AbilityInfoPresenter abilityInfoPresenter) {
        mHeroesDetectedPresenter = heroesDetectedPresenter;
        mHeroesDetectedPresenter.setDataManger(this);

        mAbilityInfoPresenter = abilityInfoPresenter;
    }

    public boolean presentersRegistered() {
        return (mMainActivityPresenter != null
                && mHeroesDetectedPresenter != null
                && mAbilityInfoPresenter != null);
    }

    /**
     * This will identify the five heroes in the photo.
     *
     * While doing the hard image recognition work in the background the UI will show the results
     * as they become available. The UI goes through the following stages:
     *
     *   1) Show the loading animation (e.g. pulsing the camera button).
     *
     *   2) Show the images of the heroes we have found in the photo, but not yet identified who
     *   they are.
     *
     *   3) One by one, show the images of the heroes we think they are. Each image is processed to
     *   identify a match individually, and the results are shown on the UI as they become available.
     *
     *   4) Show the abilities of all the heroes identified.
     *
     * @param photo
     */
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

        /**
         * This is where the magic happens! Recognising the heroes in the photos goes through the
         * following steps:
         *
         *   1) zip the AsyncSubjects for loading the xml file and the similarityTest (the pictures
         *   against which we compare what we see in the photograph) to ensure both have loaded
         *   before going any further.
         *
         *   2) doOnNext: call prepareToShowResults, which gets the UI ready to start showing the
         *   results of the image processing (i.e. end the loading animation and show the images
         *   of the heroes we have found in the photo (but not yet identifeid who they are).
         *
         *   3) flatMapIterable: turn the list of unidentified heroes in the photo into chain of
         *   Observables so that each can be processed in turn.
         *
         *   4) map: identify each hero from the photo.
         *
         *   5) As each is hero is identified it will be sent to the mHeroRecognitionSubscriberRx
         *   subscriber (which will in turn call the appropriate MainActivity methods to show the
         *   results to the user as they are available). (On a Nexus 5 identifying each hero takes
         *   around 0.2 seconds, so it is good that we can show the results as they become
         *   available.)
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

    /**
     * This class enables presenters to inform the DataManager a hero has changed and that we
     * need to update the hero abilities we show.
     *
     * @param posInPhotoOfChangedHero           The position in the photo of the hero which has
     *                                          changed (counting between 0 and 4, starting from the
     *                                          left of the photo)
     * @param posInSimilarityListOfNewSelection The number in the list of similar heroes which
     *                                          has been selected. (For each image of a hero in the
     *                                          photo there is a list of all heroes in the game (the
     *                                          similarity list) ordered by how similar we think
     *                                          they are to the image.
     */
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

    /**
     * This class enables presenters to inform the DataManager a hero has changed and that we
     * need to update the hero abilities we show.
     *
     * @param posInPhotoOfChangedHero           The position in the photo of the hero which has
     *                                          changed (counting between 0 and 4, starting from the
     *                                          left of the photo)
     * @param newHeroRealName                   The name of the hero which the user has identified
     *                                          the photo to be.
     */
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
     * Create an observable to load the xml file in the background. mXmlInfoRx is subscribed to
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
     * Create an observable to load SimilarityTest (basically this loads a picture of each hero in
     * the game to be used in image recognition) in the background. mSimilarityTestRx is subscribed
     * to this observable and will complete when the file has been loaded.
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
     * This will make the MainActivity end its animations which show the hero image is being
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

    /**
     * Sets up mHeroRecognitionSubscriberRx. As each hero is identified onNext for this subscriber
     * is called.
     */
    private void prepareHeroRecognitionSubscriber() {
        ensureAllSubscribersUnsubscribed();
        // Set up the subscriber
        mHeroRecognitionSubscriberRx = new Subscriber<HeroFromPhoto>() {

            // Show the ability cards for the heroes we have now identified
            @Override
            public void onCompleted() {
                showHeroAbilities();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "mHeroRecognitionSubscriberRx. Unhandled error: " + e.toString());
            }

            // When a hero has been identified, get the mHeroesDetectedPresenter to show who we
            // think the hero is
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

//TODO-now: sort out HeroFromPhotoWithCurrentlySelected. This is a very ugly class and too many
// of my classes have "hero" in the name or members. THOUGHT IS NEEDED.

/**
 * This class contains a hero found in a photo (HeroFromPhoto) and the hero which is currently
 * selected from ths list of heroes which are similar to it (i.e. the one who's abilities are shown
 * in the UI.
 */
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
