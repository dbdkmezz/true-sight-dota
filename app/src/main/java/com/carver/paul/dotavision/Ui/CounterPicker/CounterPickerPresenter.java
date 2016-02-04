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

import java.util.ArrayList;
import java.util.List;

public class CounterPickerPresenter {
    private CounterPickerFragment mView;

    CounterPickerPresenter(CounterPickerFragment view) {
        mView = view;
    }

    public void showAdvantages(List<String> enemyNames) {

    }

    public void reset() {
        mView.reset();

        List<HeroAndAdvantages> heroesSample = new ArrayList<>();
        heroesSample.add(new HeroAndAdvantages("disruptor"));
        heroesSample.add(new HeroAndAdvantages("licejsghje shjgesjghjsehghse jhgkhekgh"));
        heroesSample.add(new HeroAndAdvantages("poop"));

        for(HeroAndAdvantages hero : heroesSample) {
            mView.addRow(hero.getName(), hero.getAdvantages(), hero.getTotalAdvantage());
        }
    }

    public void hide() {
        mView.hide();
    }

    public void show() {
        mView.show();
    }
}