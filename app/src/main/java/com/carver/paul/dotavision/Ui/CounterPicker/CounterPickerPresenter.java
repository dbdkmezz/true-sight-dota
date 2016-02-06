/**
 * True Sight for Dota 2
 * Copyright (C) 2015 Paul Broadbent
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

package com.carver.paul.dotavision.Ui.CounterPicker;

import android.util.Log;

import com.carver.paul.dotavision.ImageRecognition.ImageTools;
import com.carver.paul.dotavision.Models.HeroAndAdvantages;
import com.carver.paul.dotavision.Models.HeroInfo;
import com.carver.paul.dotavision.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;

public class CounterPickerPresenter {
    private static final String TAG = "CounterPickerPresenter";

    private CounterPickerFragment mView;
    private int mRoleFilter = R.string.all_roles;
    private List<HeroAndAdvantages> mHeroesAndAdvantages = new ArrayList<>();
    private List<HeroInfo> mEnemyHeroes = new ArrayList<>();

    CounterPickerPresenter(CounterPickerFragment view) {
        mView = view;
    }

    public void showAdvantages(List<HeroAndAdvantages> heroesAndAdvantages,
                               List<HeroInfo> enemyHeroes) {
        mHeroesAndAdvantages = heroesAndAdvantages;
        mEnemyHeroes = enemyHeroes;
        reset();
        mView.endLoadingAnimation();
    }

    public void reset() {
        mView.reset();
    }

    public void hide() {
        mView.hide();
    }

    public void show() {
        mView.show();
    }

    public void startLoadingAnimation() {
        mView.startLoadingAnimation();
    }

    protected void setRoleFilter(int roleFilter) {
        if (roleFilter == mRoleFilter) {
            return;
        } else {
            mRoleFilter = roleFilter;
            reset();
            showHeadings();
            showAdvantages();
        }
    }

    protected void loadingAnimationFinished() {
        showHeadings();
        showAdvantages();
    }

    private void showHeadings() {
        List<Integer> imageIds = new ArrayList<>();
        for (HeroInfo hero : mEnemyHeroes) {
            imageIds.add(ImageTools.getDrawableForHero(hero.imageName));
        }
        mView.showHeadings(imageIds);
    }

    private void showAdvantages() {
        //Create a observable that filters out the in appropriate roles
        Observable<HeroAndAdvantages> rowsToShowRx = Observable.from(mHeroesAndAdvantages)
                .filter(new Func1<HeroAndAdvantages, Boolean>() {
                    @Override
                    public Boolean call(HeroAndAdvantages hero) {
                        switch (mRoleFilter) {
                            case R.string.all_roles:
                                return true;
                            case R.string.carry_role:
                                return hero.isCarry();
                            case R.string.support_role:
                                return hero.isSupport();
                            case R.string.mid_role:
                                return hero.isMid();
                        }
                        Log.e(TAG, "mRoleFilter has invalid value.");
                        return true;
                    }
                });

        // Show a new row every 10 milliseconds (if we attempt to show them all at once then the UI
        // locks up because adding 120 rows at once takes too long!)
        Observable.interval(10, TimeUnit.MILLISECONDS)
                .zipWith(rowsToShowRx, new Func2<Long, HeroAndAdvantages, HeroAndAdvantages>() {
                    @Override
                    public HeroAndAdvantages call(Long aLong, HeroAndAdvantages heroAndAdvantages) {
                        return heroAndAdvantages;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HeroAndAdvantages>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(HeroAndAdvantages hero) {
                        mView.addRow(hero.getName(), hero.getAdvantages(), hero.getTotalAdvantage());
                    }
                });
    }
}