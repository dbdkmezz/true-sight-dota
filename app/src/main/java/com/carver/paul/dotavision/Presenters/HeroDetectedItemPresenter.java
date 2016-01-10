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

import com.carver.paul.dotavision.Models.HeroFromPhoto;
import com.carver.paul.dotavision.Views.HeroDetectedItemView;

import java.util.List;

public class HeroDetectedItemPresenter {
    private HeroesDetectedPresenter mParentPresenter;
    private HeroDetectedItemView mView;
    private HeroFromPhoto mHero;

    public HeroDetectedItemPresenter(HeroDetectedItemView view) {
        mView = view;
    }

    public void completeSetup(HeroesDetectedPresenter parentPresenter,
                              HeroFromPhoto hero,
                              List<String> allHeroNames) {
        mParentPresenter = parentPresenter;
        mHero = hero;
        mView.setHeroImageFromPhoto(hero.getBitmap());
        mView.initialiseHeroNameEditText(allHeroNames);
    }

    public int getPositionInPhoto() {
        return mHero.getPositionInPhoto();
    }

    public void showDetectedHero(String name) {
        mView.completeRecycler(mHero);
        //TODO-now: fix hero names
        mView.setName(name);
    }

    public void changeHero(String niceHeroName, String heroImageName) {
        mView.setName(niceHeroName);
        mView.setHeroInRecycler(heroImageName);
    }

    /**
     * The hero selected has changed to the one which is posInSimilarityList in the Similarity list
     * @param posInSimilarityList
     */
    public void reportHeroChange(int posInSimilarityList) {
        mParentPresenter.receiveHeroChangedReport(mHero.getPositionInPhoto(), posInSimilarityList);
    }
}
