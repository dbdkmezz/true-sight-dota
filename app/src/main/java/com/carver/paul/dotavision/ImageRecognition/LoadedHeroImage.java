package com.carver.paul.dotavision.ImageRecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.core.Mat;

/**
 * The image of a hero loaded from the appropriate drawable
 */
public class LoadedHeroImage {
    public Mat image;
    public String name;

    public LoadedHeroImage(Context context, int drawableId, String name) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        image = ImageTools.GetMatFromBitmap(bitmap);
        this.name = name;
    }
}