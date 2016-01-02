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

import rx.Observable;

/**
 * This is where the information about the individual abilities is shown
 */
public class AbilityInfoFragment extends Fragment {

    private List<HeroInfo> mHeroesWithVisibleInfo = new ArrayList<>();
    private LinearLayout mParentLinearLayout;
    private View mStunsVisibleTextView;
    private View mDisablesHeading;
    private View mSilencesVisibleTextView;
    private View mUltimatesHeading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflateView  = inflater.inflate(R.layout.fragment_ability_info, container, false);
        mParentLinearLayout = (LinearLayout) inflateView.findViewById(R.id.layout_results_info);
        return inflateView;
    }

    /**
     * Shows the abilities for just hero
     * @param hero
     */
    public void addHero(HeroInfo hero) {
        AddHeroCards(hero);
    }

    /**
     * Shows the ability cards for all heroes in heroesSeen
     * @param heroes
     */
    public void showAllHeroAbilities(List<HeroInfo> heroes) {
        reset();
        for(HeroInfo hero : heroes) {
            AddHeroCards(hero);
        }
    }

    /**
     * Ensures that no cards about heroes are shown
     */
    public void reset() {
        mParentLinearLayout.removeAllViews();
        mHeroesWithVisibleInfo.clear();
        mStunsVisibleTextView = null;
        mDisablesHeading = null;
        mSilencesVisibleTextView = null;
        mUltimatesHeading = null;
    }

    private void AddHeadings() {
        AddAbilityHeading(getString(R.string.stuns));
        mStunsVisibleTextView = AddAbilityText(getString(R.string.no_stuns_found));

        mDisablesHeading = AddAbilityHeading(getString(R.string.disables));
        mDisablesHeading.setVisibility(View.GONE);

        AddAbilityHeading(getString(R.string.silences));
        mSilencesVisibleTextView = AddAbilityText(getString(R.string.no_silences_found));

        mUltimatesHeading = AddAbilityHeading(getString(R.string.ultimates));
    }

    private void AddHeroCards(HeroInfo hero) {
        if(mHeroesWithVisibleInfo.contains(hero)) {
            return;
        }
        if(mHeroesWithVisibleInfo.isEmpty()) {
            AddHeadings();
        }

        mHeroesWithVisibleInfo.add(hero);

        AddAbilityHeading(hero.name);
        for (HeroAbility ability : hero.abilities) {
            addCardsForAbility(ability);
        }

    }

    private View AddAbilityHeading(String string) {
        if (string == null) return null;

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.item_ability_info_heading, mParentLinearLayout, false);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(string);
        mParentLinearLayout.addView(view);
        return view;
    }

    private View AddAbilityText(String string) {
        if (string == null) return null;

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.item_ability_info_text, mParentLinearLayout, false);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(string);
        mParentLinearLayout.addView(view);
        return view;
    }

    private void addCardsForAbility(HeroAbility ability) {
        if(ability.isStun) {
            int pos = mParentLinearLayout.indexOfChild(mStunsVisibleTextView);
            mStunsVisibleTextView.setVisibility(View.GONE);
            addAbilityCardAtPos(ability, true, HeroAbility.STUN, pos);
        } else if(ability.isDisable){
            int pos = 1 + mParentLinearLayout.indexOfChild(mDisablesHeading);
            mDisablesHeading.setVisibility(View.VISIBLE);
            addAbilityCardAtPos(ability, true, HeroAbility.DISABLE_NOT_STUN, pos);
        } else if(ability.isSilence){
            int pos = mParentLinearLayout.indexOfChild(mSilencesVisibleTextView);
            mSilencesVisibleTextView.setVisibility(View.GONE);
            addAbilityCardAtPos(ability, true, HeroAbility.SILENCE, pos);
        }

        if(ability.isUltimate){
            int pos = 1 + mParentLinearLayout.indexOfChild(mUltimatesHeading);
            addAbilityCardAtPos(ability, true, HeroAbility.STUN, pos);
        }

        addAbilityCardToBottom(ability, false);
    }

    private void addAbilityCardAtPos(HeroAbility ability, boolean showHeroName, int abilityType, int pos) {
        AbilityCard card = new AbilityCard(getActivity(), ability, showHeroName, abilityType);
        mParentLinearLayout.addView(card, pos);
    }

    private void addAbilityCardToBottom(HeroAbility ability, boolean showHeroName) {
        AbilityCard card = new AbilityCard(getActivity(), ability, showHeroName, -1);
        mParentLinearLayout.addView(card);
    }
}
