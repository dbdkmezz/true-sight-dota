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

    private TextView textView;
    private ImageView imageView;
    private HeroAbility ability;
    private int abilityType = -1;

    //bool to say if the card has full info or not (full info is toggled when clicked)
    private boolean isExtended = false;

    public AbilityCard(Context context, HeroAbility ability, int abilityType) {
        super(context);
        this.ability = ability;
        this.abilityType = abilityType;
        init();
    }

    private void init() {
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleIsExtended();
            }
        });

        //TODO-someday: Fix AbilityCard animation so that the image on the card doesn't just jump but animates smoothly like the rest of the card
        LayoutTransition transition = new LayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);
        transition.setDuration(300);
        setLayoutTransition(transition);

        inflate(getContext(), R.layout.item_ability_info, this);

        imageView = (ImageView) findViewById(R.id.imageView);
        int drawable = ImageTools.GetDrawableFromString(ability.imageName);
        if (drawable != -1)
            imageView.setImageResource(drawable);

        textView = (TextView) findViewById(R.id.textView);
        setupTextView();
    }

    private void setupTextView() {
        StringBuilder text = new StringBuilder();
        text.append("<b>" + ability.heroName + ": " + ability.name + "</b>");

        if(isExtended) {
            text.append("<br>" + ability.description);

            //TODO-prebeta: on larger phone sizes show the extended ability details in two columns
            List<String> abilityDetailsToShow = new ArrayList<>();
            abilityDetailsToShow.add(getContext().getString(R.string.cooldown) + ": " + ability.cooldown);
            abilityDetailsToShow.add(getContext().getString(R.string.mana_cost) + ": " + ability.manaCost);
            abilityDetailsToShow.addAll(ability.abilityDetails);

            text.append("<br>");
            for(String detail : abilityDetailsToShow) {
                text.append("<br>" + detail);
            }
        } else {
            if (abilityType == HeroAbility.STUN || abilityType == HeroAbility.SILENCE) {
                String abilityDuration = ability.guessAbilityDuration(abilityType);
                if (abilityDuration != null) {
                    text.append("<br>" + abilityDuration);
                }
            }

            if (ability.cooldown != null) {
                text.append("<br>" + getContext().getString(R.string.cooldown) + ": " + ability.cooldown);
            }
        }

        textView.setText(Html.fromHtml(text.toString()));
    }

    /**
     * Toggles between showing the full card, with all the details, or just the small 2 line preview.
     */
    private void ToggleIsExtended() {
        isExtended = !isExtended;
        setupTextView();
    }
}
