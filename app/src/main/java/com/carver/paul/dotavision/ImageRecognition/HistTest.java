package com.carver.paul.dotavision.ImageRecognition;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.carver.paul.dotavision.MainActivity;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.*;
import java.util.List;

import static org.opencv.android.Utils.bitmapToMat;

// http://www.pyimagesearch.com/2014/01/22/clever-girl-a-guide-to-utilizing-color-histograms-for-computer-vision-and-image-search-engines/


public class HistTest {
    static final private List<HeroWithHist> heroes;

    static {
        heroes = new ArrayList<>();
        File folder = new File(MainActivity.getImagesLocation(), "/hero icons");
        //System.out.println("loading files from: " + folder.getPath());
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                HeroWithHist hero = new HeroWithHist(file.getPath(), file.getName());
                heroes.add(hero);
                //               System.out.println("File " + listOfFiles[i].getName());
            } /*else if (listOfFiles[i].isDirectory()) {
               System.out.println("Directory " + listOfFiles[i].getName());
            }*/
        }
    }


/*    static public void FindMostSimilarHeroes(Mat image, String actualName) {
        FindMostSimilarHeroes(image, actualName, Imgproc.CV_COMP_BHATTACHARYYA, false);
    }

    static public void FindMostSimilarHeroes(Mat image, String actualName, int method) {
        FindMostSimilarHeroes(image, actualName, method, false);
    }

    static public void FindMostSimilarHeroes(Mat image, String actualName, int method, boolean saveHist) {
        List<HeroHistAndSimilarity> similarityList = OrderedListOfHueSimilarHeroes(image, method, saveHist);

        int index = similarityList.indexOf(new HeroHistAndSimilarity(actualName));

        if (index == -1)
            return;

        HeroHistAndSimilarity oneToLookFor = similarityList.get(index);

        int positionOfSearchedHero = similarityList.indexOf(oneToLookFor);
        if (positionOfSearchedHero > 1) {
            for (int i = similarityList.size() - 1; i >= similarityList.size() - positionOfSearchedHero; i--) {
                System.out.println(i + ": " + similarityList.get(i).hero.name + ", " + similarityList.get(i).similarity);
*//*                HeroNameAndSimilarity hero = similarityList.get(i);
                Path source = Paths.get(Main.imagesLoc + "/hero icons med/" + hero.name);
                Path destination = Paths.get(Main.imagesLoc + "/matches/" + oneToLookFor.name + i + ".png");
                try {
                    Files.deleteIfExists(destination);
                    Files.copy(source, destination);
                } catch (IOException e) {
                    e.printStackTrace();
                }
*//**//*                 if(saveHist) {
                     Mat singleCompHist = Histogram.DrawNewHueHistogram(photoHist);
                     Histogram.DrawHueHistogramOnTop(hero.hist, singleCompHist, new Scalar(60, 60, 0));
                     Imgcodecs.imwrite(Main.imagesLoc + "/matches/" + oneToLookFor.name + i + "hist.jpg", singleCompHist);
                     Histogram.DrawHueHistogramOnTop(hero.hist, drawnHist, new Scalar(60, 60, 0));
                 }*//*
            }
            System.out.println();
        } else {
            System.out.println("Found " + actualName + " first time!");
        }

*//*
            HeroNameAndSimilarity detectedHero = similarityList.get(similarityList.size() - 1);
            nameOfDetectedHero.append(detectedHero.name);
*//*

*//*        if(saveHist) {
            Histogram.DrawHueHistogramOnTop(oneToLookFor.hist, drawnHist, new Scalar(140, 140, 0));
            Histogram.DrawHueHistogramOnTop(photoHist, drawnHist);
            Imgcodecs.imwrite(Main.imagesLoc + "/combhists/" + actualName + "_hphover.png" + ".jpg", drawnHist);
        }*//*
    }

    static public List<HeroHistAndSimilarity> OrderedListOfHueSimilarHeroes(Mat image) {
        return OrderedListOfHueSimilarHeroes(image, Imgproc.CV_COMP_BHATTACHARYYA, false);
    }

    static public List<HeroHistAndSimilarity> OrderedListOfHueSimilarHeroes(Mat image, int method, boolean saveHist) {
        Mat photoHist = Histogram.CreateHueHistogram(image);
        Mat drawnHist = new Mat();
        if (saveHist) {
            drawnHist = Histogram.DrawNewHueHistogram(photoHist);
        }
        List<HeroHistAndSimilarity> similarityList = new ArrayList<>();
        // HeroNameAndSimilarity oneToLookFor = new HeroNameAndSimilarity("not found", new Double(0), new Mat());

        for (HeroWithHist hero : heroes) {
            Double value = Histogram.CustomCompareHist(hero.histogram, photoHist, method);
            similarityList.add(new HeroHistAndSimilarity(hero, value));
//             if (hero.heroName.startsWith(actualName)) {
//                 oneToLookFor = newOne;
// *//*                if(saveHist) {
//                     Histogram.DrawHueHistogramOnTop(hero.histogram, drawnHist, new Scalar(120, 120, 0));
//                     Imgcodecs.imwrite(Main.GetImagesLoc() + "/combhists/" + hero.heroName + ".jpg", drawnHist);
//                 }*//*
//             }
        }

        Collections.sort(similarityList);

        if (!(method == Imgproc.CV_COMP_BHATTACHARYYA || method == Imgproc.CV_COMP_CHISQR || method == 99))
            Collections.reverse(similarityList);

        return similarityList;

    }*/

    static public List<HeroHistAndSimilarity> OrderedListOfTemplateSimilarHeroes(Mat photo) {
        return OrderedListOfTemplateSimilarHeroes(photo, 0, 210, 140, 2, 5, 5);
    }

    // the combination of threshold method 2 and template method 5 is incredible!!!!
    // Efficiency improvements available
    // Also, templated matching is very sensitive to rotation, so work needed there
    static public List<HeroHistAndSimilarity> OrderedListOfTemplateSimilarHeroes(Mat photo, int photoCropPercent, int photoThreshold, int originalThreshold,
                                                                                 int thresholdType, int blurSize, int method) {

        System.out.println("Method: " + method);


        Mat subMatOfPhoto = CropMatByPercentage(photo, photoCropPercent);
        subMatOfPhoto = EnsureMatSmallerThan(subMatOfPhoto, 126, 71);
        Mat preparedPhoto = BlurAndThresholdMat(subMatOfPhoto, photoThreshold, thresholdType, blurSize);

        List<HeroHistAndSimilarity> similarityList = new ArrayList<>();
        for (HeroWithHist hero : heroes) {
            // note, will be inefficient to do this every time!
            Mat preparedOriginal = BlurAndThresholdMat(hero.image, originalThreshold, thresholdType, blurSize);
            Double value = CompareTemplate(preparedPhoto, preparedOriginal, method);
            similarityList.add(new HeroHistAndSimilarity(hero, value));
        }

        Collections.sort(similarityList);
        if (method != Imgproc.TM_SQDIFF && method != Imgproc.TM_SQDIFF_NORMED)
            Collections.reverse(similarityList);
/*
        if (!(method == Imgproc.CV_COMP_BHATTACHARYYA || method == Imgproc.CV_COMP_CHISQR || method == 99))
            Collections.reverse(similarityList);
*/

        return similarityList;
    }

    static private Mat EnsureMatSmallerThan(Mat mat, int width, int height) {
        int colDiff = 1 + mat.cols() - width;
        int rowDiff = 1 + mat.rows() - height;
        if (colDiff > 0 || rowDiff > 0) {
            if (colDiff < 0)
                colDiff = 0;
            else if (rowDiff < 0)
                rowDiff = 0;

            Mat returnMat = mat.submat(rowDiff / 2, mat.rows() - (rowDiff / 2), colDiff / 2, mat.cols() - (colDiff / 2));
            if (returnMat.width() > width) {
                throw new RuntimeException("width!");
            }
            if (returnMat.height() > height)
                throw new RuntimeException("height!");
            return returnMat;
        } else
            return mat;
    }

    static private Mat BlurAndThresholdMat(Mat mat, int threshold, int thresholdType, int blurSize) {
        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.blur(gray, gray, new Size(blurSize, blurSize));
        Imgproc.threshold(gray, gray, threshold, 255, thresholdType);
        return gray;
    }

    static private Mat CropMatByPercentage(Mat mat, int percentage) {
        if (percentage < 0 || percentage > 45)
            throw new RuntimeException("Trying to mat, but percentage to crop by is too high.");
        int minX = mat.cols() * percentage / 100;
        int minY = mat.rows() * percentage / 100;
        Mat subMat = mat.submat(minY, mat.rows() - minY, minX, mat.cols() - minY);
        return subMat;
    }

    static double CompareTemplate(Mat photo, Mat originalHero, int method) {
        if (photo.width() > originalHero.width())
            throw new RuntimeException("width!");
        if (photo.height() > originalHero.height())
            throw new RuntimeException("height!");

        int result_cols = originalHero.cols() - photo.cols() + 1;
        int result_rows = originalHero.rows() - photo.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
        Imgproc.matchTemplate(originalHero, photo, result, method);
        Core.MinMaxLocResult mmResult = Core.minMaxLoc(result);
        if (method == Imgproc.TM_SQDIFF || method == Imgproc.TM_SQDIFF_NORMED)
            return mmResult.minVal;
        else
            return mmResult.maxVal;
    }
}




