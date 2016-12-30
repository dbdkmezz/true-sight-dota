package com.carver.paul.truesight.Ui.AbilityInfo.AbilityInfoCard;

import com.carver.paul.truesight.Models.HeroAbilityInfo;

import java.util.List;

public class TalentsCardPresenter {
    private TalentsCardView mView;
    private List<HeroAbilityInfo.Talent> mTalents;
    //bool to say if the card has full info or not (full info is toggled when clicked)
    private boolean mIsExtended = true;

    protected TalentsCardPresenter(TalentsCardView view, List<HeroAbilityInfo.Talent> talents) {
        mView = view;
        mTalents = talents;
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

    private void setupText() {
        if(mIsExtended)
            for(HeroAbilityInfo.Talent t : mTalents)
                mView.addRow(t.optionOne, String.valueOf(t.level), t.optionTwo);
        else
            mView.removeRows();
    }
}


