package com.carver.paul.dotavision.Ui.AbilityInfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.carver.paul.dotavision.R;

/**
 * Created by paul on 16/10/16.
 */

public class AbilityDebuffFragment extends AbilityFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflateView  = inflater.inflate(R.layout.fragment_debuff_info, container, false);
        mParentLinearLayout = (LinearLayout) inflateView.findViewById(R.id.layout_results_info);
        return inflateView;
    }

    private AbilityDebuffPresenter mPresenter = new AbilityDebuffPresenter(this);

    public AbilityDebuffPresenter getPresenter() {
        return mPresenter;
    }
}