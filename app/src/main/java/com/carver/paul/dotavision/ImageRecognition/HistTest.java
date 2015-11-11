package com.carver.paul.dotavision.ImageRecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Pair;

import com.carver.paul.dotavision.MainActivity;
import com.carver.paul.dotavision.R;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.lang.reflect.Array;
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
    private List<HeroWithHist> heroes = new ArrayList<>();

    static final private List<Pair<Integer, String>> heroIconDrawables = new ArrayList<>();

    // TODO: replace hero icon R.drawaable string pairs with just having the R.draawable in the xml

    static {
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.abaddon_hphover, "abaddon"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.alchemist_hphover, "alchemist"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.ancient_apparition_hphover, "ancient_apparition"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.antimage_hphover, "antimage"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.axe_hphover, "axe"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.bane_hphover, "bane"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.batrider_hphover, "batrider"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.beastmaster_hphover, "beastmaster"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.bloodseeker_hphover, "bloodseeker"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.bounty_hunter_hphover, "bounty_hunter"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.brewmaster_hphover, "brewmaster"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.bristleback_hphover, "bristleback"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.broodmother_hphover, "broodmother"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.centaur_hphover, "centaur"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.chaos_knight_hphover, "chaos_knight"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.chen_hphover, "chen"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.clinkz_hphover, "clinkz"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.crystal_maiden_hphover, "crystal_maiden"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.dark_seer_hphover, "dark_seer"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.dazzle_hphover, "dazzle"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.death_prophet_hphover, "death_prophet"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.disruptor_hphover, "disruptor"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.doom_bringer_hphover, "doom_bringer"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.dragon_knight_hphover, "dragon_knight"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.drow_ranger_hphover, "drow_ranger"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.earthshaker_hphover, "earthshaker"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.earth_spirit_hphover, "earth_spirit"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.elder_titan_hphover, "elder_titan"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.ember_spirit_hphover, "ember_spirit"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.enchantress_hphover, "enchantress"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.enigma_hphover, "enigma"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.faceless_void_hphover, "faceless_void"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.furion_hphover, "furion"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.gyrocopter_hphover, "gyrocopter"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.huskar_hphover, "huskar"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.invoker_hphover, "invoker"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.jakiro_hphover, "jakiro"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.juggernaut_hphover, "juggernaut"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.keeper_of_the_light_hphover, "keeper_of_the_light"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.kunkka_hphover, "kunkka"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.legion_commander_hphover, "legion_commander"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.leshrac_hphover, "leshrac"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.lich_hphover, "lich"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.life_stealer_hphover, "life_stealer"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.lina_hphover, "lina"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.lion_hphover, "lion"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.lone_druid_hphover, "lone_druid"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.luna_hphover, "luna"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.lycan_hphover, "lycan"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.magnataur_hphover, "magnataur"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.medusa_hphover, "medusa"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.meepo_hphover, "meepo"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.mirana_hphover, "mirana"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.morphling_hphover, "morphling"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.naga_siren_hphover, "naga_siren"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.necrolyte_hphover, "necrolyte"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.nevermore_hphover, "nevermore"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.night_stalker_hphover, "night_stalker"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.nyx_assassin_hphover, "nyx_assassin"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.obsidian_destroyer_hphover, "obsidian_destroyer"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.ogre_magi_hphover, "ogre_magi"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.omniknight_hphover, "omniknight"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.oracle_hphover, "oracle"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.phantom_assassin_hphover, "phantom_assassin"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.phantom_lancer_hphover, "phantom_lancer"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.phoenix_hphover, "phoenix"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.puck_hphover, "puck"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.pudge_hphover, "pudge"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.pugna_hphover, "pugna"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.queenofpain_hphover, "queenofpain"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.rattletrap_hphover, "rattletrap"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.razor_hphover, "razor"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.riki_hphover, "riki"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.rubick_hphover, "rubick"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.sand_king_hphover, "sand_king"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.shadow_demon_hphover, "shadow_demon"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.shadow_shaman_hphover, "shadow_shaman"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.shredder_hphover, "shredder"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.silencer_hphover, "silencer"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.skeleton_king_hphover, "skeleton_king"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.skywrath_mage_hphover, "skywrath_mage"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.slardar_hphover, "slardar"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.slark_hphover, "slark"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.sniper_hphover, "sniper"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.spectre_hphover, "spectre"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.spirit_breaker_hphover, "spirit_breaker"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.storm_spirit_hphover, "storm_spirit"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.sven_hphover, "sven"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.techies_hphover, "techies"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.templar_assassin_hphover, "templar_assassin"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.terrorblade_hphover, "terrorblade"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.tidehunter_hphover, "tidehunter"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.tinker_hphover, "tinker"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.tiny_hphover, "tiny"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.treant_hphover, "treant"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.troll_warlord_hphover, "troll_warlord"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.tusk_hphover, "tusk"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.undying_hphover, "undying"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.ursa_hphover, "ursa"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.vengefulspirit_hphover, "vengefulspirit"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.venomancer_hphover, "venomancer"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.viper_hphover, "viper"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.visage_hphover, "visage"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.warlock_hphover, "warlock"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.weaver_hphover, "weaver"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.windrunner_hphover, "windrunner"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.winter_wyvern_hphover, "winter_wyvern"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.wisp_hphover, "wisp"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.witch_doctor_hphover, "witch_doctor"));
        heroIconDrawables.add(new Pair<Integer, String>(R.drawable.zuus_hphover, "zuus"));
    }

    //R.drawable.abaddon_hphover, R.drawable.alchemist_hphover, R.drawable.ancient_apparition_hphover, R.drawable.antimage_hphover, R.drawable.axe_hphover, R.drawable.bane_hphover, R.drawable.batrider_hphover, R.drawable.beastmaster_hphover, R.drawable.bloodseeker_hphover, R.drawable.bounty_hunter_hphover, R.drawable.brewmaster_hphover, R.drawable.bristleback_hphover, R.drawable.broodmother_hphover, R.drawable.centaur_hphover, R.drawable.chaos_knight_hphover, R.drawable.chen_hphover, R.drawable.clinkz_hphover, R.drawable.crystal_maiden_hphover, R.drawable.dark_seer_hphover, R.drawable.dazzle_hphover, R.drawable.death_prophet_hphover, R.drawable.disruptor_hphover, R.drawable.doom_bringer_hphover, R.drawable.dragon_knight_hphover, R.drawable.drow_ranger_hphover, R.drawable.earthshaker_hphover, R.drawable.earth_spirit_hphover, R.drawable.elder_titan_hphover, R.drawable.ember_spirit_hphover, R.drawable.enchantress_hphover, R.drawable.enigma_hphover, R.drawable.faceless_void_hphover, R.drawable.furion_hphover, R.drawable.gyrocopter_hphover, R.drawable.huskar_hphover, R.drawable.invoker_hphover, R.drawable.jakiro_hphover, R.drawable.juggernaut_hphover, R.drawable.keeper_of_the_light_hphover, R.drawable.kunkka_hphover, R.drawable.legion_commander_hphover, R.drawable.leshrac_hphover, R.drawable.lich_hphover, R.drawable.life_stealer_hphover, R.drawable.lina_hphover, R.drawable.lion_hphover, R.drawable.lone_druid_hphover, R.drawable.luna_hphover, R.drawable.lycan_hphover, R.drawable.magnataur_hphover, R.drawable.medusa_hphover, R.drawable.meepo_hphover, R.drawable.mirana_hphover, R.drawable.morphling_hphover, R.drawable.naga_siren_hphover, R.drawable.necrolyte_hphover, R.drawable.nevermore_hphover, R.drawable.night_stalker_hphover, R.drawable.nyx_assassin_hphover, R.drawable.obsidian_destroyer_hphover, R.drawable.ogre_magi_hphover, R.drawable.omniknight_hphover, R.drawable.oracle_hphover, R.drawable.phantom_assassin_hphover, R.drawable.phantom_lancer_hphover, R.drawable.phoenix_hphover, R.drawable.puck_hphover, R.drawable.pudge_hphover, R.drawable.pugna_hphover, R.drawable.queenofpain_hphover, R.drawable.rattletrap_hphover, R.drawable.razor_hphover, R.drawable.riki_hphover, R.drawable.rubick_hphover, R.drawable.sand_king_hphover, R.drawable.shadow_demon_hphover, R.drawable.shadow_shaman_hphover, R.drawable.shredder_hphover, R.drawable.silencer_hphover, R.drawable.skeleton_king_hphover, R.drawable.skywrath_mage_hphover, R.drawable.slardar_hphover, R.drawable.slark_hphover, R.drawable.sniper_hphover, R.drawable.spectre_hphover, R.drawable.spirit_breaker_hphover, R.drawable.storm_spirit_hphover, R.drawable.sven_hphover, R.drawable.techies_hphover, R.drawable.templar_assassin_hphover, R.drawable.terrorblade_hphover, R.drawable.tidehunter_hphover, R.drawable.tinker_hphover, R.drawable.tiny_hphover, R.drawable.treant_hphover, R.drawable.troll_warlord_hphover, R.drawable.tusk_hphover, R.drawable.undying_hphover, R.drawable.ursa_hphover, R.drawable.vengefulspirit_hphover, R.drawable.venomancer_hphover, R.drawable.viper_hphover, R.drawable.visage_hphover, R.drawable.warlock_hphover, R.drawable.weaver_hphover, R.drawable.windrunner_hphover, R.drawable.winter_wyvern_hphover, R.drawable.wisp_hphover, R.drawable.witch_doctor_hphover, R.drawable.zuus_hphover);

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
        for(Pair<Integer, String> drawableIdAndName : heroIconDrawables) {
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




