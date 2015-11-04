package com.carver.paul.dotavision.ImageRecognition.Debug;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.carver.paul.dotavision.ImageRecognition.HeroRect;
import com.carver.paul.dotavision.ImageRecognition.ImageTools;
import com.carver.paul.dotavision.ImageRecognition.Variables;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;

/**
 * Created by paul on 25/10/15.
 */
public class DebugLineDetection {
    public static Bitmap TestMask(Bitmap bitmap, int hMin, int hMax, int sMin, int sMax, int vMin, int vMax) { //, ImageView imageViewLines) {

        Mat load = ImageTools.GetMatFromBitmap(bitmap);

        Scalar lowerHsv = new Scalar(hMin, sMin, vMin);
        Scalar upperHsv = new Scalar(hMax, sMax, vMax);
        ImageTools.MaskAColourFromImage(load, lowerHsv, upperHsv, load);

        return ImageTools.GetBitmapFromMat(load, false);

/*        List<Mat> linesList = HeroRect.findHeroTopLinesInImage(load, Variables.sRange.get(0), Variables.vRange.get(0), Variables.sRange.get(1), Variables.vRange.get(1));

        for (Mat lines : linesList) {
            ImageTools.drawLinesOnImage(lines, load);
        }

        Bitmap linesBitmap = Bitmap.createBitmap(load.cols(), load.rows(), Bitmap.Config.ARGB_8888);
        matToBitmap(load, linesBitmap);
        imageViewLines.setImageBitmap(linesBitmap);*/
    }

    public static void TestLines(Bitmap bitmap, ImageView imageView) {
        Mat load = new Mat();
        bitmapToMat(bitmap, load);
        Imgproc.cvtColor(load, load, Imgproc.COLOR_BGR2BGRA);

        List<Mat> linesList = HeroRect.findHeroTopLinesInImage(load, Variables.sRange.get(0), Variables.vRange.get(0), Variables.sRange.get(1), Variables.vRange.get(1));

        for (Mat lines : linesList) {
            ImageTools.drawLinesOnImage(lines, load);
        }

        Bitmap linesBitmap = Bitmap.createBitmap(load.cols(), load.rows(), Bitmap.Config.ARGB_8888);
        matToBitmap(load, linesBitmap);
        imageView.setImageBitmap(linesBitmap);
    }
}