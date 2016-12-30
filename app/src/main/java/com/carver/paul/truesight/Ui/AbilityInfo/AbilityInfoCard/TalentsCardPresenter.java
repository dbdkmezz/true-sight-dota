package com.carver.paul.truesight.Ui.AbilityInfo.AbilityInfoCard;

import com.carver.paul.truesight.Models.HeroAbilityInfo;
import com.carver.paul.truesight.R;

import java.util.List;

/**
 * Created by paul on 30/12/16.
 */

public class TalentsCardPresenter implements IAbilityCardPresenter {
    private AbilityCardView mView;
    private List<HeroAbilityInfo.Talent> mTalents;

    //bool to say if the card has full info or not (full info is toggled when clicked)
    private boolean mIsExtended = false;

    protected TalentsCardPresenter(AbilityCardView view, List<HeroAbilityInfo.Talent> talents) {
        mView = view;
        mTalents = talents;

        setupText();
    }

    public void toggleIsExtended() {
        mIsExtended = !mIsExtended ;
        setupText();
    }

    /**
     * Setups all the text to show in the card and gets the view to show it. If mIsExtended is false
     * this this will just show the ability title and two lines of text. If mIsExtended is true then
     * this will show the full info on the ability.
     */
    private void setupText() {
        StringBuilder text = new StringBuilder();
        text.append("<b>Talents</b>");

        if(mIsExtended) {
            for(HeroAbilityInfo.Talent t : mTalents) {
                text.append(
                        "<br>" + t.optionOne
                        + "<b>" + String.valueOf(t.level) + "</b>"
                        + t.optionTwo);
            }
        }

        mView.setText(text.toString());
    }
}


