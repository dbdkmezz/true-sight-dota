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

package com.carver.paul.dotavision.Presenters;

import android.util.Log;

import com.carver.paul.dotavision.BuildConfig;
import com.carver.paul.dotavision.Models.DataManager;
import com.carver.paul.dotavision.Models.HeroFromPhoto;
import com.carver.paul.dotavision.Models.HeroInfo;
import com.carver.paul.dotavision.Views.HeroesDetectedFragment;

import java.util.ArrayList;
import java.util.List;

public class HeroesDetectedPresenter {
    private static final String TAG = "HeroesDetectedPresenter";

    private HeroesDetectedFragment mView;
    private DataManager mDataManger;
    private List<HeroDetectedItemPresenter> mHeroDetectedItemPresenters = new ArrayList<>();
    private List<String> mAllHeroNames;

    public HeroesDetectedPresenter(HeroesDetectedFragment view) {
        mView = view;
    }

    public void reset() {
        mHeroDetectedItemPresenters.clear();
        mView.removeAllViews();
    }

    public void setDataManger(DataManager dataManger) {
        mDataManger = dataManger;
    }

    public void prepareToShowResults(List<HeroFromPhoto> heroes,
                                     List<HeroInfo> heroInfoFromXml) {
        mHeroDetectedItemPresenters = mView.createHeroDetectedViews(heroes.size());

        if(mAllHeroNames == null) {
            mAllHeroNames = getHeroNames(heroInfoFromXml);
        }

        for(int i = 0; i < mHeroDetectedItemPresenters.size() && i < heroes.size(); i++) {
            mHeroDetectedItemPresenters.get(i).completeSetup(this, heroes.get(i));
        }
    }

    public void heroIdentified(int positionInPhoto, String name) {
        if(mAllHeroNames == null) {
            throw new RuntimeException("Attempting to disaply an identified hero without having " +
                    "set up the hero names");
        }

        HeroDetectedItemPresenter heroDetected = HeroDetectedItemPresenterWithPosition(positionInPhoto);
        heroDetected.showDetectedHero(mAllHeroNames, name);
    }

    //TODO-now: is receiveHeroChangedReport really a good way to do it. Would some RX work better?
    public void receiveHeroChangedReport(int posInPhotoOfChangedHero,
                                         int posInSimilarityList) {
        mDataManger.receiveHeroChangedReport(posInPhotoOfChangedHero, posInSimilarityList);
    }

    public void receiveHeroChangedReport(int posInPhotoOfChangedHero,
                                         String newHeroName) {
        mDataManger.receiveHeroChangedReport(posInPhotoOfChangedHero, newHeroName);
    }

    /**
     * Changes the hero which is positionInPhoto of those visible. niceHeroName specifies the name of the
     * hero to display in the box. heroImageName is the name of the hero's image, for use when
     * scrolling to the hero with the recyclerView.
     * @param positionInPhoto
     */
    public void changeHero(int positionInPhoto, String name, int posInSimilarityList) {
        if(mHeroDetectedItemPresenters.isEmpty()) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Attempting to change hero, but mHeroDetectedItemPresenters is empty.");
            }
            return;
        }

        HeroDetectedItemPresenter hero = HeroDetectedItemPresenterWithPosition(positionInPhoto);
        hero.changeHero(name, posInSimilarityList);

        mView.hideKeyboard();
    }

    private HeroDetectedItemPresenter HeroDetectedItemPresenterWithPosition(int positionInPhoto) {
        for(HeroDetectedItemPresenter heroPresenter : mHeroDetectedItemPresenters) {
            if(heroPresenter.getPositionInPhoto() == positionInPhoto) {
                return heroPresenter;
            }
        }

        throw new RuntimeException("Can't find presenter for hero in photo");
    }

    private static List<String> getHeroNames(List<HeroInfo> heroInfoFromXml) {
        List<String> names = new ArrayList<>();
        for(HeroInfo heroInfo : heroInfoFromXml) {
            names.add(heroInfo.name);
        }
        return names;
    }
}
