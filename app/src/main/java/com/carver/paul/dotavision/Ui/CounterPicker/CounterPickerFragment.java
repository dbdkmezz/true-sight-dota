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

package com.carver.paul.dotavision.Ui.CounterPicker;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carver.paul.dotavision.R;

import java.util.Arrays;
import java.util.List;

//TODO-now: make it show when it's loading advantages

public class CounterPickerFragment extends Fragment {

    static private final List<Integer> advantageIds = Arrays.asList(R.id.advantage1,
            R.id.advantage2, R.id.advantage3, R.id.advantage4, R.id.advantage5);

    private CounterPickerPresenter mPresenter;
    LinearLayout mParentLinearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_counter_picker, container, false);
        mParentLinearLayout = (LinearLayout) inflateView.findViewById(R.id.layout_counter_picker);
        mPresenter = new CounterPickerPresenter(this);

        return inflateView;
    }

    public CounterPickerPresenter getPresenter() {
        return mPresenter;
    }

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

    protected void addRow(String name, List<Double> advantages, Double totalAdvantage) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View itemView = inflater.inflate(R.layout.item_counter_picker, mParentLinearLayout,
                false);

        TextView nameTextView = (TextView) itemView.findViewById(R.id.name);
        nameTextView.setText(name);

        for(int i = 0; i < advantages.size() && i < advantageIds.size(); i++) {
            TextView advTextView = (TextView) itemView.findViewById(advantageIds.get(i));
            advTextView.setText(String.format("%.1f", advantages.get(i)));
        }

        TextView totalAdvTextView = (TextView) itemView.findViewById(R.id.total_advantage);
        totalAdvTextView.setText(String.format("%.1f", totalAdvantage));

        mParentLinearLayout.addView(itemView);
    }
}