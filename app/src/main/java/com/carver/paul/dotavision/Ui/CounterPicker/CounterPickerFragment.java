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

import android.animation.Animator;
import android.support.v4.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.carver.paul.dotavision.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO-next: only show the counter picker first if we're on the hero select screen

//TODO-next: add "role" text to the left of the spinner

public class CounterPickerFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    static private final List<Integer> headingImageViewIds = Arrays.asList(R.id.hero1,
            R.id.hero2, R.id.hero3, R.id.hero4, R.id.hero5);

    static private final List<Integer> advantageTextViewIds = Arrays.asList(R.id.advantage1,
            R.id.advantage2, R.id.advantage3, R.id.advantage4, R.id.advantage5);

    static private final List<Integer> roleStringIds = Arrays.asList(R.string.all_roles,
            R.string.carry_role, R.string.support_role, R.string.mid_role, R.string.roaming_role,
            R.string.off_lane_role, R.string.jungler_role);

    private CounterPickerPresenter mPresenter = new CounterPickerPresenter(this);

    private LinearLayout mMainLinearLayout;
    private TextView mLoadingText;
    private List<View> mRowViews = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_counter_picker, container, false);
        mMainLinearLayout = (LinearLayout) inflateView.findViewById(R.id.layout_counter_picker);
       // mPresenter = new CounterPickerPresenter(this);

        mLoadingText = (TextView) inflateView.findViewById(R.id.text_loading);
        mLoadingText.setVisibility(View.GONE);

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

    public void reset() {
        removeAllRows();
        resetMinimumHeight();
    }

    protected void resetMinimumHeight() {
        mMainLinearLayout.setMinimumHeight(0);
    }

    /**
     * Removes all ability cards from the view so that the view will be empty
     */
    protected void removeAllRows() {
        if(!mRowViews.isEmpty()) {
            // After removing the rows we are likely to add a load of new ones, and if the height
            // shrinks then grows again in a moment then the view will jump up, this stops that
            mMainLinearLayout.setMinimumHeight(mMainLinearLayout.getHeight());
        }

        for(View v: mRowViews) {
            mMainLinearLayout.removeView(v);
        }
        mRowViews.clear();
    }

    /**
     * Shows the loading text and pulses it. Ensures everything else is hidden
     */
    protected void startLoadingAnimation() {
        mLoadingText.setVisibility(View.VISIBLE);
        mLoadingText.setAlpha(1f);

        AlphaAnimation pulseAlphaAnimation = new AlphaAnimation(0.2f, 1f);
        pulseAlphaAnimation.setDuration(300);
        pulseAlphaAnimation.setRepeatCount(Animation.INFINITE);
        pulseAlphaAnimation.setRepeatMode(Animation.REVERSE);
        mLoadingText.startAnimation(pulseAlphaAnimation);
    }

    /**
     * End the loading animation and then inform the presenter that it is complete by calling
     * loadingAnimationFinished
     */
    //TODO-beauty: when I get lambda support remove animation boilerplate code
    protected void endLoadingAnimation() {
        Animation pulseAlphaAnimation = mLoadingText.getAnimation();
        if (pulseAlphaAnimation == null) {
            mLoadingText.setVisibility(View.GONE);
            mPresenter.loadingAnimationFinished();
        } else {
            pulseAlphaAnimation.setRepeatCount(0);

            mLoadingText.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoadingText.setVisibility(View.GONE);
                            mPresenter.loadingAnimationFinished();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        }
    }

    protected void showHeadings(List<Integer> enemyImages) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View headerView = inflater.inflate(R.layout.item_counter_picker_header, mMainLinearLayout,
                false);

        for(int i = 0; i < enemyImages.size(); i++) {
            ImageView imageView = (ImageView) headerView.findViewById(headingImageViewIds.get(i));
            imageView.setImageResource(enemyImages.get(i));
        }

        mMainLinearLayout.addView(headerView);
        mRowViews.add(headerView);
    }

    /**
     * Add a row detailing the advantages a particular hero has over the five in the photo.
     *
     * @param name
     * @param advantages        This is a list of pairs, the string is the value of this hero's
     *                          advantage over that enemy. The boolean is whether or no the text
     *                          should be bold.
     * @param totalAdvantage
     */
    protected void addRow(String name, List<Pair<String, Boolean>> advantages, String totalAdvantage) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if(inflater == null) return;

        View itemView = inflater.inflate(R.layout.item_counter_picker, mMainLinearLayout,
                false);
        if(itemView == null) return;

        TextView nameTextView = (TextView) itemView.findViewById(R.id.name);
        if(nameTextView == null) return;
        nameTextView.setText(name);

        for(int i = 0; i < advantages.size() && i < advantageTextViewIds.size(); i++) {
            TextView advTextView = (TextView) itemView.findViewById(advantageTextViewIds.get(i));
            if(advTextView == null) return;
            advTextView.setText(advantages.get(i).first);

            if(advantages.get(i).second) {
                advTextView.setTypeface(null, Typeface.BOLD);
            }
        }

        TextView totalAdvTextView = (TextView) itemView.findViewById(R.id.total_advantage);
        if(totalAdvantage == null) return;
        totalAdvTextView.setText(totalAdvantage);

        mMainLinearLayout.addView(itemView);
        mRowViews.add(itemView);
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