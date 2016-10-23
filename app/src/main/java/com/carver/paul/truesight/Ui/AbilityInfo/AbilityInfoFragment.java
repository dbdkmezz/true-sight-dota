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

package com.carver.paul.truesight.Ui.AbilityInfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carver.paul.truesight.Models.HeroAbilityInfo;
import com.carver.paul.truesight.R;
import com.carver.paul.truesight.Ui.AbilityInfo.AbilityInfoCard.AbilityCardView;

/**
 * This is where the information about the individual abilities is shown.
 *
 * A card is generated for each ability, with cards for stuns, disables, silences and ultimates
 * first, and then cards for all the abilities for each hero in turn.
 *
 * When clicked the cards will expand to show more information about the ability.
 */
public class AbilityInfoFragment<T> extends Fragment {

    private T mPresenter;
    private LinearLayout mParentLinearLayout;

    public void setPresenter(T presenter) {
        mPresenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflateView  = inflater.inflate(R.layout.fragment_ability_info, container, false);
        mParentLinearLayout = (LinearLayout) inflateView.findViewById(R.id.layout_results_info);
        return inflateView;
    }

    public T getPresenter() {
        return mPresenter;
    }

    /**
     * Removes all ability cards from the view so that the view will be empty
     */
    public void reset() {
        mParentLinearLayout.removeAllViews();
    }

    protected void hide() {
        mParentLinearLayout.setVisibility(View.GONE);
    }

    protected void show() {
        mParentLinearLayout.setVisibility(View.VISIBLE);
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

    protected void addAbilityCard(HeroAbilityInfo ability, boolean showHeroName) {
        addAbilityCard(ability, showHeroName, -1);
    }

    protected void addAbilityCard(HeroAbilityInfo ability, boolean showHeroName, int abilityType) {
        AbilityCardView card = new AbilityCardView(getActivity(), ability, showHeroName, abilityType);
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
