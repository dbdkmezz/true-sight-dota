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

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.carver.paul.dotavision.BuildConfig;
import com.carver.paul.dotavision.Models.HeroAndSimilarity;
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

public class SimilarityTest {
    private List<LoadedHeroImage> mHeroes = new ArrayList<>();

    static final public List<Pair<Integer, String>> mHeroIconDrawables;
    static final private String TAG = "SimilarityTest";

    // TODO-someday: There's got to be a better way of saving the hero icon drawables other than a huge list of int and String pairs!

    static {
        mHeroIconDrawables = Arrays.asList(new Pair<>(R.drawable.abaddon_hphover, "abaddon"),
                new Pair<>(R.drawable.alchemist_hphover, "alchemist"),
                new Pair<>(R.drawable.ancient_apparition_hphover, "ancient_apparition"),
                new Pair<>(R.drawable.antimage_hphover, "antimage"),
                new Pair<>(R.drawable.axe_hphover, "axe"),
                new Pair<>(R.drawable.bane_hphover, "bane"),
                new Pair<>(R.drawable.batrider_hphover, "batrider"),
                new Pair<>(R.drawable.beastmaster_hphover, "beastmaster"),
                new Pair<>(R.drawable.bloodseeker_hphover, "bloodseeker"),
                new Pair<>(R.drawable.bounty_hunter_hphover, "bounty_hunter"),
                new Pair<>(R.drawable.brewmaster_hphover, "brewmaster"),
                new Pair<>(R.drawable.bristleback_hphover, "bristleback"),
                new Pair<>(R.drawable.broodmother_hphover, "broodmother"),
                new Pair<>(R.drawable.centaur_hphover, "centaur"),
                new Pair<>(R.drawable.chaos_knight_hphover, "chaos_knight"),
                new Pair<>(R.drawable.chen_hphover, "chen"),
                new Pair<>(R.drawable.clinkz_hphover, "clinkz"),
                new Pair<>(R.drawable.crystal_maiden_hphover, "crystal_maiden"),
                new Pair<>(R.drawable.dark_seer_hphover, "dark_seer"),
                new Pair<>(R.drawable.dazzle_hphover, "dazzle"),
                new Pair<>(R.drawable.death_prophet_hphover, "death_prophet"),
                new Pair<>(R.drawable.disruptor_hphover, "disruptor"),
                new Pair<>(R.drawable.doom_bringer_hphover, "doom_bringer"),
                new Pair<>(R.drawable.dragon_knight_hphover, "dragon_knight"),
                new Pair<>(R.drawable.drow_ranger_hphover, "drow_ranger"),
                new Pair<>(R.drawable.earthshaker_hphover, "earthshaker"),
                new Pair<>(R.drawable.earth_spirit_hphover, "earth_spirit"),
                new Pair<>(R.drawable.elder_titan_hphover, "elder_titan"),
                new Pair<>(R.drawable.ember_spirit_hphover, "ember_spirit"),
                new Pair<>(R.drawable.enchantress_hphover, "enchantress"),
                new Pair<>(R.drawable.enigma_hphover, "enigma"),
                new Pair<>(R.drawable.faceless_void_hphover, "faceless_void"),
                new Pair<>(R.drawable.furion_hphover, "furion"),
                new Pair<>(R.drawable.gyrocopter_hphover, "gyrocopter"),
                new Pair<>(R.drawable.huskar_hphover, "huskar"),
                new Pair<>(R.drawable.invoker_hphover, "invoker"),
                new Pair<>(R.drawable.jakiro_hphover, "jakiro"),
                new Pair<>(R.drawable.juggernaut_hphover, "juggernaut"),
                new Pair<>(R.drawable.keeper_of_the_light_hphover, "keeper_of_the_light"),
                new Pair<>(R.drawable.kunkka_hphover, "kunkka"),
                new Pair<>(R.drawable.legion_commander_hphover, "legion_commander"),
                new Pair<>(R.drawable.leshrac_hphover, "leshrac"),
                new Pair<>(R.drawable.lich_hphover, "lich"),
                new Pair<>(R.drawable.life_stealer_hphover, "life_stealer"),
                new Pair<>(R.drawable.lina_hphover, "lina"),
                new Pair<>(R.drawable.lion_hphover, "lion"),
                new Pair<>(R.drawable.lone_druid_hphover, "lone_druid"),
                new Pair<>(R.drawable.luna_hphover, "luna"),
                new Pair<>(R.drawable.lycan_hphover, "lycan"),
                new Pair<>(R.drawable.magnataur_hphover, "magnataur"),
                new Pair<>(R.drawable.medusa_hphover, "medusa"),
                new Pair<>(R.drawable.meepo_hphover, "meepo"),
                new Pair<>(R.drawable.mirana_hphover, "mirana"),
                new Pair<>(R.drawable.morphling_hphover, "morphling"),
                new Pair<>(R.drawable.naga_siren_hphover, "naga_siren"),
                new Pair<>(R.drawable.necrolyte_hphover, "necrolyte"),
                new Pair<>(R.drawable.nevermore_hphover, "nevermore"),
                new Pair<>(R.drawable.night_stalker_hphover, "night_stalker"),
                new Pair<>(R.drawable.nyx_assassin_hphover, "nyx_assassin"),
                new Pair<>(R.drawable.obsidian_destroyer_hphover, "obsidian_destroyer"),
                new Pair<>(R.drawable.ogre_magi_hphover, "ogre_magi"),
                new Pair<>(R.drawable.omniknight_hphover, "omniknight"),
                new Pair<>(R.drawable.oracle_hphover, "oracle"),
                new Pair<>(R.drawable.phantom_assassin_hphover, "phantom_assassin"),
                new Pair<>(R.drawable.phantom_lancer_hphover, "phantom_lancer"),
                new Pair<>(R.drawable.phoenix_hphover, "phoenix"),
                new Pair<>(R.drawable.puck_hphover, "puck"),
                new Pair<>(R.drawable.pudge_hphover, "pudge"),
                new Pair<>(R.drawable.pugna_hphover, "pugna"),
                new Pair<>(R.drawable.queenofpain_hphover, "queenofpain"),
                new Pair<>(R.drawable.rattletrap_hphover, "rattletrap"),
                new Pair<>(R.drawable.razor_hphover, "razor"),
                new Pair<>(R.drawable.riki_hphover, "riki"),
                new Pair<>(R.drawable.rubick_hphover, "rubick"),
                new Pair<>(R.drawable.sand_king_hphover, "sand_king"),
                new Pair<>(R.drawable.shadow_demon_hphover, "shadow_demon"),
                new Pair<>(R.drawable.shadow_shaman_hphover, "shadow_shaman"),
                new Pair<>(R.drawable.shredder_hphover, "shredder"),
                new Pair<>(R.drawable.silencer_hphover, "silencer"),
                new Pair<>(R.drawable.skeleton_king_hphover, "skeleton_king"),
                new Pair<>(R.drawable.skywrath_mage_hphover, "skywrath_mage"),
                new Pair<>(R.drawable.slardar_hphover, "slardar"),
                new Pair<>(R.drawable.slark_hphover, "slark"),
                new Pair<>(R.drawable.sniper_hphover, "sniper"),
                new Pair<>(R.drawable.spectre_hphover, "spectre"),
                new Pair<>(R.drawable.spirit_breaker_hphover, "spirit_breaker"),
                new Pair<>(R.drawable.storm_spirit_hphover, "storm_spirit"),
                new Pair<>(R.drawable.sven_hphover, "sven"),
                new Pair<>(R.drawable.techies_hphover, "techies"),
                new Pair<>(R.drawable.templar_assassin_hphover, "templar_assassin"),
                new Pair<>(R.drawable.terrorblade_hphover, "terrorblade"),
                new Pair<>(R.drawable.tidehunter_hphover, "tidehunter"),
                new Pair<>(R.drawable.tinker_hphover, "tinker"),
                new Pair<>(R.drawable.tiny_hphover, "tiny"),
                new Pair<>(R.drawable.treant_hphover, "treant"),
                new Pair<>(R.drawable.troll_warlord_hphover, "troll_warlord"),
                new Pair<>(R.drawable.tusk_hphover, "tusk"),
                new Pair<>(R.drawable.undying_hphover, "undying"),
                new Pair<>(R.drawable.ursa_hphover, "ursa"),
                new Pair<>(R.drawable.vengefulspirit_hphover, "vengefulspirit"),
                new Pair<>(R.drawable.venomancer_hphover, "venomancer"),
                new Pair<>(R.drawable.viper_hphover, "viper"),
                new Pair<>(R.drawable.visage_hphover, "visage"),
                new Pair<>(R.drawable.warlock_hphover, "warlock"),
                new Pair<>(R.drawable.weaver_hphover, "weaver"),
                new Pair<>(R.drawable.windrunner_hphover, "windrunner"),
                new Pair<>(R.drawable.winter_wyvern_hphover, "winter_wyvern"),
                new Pair<>(R.drawable.wisp_hphover, "wisp"),
                new Pair<>(R.drawable.witch_doctor_hphover, "witch_doctor"),
                new Pair<>(R.drawable.zuus_hphover, "zuus"),
                new Pair<>(R.drawable.arc_warden_hphover, "arc_warden"));
                //new Pair<>(R.drawable.blank_hero, "blank_hero"));
    }

    public int NumberOfHeroesLoaded() {
        return mHeroes.size();
    }

    /**
     * This constructor sets loading all the heroes needed for the comparison work
     * @param context
     */
    public SimilarityTest(Context context) {
        if (BuildConfig.DEBUG) Log.d(TAG, "Loading comparison images.");

        for (Pair<Integer, String> drawableIdAndName : mHeroIconDrawables) {
            LoadedHeroImage hero = new LoadedHeroImage(context, drawableIdAndName.first, drawableIdAndName.second);
            mHeroes.add(hero);
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Loaded " + NumberOfHeroesLoaded() + " hero images.");
        }
    }

    /**
     * Return a list of heroes with the similarity to the photo, ordered with the most similar
     * first.
     * @param photo
     * @return
     */
    public List<HeroAndSimilarity> OrderedListOfTemplateSimilarHeroes(Mat photo) {
        return OrderedListOfTemplateSimilarHeroes(photo, 10, 210, 140, 2, 5, 5); // photo crop % was 0 until 2 Nov
    }

    // the combination of threshold method 2 and template method 5 is incredible!!!!
    // Efficiency improvements available
    // Also, templated matching is very sensitive to rotation, so work needed there
    public List<HeroAndSimilarity> OrderedListOfTemplateSimilarHeroes(Mat photo, int photoCropPercent, int photoThreshold, int originalThreshold,
                                                                          int thresholdType, int blurSize, int method) {
        Mat subMatOfPhoto = CropMatByPercentage(photo, photoCropPercent);
        subMatOfPhoto = EnsureMatSmallerThan(subMatOfPhoto, 126, 71);
        Mat preparedPhoto = BlurAndThresholdMat(subMatOfPhoto, photoThreshold, thresholdType, blurSize);

        List<HeroAndSimilarity> similarityList = new ArrayList<>();
        for (LoadedHeroImage hero : mHeroes) {
            if(hero.comparisonMat == null) {
                hero.comparisonMat =
                        BlurAndThresholdMat(hero.mat, originalThreshold, thresholdType, blurSize);
            }
            Double value = CompareTemplate(preparedPhoto, hero.comparisonMat, method);
            similarityList.add(new HeroAndSimilarity(hero, value));
        }

        Collections.sort(similarityList);
        if (method != Imgproc.TM_SQDIFF && method != Imgproc.TM_SQDIFF_NORMED)
            Collections.reverse(similarityList);

        if(isMissingHero(preparedPhoto)) {
            similarityList.add(0, new HeroAndSimilarity(LoadedHeroImage.newMissingHero(), 0));
        } else {
            similarityList.add(2, new HeroAndSimilarity(LoadedHeroImage.newMissingHero(), 0));
        }

        return similarityList;
    }

    /**
     * Returns true if mat is of just a blank space -- i.e. no hero has been picked for that slot
     * yet
     * @param mat
     * @return
     */
    static private boolean isMissingHero(Mat mat) {
        return( Core.mean(mat).val[0] < 50 );
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

    static private double CompareTemplate(Mat photo, Mat originalHero, int method) {
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




