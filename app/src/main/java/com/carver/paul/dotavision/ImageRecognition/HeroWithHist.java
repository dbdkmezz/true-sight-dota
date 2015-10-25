package com.carver.paul.dotavision.ImageRecognition;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

    public HeroWithHist(String path, String filename) {
        //image = Imgcodecs.imread(path);

        Bitmap bitmap = BitmapFactory.decodeFile(path);
        image = new Mat();
        bitmapToMat(bitmap, image);
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2BGRA);

        name = filename.substring(0, filename.indexOf("_hphover"));
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
