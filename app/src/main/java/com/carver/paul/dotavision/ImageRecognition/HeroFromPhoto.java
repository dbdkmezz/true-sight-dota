package com.carver.paul.dotavision.ImageRecognition;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.List;

public class HeroFromPhoto {

    private static final double rationHeightToWidthbeforeCuts = 1.8;

    private static final double ratioToCutFromSide = 0.05;
    private static final double ratioToCutFromTop = 0.05;
    // ratioToCutFromBottom may need to be larger because a red box with MMR at the bottom may obscure the image
    private static final double ratioToCutFromBottom = 0.05;

    public Mat image;

    // List of the heroes which are most similar to this hero in the photo, ordered by how simialr
    // they are, with the most similar first.
    private List<HeroAndSimilarity> similarityList = null;

    /**
     * Uses the coloured line above a picture of a hero in the photo to create a new HeroFromPhoto
     *
     * @param line
     * @param backgroundImage
     */
    public HeroFromPhoto(HeroLine line, Mat backgroundImage) {

        if (line.isRealLine == false) {
//            throw new RuntimeException("Trying to create HeroFromPhoto with invalid HeroLine!");
            Rect rect = new Rect(0, 0, 10, 10);
            image = new Mat(backgroundImage, rect);
//            System.out.println("ERROR, trying to create HeroFromPhoto with invalid HeroLine.");
            return;
        }

        double heightWithoutCuts = line.rect.width() / rationHeightToWidthbeforeCuts;
        int left = line.rect.left + (int) (line.rect.width() * ratioToCutFromSide);
        int width = line.rect.width() - (int) (line.rect.width() * 2 * ratioToCutFromSide);
        int top = line.rect.top + line.rect.height() + (int) (heightWithoutCuts * ratioToCutFromTop);
        int finalHeight = (int) heightWithoutCuts - (int) (heightWithoutCuts * ratioToCutFromBottom);

        if (left + width > backgroundImage.width())
            width = backgroundImage.width() - left;
        if (top + finalHeight > backgroundImage.height())
            finalHeight = backgroundImage.height() - top;

        Rect rect = new Rect(left, top, width, finalHeight);

        image = new Mat(backgroundImage, rect);
    }

    public void calcSimilarityList(SimilarityTest similarityTest) {
        similarityList = similarityTest.OrderedListOfTemplateSimilarHeroes(image);
    }

    public List<HeroAndSimilarity> getSimilarityList() {
        if (similarityList == null)
            throw new RuntimeException("Can't get similarity list if not loaded!");
        return similarityList;
    }
}