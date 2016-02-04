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
            advTextView.setText(advantages.get(i).toString());
        }

        TextView totalAdvTextView = (TextView) itemView.findViewById(R.id.total_advantage);
        totalAdvTextView.setText(totalAdvantage.toString());

        mParentLinearLayout.addView(itemView);
    }

/*    private void setupRecycler(View inflateView) {
        mRecycler = (RecyclerView) inflateView.findViewById(R.id.recycler_view_counter_picker);

        LinearLayoutManager layoutManager = new LinearLayoutManager(inflateView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        List<HeroAndAdvantages> heroesSample = new ArrayList<>();
        heroesSample.add(new HeroAndAdvantages("disruptor"));
        heroesSample.add(new HeroAndAdvantages("lich"));
        heroesSample.add(new HeroAndAdvantages("poop"));

        mRecycler.setAdapter(new CounterPickerAdapter(heroesSample));
        mRecycler.setLayoutManager(layoutManager);
    }*/

/*    private void completeRecycler() {
*//*
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecycler.setAdapter(new HeroImageAdapter(similarHeroImages));
*//*
    }*/
}

/*
class CounterPickerAdapter extends RecyclerView.Adapter<CounterPickerAdapter.ViewHolder> {
    private List<HeroAndAdvantages> mHeroes;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private LinearLayout mLinearLayout;

        public ViewHolder(LinearLayout v) {
            super(v);
            mLinearLayout = v;
        }

        public void setName(String name) {
            TextView textView = (TextView) mLinearLayout.findViewById(R.id.name);
            textView.setText(name);
        }
    }

*/
/*    public CounterPickerAdapter() {
        mHeroes = new ArrayList<>();
    }
    *//*

    // Provide a suitable constructor (depends on the kind of dataset)
    public CounterPickerAdapter(List<HeroAndAdvantages> heroes) {
        mHeroes = heroes;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CounterPickerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_counter_picker, parent, false);
        // google says that here you set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.setName(mHeroes.get(position).getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mHeroes.size();
    }
}

*/
