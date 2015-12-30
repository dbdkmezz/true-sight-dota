package com.carver.paul.dotavision;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.util.Log;

import com.carver.paul.dotavision.ImageRecognition.HeroFromPhoto;
import com.carver.paul.dotavision.ImageRecognition.Recognition;
import com.carver.paul.dotavision.ImageRecognition.SimilarityTest;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;

/**
 * This class does all the work in the UI thread showing that the image is being processed,
 * processes the image in a background thread to identify the five heroes, and then presents the
 * results in the UI
 */
class RecognitionWithRx {
    static final String TAG = "RecognitionRx";

    //TODO-someday: improve performance by saving mSimilarityTest mHeroInfoList once they have
    // been loaded the first time
    private SimilarityTest mSimilarityTest;
    private AsyncSubject<List<HeroInfo>> xmlInfo;

    RecognitionWithRx(final Context context) {
        StartBackgroundLoading(context);
    }

    public List<HeroInfo> getXmlInfo() {
        return xmlInfo.getValue();
    }

    /**
     * Reads the XML and opens the hero images in the background. This should be launched when the
     * app is first launched, and then everything should be ready when it is needed.
     */
    private void StartBackgroundLoading(final Context context) {
        xmlInfo = AsyncSubject.create();

        // Create an observable to load the xml and then subscribe the AsyncSubject xmlInfo to it
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(xmlInfo);
    }

    public void Run(final MainActivity mainActivity, final Bitmap photoBitmap) {
        mainActivity.preRecognitionUiTasks(photoBitmap);

        Observable.create(new Observable.OnSubscribe<List<HeroFromPhoto>>() {
            @Override
            public void call(Subscriber<? super List<HeroFromPhoto>> observer) {
                //TODO-beauty: handle errors in the observer
                if (!observer.isUnsubscribed()) {
                    List<HeroFromPhoto> heroes = backgroundWork(mainActivity, photoBitmap);
                    observer.onNext(heroes);
                    observer.onCompleted();
                }
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<HeroFromPhoto>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<HeroFromPhoto> heroes) {
                        mainActivity.postRecognitionUiTasks(heroes);
                    }
                });
    }


    // This is where the hard work happens which needs to be off the UI thread
    private List<HeroFromPhoto> backgroundWork(final Context context, Bitmap photoBitmap) {
        //NEED to add code to ensure that the xml has loaded

        if (mSimilarityTest == null)
            loadHistTest(context);

        // do the hard work of the image recognition
        return Recognition.Run(photoBitmap, mSimilarityTest);
    }

    private void loadHistTest(final Context context) {
        if (BuildConfig.DEBUG) Log.d(TAG, "Loading comparison images.");

        mSimilarityTest = new SimilarityTest(context);

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Loaded " + mSimilarityTest.NumberOfHeroesLoaded() + " hero images.");
        }
    }
}
