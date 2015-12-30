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
import rx.Single;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
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

    //TODO-someday: improve performance by saving mSimilarityTest mHeroInfoList once they have
    // been loaded the first time
//    private SimilarityTest mSimilarityTest;
    private AsyncSubject<List<HeroInfo>> mXmlInfoRx;
    private AsyncSubject<SimilarityTest> mSimilarityTestRx;

    RecognitionWithRx(final Context context) {
        StartXmlLoading(context);
        StartSimilarityTestLoading(context);
    }

    public List<HeroInfo> getXmlInfo() {
        return mXmlInfoRx.getValue();
    }

    /**
     * Reads the XML and opens the hero images in the background. This should be launched when the
     * app is first launched, and then everything should be ready when it is needed.
     */
    private void StartXmlLoading(final Context context) {
        mXmlInfoRx = AsyncSubject.create();

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
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mXmlInfoRx);
    }

    private void StartSimilarityTestLoading(final Context context) {
        mSimilarityTestRx = AsyncSubject.create();

        Observable.create(new Observable.OnSubscribe<SimilarityTest>() {
            @Override
            public void call(Subscriber<? super SimilarityTest> observer) {
                observer.onNext(new SimilarityTest(context));
                observer.onCompleted();
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mSimilarityTestRx);
    }

    public void Run(final MainActivity mainActivity, final Bitmap photoBitmap) {
        mainActivity.preRecognitionUiTasks(photoBitmap);

        //TODO: this is created every time this is run. Memory leak danger???
        //Should photo bitmap be in there?

        // Zipping the two observables used in the loading task will ensure that both the xml and
        // the similarity test have loaded successfully before attempting to do tje image
        // recognition
        Observable.zip(mXmlInfoRx, mSimilarityTestRx, new Func2<List<HeroInfo>, SimilarityTest, List<HeroFromPhoto>>() {
            @Override
            public List<HeroFromPhoto> call(List<HeroInfo> heroInfoList, SimilarityTest similarityTest) {
                return Recognition.Run(photoBitmap, similarityTest);
            }
        })
                .subscribeOn(Schedulers.computation())
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
}
