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

package com.carver.paul.dotavision.Ui.CounterPicker;

import com.carver.paul.dotavision.Models.HeroAndAdvantages;
import com.carver.paul.dotavision.Models.HeroInfo;
import com.carver.paul.dotavision.R;

import java.util.ArrayList;
import java.util.List;

public class CounterPickerPresenter {
    private CounterPickerFragment mView;
    private int mRoleFilter = R.string.all_roles;
    private List<HeroAndAdvantages> mHeroesAndAdvantages = new ArrayList<>();

    CounterPickerPresenter(CounterPickerFragment view) {
        mView = view;
    }

    public void showAdvantages(List<HeroAndAdvantages> heroesAndAdvantages,
                               List<HeroInfo> enemyHeroes) {
        mHeroesAndAdvantages = heroesAndAdvantages;
        showAdvantages();
    }

    public void reset() {
        mView.reset();
    }

    public void hide() {
        mView.hide();
    }

    public void show() {
        mView.show();
    }

    protected void setRoleFilter(int roleFilter) {
        if(roleFilter == mRoleFilter) {
            return;
        } else {
            mRoleFilter = roleFilter;
            showAdvantages();
        }
    }

    private void showAdvantages() {
        reset();

        for(HeroAndAdvantages hero : mHeroesAndAdvantages) {
            if(mRoleFilter == R.string.all_roles
                    || (mRoleFilter == R.string.carry_role && hero.isCarry())
                    || (mRoleFilter == R.string.support_role && hero.isSupport())
                    || (mRoleFilter == R.string.mid_role && hero.isMid())) {
                mView.addRow(hero.getName(), hero.getAdvantages(), hero.getTotalAdvantage());
            }
        }
    }
}