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

package com.carver.paul.dotavision;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Pack200;

/**
 * This is where the information about the individual abilities is shown
 */
public class AbilityInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ability_info, container, false);
    }

    /**
     * remove all the views showing what we already knew
     */
    public void reset() {
        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.layout_results_info);
        layout.removeAllViews();
    }

    public void showHeroAbilities(List<HeroInfo> heroesSeen) {
        List<HeroInfo> heroesSeenWithoutDuplicates = removeDuplicates(heroesSeen);
        AddAllCardsAboutHeroes(heroesSeenWithoutDuplicates);
    }

    private List<HeroInfo> removeDuplicates(List<HeroInfo> list) {
        List<HeroInfo> returnList = new ArrayList<>();
        for(HeroInfo item : list) {
            if(!returnList.contains(item)) {
                returnList.add(item);
            }
        }
        return returnList;
    }

    private void AddAllCardsAboutHeroes(List<HeroInfo> heroesSeen) {
        //TODO-prebeta: don't show disables heading when there aren't any other disables
        AddAbilityHeading("Stuns");
        boolean cardsAdded = AddAbilityCardsForHeroesList(heroesSeen, HeroAbility.STUN);
        if(!cardsAdded)
            AddAbilityText("No stuns found!");

        AddAbilityHeading("Disables");
        cardsAdded = AddAbilityCardsForHeroesList(heroesSeen, HeroAbility.DISABLE_NOT_STUN);
        if(!cardsAdded)
            AddAbilityText("No other disables found!");

        AddAbilityHeading("Silences");
        cardsAdded = AddAbilityCardsForHeroesList(heroesSeen, HeroAbility.SILENCE);
        if(!cardsAdded)
            AddAbilityText("No silences found!");

        AddAbilityHeading("Ultimates");
        AddAbilityCardsForHeroesList(heroesSeen, HeroAbility.ULTIMATE);

        AddAbilityCardsForAllHeroAbilities(heroesSeen);
    }

    private void AddAbilityHeading(String string) {
        if (string == null) return;

        LinearLayout parent = (LinearLayout) getActivity().findViewById(R.id.layout_results_info);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.item_ability_info_heading, parent, false);
        TextView textView = (TextView) v.findViewById(R.id.textView);
        textView.setText(string);
        parent.addView(v);
    }

    private void AddAbilityText(String string) {
        if (string == null) return;

        LinearLayout parent = (LinearLayout) getActivity().findViewById(R.id.layout_results_info);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.item_ability_info_text, parent, false);
        TextView textView = (TextView) v.findViewById(R.id.textView);
        textView.setText(string);
        parent.addView(v);
    }

    /**
     * Adds ability cards for these heroes which are of the specified abilityType
     * @param heroes
     * @param abilityType
     * @return returns true if any cards have been added
     */
    private boolean AddAbilityCardsForHeroesList(List<HeroInfo> heroes, int abilityType) {
        List<HeroAbility> abilities = new ArrayList<>();
        for (HeroInfo hero : heroes) {
            for (HeroAbility ability : hero.abilities) {
                if (abilityType == HeroAbility.STUN && ability.isStun)
                    abilities.add(ability);
                else if (abilityType == HeroAbility.DISABLE_NOT_STUN && ability.isDisable
                        && !ability.isStun)
                    abilities.add(ability);
                else if (abilityType == HeroAbility.SILENCE && ability.isSilence)
                    abilities.add(ability);
                else if (abilityType == HeroAbility.ULTIMATE && ability.isUltimate)
                    abilities.add(ability);
            }
        }

        return AddAbilityCards(abilities, abilityType);
    }

    private boolean AddAbilityCards(List<HeroAbility> abilities) {
        return AddAbilityCards(abilities, -1);
    }

    private boolean AddAbilityCards(List<HeroAbility> abilities, int abilityType) {
        LinearLayout parent = (LinearLayout) getActivity().findViewById(R.id.layout_results_info);
        boolean cardsAdded = false;

        for (HeroAbility ability : abilities) {
            AbilityCard card = new AbilityCard(getActivity(), ability, abilityType);
            parent.addView(card);
            cardsAdded = true;
        }

        return cardsAdded;
    }

    private void AddAbilityCardsForAllHeroAbilities(List<HeroInfo> heroes) {
        for (HeroInfo hero : heroes) {
            AddAbilityHeading(hero.name);
            AddAbilityCards(hero.abilities);
        }
    }
}
