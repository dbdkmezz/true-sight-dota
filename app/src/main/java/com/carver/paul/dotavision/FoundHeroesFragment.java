/**
 * True Sight for Dota 2
 * Copyright (C) 2015 Paul Broadbent
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */

package com.carver.paul.dotavision;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carver.paul.dotavision.ImageRecognition.HeroAndSimilarity;
import com.carver.paul.dotavision.ImageRecognition.HeroFromPhoto;
import com.carver.paul.dotavision.ImageRecognition.ImageTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows the heroes which have been seen in the image, and allows the user to change them.
 */
public class FoundHeroesFragment extends Fragment {
    OnHeroChangedListener mHeroChangedListener;
    List<TextView> mHeroNamesTextViews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_found_heroes, container, false);
    }

    /**
     * Ensures the parent activity implements the OnHeroChangedListener
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mHeroChangedListener = (OnHeroChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeroChangedListener");
        }
    }

    /**
     * For use when the user scrolls to select a different hero, changing from oldHero to newHero
     */
    public interface OnHeroChangedListener {
        public void onHeroChanged(int posInList, HeroAndSimilarity newHero);
    }

    public void showFoundHeroes(List<HeroFromPhoto> heroes, List<HeroInfo> heroInfoList) {
        LinearLayout parent = (LinearLayout) getActivity().findViewById(
                R.id.layout_found_hero_pictures);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        mHeroNamesTextViews = new ArrayList<>();

        int posInList = 0;
        for (HeroFromPhoto hero : heroes) {
            LinearLayout foundPicturesView = (LinearLayout) inflater.inflate(
                    R.layout.item_found_hero_picture, parent, false);
            ImageView leftImage = (ImageView) foundPicturesView.findViewById(R.id.image_left);
            leftImage.setImageBitmap(ImageTools.GetBitmapFromMat(hero.image));

            RecyclerView recyclerView = (RecyclerView) foundPicturesView.findViewById(
                    R.id.recycler_correct_image);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(new HeroImageAdapter(hero.getSimilarityList()));

            TextView heroNameTextView = (TextView) foundPicturesView.findViewById(
                    R.id.text_hero_name);
            heroNameTextView.setText(heroInfoList.get(posInList).name);
            mHeroNamesTextViews.add(heroNameTextView);

            //TODO-someday: make the FoundHeroesFragment not depend on the screen width for finding
            // its centre, should instead use the Fragment's width
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            // This makes the recyclerView automatically lock on the image which has been
            // scrolled to. Thanks stackoverflow and Github :)
            int center = (11 * metrics.widthPixels / 24);
            recyclerView.addOnScrollListener(new CenterLockListener(center, mHeroChangedListener,
                    hero.getSimilarityList(), posInList));

            parent.addView(foundPicturesView);

            if (BuildConfig.DEBUG && MainActivity.sDebugMode)
                showSimilarityInfo(hero.getSimilarityList());

            posInList++;
        }
    }

    public void changeHeroName(int posInList, String newName) {
        mHeroNamesTextViews.get(posInList).setText(newName);
    }

    private void showSimilarityInfo(List<HeroAndSimilarity> similarityList) {
        TextView infoText = (TextView) getActivity().findViewById(R.id.text_debug_similarity_info);
        infoText.setText("");
        infoText.setVisibility(View.VISIBLE);
        HeroAndSimilarity matchingHero = similarityList.get(0);

        infoText.append(matchingHero.hero.name + ", " + matchingHero.similarity);

        // poor result, so lets show some alternatives
        if (matchingHero.similarity < 0.65) {
            infoText.append(". (Alternatives: ");
            for (int i = 1; i < 6; i++) {
                infoText.append(similarityList.get(i).hero.name + ","
                        + similarityList.get(i).similarity + ". ");
            }
            infoText.append(")");
        }

        infoText.append(System.getProperty("line.separator")
                + System.getProperty("line.separator"));
    }
}
