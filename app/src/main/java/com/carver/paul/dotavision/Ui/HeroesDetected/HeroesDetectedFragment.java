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

package com.carver.paul.dotavision.Ui.HeroesDetected;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.carver.paul.dotavision.R;
import com.carver.paul.dotavision.Ui.HeroesDetected.HeroesDetectedItem.HeroDetectedItemPresenter;
import com.carver.paul.dotavision.Ui.HeroesDetected.HeroesDetectedItem.HeroDetectedItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * This class shows the heroes which have been found in the image.
 *
 * For each of them we show:
 *
 *   1) The image of the hero we found in the photo.
 *
 *   2) The name of the hero (this is editable by the user to change the hero identified)
 *
 *   3) A horizontal RecyclerView showing all the images of the heroes in the game, in order of how
 *   similar we think they are to the image of the hero in the photo. The user can scroll through
 *   these to select a different hero.
 *
 *   HeroesDetectedFragment uses a HeroDetectedItem for each hero.
 */
//TODO-now: rename HeroesDetectedFragment, it's not all clear!
public class HeroesDetectedFragment extends Fragment {
    private static final String TAG = "HeroesDetectedFragment";

    private HeroesDetectedPresenter mPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mPresenter = new HeroesDetectedPresenter(this);
        return inflater.inflate(R.layout.fragment_found_heroes, container, false);
    }

    public HeroesDetectedPresenter getPresenter() {
        return mPresenter;
    }

    protected List<HeroDetectedItemPresenter> createHeroDetectedViews(int totalHeroesToShow) {
        LinearLayout parent = (LinearLayout) getActivity().findViewById(
                R.id.layout_found_hero_pictures);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        //TODO-now: fix screenwidth code, will make rotation impossible!
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;

        List<HeroDetectedItemPresenter> presenters = new ArrayList<>();

        for (int i = 0; i < totalHeroesToShow; i++) {
            HeroDetectedItemView heroDetectedItemView =
                    new HeroDetectedItemView(getActivity(), inflater, parent, screenWidth);
            parent.addView(heroDetectedItemView.getView());
            presenters.add(heroDetectedItemView.getPresenter());
        }

        return presenters;
    }

    protected void removeAllViews() {
        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.layout_found_hero_pictures);
        layout.removeAllViews();
    }

    protected void hideKeyboard() {
        // Hide the keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        super.onDestroy();
    }
}