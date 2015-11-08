package com.carver.paul.dotavision.ImageRecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import static org.opencv.android.Utils.bitmapToMat;

/**
 * Created by paul on 25/10/15.
 */
/*
TODO: Give HeroWithHist a sensible name now that I don't use histograms!
*/

public class HeroWithHist {
    // protected Mat histogram;
    public Mat image;
    public String name;

/*    public HeroWithHist(String path, String filename) {

        Bitmap bitmap = BitmapFactory.decodeFile(path);
        image = new Mat();
        bitmapToMat(bitmap, image);
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2BGRA);

        name = filename.substring(0, filename.indexOf("_hphover"));
    }*/

    public HeroWithHist(int drawableId, String name, Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        image = ImageTools.GetMatFromBitmap(bitmap);
/*        image = new Mat();
        bitmapToMat(bitmap, image);
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2BGRA);*/

        this.name = name;
    }

    public HeroWithHist(String name) {
        this.name = name;
    }
/*    protected HeroWithHist(String heroName, String fileName) {
        this.heroName = heroName;
        Mat load = Imgcodecs.imread(fileName);

        histogram = Histogram.CreateHueHistogram(load);
        Imgcodecs.imwrite(Main.imagesLoc + "/Hists/" + heroName + "hist.jpg", Histogram.DrawNewHueHistogram(histogram));
    }*/
}
