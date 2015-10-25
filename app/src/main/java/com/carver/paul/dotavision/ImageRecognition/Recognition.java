package com.carver.paul.dotavision.ImageRecognition;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;

import com.carver.paul.dotavision.MainActivity;
import com.carver.paul.dotavision.R;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.List;

import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;

/**
 * Created by paul on 24/10/15.
 */
public class Recognition {

    public static Bitmap Run(Bitmap bitmap) { //Bitmap bitmap, int hMin, int hMax, int sMin, int sMax, int vMin, int vMax) {


        Mat load = new Mat();
//        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        bitmapToMat(bitmap, load);
        Imgproc.cvtColor(load, load, Imgproc.COLOR_BGR2BGRA);

/*        Scalar lowerHsv = new Scalar(hMin, sMin, vMin);
        Scalar upperHsv = new Scalar(hMax, sMax, vMax);
        ImageTools.MaskAColourFromImage(load, lowerHsv, upperHsv, load);*/

        List<Mat> linesList = HeroRect.findHeroTopLinesInImage(load, Variables.sRange.get(0), Variables.vRange.get(0), Variables.sRange.get(1), Variables.vRange.get(1));

/*        for (Mat lines : linesList) {
            ImageTools.drawLinesOnImage(lines, load);
        }*/


        List<HeroRect> heroes = HeroRect.CalculateHeroRects(linesList, load);

        for (HeroRect hero : heroes) {
            List<HeroHistAndSimilarity> similarityList = HistTest.OrderedListOfTemplateSimilarHeroes(hero.image);
            System.out.println("Found:" + similarityList.get(0).hero.name);
        }

        Bitmap bitmap2 = Bitmap.createBitmap(load.cols(), load.rows(), Bitmap.Config.ARGB_8888);
        matToBitmap(load, bitmap2);
        return bitmap2;
    }
}
