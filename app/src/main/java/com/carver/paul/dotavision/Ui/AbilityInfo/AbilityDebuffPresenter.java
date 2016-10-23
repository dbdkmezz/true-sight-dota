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
import com.carver.paul.dotavision.Ui.IInfoPresenter_P;

import java.util.ArrayList;
import java.util.List;

public class AbilityDebuffPresenter implements IInfoPresenter_Data, IInfoPresenter_P {
    private AbilityInfoFragment mView;

    public AbilityDebuffPresenter(AbilityInfoFragment view) {
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

        List<HeroAbilityInfo> piercesSpellImmunity = new ArrayList<>();
        List<HeroAbilityInfo> notPiercesSpellImmunity = new ArrayList<>();

        List<HeroAbilityInfo> removedByStrongDisepell = new ArrayList<>();
        List<HeroAbilityInfo> notRemovedByStrongDisepell = new ArrayList<>();

        List<HeroAbilityInfo> removedByBasicDisepell = new ArrayList<>();

        heroes = AbilityInfoPresenter.removeDuplicates(heroes);

        for (HeroInfo hero : heroes) {
            for (HeroAbilityInfo ability : hero.abilities) {
                if(ability.piercesSpellImmunity != null) {
                    if(ability.piercesSpellImmunity)
                        piercesSpellImmunity.add(ability.Copy());
                    else
                        notPiercesSpellImmunity.add(ability.Copy());
                }
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
                }
            }
        }

        showAbilities("Pierces spell immunity", "(Such as BKB)", piercesSpellImmunity, HeroAbilityInfo.SPELL_IMMUNITY);
        showAbilities("Blocked by spell immunity", null, notPiercesSpellImmunity, HeroAbilityInfo.SPELL_IMMUNITY);

        showAbilities("Debuff removed by basic dispel", "(Such as Eul's Cyclone)",  removedByBasicDisepell);

        showAbilities("Debuff removed by strong dispel", "(Such as Slark Dark Pact)", removedByStrongDisepell);
        showAbilities("Debuff immune to all dispels", null, notRemovedByStrongDisepell);
    }

    public void setAdvantageData(List<HeroAndAdvantages> advantageData) {}

    public void hide() {
        mView.hide();
    }

    public void show() {
        mView.show();
    }

    private void showAbilities(String heading, String example, List<HeroAbilityInfo> abilities) {
        showAbilities(heading, example, abilities, -1);
    }

    private void showAbilities(String heading, String example, List<HeroAbilityInfo> abilities,
                               int abilityType) {
        mView.addHeading(heading);
        if (abilities.isEmpty()) {
            mView.addAbilityText("None");
        } else {
            if(example != null) mView.addAbilityText(example);
            for (HeroAbilityInfo ability : abilities) {
                mView.addAbilityCard(ability, true, abilityType);
            }
        }
    }
}
