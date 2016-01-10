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
import android.util.Log;

import com.carver.paul.dotavision.BuildConfig;
import com.carver.paul.dotavision.MainActivity;
import com.carver.paul.dotavision.Models.HeroFromPhoto;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//TODO-prebeta: Implement arcana hero images too

/**
 * This is where all the hard work processing the photos to recognise heroes happens
 */
public class RecognitionModel {

    public RecognitionModel() {}

    // mDebugString is used when in debug mode to keep track of how recognition is going.
    public static String mDebugString = "";

    private static final String TAG = "RecognitionModel";

    public static List<HeroFromPhoto> findFiveHeroesInPhoto(final Bitmap photoBitmap) {
        if (BuildConfig.DEBUG && MainActivity.sDebugMode == true) mDebugString = "";

        // Load the bitmap into a format OpenCV can use
        Mat load = ImageTools.GetMatFromBitmap(photoBitmap);

        if (BuildConfig.DEBUG) Log.d(TAG, "Got mat from bitmap.");

        // Find the coloured lines which are above each hero (these are used to locate the heroes
        // in the image.
        List<Mat> linesList = findHeroTopLinesInImage(load);

        if (BuildConfig.DEBUG) Log.d(TAG, "Found " + linesList.size() + " top hero lines.");

        // Find the rectangles where the images of the individuals heroes are
        List<HeroFromPhoto> heroes = CalculateHeroRects(linesList, load);

        if (BuildConfig.DEBUG) Log.d(TAG, "Found " + heroes.size() + " hero rects.");

        return heroes;
    }

    public static HeroFromPhoto identifyHeroFromPhoto(HeroFromPhoto heroFromPhoto,
                                                 SimilarityTest similarityTest) {
        heroFromPhoto.calcSimilarityList(similarityTest);
        return heroFromPhoto;
    }

    public static List<Mat> findHeroTopLinesInImage(Mat photo) {
        return findHeroTopLinesInImage(photo, Variables.sRange.get(0), Variables.vRange.get(0),
                Variables.sRange.get(1), Variables.vRange.get(1));
    }

    public static List<HeroFromPhoto> CalculateHeroRects(List<Mat> linesList, Mat backgroundImage) {
        List<HeroFromPhoto> heroes = new ArrayList<>();

/*        for( Mat lines : linesList ) {
            HeroFromPhoto hero = new HeroFromPhoto(lines);
            heroes.add(hero);
        }*/

        // Doing it this slow way so that I can remoove unusal lines

        List<HeroLine> heroLines = new ArrayList<>();

        for (Mat lines : linesList) {
            heroLines.add(new HeroLine(lines));
        }

        if (heroLines.size() > 1)
            HeroLine.FixHeroLines(heroLines, backgroundImage.width(), backgroundImage.height());
/*
        LinesHorizontally(heroLines, backgroundImage);
        HeroLine.FixHeroLinesWithBadHeights(heroLines);
*/

        int positionInPhoto = 0;
        for (HeroLine hLine : heroLines) {
            HeroFromPhoto heroImage = calculateHeroFromPhoto(hLine, positionInPhoto, backgroundImage);
            positionInPhoto++;
            heroes.add(heroImage);
        }

        return heroes;
    }

    private static List<Mat> findHeroTopLinesInImage(Mat photo, int lowerHsvS, int lowerHsvV, int upperHsvS, int upperHsvV) {
        List<Mat> leftLines = findHeroTopLinesInImage(photo, Variables.leftColoursRanges, lowerHsvS, lowerHsvV, upperHsvS, upperHsvV);
        List<Mat> rightLines = findHeroTopLinesInImage(photo, Variables.rightColoursRanges, lowerHsvS, lowerHsvV, upperHsvS, upperHsvV);

        if (BuildConfig.DEBUG && MainActivity.sDebugMode) {
            RecognitionModel.mDebugString = RecognitionModel.mDebugString + System.getProperty("line.separator") + debugStringForLines(leftLines) + "-" + debugStringForLines(rightLines);
        }

        int totalLeftLines = countLinesInMats(leftLines);
        int totalRightLines = countLinesInMats(rightLines);

        if (totalLeftLines > totalRightLines) {
            return leftLines;
        } else {
            return rightLines;
        }
    }

    public static List<Mat> findHeroTopLinesInImage(Mat photo, List<List<Integer>> colourRanges, int lowerHsvS, int lowerHsvV, int upperHsvS, int upperHsvV) {
        List<Mat> linesList = new ArrayList<>();
        int pos = 0;
        int photoWidth = photo.width();

        for (List<Integer> colourRange : colourRanges) {
            int minX;
            int maxX;

            if (colourRanges.size() == 1) {
                minX = 0;
                maxX = photoWidth;
            } else {
                minX = pos * photoWidth / 6;
                maxX = (2 + pos) * photoWidth / 6;
            }

            Scalar lowerHsv = new Scalar(colourRange.get(0), lowerHsvS, lowerHsvV);
            Scalar upperHsv = new Scalar(colourRange.get(1), upperHsvS, upperHsvV);

            Mat subMat = photo.submat(0, photo.height() / 2, minX, maxX);
            Mat mask = new Mat();
            ImageTools.MaskAColourFromImage(subMat, lowerHsv, upperHsv, mask);

            Mat lines = new Mat();

            // note, this is the part that takes most time.
            ImageTools.getLineFromTopRectMask(mask, lines, photoWidth / 7); //USED TO BE 8!!!!

            adjustXPosOfLines(lines, minX);
            linesList.add(lines);

            //   Main.DrawMatInImageBox(mask, maskImage); // just for debug
            pos++;
        }

        return linesList;
    }

    // Adds xPosAdjustment to the x-coordinate of each of the lines
    private static void adjustXPosOfLines(Mat lines, int xPosAdjustment) {
        if (xPosAdjustment == 0)
            return;

        for (int i = 0; i < lines.rows(); i++) {
            double[] line = lines.get(i, 0);
            line[0] += xPosAdjustment;
            line[2] += xPosAdjustment;
            lines.put(i, 0, line);
        }
    }

    private static int countLinesInMats(List<Mat> lines) {
        int totalLines = 0;
        for (Mat mat : lines) {
            if (mat.rows() > 0)
                totalLines++;
        }
        return totalLines;
    }

    // Creates a string to be used when debugging, showing a 1 for found lines, and a - for no line
    private static String debugStringForLines(List<Mat> lines) {
        String debugString = "";
        for (Mat mat : lines) {
            if (mat.rows() > 0)
                debugString = debugString + "1";
            else
                debugString = debugString + "0";
        }
        return debugString;
    }

    private static HeroFromPhoto calculateHeroFromPhoto(HeroLine line, int positionInPhoto,
                                                 Mat backgroundImage) {
        if (line.isRealLine == false) {
            return makeFakeHeroFromPhoto(backgroundImage, positionInPhoto);
        }

        final double rationHeightToWidthbeforeCuts = 1.8;

        final double ratioToCutFromSide = 0.05;
        final double ratioToCutFromTop = 0.05;
        // ratioToCutFromBottom may need to be larger because a red box with MMR at the bottom may obscure the image
        final double ratioToCutFromBottom = 0.05;

        double heightWithoutCuts = line.rect.width() / rationHeightToWidthbeforeCuts;
        int left = line.rect.left + (int) (line.rect.width() * ratioToCutFromSide);
        int width = line.rect.width() - (int) (line.rect.width() * 2 * ratioToCutFromSide);
        int top = line.rect.top + line.rect.height() + (int) (heightWithoutCuts * ratioToCutFromTop);
        int finalHeight = (int) heightWithoutCuts - (int) (heightWithoutCuts * ratioToCutFromBottom);

        Log.d("AA", "width " + width + ", height" + finalHeight);

        if (left + width > backgroundImage.width())
            width = backgroundImage.width() - left;
        if (top + finalHeight > backgroundImage.height())
            finalHeight = backgroundImage.height() - top;


        if (left < 0) left = 0;
        if (top < 0) top = 0;

        if (left > backgroundImage.width() || top > backgroundImage.height()) {
            return makeFakeHeroFromPhoto(backgroundImage, positionInPhoto);
        }

        Rect rect = new Rect(left, top, width, finalHeight);
        Mat image = new Mat(backgroundImage, rect);

        return new HeroFromPhoto(image, positionInPhoto);
    }

    private static HeroFromPhoto makeFakeHeroFromPhoto(Mat backgroundImage, int positionInPhoto) {
        int width = 26;
        int height = 15;//(int) (width / rationHeightToWidthbeforeCuts);
        Rect rect = new Rect(0, 0, width, height);

        Mat image = new Mat(backgroundImage, rect);
        return new HeroFromPhoto(image, positionInPhoto);
    }
}


// https://github.com/badlogic/opencv-fun/blob/master/src/pool/tests/HoughLines.java

class HeroLine {
    public android.graphics.Rect rect;
    boolean isRealLine;

    public HeroLine(Mat lines) {
        //rect = new android.graphics.Rect();//0, 0, -1, -1);

        if (lines.rows() == 0) {
            isRealLine = false;
        } else {
            isRealLine = true;
            for (int i = 0; i < lines.rows(); i++) {
                double[] val = lines.get(i, 0);
                if (i == 0) {
                    initialiseRect(val);
                } else {
                    rect.union((int) val[0], (int) val[1]);
                    rect.union((int) val[2], (int) val[3]);
                }
            }
            //System.out.println("Created rect with width: " + rect.width());
        }
    }

    // This is needed because rect.union doesn't check if the rectangle is emplty.
    private void initialiseRect(double[] val) {
        int left;
        int right;
        int top;
        int bottom;

        if ((int) val[0] < (int) val[2]) {
            left = (int) val[0];
            right = (int) val[2];
        } else {
            left = (int) val[2];
            right = (int) val[0];
        }

        if ((int) val[1] < (int) val[3]) {
            top = (int) val[1];
            bottom = (int) val[3];
        } else {
            top = (int) val[3];
            bottom = (int) val[1];
        }

        rect = new android.graphics.Rect(left, top, right, bottom);
    }

    /**
     * Draws the line onto the image, used for debugging.
     * @param img
     */
    public void Draw(Mat img) {
        Imgproc.rectangle(img, new org.opencv.core.Point(rect.left, rect.top), new org.opencv.core.Point(rect.right, rect.bottom), new Scalar(0, 255, 0), 2);
    }

    // I must have 5 heroLines for this to work
    // lines are bad if they:
    //   - have a width > 1/5th of the imageWidth
    //   - are out of order ** NOT YET DONE **
    //   - have a height 1.2 times greater than average
    static protected void FixHeroLines(List<HeroLine> heroLines, int imageWidth, int imageHeight) {
        final double MAX_ACCEPTABLE_PROPORTIONAL_DEVIANCE_FROM_AV_HEIGHT = 0.2;
        final double MAX_ACCEPTABLE_PROPORTIONAL_DEVIANCE_FROM_AV_WIDTH = 0.12; // used to be 0.16
        final int MAX_ACCEPTABLE_WIDTH = (int) (imageWidth / 4.5);
        final int MIN_ACCEPTABLE_HEIGHT = 3;
        //   final double MAX_ACCEPTABLE_DEVIANCE_FROM_AV_Y = imageHeight * 22 / 90;

        if (heroLines.size() != 5)
            throw new RuntimeException("Trying to fix hero lines but I don't have 5 of them.");

        List<HeroLine> badLines = new ArrayList<>();
        List<HeroLine> goodLines = new ArrayList<>();

        // remove lines already identified as bad, or far too wide
        int totalGoodHeight = 0;
        int numImagesWithRealHeights = 0;
        for (HeroLine heroLine : heroLines) {
            if (heroLine.isRealLine == false || heroLine.rect.width() > MAX_ACCEPTABLE_WIDTH) {
                heroLine.isRealLine = false;
                badLines.add(heroLine);
            } else {
                if (heroLine.rect.height() > MIN_ACCEPTABLE_HEIGHT) { // need to check this, because single lines may have a height of 0!
                    totalGoodHeight += heroLine.rect.height();
                    numImagesWithRealHeights++;
                }
                goodLines.add(heroLine); // but just becuase they are short they still count as good!
            }
        }

        if (BuildConfig.DEBUG && MainActivity.sDebugMode)
            updateDebugStringForGoodLines(heroLines, goodLines, "w/o none or wide lines: ");

        if (goodLines.size() < 2) {
            if (BuildConfig.DEBUG && MainActivity.sDebugMode) {
                RecognitionModel.mDebugString = RecognitionModel.mDebugString + System.getProperty("line.separator") +
                        "After removing lines without lines, and overly wide lines I'm only left " + goodLines.size() + " hero lines. So I'm giving up trying to fix them.";
            }
            return;
        }


        int totalGoodWidth = 0;

        //TODO-prebeta: improve code dealing with lines which are the wrong height, perhaps just remove the points with the wrong height and recalculate the rect again
        // (see what happens with lion in the demo picture)


        // check there are more than 2 good lines, if not then there's no reason to try and reduce the number of lines!
        if (goodLines.size() > 2 && numImagesWithRealHeights > 1) {
            int averageHeight = totalGoodHeight / numImagesWithRealHeights;
            int maxAcceptableHeight = averageHeight + (int) (averageHeight * MAX_ACCEPTABLE_PROPORTIONAL_DEVIANCE_FROM_AV_HEIGHT);

            for (Iterator<HeroLine> iterator = goodLines.iterator(); iterator.hasNext(); ) {
                HeroLine heroLine = iterator.next();
                if (heroLine.rect.height() > maxAcceptableHeight) {
                    iterator.remove();
                    heroLine.isRealLine = false;
                    badLines.add(heroLine);
                } else {
                    totalGoodWidth += heroLine.rect.width();
                }
            }
        } else {
            for (HeroLine heroLine : goodLines) {
                totalGoodWidth += heroLine.rect.width();
            }
        }

        if (BuildConfig.DEBUG && MainActivity.sDebugMode)
            updateDebugStringForGoodLines(heroLines, goodLines, "w/o tall lines: ");

        if (goodLines.size() < 2) {
            if (BuildConfig.DEBUG && MainActivity.sDebugMode) {
                RecognitionModel.mDebugString = RecognitionModel.mDebugString + System.getProperty("line.separator") +
                        "After getting ride of lines which are too tall I'm only left with " + goodLines.size() +
                        " hero lines. So I'm giving up trying to fix them. I think the code could be improved to get round this. So come back if this comes up lots!";
            }
            return;
        }

        int averageGoodWidth = totalGoodWidth / goodLines.size();
        int minAcceptableWidth = averageGoodWidth - (int) (averageGoodWidth * MAX_ACCEPTABLE_PROPORTIONAL_DEVIANCE_FROM_AV_WIDTH);
        int maxAcceptableWidth = averageGoodWidth + (int) (averageGoodWidth * MAX_ACCEPTABLE_PROPORTIONAL_DEVIANCE_FROM_AV_WIDTH);

        for (Iterator<HeroLine> iterator = goodLines.iterator(); iterator.hasNext(); ) {
            HeroLine heroLine = iterator.next();
            if (heroLine.rect.width() > maxAcceptableWidth || heroLine.rect.width() < minAcceptableWidth) {
                iterator.remove();
                heroLine.isRealLine = false;
                badLines.add(heroLine);
            }
        }

        if (BuildConfig.DEBUG && MainActivity.sDebugMode)
            updateDebugStringForGoodLines(heroLines, goodLines, "w/o wide lines: ");

        if (goodLines.size() < 2) {
            if (BuildConfig.DEBUG && MainActivity.sDebugMode) {
                RecognitionModel.mDebugString = RecognitionModel.mDebugString + System.getProperty("line.separator") +
                        "After getting ride of lines which are too wide I'm not left with enough good hero lines. So I'm giving up trying to fix them. I think the code could be improved to get round this. So come back if this comes up lots!";
            }
            return;
        }

        if (goodLines.size() == 5) {
            return; //nothing needs fixing!
        }

        FixBadLines(heroLines);
    }

    // Creates a string to be used when debugging, showing a 1 for found lines, and a - for no line
    private static void updateDebugStringForGoodLines(List<HeroLine> heroLines, List<HeroLine> goodLines, String description) {
        RecognitionModel.mDebugString = RecognitionModel.mDebugString + System.getProperty("line.separator") + description;

        for (HeroLine line : heroLines) {
            if (goodLines.contains(line))
                RecognitionModel.mDebugString = RecognitionModel.mDebugString + "1";
            else
                RecognitionModel.mDebugString = RecognitionModel.mDebugString + "0";
        }
    }

    // This function makes lines marked as not real in proportion to the good lines
    // This function assumes that the lines are in order from left to right
    // It requires at least two good lines
    static private void FixBadLines(List<HeroLine> heroLines) {
        if (heroLines.size() != 5)
            throw new RuntimeException("Trying to fix hero lines but I don't have 5 of them.");

        int totalGoodWidth = 0;
        int totalGoodHeight = 0;
        int totalGoodY = 0;

        int totalGoodLines = 0;
        int totalBadLines = 0;

        int firstGoodX = -1;
        int lastGoodX = -1;

        int firstGoodPos = -1;
        int lastGoodPos = -1;

        int pos = 0;

        for (HeroLine heroLine : heroLines) {
            if (heroLine.isRealLine == false) {
                totalBadLines++;
            } else {
                if (lastGoodPos == -1) {
                    firstGoodX = heroLine.rect.left;
                    firstGoodPos = pos;
                }

                lastGoodX = heroLine.rect.left;
                lastGoodPos = pos;
                totalGoodY += heroLine.rect.top;
                totalGoodWidth += heroLine.rect.width();
                totalGoodHeight += heroLine.rect.height();
                totalGoodLines++;
            }
            pos++;
        }

        if (totalGoodLines < 2)
            throw new RuntimeException("Trying to fix hero lines but I've not been given two good ones.");

        if (totalBadLines == 0)
            return;

        int avGoodWidth = totalGoodWidth / totalGoodLines;
        int avGoodXDistance = (lastGoodX - firstGoodX) / (lastGoodPos - firstGoodPos);
        int avGoodY = totalGoodY / totalGoodLines;
        int avGoodHeight = totalGoodHeight / totalGoodLines;

        // assign each bad line an x position based on the x of the first good line and the average distribution of the other good lines
        pos = 0;
        for (HeroLine heroLine : heroLines) {
            if (heroLine.isRealLine == false) {
                int left = firstGoodX + ((pos - firstGoodPos) * avGoodXDistance);
                int right = left + avGoodWidth;
                int top = avGoodY;
                int bottom = top + avGoodHeight;
                heroLine.rect = new android.graphics.Rect(left, top, right, bottom);
                heroLine.isRealLine = true;
            }
            pos++;
        }
    }

/*
    // This function assumes that the lines are in order from left to right
    // This function uses all the lines that have a width of less than 1/5th of that of the background image,
    // It assumes that all those are good and uses those to calibrate the width and x positions of the other lines.
    static public void FixLinesHorizontally(List<HeroLine> heroLines, Mat backgroundImage) {

        List<HeroLine> badLines = new ArrayList<>();
        List<HeroLine> goodLines = new ArrayList<>();

        int totalGoodWidth = 0;
        int lastGoodX = 0;
//        int varForWorkingAvGoodXDistance = 0;
        //       int pointsToDivideWorkingVarBy = 0;
        int pos = 0;
        int lastGoodPos = -1;
        int firstGoodX = -1;
        int firstGoodPos = -1;



        for (HeroLine heroLine : heroLines) {
            if (heroLine.rect.width * 5 > backgroundImage.width()) {
                badLines.add(heroLine);
            } else {
                if (heroLine.rect.x < lastGoodX) {
                    System.out.println("Fix lines horizontally failed because the good lines are not in order left to right.");
                }

                if (lastGoodPos == -1) {
                    firstGoodX = heroLine.rect.x;
                    firstGoodPos = pos;
                } //else {
                    //                   varForWorkingAvGoodXDistance += heroLine.rect.x - lastGoodX;
                    //                   pointsToDivideWorkingVarBy += (pos - posOfLastGoodLine);
               // }

                lastGoodX = heroLine.rect.x;
                goodLines.add(heroLine);
                totalGoodWidth += heroLine.rect.width;
                lastGoodPos = pos;
            }
            pos++;
        }

        if (badLines.isEmpty())
            return;

        if (heroLines.size() != 5) {
            System.out.println("Found lines with bad widths, but can't fix them because don't have all five lines.");
            return;
        }

        int avGoodWidth = totalGoodWidth / goodLines.size();
//        int avGoodXDistance = varForWorkingAvGoodXDistance / pointsToDivideWorkingVarBy;
        int avGoodXDistance = (lastGoodX - firstGoodX) / (lastGoodPos - firstGoodPos);

        // assign each bad line an x position based on the x of the first good line and the average distribution of the other good lines
        pos = 0;
        for (HeroLine heroLine : heroLines) {
            if (badLines.contains(heroLine)) {
                heroLine.rect.x = firstGoodX + ((pos - firstGoodPos) * avGoodXDistance);
                heroLine.rect.width = avGoodWidth;
            }
            pos++;
        }
    }

    static public void FixHeroLinesWithBadHeights(List<HeroLine> heroLines) {
        if (heroLines.isEmpty())
            return;

        int totalHeight = 0;
        for (HeroLine heroLine : heroLines) {
            totalHeight += heroLine.rect.height;
        }
        int avHeight = totalHeight / heroLines.size();

        List<HeroLine> goodHeroLines = new ArrayList<>();
        List<HeroLine> badHeroLines = new ArrayList<>();
        int totalGoodY = 0;
        int totalGoodHeight = 0;

        for (HeroLine heroLine : heroLines) {
            if (heroLine.rect.height < avHeight * 1.2) {
                goodHeroLines.add(heroLine);
                totalGoodY += heroLine.rect.y;
                totalGoodHeight += heroLine.rect.height;
            } else {
                badHeroLines.add(heroLine);
            }
        }

        if (badHeroLines.isEmpty())
            return;

        if (goodHeroLines.isEmpty()) {
            System.out.println("Oh no, couldn't find any hero line with good heights");
            return;
        }

        int avGoodY = totalGoodY / goodHeroLines.size();
        int avGoodHeight = totalGoodHeight / goodHeroLines.size();

        for (HeroLine badLine : badHeroLines) {
            System.out.println("Fixing height and y of a line.");
            badLine.rect.y = avGoodY;
            badLine.rect.height = avGoodHeight;
        }
    }
    */
}


