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

import android.animation.LayoutTransition;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.carver.paul.dotavision.ImageRecognition.ImageTools;

import java.util.ArrayList;
import java.util.List;

class AbilityCard extends FrameLayout {

    private TextView mTextView;
    private ImageView mImageView;
    private HeroAbility mAbility;
    private int mAbilityType = -1;

    //bool to say if the card has full info or not (full info is toggled when clicked)
    private boolean isExtended = false;

    public AbilityCard(Context context, HeroAbility ability, int abilityType) {
        super(context);
        mAbility = ability;
        mAbilityType = abilityType;
        init();
    }

    private void init() {
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleIsExtended();
            }
        });

        //TODO-beauty: Fix AbilityCard animation so that the image on the card doesn't just jump but animates smoothly like the rest of the card
        LayoutTransition transition = new LayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);
        transition.setDuration(300);
        setLayoutTransition(transition);

        inflate(getContext(), R.layout.item_ability_info, this);

        mImageView = (ImageView) findViewById(R.id.imageView);
        int drawable = ImageTools.GetDrawableFromString(mAbility.imageName);
        if (drawable != -1)
            mImageView.setImageResource(drawable);

        mTextView = (TextView) findViewById(R.id.textView);
        setupTextView();
    }

    private void setupTextView() {
        StringBuilder text = new StringBuilder();
        text.append("<b>" + mAbility.heroName + ": " + mAbility.name + "</b>");

        if(isExtended) {
            text.append("<br>" + mAbility.description);

            //TODO-beauty: on larger phone sizes show the extended ability details in two columns
            List<String> abilityDetailsToShow = new ArrayList<>();

            if(mAbility.cooldown != null)
                abilityDetailsToShow.add(getContext().getString(R.string.cooldown) + ": " + mAbility.cooldown);
            if(mAbility.manaCost != null)
                abilityDetailsToShow.add(getContext().getString(R.string.mana_cost) + ": " + mAbility.manaCost);
            abilityDetailsToShow.addAll(mAbility.abilityDetails);

            text.append("<br>");
            for(String detail : abilityDetailsToShow) {
                text.append("<br>" + detail);
            }
        } else {
            if (mAbilityType == HeroAbility.STUN
                    || mAbilityType == HeroAbility.DISABLE_NOT_STUN
                    || mAbilityType == HeroAbility.SILENCE) {
                String abilityDuration = mAbility.guessAbilityDuration(mAbilityType);
                if (abilityDuration != null) {
                    text.append("<br>" + abilityDuration);
                }
            }

            if (mAbility.cooldown != null) {
                text.append("<br>" + getContext().getString(R.string.cooldown) + ": " + mAbility.cooldown);
            } else {
                //TODO-someday: put passive abilities in XML, rather than calculating them in app
                for(String abilityDetail : mAbility.abilityDetails) {
                    if(abilityDetail.endsWith("Passive")) {
                        text.append("<br><i>" + getContext().getString(R.string.passive) + "</i>");
                        break;
                    } else if(abilityDetail.endsWith("Passive, Aura")) {
                        text.append("<br><i>" + getContext().getString(R.string.passive_aura) + "</i>");
                        break;
                    }
                }
            }
        }

        mTextView.setText(Html.fromHtml(text.toString()));
    }

    /**
     * Toggles between showing the full card, with all the details, or just the small 2 line preview.
     */
    private void ToggleIsExtended() {
        isExtended = !isExtended;
        setupTextView();
    }
}
