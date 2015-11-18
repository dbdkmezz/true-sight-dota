package com.carver.paul.dotavision.ImageRecognition;

import android.graphics.Bitmap;

import com.carver.paul.dotavision.MainActivity;

import org.opencv.core.Mat;

import java.util.List;

//TODO-prebeta: Implement arcana hero images too
//TODO-beauty: repackage ImageRecgonition so that there's only one public class?

public class Recognition {

    public static String debugString = "";

    public static List<HeroRect> Run(Bitmap bitmap, HistTest histTest) { //Bitmap bitmap, int hMin, int hMax, int sMin, int sMax, int vMin, int vMax) {

        if (MainActivity.sDebugMode) debugString = "";

        Mat load = ImageTools.GetMatFromBitmap(bitmap);

/*        Mat load = new Mat();
//        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        bitmapToMat(bitmap, load);
        Imgproc.cvtColor(load, load, Imgproc.COLOR_BGR2BGRA);*/

/*        Scalar lowerHsv = new Scalar(hMin, sMin, vMin);
        Scalar upperHsv = new Scalar(hMax, sMax, vMax);
        ImageTools.MaskAColourFromImage(load, lowerHsv, upperHsv, load);*/

        List<Mat> linesList = HeroRect.findHeroTopLinesInImage(load);

/*        for (Mat lines : linesList) {
            ImageTools.drawLinesOnImage(lines, load);
        }*/


        List<HeroRect> heroes = HeroRect.CalculateHeroRects(linesList, load);

        for (HeroRect hero : heroes) {
            hero.calcSimilarityList(histTest); //HistTest.OrderedListOfTemplateSimilarHeroes(hero.image);
            //System.out.println("Found:" + similarityList.get(0).hero.name);
        }

        return heroes;

/*        Bitmap bitmap2 = Bitmap.createBitmap(load.cols(), load.rows(), Bitmap.Config.ARGB_8888);
        matToBitmap(load, bitmap2);
        return bitmap2;*/
    }
}
