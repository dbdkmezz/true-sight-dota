/**
 * True Sight for Dota 2
 * Copyright (C) 2016 Paul Broadbent
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

package com.carver.paul.dotavision.Models;

import java.util.List;

/**
 * Contains a list of heroes which are similar to a picture of a hero.
 */
public class SimilarityListAndPosition {
    private final int mPosition;
    private final List<HeroAndSimilarity> mSimilarityList;

    /**
     * Contains a list of heroes which are similar to a picture of a hero.
     * @param similarityList    A list of heroes which are similar to the hero at this position,
     *                          ordered by how similar to it they are.
     * @param position  A number between 0 and 4, giving the position of the image in the photo
     *                  (counting from left to right).
     */
    public SimilarityListAndPosition(List<HeroAndSimilarity> similarityList, int position) {
        if (position < 0 || position > 4) {
            throw new RuntimeException("Attempting to create a SimilarityListAndPosition with an " +
                    "invalid position");
        }
        mPosition = position;
        mSimilarityList = similarityList;
    }

    public int getPosition() {
        return mPosition;
    }

    public List<HeroAndSimilarity> getSimilarityList() {
        return mSimilarityList;
    }
}
