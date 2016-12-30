package com.carver.paul.truesight.Ui.AbilityInfo.AbilityInfoCard;

import android.animation.LayoutTransition;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carver.paul.truesight.Models.HeroAbilityInfo;
import com.carver.paul.truesight.R;

import java.util.List;

/**
 * Created by paul on 30/12/16.
 */

public class TalentsCardView extends FrameLayout {
    private TalentsCardPresenter mPresenter;
    private Context mContext;

    private final int TOTAL_TALENTS_PER_HERO = 4;

    public TalentsCardView(Context context, List<HeroAbilityInfo.Talent> talents) {
        super(context);
        init();
        mContext = context;
        mPresenter = new TalentsCardPresenter(this, talents);
    }

    protected void removeRows() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.talentsLinearLayout);
        if(linearLayout.getChildCount() == TOTAL_TALENTS_PER_HERO + 1)
            linearLayout.removeViews(1, TOTAL_TALENTS_PER_HERO);
    }

    protected void addRow(String optionOne, String level, String optionTwo) {
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.item_talent_line, null);

        TextView optionOneTextView = (TextView) v.findViewById(R.id.optionOwoTextView);
        optionOneTextView.setText(optionOne);
        TextView levelTextView = (TextView) v.findViewById(R.id.levelTextView);
        levelTextView.setText(level);
        TextView optionTwoTextView = (TextView) v.findViewById(R.id.optionTwoTextView);
        optionTwoTextView.setText(optionTwo);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.talentsLinearLayout);
        linearLayout.addView(v);
    }

    private void init() {
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.toggleIsExtended();
            }
        });

        LayoutTransition transition = new LayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);
        transition.setDuration(300);
        setLayoutTransition(transition);
        inflate(getContext(), R.layout.item_talents_info, this);
    }
}
