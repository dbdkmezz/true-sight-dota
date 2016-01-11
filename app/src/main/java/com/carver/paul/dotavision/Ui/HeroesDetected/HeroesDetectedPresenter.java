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

package com.carver.paul.dotavision.Ui.HeroesDetected;

import android.util.Log;

import com.carver.paul.dotavision.BuildConfig;
import com.carver.paul.dotavision.Models.DataManager;
import com.carver.paul.dotavision.Models.HeroFromPhoto;
import com.carver.paul.dotavision.Models.HeroInfo;
import com.carver.paul.dotavision.Ui.HeroesDetected.HeroesDetectedItem.HeroDetectedItemPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class shows the heroes which have been found in the image.
 *
 * For each of them we show:
 *
 *   1) The image of the hero we found in the photo.
 *
 *   2) The name of the hero (this is editable by the user to change the hero identified)
 *
 *   3) A horizontal RecyclerView showing all the images of the heroes in the game, in order of how
 *   similar we think they are to the image of the hero in the photo. The user can scroll through
 *   these to select a different hero.
 *
 *   These is a HeroDetectedItem for each hero.
 */
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

    //TODO-now: is receiveHeroChangedReport really a good way to do it. Would some RX work better?
    public void receiveHeroChangedReport(int posInPhotoOfChangedHero,
                                            int posInSimilarityList) {
        mDataManger.receiveHeroChangedReport(posInPhotoOfChangedHero, posInSimilarityList);
    }

    public void receiveHeroChangedReport(int posInPhotoOfChangedHero,
                                            String newHeroName) {
        mDataManger.receiveHeroChangedReport(posInPhotoOfChangedHero, newHeroName);
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
