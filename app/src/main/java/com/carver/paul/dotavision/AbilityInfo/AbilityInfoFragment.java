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

package com.carver.paul.dotavision.AbilityInfo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carver.paul.dotavision.AbilityInfo.AbilityInfoCard.AbilityCardView;
import com.carver.paul.dotavision.HeroAbility;
import com.carver.paul.dotavision.HeroInfo;
import com.carver.paul.dotavision.R;

import java.util.List;

/**
 * This is where the information about the individual abilities is shown
 */
public class AbilityInfoFragment extends Fragment {

    private AbilityInfoPresenter mPresenter;
    private LinearLayout mParentLinearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflateView  = inflater.inflate(R.layout.fragment_ability_info, container, false);
        mParentLinearLayout = (LinearLayout) inflateView.findViewById(R.id.layout_results_info);
        mPresenter = new AbilityInfoPresenter(this);
        return inflateView;
    }

    /**
     * Removes all ability cards from the view so that the view is clear
     */
    public void reset() {
        mParentLinearLayout.removeAllViews();
    }

    /**
     * Resets the fragment, removing all old ability cards, and then shows the cards for the heroes
     * in the list heroes
     * @param heroes
     */
    public void showHeroAbilities(List<HeroInfo> heroes) {
        reset();
        mPresenter.showHeroAbilities(heroes);
    }

    protected void addHeading(int stringInt) {
        addHeading(getString(stringInt));
    }

    protected void addHeading(String string) {
        addText(string, R.layout.item_ability_info_heading);
    }

    protected void addAbilityText(int stringInt) {
        addAbilityText(getString(stringInt));
    }

    protected void addAbilityText(String string) {
        addText(string, R.layout.item_ability_info_text);
    }
    protected void addAbilityCard(HeroAbility ability, boolean showHeroName) {
        AbilityCardView card = new AbilityCardView(getActivity(), ability, showHeroName, -1);
        mParentLinearLayout.addView(card);
    }

    private void addText(String string, int layout) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(layout, mParentLinearLayout, false);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(string);
        mParentLinearLayout.addView(view);
    }
}
