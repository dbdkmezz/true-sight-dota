package com.carver.paul.dotavision.Ui.AbilityInfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carver.paul.dotavision.Models.HeroAbilityInfo;
import com.carver.paul.dotavision.R;
import com.carver.paul.dotavision.Ui.AbilityInfo.AbilityInfoCard.AbilityCardView;

/**
 * Created by paul on 16/10/16.
 */


/**
 * This is where the information about the individual abilities is shown.
 *
 * A card is generated for each ability, with cards for stuns, disables, silences and ultimates
 * first, and then cards for all the abilities for each hero in turn.
 *
 * When clicked the cards will expand to show more information about the ability.
 */
public abstract class AbilityFragment extends Fragment {

    protected LinearLayout mParentLinearLayout;

    /**
     * Removes all ability cards from the view so that the view will be empty
     */
    public void reset() {
        mParentLinearLayout.removeAllViews();
    }

    protected void hide() {
        mParentLinearLayout.setVisibility(View.GONE);
    }

    protected void show() {
        mParentLinearLayout.setVisibility(View.VISIBLE);
    }

    protected void addHeading(int stringInt) {
        addHeading(getString(stringInt));
    }

    protected void addHeading(String string) {
        addText(string, R.layout.item_ability_info_heading);
    }

    protected void addAbilityText(int stringInt) {
        addAbilityText(getString(stringInt));
    }

    protected void addAbilityText(String string) {
        addText(string, R.layout.item_ability_info_text);
    }

    protected void addAbilityCard(HeroAbilityInfo ability, boolean showHeroName) {
        addAbilityCard(ability, showHeroName, -1);
    }

    protected void addAbilityCard(HeroAbilityInfo ability, boolean showHeroName, int abilityType) {
        AbilityCardView card = new AbilityCardView(getActivity(), ability, showHeroName, abilityType);
        mParentLinearLayout.addView(card);
    }

    private void addText(String string, int layout) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(layout, mParentLinearLayout, false);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(string);
        mParentLinearLayout.addView(view);
    }
}
