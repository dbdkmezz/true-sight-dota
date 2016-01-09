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

package com.carver.paul.dotavision;

import java.util.ArrayList;
import java.util.List;

public class AbilityInfoPresenter {
    private AbilityInfoFragment mView;

    AbilityInfoPresenter(AbilityInfoFragment view) {
        mView = view;
    }

    public void showHeroAbilities(List<HeroInfo> heroes) {
        List<HeroAbility> stunAbilities = new ArrayList<>();
        List<HeroAbility> disableAbilities = new ArrayList<>();
        List<HeroAbility> silenceAbilities = new ArrayList<>();
        List<HeroAbility> ultimateAbilities = new ArrayList<>();

        heroes = removeDuplicates(heroes);

        for (HeroInfo hero : heroes) {
            for (HeroAbility ability : hero.abilities) {
                if (ability.isStun) stunAbilities.add(ability);
                else if (ability.isDisable) disableAbilities.add(ability);
                else if (ability.isSilence) silenceAbilities.add(ability);

                if (ability.isUltimate) ultimateAbilities.add(ability);
            }
        }

        showStuns(stunAbilities);
        showDisables(disableAbilities);
        showSilences(silenceAbilities);
        showUltimates(ultimateAbilities);
        showAbilitiesForAllHeroes(heroes);
    }

    private List<HeroInfo> removeDuplicates(List<HeroInfo> list) {
        List<HeroInfo> listWithoutDuplicates = new ArrayList<>();
        for(HeroInfo hero : list) {
            if(!listWithoutDuplicates.contains(hero)) {
                listWithoutDuplicates.add(hero);
            }
        }
        return listWithoutDuplicates;
    }

    private void showStuns(List<HeroAbility> stunAbilities) {
        mView.addHeading(R.string.stuns);
        if (stunAbilities.isEmpty()) {
            mView.addAbilityText(R.string.no_stuns_found);
        } else {
            for (HeroAbility ability : stunAbilities) {
                mView.addAbilityCard(ability, true);
            }
        }
    }

    private void showDisables(List<HeroAbility> disableAbilities) {
        if (!disableAbilities.isEmpty()) {
            mView.addHeading(R.string.disables);

            for (HeroAbility ability : disableAbilities) {
                mView.addAbilityCard(ability, true);
            }
        }
    }

    private void showSilences(List<HeroAbility> silenceAbilities) {
        mView.addHeading(R.string.silences);
        if (silenceAbilities.isEmpty()) {
            mView.addAbilityText(R.string.no_silences_found);
        } else {
            for (HeroAbility ability : silenceAbilities) {
                mView.addAbilityCard(ability, true);
            }
        }
    }

    private void showUltimates(List<HeroAbility> ultimateAbilities) {
        mView.addHeading(R.string.ultimates);
        for (HeroAbility ability : ultimateAbilities) {
            mView.addAbilityCard(ability, true);
        }
    }

    private void showAbilitiesForAllHeroes(List<HeroInfo> heroes) {
        for(HeroInfo hero : heroes) {
            mView.addHeading(hero.name);
            for (HeroAbility ability : hero.abilities) {
                mView.addAbilityCard(ability, false);
            }
        }
    }
}
