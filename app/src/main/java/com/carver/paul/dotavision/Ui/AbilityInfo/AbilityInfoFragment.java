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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.carver.paul.dotavision.R;

/**
 * This is where the information about the individual abilities is shown.
 *
 * A card is generated for each ability, with cards for stuns, disables, silences and ultimates
 * first, and then cards for all the abilities for each hero in turn.
 *
 * When clicked the cards will expand to show more information about the ability.
 */
public class AbilityInfoFragment extends AbilityFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflateView  = inflater.inflate(R.layout.fragment_ability_info, container, false);
        mParentLinearLayout = (LinearLayout) inflateView.findViewById(R.id.layout_results_info);
        return inflateView;
    }

    private AbilityInfoPresenter mPresenter = new AbilityInfoPresenter(this);

    public AbilityInfoPresenter getPresenter() {
        return mPresenter;
    }
}
