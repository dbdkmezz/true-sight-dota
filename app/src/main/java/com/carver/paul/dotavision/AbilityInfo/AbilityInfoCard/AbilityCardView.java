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

package com.carver.paul.dotavision.AbilityInfo.AbilityInfoCard;

import android.animation.LayoutTransition;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.carver.paul.dotavision.Models.HeroAbilityInfo;
import com.carver.paul.dotavision.R;

/**
 * AbilityCardView is a card in which the information about an ability is shown.
 *
 * When pressed the card will toggle between an expanded and non-expanded view, show that the full
 * info on tbe ability can be seen when pressed. By default just the three-line non-expanded version
 * will be shown.
 */
public class AbilityCardView extends FrameLayout {

    private AbilityCardPresenter mPresenter;

    /**
     *
     * @param context
     * @param ability
     * @param showHeroName
     * @param abilityType   The type of ability which this card is being used to demonstrate.
     */
    public AbilityCardView(Context context, HeroAbilityInfo ability, boolean showHeroName,
                           int abilityType) {
        super(context);
        init();
        mPresenter = new AbilityCardPresenter(this, ability, showHeroName, abilityType);
    }

    private void init() {
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.toggleIsExtended();
            }
        });

        //TODO-beauty: Fix AbilityCard animation so that the image on the card doesn't just jump
        // but animates smoothly like the rest of the card

        LayoutTransition transition = new LayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);
        transition.setDuration(300);
        setLayoutTransition(transition);

        inflate(getContext(), R.layout.item_ability_info, this);
    }

    protected void setIcon(int drawable) {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(drawable);
    }

    protected void setText(String text) {
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(Html.fromHtml(text));
    }

    protected String getString(int stringInt) {
        return getContext().getString(stringInt);
    }
}
