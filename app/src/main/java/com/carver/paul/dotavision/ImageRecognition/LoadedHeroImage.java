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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.core.Mat;

/**
 * The image of a hero loaded from the appropriate drawable
 */

public class LoadedHeroImage {
    public Mat mat;
    public String name;

    private int mDrawableId;

    public LoadedHeroImage(Context context, int drawableId, String name) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        mat = ImageTools.GetMatFromBitmap(bitmap);
        this.name = name;
        mDrawableId = drawableId;
    }

    public Bitmap getBitmap(Context context) {
        return BitmapFactory.decodeResource(context.getResources(), mDrawableId);
    }
}