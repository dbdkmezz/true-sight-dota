/**
 * True Sight for Dota 2
 * Copyright (C) 2015 Paul Broadbent
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

package com.carver.paul.dotavision.ImageRecognition;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.List;

public class HeroFromPhoto {

    private static final double rationHeightToWidthbeforeCuts = 1.8;

    private static final double ratioToCutFromSide = 0.05;
    private static final double ratioToCutFromTop = 0.05;
    // ratioToCutFromBottom may need to be larger because a red box with MMR at the bottom may obscure the image
    private static final double ratioToCutFromBottom = 0.05;

    public Mat image;

    // List of the heroes which are most similar to this hero in the photo, ordered by how simialr
    // they are, with the most similar first.
    private List<HeroAndSimilarity> similarityList = null;

    /**
     * Uses the coloured line above a picture of a hero in the photo to create a new HeroFromPhoto
     *
     * @param line
     * @param backgroundImage
     */
    public HeroFromPhoto(HeroLine line, Mat backgroundImage) {

        if (line.isRealLine == false) {
            setupFakeHeroFromPhoto(backgroundImage);
            return;
        }

        double heightWithoutCuts = line.rect.width() / rationHeightToWidthbeforeCuts;
        int left = line.rect.left + (int) (line.rect.width() * ratioToCutFromSide);
        int width = line.rect.width() - (int) (line.rect.width() * 2 * ratioToCutFromSide);
        int top = line.rect.top + line.rect.height() + (int) (heightWithoutCuts * ratioToCutFromTop);
        int finalHeight = (int) heightWithoutCuts - (int) (heightWithoutCuts * ratioToCutFromBottom);

        Log.d("AA", "width " + width + ", height" + finalHeight);

        if (left + width > backgroundImage.width())
            width = backgroundImage.width() - left;
        if (top + finalHeight > backgroundImage.height())
            finalHeight = backgroundImage.height() - top;


        if (left < 0) left = 0;
        if (top < 0) top = 0;

        if (left > backgroundImage.width() || top > backgroundImage.height()) {
            setupFakeHeroFromPhoto(backgroundImage);
            return;
        }

        Rect rect = new Rect(left, top, width, finalHeight);
        image = new Mat(backgroundImage, rect);
    }

    public void calcSimilarityList(SimilarityTest similarityTest) {
        similarityList = similarityTest.OrderedListOfTemplateSimilarHeroes(image);
    }

    public List<HeroAndSimilarity> getSimilarityList() {
        if (similarityList == null)
            throw new RuntimeException("Can't get similarity list if not loaded!");
        return similarityList;
    }

    private void setupFakeHeroFromPhoto(Mat backgroundImage) {
        int width = 26;
        int height = 15;//(int) (width / rationHeightToWidthbeforeCuts);
        Rect rect = new Rect(0, 0, width, height);

        image = new Mat(backgroundImage, rect);
    }
}