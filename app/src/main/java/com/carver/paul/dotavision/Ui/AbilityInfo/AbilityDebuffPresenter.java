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

import com.carver.paul.dotavision.Models.HeroAbilityInfo;
import com.carver.paul.dotavision.Models.HeroAndAdvantages;
import com.carver.paul.dotavision.Models.HeroInfo;
import com.carver.paul.dotavision.Models.IInfoPresenter_Data;
import com.carver.paul.dotavision.R;
import com.carver.paul.dotavision.Ui.IInfoPresenter_P;

import java.util.ArrayList;
import java.util.List;

public class AbilityDebuffPresenter implements IInfoPresenter_Data, IInfoPresenter_P {
    private AbilityInfoFragment mView;

    AbilityDebuffPresenter(AbilityInfoFragment view) {
        mView = view;
    }

    public void reset() {
        mView.reset();
    }

    public void prepareForFreshList() {}

    /**
     * Gets the AbilityInfoFragment to show ability cards for all of heroes' abilities
     * @param heroes    The list of heroes whose abilities to show.
     */
    public void setEnemyHeroes(List<HeroInfo> heroes) {
        mView.reset();

        List<HeroAbilityInfo> removedByStrongDisepell = new ArrayList<>();
        List<HeroAbilityInfo> notRemovedByStrongDisepell = new ArrayList<>();

        List<HeroAbilityInfo> removedByBasicDisepell = new ArrayList<>();
        List<HeroAbilityInfo> notRemovedByBasicDisepell = new ArrayList<>();

        heroes = AbilityInfoPresenter.removeDuplicates(heroes);

        for (HeroInfo hero : heroes) {
            for (HeroAbilityInfo ability : hero.abilities) {
                for(HeroAbilityInfo.RemovableBuff b : ability.removableDebuffs) {
                    HeroAbilityInfo abilityCopy = ability.Copy();
                    if(b.description != null)
                        abilityCopy.description += " (" + b.description + ")";
                    if(b.strongDispel)
                        removedByStrongDisepell.add(abilityCopy);
                    else
                        notRemovedByStrongDisepell.add(abilityCopy);

                    if(b.basicDispel)
                        removedByBasicDisepell.add(abilityCopy);
                    else
                        notRemovedByBasicDisepell.add(abilityCopy);
                }
            }
        }

        showAbilities("Removed by basic dispell", "Such as Eul's Cyclone",  removedByBasicDisepell);
        showAbilities("Immune to basic dispell", null, notRemovedByBasicDisepell);

        showAbilities("Removed by strong dispell", "Such as Slark's Dark Pact", removedByStrongDisepell);
        showAbilities("Immune to strong dispell", null, notRemovedByStrongDisepell);
    }

    public void setAdvantageData(List<HeroAndAdvantages> advantageData) {}

    public void hide() {
        mView.hide();
    }

    public void show() {
        mView.show();
    }

    private void showAbilities(String heading, String example, List<HeroAbilityInfo> abilities) {
        mView.addHeading(heading);
        if (abilities.isEmpty()) {
            mView.addAbilityText("None");
        } else {
            if(example != null) mView.addAbilityText(example);
            for (HeroAbilityInfo ability : abilities) {
                mView.addAbilityCard(ability, true);
            }
        }
    }
}
