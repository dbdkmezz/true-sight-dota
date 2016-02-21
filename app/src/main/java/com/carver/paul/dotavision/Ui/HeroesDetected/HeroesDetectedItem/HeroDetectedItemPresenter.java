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

package com.carver.paul.dotavision.Ui.HeroesDetected.HeroesDetectedItem;

import com.carver.paul.dotavision.Models.HeroAndSimilarity;
import com.carver.paul.dotavision.Models.HeroImageAndPosition;
import com.carver.paul.dotavision.Ui.HeroesDetected.HeroesDetectedPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class shows a hero which has been found in the image.
 *
 * For the hero we show:
 *
 *   1) The image of the hero we found in the photo.
 *
 *   2) The name of the hero (this is editable by the user to change the hero identified)
 *
 *   3) A horizontal RecyclerView showing all the images of the heroes in the game, in order of how
 *   similar we think they are to the image of the hero in the photo. The user can scroll through
 *   these to select a different hero.
 */
public class HeroDetectedItemPresenter {
    private final HeroesDetectedPresenter mParentPresenter;
    private HeroDetectedItemView mView;

    private int mPositionInPhoto;
    private String mName = "";
    private List<HeroAndSimilarity> mSimilarityList;

    public HeroDetectedItemPresenter(HeroDetectedItemView view,
                                     HeroesDetectedPresenter parentPresenter) {
        mView = view;
        mParentPresenter = parentPresenter;
    }

    public void setPhotoImage(HeroImageAndPosition heroImage) {
        mPositionInPhoto = heroImage.getPosition();
        mView.setHeroImage(heroImage.getImage());
    }

    public int getPositionInPhoto() {
        return mPositionInPhoto;
    }

    public void setSimilarityListAndName(List<HeroAndSimilarity> similarityList,
                                         String name) {
        mName = name;
        mSimilarityList = similarityList;

        List<Integer> similarHeroesImages = new ArrayList<>();
        for (HeroAndSimilarity similarHero : mSimilarityList) {
            similarHeroesImages.add(similarHero.hero.getImageResource());
        }

        mView.completeRecycler(similarHeroesImages);
        mView.setName(mName);
    }

    public String getName() {
        return mName;
    }

    public void setupTextAutoCompleteAndChangeListener(List<String> allHeroNames) {
        mView.setupTextAutoCompleteAndChangeListener(mName, allHeroNames);
    }

    /**
     * Used when the hero selected in the RecyclerView list of similar hero images has changed. This
     * will update the name accordingly and tell the parent presenter about the change to update
     * other views as needed.
     *
     * @param posInSimilarityList the position in the similarity list of the hero we have changed to
     */
    protected void updateFromSimilarityListChange(int posInSimilarityList) {
        String heroImageName = mSimilarityList.get(posInSimilarityList).hero.name;
        mName = mParentPresenter.getHeroRealName(heroImageName);
        mView.setName(mName);
        mParentPresenter.sendUpdatedHeroList();
    }

    /**
     * Used when the name of the hero has been changed by the user. This will update the name
     * RecyclerView showing images of similar heroes and tell the parent presenter about the change
     * to update other views as needed.
     *
     * @param newHeroName
     */
    protected void updateFromNameChange(String newHeroName) {
        if(mName.equals(newHeroName)) return;

        mName = newHeroName;
        String imageName = mParentPresenter.getHeroImageName(newHeroName);

        if(mView.recyclerInitialised()) {
            for (int i = 0; i < mSimilarityList.size(); i++) {
                if (mSimilarityList.get(i).equals(imageName)) {
                    mView.setHeroInRecycler(i);
                    break;
                }
            }
        }

        mParentPresenter.hideKeyboard();
        mParentPresenter.sendUpdatedHeroList();
    }
}
