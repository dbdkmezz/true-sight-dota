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

package com.carver.paul.dotavision.Ui.AbilityInfo.AbilityInfoCard;

import com.carver.paul.dotavision.Models.HeroAbilityInfo;
import com.carver.paul.dotavision.ImageRecognition.ImageTools;
import com.carver.paul.dotavision.R;

import java.util.ArrayList;
import java.util.List;

//TODO-beauty: Ability cards are slow -- the UI hangs for a second when we try to draw a lot of them
// Look into whether this is because of the use of StringBuilder here, and if it would be worth
// caching strings as they are loaded.

/**
 * The presenter behind AbilityCardView
 */
class AbilityCardPresenter {
    private AbilityCardView mView;
    private HeroAbilityInfo mAbility;
    private int mAbilityType;
    private boolean mShowHeroName;

    //bool to say if the card has full info or not (full info is toggled when clicked)
    private boolean mIsExtended = false;

    protected AbilityCardPresenter(AbilityCardView view, HeroAbilityInfo ability, boolean showHeroName, int abilityType) {
        mView = view;
        mAbility = ability;
        mShowHeroName = showHeroName;
        mAbilityType = abilityType;

        setupIcon();
        setupText();
    }

    /**
     * Toggles between showing the full card, with all the details on the ability, or just a small
     * two line preview.
     */
    protected void toggleIsExtended() {
        mIsExtended = !mIsExtended ;
        setupText();
    }

    /**
     * Gets the view to show the icon for this ability
     */
    private void setupIcon() {
        int drawable = ImageTools.getDrawableForAbility(mAbility.imageName);
        if (drawable != -1)
            mView.setIcon(drawable);
    }

    /**
     * Setups all the text to show in the card and gets the view to show it. If mIsExtended is false
     * this this will just show the ability title and two lines of text. If mIsExtended is true then
     * this will show the full info on the ability.
     */
    private void setupText() {
        StringBuilder text = new StringBuilder();

        appendTitle(text);

        if(mIsExtended) {
            appendExtendedText(text);
        } else {
            appendTwoLineText(text);
        }

        mView.setText(text.toString());
    }

    private void appendExtendedText(StringBuilder text) {
        text.append("<br>" + mAbility.description);

        //TODO-beauty: on larger phone sizes show the extended ability details in two columns
        List<String> abilityDetailsToShow = new ArrayList<>();

        if (mAbility.cooldown != null)
            abilityDetailsToShow.add(mView.getString(R.string.cooldown) + ": " + mAbility.cooldown);
        if (mAbility.manaCost != null)
            abilityDetailsToShow.add(mView.getString(R.string.mana_cost) + ": " + mAbility.manaCost);

        abilityDetailsToShow.addAll(mAbility.abilityDetails);

        text.append("<br>");
        for (String detail : abilityDetailsToShow) {
            text.append("<br>" + detail);
        }
    }

    private void appendTitle(StringBuilder text) {
        text.append("<b>");
        if (mShowHeroName) {
            text.append(mAbility.heroName + ": ");
        }
        text.append(mAbility.name + "</b>");
    }

    private void appendTwoLineText(StringBuilder text) {
        if (mAbilityType == HeroAbilityInfo.STUN
                || mAbilityType == HeroAbilityInfo.DISABLE_NOT_STUN
                || mAbilityType == HeroAbilityInfo.SILENCE) {
            String abilityDuration = mAbility.guessAbilityDuration(mAbilityType);
            if (abilityDuration != null) {
                text.append("<br>" + abilityDuration);
            }
        }

        if (mAbility.cooldown != null) {
            text.append("<br>" + mView.getString(R.string.cooldown) + ": " + mAbility.cooldown);
        } else {
            //TODO-someday: put passive abilities in XML, rather than calculating them in app
            for (String abilityDetail : mAbility.abilityDetails) {
                if (abilityDetail.endsWith("Passive")) {
                    text.append("<br><i>" + mView.getString(R.string.passive) + "</i>");
                    break;
                } else if (abilityDetail.endsWith("Passive, Aura")) {
                    text.append("<br><i>" + mView.getString(R.string.passive_aura) + "</i>");
                    break;
                }
            }
        }
    }
}
