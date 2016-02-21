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

package com.carver.paul.dotavision.ImageRecognition;

import android.graphics.Bitmap;
import android.util.Pair;

import com.carver.paul.dotavision.R;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;

public class ImageTools {

    private ImageTools() {}

    public static void MaskAColourFromImage(Mat image, Scalar lowerHsv, Scalar upperHsv, Mat mask) {
        Imgproc.cvtColor(image, mask, Imgproc.COLOR_BGR2HSV);
        Core.inRange(mask, lowerHsv, upperHsv, mask);
    }

    public static Bitmap GetBitmapFromMat(Mat mat) {
        return GetBitmapFromMat(mat, true);
    }

    public static Bitmap GetBitmapFromMat(Mat mat, boolean convertColor) {
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        if (convertColor) {
            Mat finalColourMat = new Mat();
            Imgproc.cvtColor(mat, finalColourMat, Imgproc.COLOR_BGR2RGB);
            matToBitmap(finalColourMat, bitmap);
        } else {
            matToBitmap(mat, bitmap);
        }

        return bitmap;
    }

    public static Mat GetMatFromBitmap(Bitmap bitmap) {
        Mat mat = new Mat();
        bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2BGR);
        return mat;
    }

    public static void getLineFromTopRectMask(Mat mask, Mat lines, int minLineLength) {
        Imgproc.HoughLinesP(mask, lines, 1, Math.PI / 180, 80, minLineLength, 10);
    }

    public static int getDrawableForAbility(String abilityImageName) {
        for (Pair<Integer, String> pair : Variables.abilityDrawables) {
            if (pair.second.equals(abilityImageName))
                return pair.first;
        }
        return -1;
    }

    public static int getResIdForHeroImage(String heroImageName) {
        if(heroImageName == null || heroImageName.equals("")) {
            return R.drawable.missing_hero;
        }

        for (Pair<Integer, String> pair : SimilarityTest.mHeroIconDrawables) {
            if (pair.second.equals(heroImageName))
                return pair.first;
        }
        return -1;
    }

    public static void drawLinesOnImage(Mat lines, Mat image) {
        for (int i = 0; i < lines.rows(); i++) {
            double[] val = lines.get(i, 0);
            Imgproc.line(image, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(0, 255, 0), 2);
        }
    }

    public static int findLongestLine(List<Mat> linesList) {
        int longestLine = 0;

        for (Mat lines : linesList) {
            for (int i = 0; i < lines.rows(); i++) {
                if (lineLength(lines.get(i, 0)) > longestLine)
                    longestLine = lineLength(lines.get(i, 0));
            }
        }

        return longestLine;
    }

    private static int lineLength(double[] line) {
        if (line[2] > line[0])
            return (int) (line[2] - line[0]);

        return (int) (line[0] - line[1]);
    }
}
