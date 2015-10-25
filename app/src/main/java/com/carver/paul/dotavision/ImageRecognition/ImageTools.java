package com.carver.paul.dotavision.ImageRecognition;

import android.graphics.Bitmap;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;

import java.util.List;

public class ImageTools {
    public static void MaskAColourFromImage(Mat image, Scalar lowerHsv, Scalar upperHsv, Mat mask) {

        Imgproc.cvtColor(image, mask, Imgproc.COLOR_BGR2HSV);

        // define range of color in HSV
//            Scalar lowerHsv = new Scalar(colourRange.get(0), 140, 180);
        //          Scalar upperHsv = new Scalar(colourRange.get(1), 255, 255);

        Core.inRange(mask, lowerHsv, upperHsv, mask);

        //      //Bitwise-AND mask and original image
        //    Mat res = new Mat();
        //  Core.bitwise_and(load, load, res, mask);
    }

    public static Bitmap GetBitmapFromMap(Mat mat) {
/*        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Mat rightColourMat = new Mat();
        Imgproc.cvtColor(mat, rightColourMat, Imgproc.COLOR_BGRA2BGR);
        matToBitmap(rightColourMat, bitmap);
        return bitmap;*/

        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        matToBitmap(mat, bitmap);
        return bitmap;
    }

    public static Mat GetMatFromBitmap(Bitmap bitmap) {
        Mat mat = new Mat();
        bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2BGRA);
        return mat;
    }

    public static void getLineFromTopRectMask(Mat mask, Mat lines, int minLineLength) {
//        Imgproc.cvtColor(mask, mask, Imgproc.COLOR_RGB2GRAY);

//        Imgproc.threshold(gray, gray, 125, 255, 0);
/*
        Mat canny = new Mat();
        int lowThreshold = 50;
        int ratio = 3;
        Imgproc.Canny(gray, canny, lowThreshold, lowThreshold * ratio);
        Imgproc.blur(gray, gray, new Size(3, 3));
*/
//        Imgproc.threshold(gray, canny, 125, 255, 0);

        Imgproc.HoughLinesP(mask, lines, 1, Math.PI / 180, 80, minLineLength, 10);
    }

    public static void drawLinesOnImage(Mat lines, Mat image) {
        for (int i = 0; i < lines.rows(); i++) {
            double[] val = lines.get(i, 0);
            Imgproc.line(image, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(0, 255, 0), 2);
        }
    }

/*    public static void DrawMatInImageBox(Mat matImage, ImageView imagebox) {
        String location = Main.imagesLoc + "/tmp.jpg";
        Imgcodecs.imwrite(location, matImage);

        // Create Image and ImageView objects
        Image image = new Image("file:" + location);
        imagebox.setImage(image);
    }

    public static void DrawCannyMatInImageBox(Mat mat, ImageView imagebox) {
        DrawCannyMatInImageBox(mat, imagebox, 125, 255, 0, 50, 150, 3, false);
    }

    public static void DrawCannyMatInImageBox(Mat mat, ImageView imagebox, int threshold, int thresholdMax, int thresholdType,
                                              int cannyThreshold1, int cannyThreshold2, int cannyApertureSize, boolean cannyL2gradient) {
        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_RGB2GRAY);
        // Imgproc.blur(gray, gray, new Size(3, 3));

        Imgproc.threshold(gray, gray, threshold, thresholdMax, thresholdType);
        Imgproc.Canny(gray, gray, cannyThreshold1, cannyThreshold2, cannyApertureSize, cannyL2gradient);

        DrawMatInImageBox(gray, imagebox);
    }

    public static void DrawThresholdedMatInImageBox(Mat mat, ImageView imagebox) {
        DrawThresholdedMatInImageBox(mat, imagebox, 125, 255, 0);
    }

    public static void DrawThresholdedMatInImageBox(Mat mat, ImageView imagebox, int threshold, int thresholdMax, int thresholdType) {
        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.blur(gray, gray, new Size(5, 5));
        Imgproc.threshold(gray, gray, threshold, thresholdMax, thresholdType);
        DrawMatInImageBox(gray, imagebox);
    }*/



/*    // Warning, this is a hack because it has to save it first!
    private static void DrawMatInNewWindow(Mat matImage) {
        String location = Main.imagesLoc + "/Dota-mask.jpg";
        Imgcodecs.imwrite(location, matImage);

        // Create Image and ImageView objects
        Image image = new Image("file:" + location);
        ImageView imageView = new ImageView();
        imageView.setImage(image);

        // Display image on screen
        StackPane root = new StackPane();
        root.getChildren().add(imageView);
        Scene scene = new Scene(root, 743, 317);

        primaryStage.setTitle("Image Read Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }*/
/*
    private static void DrawImageInNewWindow(String imageLocation) {
        // Create Image and ImageView objects
        Image image = new Image("file:" + imageLocation);
        ImageView imageView = new ImageView();
        imageView.setImage(image);

        // Display image on screen
        StackPane root = new StackPane();
        root.getChildren().add(imageView);
        Scene scene = new Scene(root, 743, 317);

        primaryStage.setTitle("Image Read Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }*/

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
