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

import com.carver.paul.dotavision.ImageRecognition.LoadHeroXml;
import com.carver.paul.dotavision.ImageRecognition.RecognitionModel;
import com.carver.paul.dotavision.ImageRecognition.SimilarityTest;
import com.carver.paul.dotavision.R;
import com.carver.paul.dotavision.Ui.AbilityInfo.AbilityInfoPresenter;
import com.carver.paul.dotavision.Ui.CounterPicker.CounterPickerPresenter;
import com.carver.paul.dotavision.Ui.HeroesDetected.HeroesDetectedPresenter;
import com.carver.paul.dotavision.Ui.MainActivityPresenter;

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
    private CounterPickerPresenter mCounterPickerPresenter;

    private AsyncSubject<List<HeroInfo>> mXmlInfoRx;
    private AsyncSubject<SimilarityTest> mSimilarityTestRx;

    /**
     * By calling StartXmlLoading and StartSimilarityTestLoading as soon as DataManager is
     * created (hopefully when the activity is first launched) this hard work can be done in a
     * background thread immediately. These loading tasks are likely to complete before the results
     * are needed, but the use of rxJava later on means that it doesn't matter if they're not.
     *
     * @param mainActivityPresenter
     */
    public DataManager(final MainActivityPresenter mainActivityPresenter) {
        mMainActivityPresenter = mainActivityPresenter;

        startXmlLoading();
        startSimilarityTestLoading();
    }

    /**
     * Registers the UI presenters needed to send the results of image recognition back to the UI
     * views. (In MVP Presenters are the middleman between the hard work done in the Models, and
     * the Views with which the user interacts.)
     *
     * This must be called before attempting to identify heroes in a photo otherwise an exception
     * will be thrown, since we won't have anywhere to send the results to.
     *
     * @param heroesDetectedPresenter
     * @param abilityInfoPresenter
     */
    public void registerPresenters(final HeroesDetectedPresenter heroesDetectedPresenter,
                                   final AbilityInfoPresenter abilityInfoPresenter,
                                   final CounterPickerPresenter counterPickerPresenter) {
        mHeroesDetectedPresenter = heroesDetectedPresenter;
        mHeroesDetectedPresenter.setDataManger(this);

        mAbilityInfoPresenter = abilityInfoPresenter;
        mCounterPickerPresenter = counterPickerPresenter;
    }

    public boolean presentersRegistered() {
        return (mMainActivityPresenter != null
                && mHeroesDetectedPresenter != null
                && mAbilityInfoPresenter != null
                && mCounterPickerPresenter != null);
    }

    /**
     * This will identify the five heroes in the photo.
     *
     * The hard hard image recognition work will be done in the background, as work progresses the
     * UI will show the results as they become available.
     *
     * @param photo
     */
    public void identifyHeroesInPhoto(final Bitmap photo) {
        if (!presentersRegistered()) {
            throw new RuntimeException("Attempting to identify heroes before registering " +
                    "presenters.");
        }

        // Asks the main activity to show the "detecting heroes" loading screen
        mMainActivityPresenter.startHeroRecognitionLoadingAnimations(photo);

        mHeroesDetectedPresenter.reset();
        mAbilityInfoPresenter.reset();
        mCounterPickerPresenter.reset();

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
         *   of the heroes we have found in the photo (but not yet identified who they are)).
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
                List<HeroImageAndPosition>>() {
            @Override
            public List<HeroImageAndPosition> call(List<HeroInfo> heroInfoList,
                                                   SimilarityTest similarityTest) {
                return RecognitionModel.findFiveHeroesInPhoto(photo);
            }
        })
                .doOnNext(new Action1<List<HeroImageAndPosition>>() {
                    @Override
                    public void call(List<HeroImageAndPosition> heroImages) {
                        prepareToShowResults(heroImages);
                    }
                })
                .flatMapIterable(new Func1<List<HeroImageAndPosition>,
                        Iterable<HeroImageAndPosition>>() {
                    @Override
                    public Iterable<HeroImageAndPosition> call(List<HeroImageAndPosition> heroFromPhotos) {
                        return heroFromPhotos;
                    }
                })
                .map(new Func1<HeroImageAndPosition, SimilarityListAndPosition>() {
                    @Override
                    public SimilarityListAndPosition call(HeroImageAndPosition unidentifiedHero) {
                        return RecognitionModel.identifyHeroFromPhoto(unidentifiedHero,
                                mSimilarityTestRx.getValue());
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mHeroesDetectedPresenter.getHeroRecognitionSubscriberRx());
    }

    public List<HeroInfo> getHeroInfo() {
        return mXmlInfoRx.getValue();
    }

    public void sendUpdatedHeroList(List<HeroInfo> heroInfoList) {
        mAbilityInfoPresenter.showHeroAbilities(heroInfoList);
        showAdvantages(heroInfoList);
    }

    private void showAdvantages(List<HeroInfo> heroInfoList) {
        List<String> heroNames = new ArrayList<>();
        for(HeroInfo heroInfo : heroInfoList) {
            heroNames.add(heroInfo.name);
        }

        List<HeroAndAdvantages> heroes =
                SqlLoader.calculateAdvantages(mMainActivityPresenter.getContext(), heroNames);

        mCounterPickerPresenter.showAdvantages(heroes);
    }

    /**
     * Create an observable to load the xml file in the background. mXmlInfoRx is subscribed to
     * it and will complete when the file has been loaded.
     */
    private void startXmlLoading() {
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
//TODO-beauty: check if the XML parsing should be done on the io thread instead?
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mXmlInfoRx);
    }

    /**
     * Create an observable to load SimilarityTest (basically this loads a picture of each hero in
     * the game to be used in image recognition) in the background. mSimilarityTestRx is subscribed
     * to this observable and will complete when the file has been loaded.
     */
    private void startSimilarityTestLoading() {
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
     * This will make the MainActivity end the animations used show the hero image is being
     * processed. It also sends the unidentified heroes up to the HeroesDetectedPresenter so that
     * the photos of them can be shown.
     *
     * This method is safe to call from a background thread. RxJava is used here to ensure that the
     * required work in the UI this work will be done in the UI thread.
     *
     * @param heroImages the list of heroes images found in the photo, currently no work has been
     *                   done to identify who they are, these are just pictures of them with their
     *                   positions in the photo.
     */
    private void prepareToShowResults(List<HeroImageAndPosition> heroImages) {
        Single.just(heroImages)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<HeroImageAndPosition>>() {
                    public void call(List<HeroImageAndPosition> heroImages) {
                        mMainActivityPresenter.stopHeroRecognitionLoadingAnimations();
                        //TODO-now: make it only show counter picker if the photo is of the hero
                        // select screen
                        mMainActivityPresenter.showCounterPicker();
                        mHeroesDetectedPresenter.showHeroImages(heroImages);
                    }
                });
    }
}