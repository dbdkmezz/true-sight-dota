package com.carver.paul.dotavision;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.support.v4.util.Pair;
import android.util.Log;

import com.carver.paul.dotavision.ImageRecognition.HeroFromPhoto;
import com.carver.paul.dotavision.ImageRecognition.Recognition;
import com.carver.paul.dotavision.ImageRecognition.SimilarityTest;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Single;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;

/**
 * This class does all the work in the UI thread showing that the image is being processed,
 * processes the image in a background thread to identify the five heroes, and then presents the
 * results in the UI
 */
class RecognitionWithRx {
    static final String TAG = "RecognitionRx";

    private AsyncSubject<List<HeroInfo>> mXmlInfoRx;
    private AsyncSubject<SimilarityTest> mSimilarityTestRx;
    private Subscriber<HeroFromPhoto> mHeroRecognitionSubscriberRx;

    /**
     * Reads the XML and opens the hero images in the background. This should be launched when the
     * app is first launched, and then everything will be ready when it is needed.
     */
    RecognitionWithRx(final Context context) {
        StartXmlLoading(context);
        StartSimilarityTestLoading(context);
    }

    public List<HeroInfo> getXmlInfo() {
        return mXmlInfoRx.getValue();
    }

    /**
     *Create an observable to load the xml file in the background. mXmlInfoRx is subscribed to
     * it and will complete when the file has been loaded.
     * @param context
     */
    private void StartXmlLoading(final Context context) {
        mXmlInfoRx = AsyncSubject.create();

        Observable.create(new Observable.OnSubscribe<List<HeroInfo>>() {
            @Override
            public void call(Subscriber<? super List<HeroInfo>> observer) {
                XmlResourceParser parser = context.getResources().getXml(R.xml.hero_info_from_web);
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
     * @param context
     */
    private void StartSimilarityTestLoading(final Context context) {
        mSimilarityTestRx = AsyncSubject.create();

        Observable.create(new Observable.OnSubscribe<SimilarityTest>() {
            @Override
            public void call(Subscriber<? super SimilarityTest> subscriber) {
                subscriber.onNext(new SimilarityTest(context));
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mSimilarityTestRx);
    }

    /**
     * When doing the work recognising the heroes in a photo there are four methods in MainActivity
     * that should be called. These are named recognition1_ through to recognition4_.
     *
     * This method does the image recognition work in a background thread, and when necessary calls
     * the appropriate methods on MainActivity to show the progress to the user.
     * @param mainActivity
     * @param photo
     */
    public void Run(final MainActivity mainActivity, final Bitmap photo) {
        prepareHeroRecognitionSubscriber(mainActivity);

        mainActivity.recognition1ShowDetectingHeroes(photo);

        /**
         * This is where the magic happens! Recognising the heroes in the photos goes through the
         * following steps:
         *
         *   1) zip the AsyncSubjects for loading the xml file and the similarityTest (the pictures
         *   against which we compare what we see in the photograph) to ensure both have loaded
         *   before going any further.
         *
         *   2) doOnNext: call prepareToShowResults, which gets the mainActivity UI ready to start
         *   showing the results of the image processing
         *
         *   3) flatMapIterable: turn the list of unidentified heroes in the photo into chain of
         *   Observables for each to be processed.
         *
         *   4) identify each hero found in the photo
         *
         *   5) send the identified heroes to the mHeroRecognitionSubscriberRx subscriber (which
         *   will in turn call the appropriate MainActivity methods to show the results to the user.
         */
        Observable.zip(mXmlInfoRx, mSimilarityTestRx, new Func2<List<HeroInfo>, SimilarityTest,
                List<HeroFromPhoto>>() {
            @Override
            public List<HeroFromPhoto> call(List<HeroInfo> heroInfoList,
                                            SimilarityTest similarityTest) {
                return Recognition.findFiveHeroesInPhoto(photo);
            }
        })
                .doOnNext(new Action1<List<HeroFromPhoto>>() {
            @Override
                    public void call(List<HeroFromPhoto> unidentifiedHeroes) {
                prepareToShowResults(mainActivity, unidentifiedHeroes);
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
                        return Recognition.identifyHeroFromPhoto(unidentifiedHero,
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
     * calls the method recognition2prepareToShowResults on mainActivity in the mainThread (i.e.
     * the UI thread).
     *
     * @param mainActivity
     * @param unidentifiedHeroes
     */
    private void prepareToShowResults(final MainActivity mainActivity, List<HeroFromPhoto> unidentifiedHeroes) {
        Single.just(unidentifiedHeroes)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<HeroFromPhoto>>() {
                    public void call(List<HeroFromPhoto> unidentifiedHeroes) {
                        mainActivity.recognition2prepareToShowResults(unidentifiedHeroes);
                    }
                });
    }

    /**
     * Sets up mHeroRecognitionSubscriberRx
     * @param mainActivity
     */
    private void prepareHeroRecognitionSubscriber(final MainActivity mainActivity) {
        ensureAllSubscribersUnsubscribed();

        // Set up the subscriber
        mHeroRecognitionSubscriberRx = new Subscriber<HeroFromPhoto>() {
            // Finish off showing the results of the image processing.
            @Override
            public void onCompleted() {
                mainActivity.recognition4ShowHeroAbilities();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "mHeroRecognitionSubscriberRx. Unhandled error: " + e.toString());

            }

            // For each hero identified in the photo, get MainActivity to show it
            @Override
            public void onNext(HeroFromPhoto hero) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Adding " + hero.getSimilarityList().get(0).hero.name);
                }
                mainActivity.recognition3AddHero(hero);
            }
        };
    }

    private void ensureAllSubscribersUnsubscribed() {
        //TODO-beauty: test if we actually need to unsubscribe from the observer, and if
        // unsubscribing like this does clear it from memory
        if(mHeroRecognitionSubscriberRx != null) {
            mHeroRecognitionSubscriberRx.unsubscribe();
            mHeroRecognitionSubscriberRx = null;
        }
    }
}
