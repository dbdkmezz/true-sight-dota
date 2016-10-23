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

package com.carver.paul.truesight.Models;

import android.graphics.Bitmap;

import com.carver.paul.truesight.ImageRecognition.ImageTools;

import org.opencv.core.Mat;

/**
 * HeroImageAndPosition contains an image of a hero taken from the photo and the position in
 * the photo of the image.
 */
public class HeroImageAndPosition {
    private final Bitmap mImage;
    private final int mPosition;

    /**
     * HeroImageAndPosition contains an image of a hero taken from the photo and the position in
     * the photo of the image.
     * @param image
     * @param position  A number between 0 and 4, giving the position of the image in the photo
     *                  (counting from left to right).
     */
    public HeroImageAndPosition(Mat image, int position) {
        if (position < 0 || position > 4) {
            throw new RuntimeException("Attempting to create a HeroImageAndPosition with an " +
                    "invalid position");
        }
        mImage = ImageTools.GetBitmapFromMat(image);
        mPosition = position;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public int getPosition() {
        return mPosition;
    }
}
