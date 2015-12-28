package com.carver.paul.dotavision;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.util.Log;

import com.carver.paul.dotavision.ImageRecognition.HeroFromPhoto;
import com.carver.paul.dotavision.ImageRecognition.Recognition;
import com.carver.paul.dotavision.ImageRecognition.SimilarityTest;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    private List<HeroInfo> mHeroInfoList;

    public void Run(final MainActivity mainActivity, final Bitmap photoBitmap, final List<HeroInfo> heroInfoList) {
        mHeroInfoList = heroInfoList;
        mainActivity.preRecognitionUiTasks(photoBitmap);

        Observable.create(new Observable.OnSubscribe<List<HeroFromPhoto>>() {
            @Override
            public void call(Subscriber<? super List<HeroFromPhoto>> observer) {
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
        if (mHeroInfoList == null)
            throw new RuntimeException("mHeroInfoFromXml has not been instantiated as a list.");

        if (mHeroInfoList.isEmpty())
            LoadXML(context);

        if (mSimilarityTest == null)
            loadHistTest(context);

        // do the hard work of the image recognition
        return Recognition.Run(photoBitmap, mSimilarityTest);
    }


    private void LoadXML(final Context context) {
        XmlResourceParser parser = context.getResources().getXml(R.xml.hero_info_from_web);
        LoadHeroXml.Load(parser, mHeroInfoList);
//            AddBlankHeroImage();
    }

    private void loadHistTest(final Context context) {
        if (BuildConfig.DEBUG) Log.d(TAG, "Loading comparison images.");

        mSimilarityTest = new SimilarityTest(context);

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Loaded " + mSimilarityTest.NumberOfHeroesLoaded() + " hero images.");
        }
    }
}
