package com.carver.paul.dotavision.ImageRecognition.Debug;

import android.graphics.Bitmap;

import com.carver.paul.dotavision.ImageRecognition.HeroFromPhoto;
import com.carver.paul.dotavision.ImageRecognition.ImageTools;
import com.carver.paul.dotavision.ImageRecognition.Recognition;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.util.Arrays;
import java.util.List;

public class DebugLineDetection {
    public static Bitmap TestMask(Bitmap bitmap, int hMin, int hMax, int sMin, int sMax, int vMin, int vMax) { //, ImageView imageViewLines) {

        Mat load = ImageTools.GetMatFromBitmap(bitmap);

        Scalar lowerHsv = new Scalar(hMin, sMin, vMin);
        Scalar upperHsv = new Scalar(hMax, sMax, vMax);

        ImageTools.MaskAColourFromImage(load, lowerHsv, upperHsv, load);

        return ImageTools.GetBitmapFromMat(load, false);

/*        List<Mat> linesList = HeroFromPhoto.findHeroTopLinesInImage(load, Variables.sRange.get(0), Variables.vRange.get(0), Variables.sRange.get(1), Variables.vRange.get(1));

        for (Mat lines : linesList) {
            ImageTools.drawLinesOnImage(lines, load);
        }

        Bitmap linesBitmap = Bitmap.createBitmap(load.cols(), load.rows(), Bitmap.Config.ARGB_8888);
        matToBitmap(load, linesBitmap);
        imageViewLines.setImageBitmap(linesBitmap);*/
    }



/*    public static Bitmap TestLines(Bitmap bitmap) {
        Mat load = ImageTools.GetMatFromBitmap(bitmap);

        List<Mat> linesList = HeroFromPhoto.findHeroTopLinesInImage(load, Variables.sRange.get(0), Variables.vRange.get(0), Variables.sRange.get(1), Variables.vRange.get(1));

        for (Mat lines : linesList) {
            ImageTools.drawLinesOnImage(lines, load);
        }

        return ImageTools.GetBitmapFromMat(load, false);
    }*/

    public static Bitmap TestLines(Bitmap bitmap, int hMin, int hMax, int sMin, int sMax, int vMin, int vMax) {
        Mat load = ImageTools.GetMatFromBitmap(bitmap);

//        List<Integer> colourRange = Arrays.asList(hMin, hMax);
        List<List<Integer>> colourRanges = Arrays.asList(Arrays.asList(hMin, hMax));

        List<Mat> linesList = Recognition.findHeroTopLinesInImage(load, colourRanges, sMin, vMin, sMax, vMax);

        for (Mat lines : linesList) {
            ImageTools.drawLinesOnImage(lines, load);
        }

        return ImageTools.GetBitmapFromMat(load);
    }

    public static Bitmap TestRects(Bitmap bitmap) {
        Mat load = ImageTools.GetMatFromBitmap(bitmap);


        List<Mat> linesList = Recognition.findHeroTopLinesInImage(load);
/*        for (Mat lines : linesList) {
            ImageTools.drawLinesOnImage(lines, load);
        }*/
        List<HeroFromPhoto> heroes = Recognition.CalculateHeroRects(linesList, load);


        return ImageTools.GetBitmapFromMat(load);
    }
}