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