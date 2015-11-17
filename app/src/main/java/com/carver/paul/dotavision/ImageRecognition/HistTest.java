package com.carver.paul.dotavision.ImageRecognition;

import android.content.Context;
import android.util.Pair;

import com.carver.paul.dotavision.R;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// http://www.pyimagesearch.com/2014/01/22/clever-girl-a-guide-to-utilizing-color-histograms-for-computer-vision-and-image-search-engines/

public class HistTest {
    private List<HeroWithHist> heroes = new ArrayList<>();

    static final private List<Pair<Integer, String>> heroIconDrawables;

    // TODO-someday: There's got to be a better way of saving the hero icon drawables other than a huge list of int and String pairs!

    static {
        heroIconDrawables = Arrays.asList(new Pair<Integer, String>(R.drawable.abaddon_hphover, "abaddon"),
                new Pair<Integer, String>(R.drawable.alchemist_hphover, "alchemist"),
                new Pair<Integer, String>(R.drawable.ancient_apparition_hphover, "ancient_apparition"),
                new Pair<Integer, String>(R.drawable.antimage_hphover, "antimage"),
                new Pair<Integer, String>(R.drawable.axe_hphover, "axe"),
                new Pair<Integer, String>(R.drawable.bane_hphover, "bane"),
                new Pair<Integer, String>(R.drawable.batrider_hphover, "batrider"),
                new Pair<Integer, String>(R.drawable.beastmaster_hphover, "beastmaster"),
                new Pair<Integer, String>(R.drawable.bloodseeker_hphover, "bloodseeker"),
                new Pair<Integer, String>(R.drawable.bounty_hunter_hphover, "bounty_hunter"),
                new Pair<Integer, String>(R.drawable.brewmaster_hphover, "brewmaster"),
                new Pair<Integer, String>(R.drawable.bristleback_hphover, "bristleback"),
                new Pair<Integer, String>(R.drawable.broodmother_hphover, "broodmother"),
                new Pair<Integer, String>(R.drawable.centaur_hphover, "centaur"),
                new Pair<Integer, String>(R.drawable.chaos_knight_hphover, "chaos_knight"),
                new Pair<Integer, String>(R.drawable.chen_hphover, "chen"),
                new Pair<Integer, String>(R.drawable.clinkz_hphover, "clinkz"),
                new Pair<Integer, String>(R.drawable.crystal_maiden_hphover, "crystal_maiden"),
                new Pair<Integer, String>(R.drawable.dark_seer_hphover, "dark_seer"),
                new Pair<Integer, String>(R.drawable.dazzle_hphover, "dazzle"),
                new Pair<Integer, String>(R.drawable.death_prophet_hphover, "death_prophet"),
                new Pair<Integer, String>(R.drawable.disruptor_hphover, "disruptor"),
                new Pair<Integer, String>(R.drawable.doom_bringer_hphover, "doom_bringer"),
                new Pair<Integer, String>(R.drawable.dragon_knight_hphover, "dragon_knight"),
                new Pair<Integer, String>(R.drawable.drow_ranger_hphover, "drow_ranger"),
                new Pair<Integer, String>(R.drawable.earthshaker_hphover, "earthshaker"),
                new Pair<Integer, String>(R.drawable.earth_spirit_hphover, "earth_spirit"),
                new Pair<Integer, String>(R.drawable.elder_titan_hphover, "elder_titan"),
                new Pair<Integer, String>(R.drawable.ember_spirit_hphover, "ember_spirit"),
                new Pair<Integer, String>(R.drawable.enchantress_hphover, "enchantress"),
                new Pair<Integer, String>(R.drawable.enigma_hphover, "enigma"),
                new Pair<Integer, String>(R.drawable.faceless_void_hphover, "faceless_void"),
                new Pair<Integer, String>(R.drawable.furion_hphover, "furion"),
                new Pair<Integer, String>(R.drawable.gyrocopter_hphover, "gyrocopter"),
                new Pair<Integer, String>(R.drawable.huskar_hphover, "huskar"),
                new Pair<Integer, String>(R.drawable.invoker_hphover, "invoker"),
                new Pair<Integer, String>(R.drawable.jakiro_hphover, "jakiro"),
                new Pair<Integer, String>(R.drawable.juggernaut_hphover, "juggernaut"),
                new Pair<Integer, String>(R.drawable.keeper_of_the_light_hphover, "keeper_of_the_light"),
                new Pair<Integer, String>(R.drawable.kunkka_hphover, "kunkka"),
                new Pair<Integer, String>(R.drawable.legion_commander_hphover, "legion_commander"),
                new Pair<Integer, String>(R.drawable.leshrac_hphover, "leshrac"),
                new Pair<Integer, String>(R.drawable.lich_hphover, "lich"),
                new Pair<Integer, String>(R.drawable.life_stealer_hphover, "life_stealer"),
                new Pair<Integer, String>(R.drawable.lina_hphover, "lina"),
                new Pair<Integer, String>(R.drawable.lion_hphover, "lion"),
                new Pair<Integer, String>(R.drawable.lone_druid_hphover, "lone_druid"),
                new Pair<Integer, String>(R.drawable.luna_hphover, "luna"),
                new Pair<Integer, String>(R.drawable.lycan_hphover, "lycan"),
                new Pair<Integer, String>(R.drawable.magnataur_hphover, "magnataur"),
                new Pair<Integer, String>(R.drawable.medusa_hphover, "medusa"),
                new Pair<Integer, String>(R.drawable.meepo_hphover, "meepo"),
                new Pair<Integer, String>(R.drawable.mirana_hphover, "mirana"),
                new Pair<Integer, String>(R.drawable.morphling_hphover, "morphling"),
                new Pair<Integer, String>(R.drawable.naga_siren_hphover, "naga_siren"),
                new Pair<Integer, String>(R.drawable.necrolyte_hphover, "necrolyte"),
                new Pair<Integer, String>(R.drawable.nevermore_hphover, "nevermore"),
                new Pair<Integer, String>(R.drawable.night_stalker_hphover, "night_stalker"),
                new Pair<Integer, String>(R.drawable.nyx_assassin_hphover, "nyx_assassin"),
                new Pair<Integer, String>(R.drawable.obsidian_destroyer_hphover, "obsidian_destroyer"),
                new Pair<Integer, String>(R.drawable.ogre_magi_hphover, "ogre_magi"),
                new Pair<Integer, String>(R.drawable.omniknight_hphover, "omniknight"),
                new Pair<Integer, String>(R.drawable.oracle_hphover, "oracle"),
                new Pair<Integer, String>(R.drawable.phantom_assassin_hphover, "phantom_assassin"),
                new Pair<Integer, String>(R.drawable.phantom_lancer_hphover, "phantom_lancer"),
                new Pair<Integer, String>(R.drawable.phoenix_hphover, "phoenix"),
                new Pair<Integer, String>(R.drawable.puck_hphover, "puck"),
                new Pair<Integer, String>(R.drawable.pudge_hphover, "pudge"),
                new Pair<Integer, String>(R.drawable.pugna_hphover, "pugna"),
                new Pair<Integer, String>(R.drawable.queenofpain_hphover, "queenofpain"),
                new Pair<Integer, String>(R.drawable.rattletrap_hphover, "rattletrap"),
                new Pair<Integer, String>(R.drawable.razor_hphover, "razor"),
                new Pair<Integer, String>(R.drawable.riki_hphover, "riki"),
                new Pair<Integer, String>(R.drawable.rubick_hphover, "rubick"),
                new Pair<Integer, String>(R.drawable.sand_king_hphover, "sand_king"),
                new Pair<Integer, String>(R.drawable.shadow_demon_hphover, "shadow_demon"),
                new Pair<Integer, String>(R.drawable.shadow_shaman_hphover, "shadow_shaman"),
                new Pair<Integer, String>(R.drawable.shredder_hphover, "shredder"),
                new Pair<Integer, String>(R.drawable.silencer_hphover, "silencer"),
                new Pair<Integer, String>(R.drawable.skeleton_king_hphover, "skeleton_king"),
                new Pair<Integer, String>(R.drawable.skywrath_mage_hphover, "skywrath_mage"),
                new Pair<Integer, String>(R.drawable.slardar_hphover, "slardar"),
                new Pair<Integer, String>(R.drawable.slark_hphover, "slark"),
                new Pair<Integer, String>(R.drawable.sniper_hphover, "sniper"),
                new Pair<Integer, String>(R.drawable.spectre_hphover, "spectre"),
                new Pair<Integer, String>(R.drawable.spirit_breaker_hphover, "spirit_breaker"),
                new Pair<Integer, String>(R.drawable.storm_spirit_hphover, "storm_spirit"),
                new Pair<Integer, String>(R.drawable.sven_hphover, "sven"),
                new Pair<Integer, String>(R.drawable.techies_hphover, "techies"),
                new Pair<Integer, String>(R.drawable.templar_assassin_hphover, "templar_assassin"),
                new Pair<Integer, String>(R.drawable.terrorblade_hphover, "terrorblade"),
                new Pair<Integer, String>(R.drawable.tidehunter_hphover, "tidehunter"),
                new Pair<Integer, String>(R.drawable.tinker_hphover, "tinker"),
                new Pair<Integer, String>(R.drawable.tiny_hphover, "tiny"),
                new Pair<Integer, String>(R.drawable.treant_hphover, "treant"),
                new Pair<Integer, String>(R.drawable.troll_warlord_hphover, "troll_warlord"),
                new Pair<Integer, String>(R.drawable.tusk_hphover, "tusk"),
                new Pair<Integer, String>(R.drawable.undying_hphover, "undying"),
                new Pair<Integer, String>(R.drawable.ursa_hphover, "ursa"),
                new Pair<Integer, String>(R.drawable.vengefulspirit_hphover, "vengefulspirit"),
                new Pair<Integer, String>(R.drawable.venomancer_hphover, "venomancer"),
                new Pair<Integer, String>(R.drawable.viper_hphover, "viper"),
                new Pair<Integer, String>(R.drawable.visage_hphover, "visage"),
                new Pair<Integer, String>(R.drawable.warlock_hphover, "warlock"),
                new Pair<Integer, String>(R.drawable.weaver_hphover, "weaver"),
                new Pair<Integer, String>(R.drawable.windrunner_hphover, "windrunner"),
                new Pair<Integer, String>(R.drawable.winter_wyvern_hphover, "winter_wyvern"),
                new Pair<Integer, String>(R.drawable.wisp_hphover, "wisp"),
                new Pair<Integer, String>(R.drawable.witch_doctor_hphover, "witch_doctor"),
                new Pair<Integer, String>(R.drawable.zuus_hphover, "zuus"));
    }


/*    static {
        heroes = new ArrayList<>();

        File folder = new File(MainActivity.getImagesLocation(), "/hero icons");
        //System.out.println("loading files from: " + folder.getPath());
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                HeroWithHist hero = new HeroWithHist(file.getPath(), file.getName());
                heroes.add(hero);
                //               System.out.println("File " + listOfFiles[i].getName());
            } *//*else if (listOfFiles[i].isDirectory()) {
               System.out.println("Directory " + listOfFiles[i].getName());
            }*//*
        }
    }*/

    public HistTest(Context context) {
        for (Pair<Integer, String> drawableIdAndName : heroIconDrawables) {
            HeroWithHist hero = new HeroWithHist(drawableIdAndName.first, drawableIdAndName.second, context);
            heroes.add(hero);
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

    public List<HeroHistAndSimilarity> OrderedListOfTemplateSimilarHeroes(Mat photo) {
        return OrderedListOfTemplateSimilarHeroes(photo, 10, 210, 140, 2, 5, 5); // photo crop % was 0 until 2 Nov
    }

    // the combination of threshold method 2 and template method 5 is incredible!!!!
    // Efficiency improvements available
    // Also, templated matching is very sensitive to rotation, so work needed there
    public List<HeroHistAndSimilarity> OrderedListOfTemplateSimilarHeroes(Mat photo, int photoCropPercent, int photoThreshold, int originalThreshold,
                                                                          int thresholdType, int blurSize, int method) {
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




