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

package com.carver.paul.dotavision.Ui.AbilityInfo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carver.paul.dotavision.Ui.AbilityInfo.AbilityInfoCard.AbilityCardView;
import com.carver.paul.dotavision.Models.HeroAbilityInfo;
import com.carver.paul.dotavision.R;

/**
 * This is where the information about the individual abilities is shown.
 *
 * A card is generated for each ability, with cards for stuns, disables, silences and ultimates
 * first, and then cards for all the abilities for each hero in turn.
 *
 * When clicked the cards will expand to show more information about the ability.
 */
public class AbilityInfoFragment extends Fragment {

    private AbilityInfoPresenter mPresenter;
    private LinearLayout mParentLinearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflateView  = inflater.inflate(R.layout.fragment_ability_info, container, false);
        mParentLinearLayout = (LinearLayout) inflateView.findViewById(R.id.layout_results_info);
        mPresenter = new AbilityInfoPresenter(this);
        return inflateView;
    }

    public AbilityInfoPresenter getPresenter() {
        return mPresenter;
    }

    /**
     * Removes all ability cards from the view so that the view will be empty
     */
    public void reset() {
        mParentLinearLayout.removeAllViews();
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
