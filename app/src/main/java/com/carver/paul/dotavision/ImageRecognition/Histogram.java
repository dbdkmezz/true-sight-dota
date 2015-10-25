/*
package com.carver.paul.dotavision.ImageRecognition;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public final class Histogram {

    static final int HIST_SIZE = 256;
    static final int HIST_W = 512;
    static final int HIST_H = 824;
    static final long BIN_W = Math.round((double) HIST_W / HIST_SIZE);

    private Histogram() {}

    static Mat CreateHueHistogram(Mat source) {

        Mat hsv = new Mat();
        Imgproc.cvtColor(source, hsv, Imgproc.COLOR_BGR2HSV);

        List<Mat> hsvPlanes = new ArrayList<>();
        Core.split(hsv, hsvPlanes);

        Mat hueHistogram = new Mat();

        List<Mat> matList = new ArrayList<>();

        matList.add(hsvPlanes.get(0));
        Imgproc.calcHist(matList, new MatOfInt(0), new Mat(), hueHistogram, new MatOfInt(256), new MatOfFloat(0f, 256f), false);
*/
/*
        int HIST_W = 512;
        int HIST_H = 600;

        Mat dawnHist = new Mat(HIST_H, HIST_W, CvType.CV_8UC1);
        Core.normalize(hueHistogram, hueHistogram, 3, dawnHist.rows(), Core.NORM_MINMAX);*//*


        return hueHistogram;
    }

    static double CustomCompareHist(Mat heroHist, Mat photoHist, int method) {
        // all values in the hist are something between 0 and 824

        //CURRENTLY DOESN'T DISCOUNT PROBLEMATIC PEAKS WHICH ARE NEXT TO GOOD PEAKS!!!!

        // care about peaks bigger than ~60 that are isolated and more than 2.5 times whats in the camera image
        final int MIN_SIG_HEIGHT = 70;
        final double MIN_SIG_HEIGHT_DIFF_FACTOR = 3.2;
        final double WEIGHT_OF_AV_HEIGHT_DIFF = 0.001;
        final double WEIGHT_OF_ALL_SIG_PEAKS = 0;

        if (method == 99) {

            double sigOfDifferences = 0;

            for (int i = 0; i < HIST_SIZE; i++) {
                int sigHeroWidth = 0;
                int sigPhotoWidth = 0;
                double totalSigHeightDiff = 0;
                while (i < HIST_SIZE &&
                        (Math.round(heroHist.get(i, 0)[0]) > MIN_SIG_HEIGHT ||
                                Math.round(photoHist.get(i, 0)[0]) > MIN_SIG_HEIGHT)) {

                    double heroHistVal = heroHist.get(i, 0)[0];
                    double photoHistVal = photoHist.get(i, 0)[0];

                    if (heroHistVal > 620)
                        heroHistVal = 620;

                    if (Math.round(heroHistVal) > MIN_SIG_HEIGHT) {
                        sigHeroWidth++;
                    }

                    if ((photoHistVal * MIN_SIG_HEIGHT_DIFF_FACTOR) > heroHistVal) {
                        sigPhotoWidth++;
                    } else {
                        totalSigHeightDiff += heroHistVal - (photoHistVal * MIN_SIG_HEIGHT_DIFF_FACTOR);
                        if (totalSigHeightDiff < 0) {
                            System.out.println("ERROR! SIG HEIGHT DIFF IS NEGATIVE!");
                        }
                    }

                    i++;
                }

                if (sigHeroWidth > (sigPhotoWidth + 1)) {
*/
/*                    double averageHeightDiff = totalSigHeightDiff / significantWidth;
                    sigOfDifferences += averageHeightDiff * WEIGHT_OF_AV_HEIGHT_DIFF;*//*

                    sigOfDifferences += totalSigHeightDiff * WEIGHT_OF_AV_HEIGHT_DIFF;
                    sigOfDifferences += WEIGHT_OF_ALL_SIG_PEAKS;
                }
            }


// This test looks for cases where the hero image has a colour which the photo doesn't have
// For the test to count the colour must also be isolated, i.e. not next to any colours which the photo has
// The problem is that I want to also count cases where the origional has a hue peak wider than the photo's peak
// (photo peaks get distorted wider, so if the other is wider it's due to not matching)

*/
/*            for (int i = 0; i < HIST_SIZE; i++) {
                int significantWidth = 0;
                double totalSigHeightDiff = 0;
                while (i < HIST_SIZE && Math.round(heroHist.get(i, 0)[0]) > MIN_SIG_HEIGHT) {

                    // ignore the hero peak if next to a photo peak
                    if((photoHist.get(i, 0)[0] * MIN_SIG_HEIGHT_DIFF_FACTOR) > heroHist.get(i, 0)[0] ||
                      Math.round(photoHist.get(i, 0)[0]) > MIN_SIG_HEIGHT) {
                        significantWidth = 0;
                        totalSigHeightDiff = 0;
                        i++; //want to move i on to skip forward one because the next one along will be next to a high peak
                        break;
                    }
                    else {
                        double debugHeroHistVal = heroHist.get(i, 0)[0];
                        double photoHistHistVal = photoHist.get(i, 0)[0];
                        totalSigHeightDiff += heroHist.get(i, 0)[0] - (photoHist.get(i, 0)[0] * MIN_SIG_HEIGHT_DIFF_FACTOR);
                        if(totalSigHeightDiff < 0) {
                            int a = 55;
                        }
                        significantWidth++;
                        i++;
                    }
                }

                if (significantWidth > 0) {
                    i--;

*//*
*/
/*                    double averageHeightDiff = totalSigHeightDiff / significantWidth;
                    sigOfDifferences += averageHeightDiff * WEIGHT_OF_AV_HEIGHT_DIFF;*//*
*/
/*
                    sigOfDifferences += totalSigHeightDiff * WEIGHT_OF_AV_HEIGHT_DIFF;
                    sigOfDifferences += WEIGHT_OF_ALL_SIG_PEAKS;
                }
            }*//*





*/
/*
            final int NUM_CHUNKS = 64;
            int chunkSize = HIST_SIZE / NUM_CHUNKS;
            List<Double> results = new ArrayList<>();
            List<Integer> hist1avs = new ArrayList<>();
            List<Integer> hist2avs = new ArrayList<>();
            double totalDiff = 0;
            for (int c = 0; c < NUM_CHUNKS; c++) {
                int total1 = 0;
                int total2 = 0;

                for (int i = c * chunkSize; i < (c + 1) * chunkSize; i++) {

                    total1 += hist1.get(i, 0)[0];
                    total2 += hist2.get(i, 0)[0];
                    if (hist1.get(i, 0)[0] < 0 || hist2.get(i, 0)[0] < 0) {
                        int iaa = 3;
                    }
                }
                int av1 = total1 / chunkSize;
                int av2 = total2 / chunkSize;
                hist1avs.add(av1);
                hist2avs.add(av2);
            }

            List<Integer> biggestThisTime;
            List<Integer> smallestThisTime;
            for (int c = 0; c < NUM_CHUNKS; c++) {
                if (hist1avs.get(c) > hist2avs.get(c)) {
                    biggestThisTime = hist1avs;
                    smallestThisTime = hist2avs;
                } else {
                    biggestThisTime = hist2avs;
                    smallestThisTime = hist1avs;
                }
                if (biggestThisTime.get(c) > 20 && smallestThisTime.get(c) < 10) {
                    if (c > 1) {
                        if (smallestThisTime.get(c - 1) < 10) {
                            totalDiff += 0.2;
                        }
                    } else {
                        totalDiff += 0.1;
                    }

                }
            }
            *//*




*/
/*                int bigav;
                int smallav;
                if(av1 > av2) {
                    bigav = av1;
                    smallav = av2;
                }
                else {
                    bigav = av2;
                    smallav = av1;
                }
                double difference = 0;

                if( bigav > 35 && smallav < 25 )
                    difference = 0.2;

//                Double difference = Math.sqrt(Math.pow(av1 - av2, 2));
                results.add(difference);
                totalDiff += difference;
            }

//          totalDiff = 0;
*//*

            return sigOfDifferences + Imgproc.compareHist(heroHist, photoHist, Imgproc.CV_COMP_BHATTACHARYYA);
        } else {
            return Imgproc.compareHist(heroHist, photoHist, method);
        }

    }

    static Mat DrawNewHueHistogram(Mat hueHistogramData) {
        return DrawNewHueHistogram(hueHistogramData, new Scalar(255, 0, 0));
    }

    static Mat DrawNewHueHistogram(Mat hueHistogramData, Scalar colour) {
        Mat dawnHist = new Mat(HIST_H, HIST_W, CvType.CV_8UC1);

        DrawHueHistogramOnTop(hueHistogramData, dawnHist, colour);
        return dawnHist;
    }

    static void DrawHueHistogramOnTop(Mat hueHistogramData, Mat hist) {
        DrawHueHistogramOnTop(hueHistogramData, hist, new Scalar(255, 0, 0));
    }

    static void DrawHueHistogramOnTop(Mat hueHistogramData, Mat hist, Scalar colour) {
        //   Core.normalize(hueHistogramData, hueHistogramData, 3, hist.rows(), Core.NORM_MINMAX);
        //       Core.normalize(s_hist, s_hist, 3, histogram.rows(), Core.NORM_MINMAX);
        //     Core.normalize(v_hist, v_hist, 3, histogram.rows(), Core.NORM_MINMAX);

        for (int i = 1; i < HIST_SIZE; i++) {
            Point p1 = new Point(BIN_W * (i - 1), HIST_H - Math.round(hueHistogramData.get(i - 1, 0)[0]));
            Point p2 = new Point(BIN_W * (i), HIST_H - Math.round(hueHistogramData.get(i, 0)[0]));

*/
/*
            // don't know why, but this seems to be needed to remove noise!
            if( p1.y >= HIST_H )
                p1.y = HIST_H - 1;
            if( p2.y >= HIST_H )
                p2.y = HIST_H - 1;*//*




            Imgproc.line(hist, p1, p2, colour, 2, 8, 0);

  */
/*          Point p3 = new Point(BIN_W * (i - 1), HIST_H - Math.round(s_hist.get(i - 1, 0)[0]));
            Point p4 = new Point(BIN_W * (i), HIST_H - Math.round(s_hist.get(i, 0)[0]));
            Imgproc.line(histogram, p3, p4, new Scalar(0, 255, 0), 2, 8, 0);

            Point p5 = new Point(BIN_W * (i - 1), HIST_H - Math.round(v_hist.get(i - 1, 0)[0]));
            Point p6 = new Point(BIN_W * (i), HIST_H - Math.round(v_hist.get(i, 0)[0]));
            Imgproc.line(histogram, p5, p6, new Scalar(0, 0, 255), 2, 8, 0);*//*


        }

*/
/*
        Imgproc.cvtColor(load, load, Imgproc.COLOR_BGR2RGB);
        List<Mat> matList = new ArrayList<>();
        matList.add(load);
      //  MatOfFloat ranges = new MatOfFloat(0,256,0,256,0,256);

        Imgproc.calcHist(
                matList,
                new MatOfInt(0, 1, 2),
                new Mat(),
                histogram,
                new MatOfInt(8,8,8),
                new MatOfFloat(0,256,0,256,0,256));

             hist = cv2.normalize(hist).flatten()
*//*


        //       # extract a 3D RGB color histogram from the image,
        //     # using 8 bins per channel, normalize, and update
        //      # the index
        //   hist = cv2.calcHist([image], [0, 1, 2], None, [8, 8, 8],
        //   [0, 256, 0, 256, 0, 256])
        //      hist = cv2.normalize(hist).flatten()

//        Imgcodecs.imwrite(Main.GetImagesLoc() + "/histogram.jpg", histogram);

        //       System.out.println("histogram\n" + histogram.dump());

*/
/*

        Imgproc.cvtColor(load, load, Imgproc.COLOR_BGR2HSV);

        List<Mat> hsv_planes = new ArrayList<Mat>();
        Core.split(load, hsv_planes);

        MatOfInt histSize = new MatOfInt(256);

        final MatOfFloat histRange = new MatOfFloat(0f, 256f);

        boolean accumulate = false;

        Mat h_hist = new Mat();
        Mat s_hist = new Mat();
        Mat v_hist = new Mat();

        Mat channels = new MatOfInt(1);

        //error appear in the following sentences

        List<Mat> sillyList = new ArrayList<>();

        sillyList.add(hsv_planes.get(0));
        Imgproc.calcHist(sillyList, new MatOfInt(0), new Mat(), h_hist, histSize, histRange, accumulate);

        sillyList.clear();
        sillyList.add(hsv_planes.get(1));
        Imgproc.calcHist(sillyList, new MatOfInt(0), new Mat(), s_hist, histSize, histRange, accumulate);

        sillyList.clear();
        sillyList.add(hsv_planes.get(2));
        Imgproc.calcHist(sillyList, new MatOfInt(0), new Mat(), v_hist, histSize, histRange, accumulate);

        int HIST_W = 512;
        int HIST_H = 600;
        long BIN_W = Math.round((double) HIST_W / 256);
        //BIN_W = Math.round((double) (HIST_W / 256));

        histogram = new Mat(HIST_H, HIST_W, CvType.CV_8UC1);
        Core.normalize(h_hist, h_hist, 3, histogram.rows(), Core.NORM_MINMAX);
        Core.normalize(s_hist, s_hist, 3, histogram.rows(), Core.NORM_MINMAX);
        Core.normalize(v_hist, v_hist, 3, histogram.rows(), Core.NORM_MINMAX);

        for (int i = 1; i < 256; i++) {
            Point p1 = new Point(BIN_W * (i - 1), HIST_H - Math.round(h_hist.get(i - 1, 0)[0]));
            Point p2 = new Point(BIN_W * (i), HIST_H - Math.round(h_hist.get(i, 0)[0]));
            Imgproc.line(histogram, p1, p2, new Scalar(255, 0, 0), 2, 8, 0);

            Point p3 = new Point(BIN_W * (i - 1), HIST_H - Math.round(s_hist.get(i - 1, 0)[0]));
            Point p4 = new Point(BIN_W * (i), HIST_H - Math.round(s_hist.get(i, 0)[0]));
            Imgproc.line(histogram, p3, p4, new Scalar(0, 255, 0), 2, 8, 0);

            Point p5 = new Point(BIN_W * (i - 1), HIST_H - Math.round(v_hist.get(i - 1, 0)[0]));
            Point p6 = new Point(BIN_W * (i), HIST_H - Math.round(v_hist.get(i, 0)[0]));
            Imgproc.line(histogram, p5, p6, new Scalar(0, 0, 255), 2, 8, 0);

        }
*//*


//        Imgcodecs.imwrite(Main.GetImagesLoc() + "/histogram.jpg", histogram);

        */
/*
        List<Mat> loadInList = new ArrayList<Mat>();
        loadInList.add(load);
        hist = new Mat();

        int hBins = 50;
        int sBins = 60;
        MatOfInt histSize = new MatOfInt( hBins,  sBins);
        MatOfFloat ranges =  new MatOfFloat( 0f,180f,0f,256f );

        Imgproc.calcHist(loadInList, new MatOfInt(1), new Mat(), hist, histSize, ranges);*//*

    }
}
*/
