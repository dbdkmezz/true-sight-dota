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

package com.carver.paul.dotavision.Views;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.carver.paul.dotavision.Presenters.HeroDetectedItemPresenter;
import com.carver.paul.dotavision.Presenters.HeroesDetectedPresenter;
import com.carver.paul.dotavision.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows the heroes which have been seen in the image, and allows the user to change them.
 */
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

    public List<HeroDetectedItemPresenter> createHeroDetectedViews(int totalHeroesToShow) {
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

    public void removeAllViews() {
        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.layout_found_hero_pictures);
        layout.removeAllViews();
    }

    public void hideKeyboard() {
        // Hide the keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }
}