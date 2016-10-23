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

package com.carver.paul.truesight.Ui.CounterPicker;

import android.util.Log;
import android.util.Pair;

import com.carver.paul.truesight.ImageRecognition.ImageTools;
import com.carver.paul.truesight.Models.HeroAndAdvantages;
import com.carver.paul.truesight.Models.HeroInfo;
import com.carver.paul.truesight.Models.IInfoPresenter_Data;
import com.carver.paul.truesight.R;
import com.carver.paul.truesight.Ui.IInfoPresenter_P;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;

public class CounterPickerPresenter implements IInfoPresenter_Data, IInfoPresenter_P {
    private static final String TAG = "CounterPickerPresenter";
    private static final int MAX_COUNTERS_TO_SHOW = 2005;

    private CounterPickerFragment mView;
    private int mRoleFilter = R.string.all_roles;
    private List<HeroAndAdvantages> mHeroesAndAdvantages = new ArrayList<>();
    private List<HeroInfo> mEnemyHeroes = new ArrayList<>();
    private Subscriber<HeroAndAdvantages> mRowAdderRx;

    CounterPickerPresenter(CounterPickerFragment view) {
        mView = view;
    }

    public void setEnemyHeroes(List<HeroInfo> enemyHeroes) {
        mEnemyHeroes = enemyHeroes;
    }

    public void setAdvantageData(List<HeroAndAdvantages> heroesAndAdvantages) {
        mHeroesAndAdvantages = heroesAndAdvantages;
        removeAllRows();
        mView.endLoadingAnimation();
    }

    public void reset() {
        unsubscribeRowAddingSubscriber();
        mView.reset();
    }

    public void hide() {
        mView.hide();
    }

    public void show() {
        mView.show();
    }

    public void prepareForFreshList() {
        mView.startLoadingAnimation();
    }

    public void removeAllRows() {
        unsubscribeRowAddingSubscriber();
        //mView.removeAllRows();
        mView.reset();
    }

    protected void setRoleFilter(int roleFilter) {
        if (roleFilter == mRoleFilter) {
            return;
        } else {
            mRoleFilter = roleFilter;
            removeAllRows();
            showHeadings();
            showAdvantages();
        }
    }

    protected void loadingAnimationFinished() {
        removeAllRows();
        showHeadings();
        showAdvantages();
    }

    private void showHeadings() {
        List<Integer> imageIds = new ArrayList<>();
        for (HeroInfo hero : mEnemyHeroes) {
            imageIds.add(ImageTools.getResIdForHeroImage(hero.imageName));
        }
        mView.showHeadings(imageIds);
    }

    /**
     * Shows a row for each hero selected of the role selected in the spinner mRoleFilter with
     * the advantage it has against the five heroes in the photo.
     *
     * A little RxJava is used here to only show one row every 10 milliseconds, this is to prevent
     * the UI from locking up (which would happen if we attempt to add all ~120 rows at once).
     */
    private void showAdvantages() {

        // Filters out the heroes which don't have the role specified in the
        // spinner filter.
        List<HeroAndAdvantages> rowsToShow = new ArrayList<>();

        for (HeroAndAdvantages hero : mHeroesAndAdvantages) {
            if (shouldShowHero(hero)) {
                rowsToShow.add(hero);
                if (rowsToShow.size() >= MAX_COUNTERS_TO_SHOW) {
                    break;
                }
            }
        }

        setupRowAddingSubscriber();

        //TODO-someday: remove the need for the interval observerable when adding rows

        // Show a new row every 10 milliseconds (if we attempt to show them all at once then the UI
        // locks up because adding 120 rows at once takes too long!)
        // (onBackpressureDrop is needed in case the intervals are coming too quick to handle)
        Observable.interval(20, TimeUnit.MILLISECONDS)
                .onBackpressureDrop()
                .zipWith(rowsToShow, new Func2<Long, HeroAndAdvantages, HeroAndAdvantages>() {
                    @Override
                    public HeroAndAdvantages call(Long count, HeroAndAdvantages heroAndAdvantages) {
                        return heroAndAdvantages;
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mRowAdderRx);
    }

    private boolean shouldShowHero(HeroAndAdvantages hero) {
        for(HeroInfo enemy : mEnemyHeroes) {
            if(hero.getName().equals(enemy.name)) {
                return false;
            }
        }

        // implement the role selection made in the spinner
        switch (mRoleFilter) {
            case R.string.all_roles:
                return true;
            case R.string.carry_role:
                return hero.isCarry();
            case R.string.support_role:
                return hero.isSupport();
            case R.string.mid_role:
                return hero.isMid();
            case R.string.roaming_role:
                return hero.isRoaming();
            case R.string.off_lane_role:
                return hero.isOffLane();
            case R.string.jungler_role:
                return hero.isJunger();
        }

        Log.e(TAG, "mRoleFilter has invalid value.");
        return true;
    }

    private void setupRowAddingSubscriber() {
        unsubscribeRowAddingSubscriber();

        mRowAdderRx = new Subscriber<HeroAndAdvantages>() {
            @Override
            public void onCompleted() {
                mView.resetMinimumHeight();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Interval observable. Unhandled error: " + e.toString());
            }

            @Override
            public void onNext(HeroAndAdvantages hero) {
                addRow(hero);
            }
        };
    }

    private void unsubscribeRowAddingSubscriber() {
        if (mRowAdderRx != null) {
            mRowAdderRx.unsubscribe();
        }
    }

    private void addRow(HeroAndAdvantages hero) {
        List<Pair<String, Boolean>> advantages = new ArrayList<>();

        for(Double advantage : hero.getAdvantages()) {
            String string;
            Boolean boldText = false;

            if(advantage == HeroAndAdvantages.NEUTRAL_ADVANTAGE) {
                string = "  - ";
            } else {
                string = String.format(Locale.US, "%.1f", advantage);
                // add an empty space to offset the lack of a minus sign
                if(advantage >= 0 && advantage < 10) {
                    string = " " + string;
                }
            }

            if(advantage > 1) {
                boldText = true;
            }

            advantages.add(new Pair<>(string, boldText));
        }
        mView.addRow(hero.getName(), advantages, String.format(Locale.US, "%.1f", hero.getTotalAdvantage()));
    }
}