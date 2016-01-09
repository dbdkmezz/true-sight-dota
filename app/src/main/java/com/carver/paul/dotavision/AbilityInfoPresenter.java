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
