/**
 * True Sight for Dota 2
 * Copyright (C) 2015 Paul Broadbent
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

package com.carver.paul.truesight.ImageRecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.carver.paul.truesight.R;

import org.opencv.core.Mat;

/**
 * The image of a hero loaded from the appropriate drawable
 */

public class LoadedHeroImage {
    public Mat mat;
    public Mat comparisonMat;
    public String name;

    private int mDrawableId;

    public LoadedHeroImage(Context context, int drawableId, String name) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        mat = ImageTools.GetMatFromBitmap(bitmap);
        this.name = name;
        mDrawableId = drawableId;
    }

    public int getImageResource() {
        return mDrawableId;
    }

    public static LoadedHeroImage newMissingHero() {
        LoadedHeroImage missingHero = new LoadedHeroImage();
        missingHero.mat = null;
        missingHero.name = "";
        missingHero.mDrawableId = R.drawable.missing_hero;
        return missingHero;
    }

    /**
     * private constructor for creating missing heroes
     */
    private LoadedHeroImage() {}
}