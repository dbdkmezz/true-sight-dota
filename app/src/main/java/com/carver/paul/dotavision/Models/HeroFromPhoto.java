/**
 * True Sight for Dota 2
 * Copyright (C) 2016 Paul Broadbent
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */

package com.carver.paul.dotavision.Models;

import android.graphics.Bitmap;

import com.carver.paul.dotavision.ImageRecognition.ImageTools;
import com.carver.paul.dotavision.ImageRecognition.SimilarityTest;

import org.opencv.core.Mat;

import java.util.List;

//TODO-now: think about whether HeroFromPhoto should be in the models directory

/**
 * HeroFromPhoto is the image of a hero detected from a hero, and an ordered list of all the heroes
 * in dota sorted by how similar they are to the image from the photo.
 *
 * Each HeroFromPhoto has a positionInPhoto indicating where in the photo they were found, numbered
 * from left to right (normally 0 to 4).
 */
public class HeroFromPhoto {
    // The position in the photo this hero is, numbered from left to right. Usuually this will be a
    // number between 0 and 4.
    private final int mPositionInPhoto;

    private Mat mImage;
    private Bitmap mBitmap;

    // List of the heroes which are most similar to this hero in the photo, ordered by how simialr
    // they are, with the most similar first.
    private List<HeroAndSimilarity> mSimilarityList = null;

    public HeroFromPhoto(Mat image, int positionInPhoto) {
        mImage = image;
        mPositionInPhoto = positionInPhoto;
    }

    public int getPositionInPhoto() { return mPositionInPhoto; }

    public Bitmap getBitmap() {
        if(mBitmap == null) {
            mBitmap = ImageTools.GetBitmapFromMat(mImage);
        }
        return mBitmap;
    }

    /**
     * Creates a list of all the heroes, order by how similar they are to this photo
     *
     * This will take significant time to compute (around 200ms on a Nexus 5) so should not be done
     * in the UI thread.
     *
     * @param similarityTest
     */
    public void calcSimilarityList(SimilarityTest similarityTest) {
        if(mSimilarityList == null) {
            mSimilarityList = similarityTest.OrderedListOfTemplateSimilarHeroes(mImage);
        }
    }

    public List<HeroAndSimilarity> getSimilarityList() {
        if (mSimilarityList == null) {
            throw new RuntimeException("Can't get similarity list if not loaded!");
        }
        return mSimilarityList;
    }

    /**
     * Two HeroFromPhoto are identical iff they are from the same position in a photograph (each
     * hero in the photo is numbered from left to right, based on where in the photo they are).
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof HeroFromPhoto))
            return false;
        HeroFromPhoto otherHero = (HeroFromPhoto) o;
        return mPositionInPhoto == otherHero.getPositionInPhoto();
    }

    @Override
    public int hashCode() {
        return mPositionInPhoto;
    }
}