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
import com.carver.paul.dotavision.Models.HeroInfo;
import com.carver.paul.dotavision.R;

import java.util.ArrayList;
import java.util.List;

public class AbilityInfoPresenter {
    private AbilityInfoFragment mView;

    AbilityInfoPresenter(AbilityInfoFragment view) {
        mView = view;
    }

    public void reset() {
        mView.reset();
    }

    public void showHeroAbilities(List<HeroInfo> heroes) {
        mView.reset();

        List<HeroAbilityInfo> stunAbilities = new ArrayList<>();
        List<HeroAbilityInfo> disableAbilities = new ArrayList<>();
        List<HeroAbilityInfo> silenceAbilities = new ArrayList<>();
        List<HeroAbilityInfo> ultimateAbilities = new ArrayList<>();

        heroes = removeDuplicates(heroes);

        for (HeroInfo hero : heroes) {
            for (HeroAbilityInfo ability : hero.abilities) {
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

    private void showStuns(List<HeroAbilityInfo> stunAbilities) {
        mView.addHeading(R.string.stuns);
        if (stunAbilities.isEmpty()) {
            mView.addAbilityText(R.string.no_stuns_found);
        } else {
            for (HeroAbilityInfo ability : stunAbilities) {
                mView.addAbilityCard(ability, true);
            }
        }
    }

    private void showDisables(List<HeroAbilityInfo> disableAbilities) {
        if (!disableAbilities.isEmpty()) {
            mView.addHeading(R.string.disables);

            for (HeroAbilityInfo ability : disableAbilities) {
                mView.addAbilityCard(ability, true);
            }
        }
    }

    private void showSilences(List<HeroAbilityInfo> silenceAbilities) {
        mView.addHeading(R.string.silences);
        if (silenceAbilities.isEmpty()) {
            mView.addAbilityText(R.string.no_silences_found);
        } else {
            for (HeroAbilityInfo ability : silenceAbilities) {
                mView.addAbilityCard(ability, true);
            }
        }
    }

    private void showUltimates(List<HeroAbilityInfo> ultimateAbilities) {
        mView.addHeading(R.string.ultimates);
        for (HeroAbilityInfo ability : ultimateAbilities) {
            mView.addAbilityCard(ability, true);
        }
    }

    private void showAbilitiesForAllHeroes(List<HeroInfo> heroes) {
        for(HeroInfo hero : heroes) {
            mView.addHeading(hero.name);
            for (HeroAbilityInfo ability : hero.abilities) {
                mView.addAbilityCard(ability, false);
            }
        }
    }
}
