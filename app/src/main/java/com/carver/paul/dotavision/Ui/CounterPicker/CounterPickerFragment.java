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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.carver.paul.dotavision.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO-now: make it show when it's loading advantages

public class CounterPickerFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    static private final List<Integer> advantageIds = Arrays.asList(R.id.advantage1,
            R.id.advantage2, R.id.advantage3, R.id.advantage4, R.id.advantage5);
    static private final List<Integer> roleStringIds = Arrays.asList(R.string.all_roles,
            R.string.carry_role, R.string.support_role, R.string.mid_role);

    private CounterPickerPresenter mPresenter;
    private LinearLayout mParentLinearLayout;
    private LinearLayout mItemsLinearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_counter_picker, container, false);
        mParentLinearLayout = (LinearLayout) inflateView.findViewById(R.id.layout_counter_picker);
        mItemsLinearLayout = (LinearLayout) inflateView.findViewById(R.id.layout_counter_picker_items);
        mPresenter = new CounterPickerPresenter(this);

        setupRolesSpinner(inflater, inflateView);

        return inflateView;
    }

    // This is called when an item in the role spinner is selected
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        String role = (String) parent.getItemAtPosition(pos);
        for(int stringId : roleStringIds) {
            if(role.equals(getString(stringId))) {
                mPresenter.setRoleFilter(stringId);
                return;
            }
        }
    }

    // needed to implement AdapterView.OnItemSelectedListener for the Spinner
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public CounterPickerPresenter getPresenter() {
        return mPresenter;
    }

    /**
     * Removes all ability cards from the view so that the view will be empty
     */
    public void reset() {
        mItemsLinearLayout.removeAllViews();
    }

    protected void hide() {
        mParentLinearLayout.setVisibility(View.GONE);
    }

    protected void show() {
        mParentLinearLayout.setVisibility(View.VISIBLE);
    }

    protected void addRow(String name, List<Double> advantages, Double totalAdvantage) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View itemView = inflater.inflate(R.layout.item_counter_picker, mItemsLinearLayout,
                false);

        TextView nameTextView = (TextView) itemView.findViewById(R.id.name);
        nameTextView.setText(name);

        for(int i = 0; i < advantages.size() && i < advantageIds.size(); i++) {
            TextView advTextView = (TextView) itemView.findViewById(advantageIds.get(i));
            advTextView.setText(String.format("%.1f", advantages.get(i)));
        }

        TextView totalAdvTextView = (TextView) itemView.findViewById(R.id.total_advantage);
        totalAdvTextView.setText(String.format("%.1f", totalAdvantage));

        mItemsLinearLayout.addView(itemView);
    }

    private void setupRolesSpinner(LayoutInflater inflater, View inflateView) {
        Spinner spinner = (Spinner) inflateView.findViewById(R.id.spinner_counter_picker);

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(inflater.getContext(),
                R.array.roles_array, android.R.layout.simple_spinner_item);

// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
    }
}