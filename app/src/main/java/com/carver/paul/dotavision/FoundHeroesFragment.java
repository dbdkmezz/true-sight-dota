/**
 * True Sight for Dota 2
 * Copyright (C) 2015 Paul Broadbent
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */

package com.carver.paul.dotavision;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carver.paul.dotavision.ImageRecognition.HeroAndSimilarity;
import com.carver.paul.dotavision.ImageRecognition.HeroFromPhoto;
import com.carver.paul.dotavision.ImageRecognition.ImageTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Shows the heroes which have been seen in the image, and allows the user to change them.
 */
public class FoundHeroesFragment extends Fragment {
    private static final String TAG = "FoundHeroesFragment";

    private List<Pair<HeroFromPhoto, LinearLayout>> mHeroesAndLayouts;

    private OnHeroChangedListener mHeroChangedListener;
    private List<TextView> mHeroNamesTextViews;
    private List<CenterLockListener> mHeroRecyclerViewListeners;
    private List<RecyclerView> mHeroRecyclerViews;
    private List<String> mAllHeroNames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_found_heroes, container, false);
    }

    /**
     * Ensures the parent activity implements the OnHeroChangedListener
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mHeroChangedListener = (OnHeroChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeroChangedListener");
        }
    }

    /**
     * For use when the user scrolls to select a different hero, changing from oldHero to newHero
     */
    public interface OnHeroChangedListener {
        public void onHeroChanged(int posInList, String newHero);
    }

    public void prepareToShowResults(List<HeroFromPhoto> heroes,
                                List<HeroInfo> heroInfoFromXml) {
        LinearLayout parent = (LinearLayout) getActivity().findViewById(
                R.id.layout_found_hero_pictures);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        mHeroNamesTextViews = new ArrayList<>();
        mHeroRecyclerViewListeners = new ArrayList<>();
        mHeroRecyclerViews = new ArrayList<>();
        mHeroesAndLayouts = new ArrayList<>();

        if(mAllHeroNames == null)
            mAllHeroNames = getHeroNames(heroInfoFromXml);

        int posInList = 0;

        for (HeroFromPhoto hero : heroes) {
            LinearLayout foundPicturesView =
                    (LinearLayout) inflater.inflate(R.layout.item_found_hero_picture, parent, false);

            addLeftHeroImage(foundPicturesView, hero);

            initialiseHeroNameEditText(
                    (AutoCompleteTextView) foundPicturesView.findViewById(R.id.text_hero_name),
                    posInList);

            initialiseHeroSelectRecycler(
                    (RecyclerView) foundPicturesView.findViewById(R.id.recycler_correct_image));

            parent.addView(foundPicturesView);
            mHeroesAndLayouts.add(new Pair<>(hero, foundPicturesView));

            posInList++;
        }
    }

    public void showHero(HeroFromPhoto hero, String displayName) {
        int pos = -1;
        LinearLayout foundPicturesView = null;
        for (Pair<HeroFromPhoto, LinearLayout> pair : mHeroesAndLayouts) {
            pos++;
            if (pair.first == hero) {
                foundPicturesView = pair.second;
                break;
            }
        }

        if (pos == -1 || foundPicturesView == null) {
            throw new RuntimeException("Attempting to show details for a hero in " +
                    "FoundHeroesFragment, but no matching hero found");
        }


        mHeroNamesTextViews.get(pos).setText(displayName);

        completeRecycler((RecyclerView) foundPicturesView.findViewById(R.id.recycler_correct_image),
                hero,
                pos);
    }

    /**
     * Changes the hero which is posInList of those visible. niceHeroName specifies the name of the
     * hero to display in the box. heroImageName is the name of the hero's image, for use when
     * scrolling to the hero with the recyclerView.
     * @param posInList
     * @param niceHeroName
     * @param heroImageName
     */
    public void changeHero(int posInList, String niceHeroName, String heroImageName) {
        if(mHeroRecyclerViewListeners.isEmpty()) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Attempting to change hero, but mHeroRecyclerViewListeners is empty.");
            }
            return;
        }

        mHeroNamesTextViews.get(posInList).setText(niceHeroName);
        mHeroRecyclerViewListeners.get(posInList).setHero(heroImageName);

        // Hide the keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

        // Give the relevant recyclerview focus. This ensures none of the text views have focus
        // after setting the name of a hero
        mHeroRecyclerViews.get(posInList).requestFocus();
    }

    public void reset() {
        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.layout_found_hero_pictures);
        layout.removeAllViews();

        if (BuildConfig.DEBUG && MainActivity.sDebugMode) {
            List<Integer> textViewIds = Arrays.asList(R.id.text_debug_similarity_info, R.id.text_image_debug);
            ResetTextViews(textViewIds);
        }
    }

    private List<String> getHeroNames(List<HeroInfo> heroInfoFromXml) {
        List<String> names = new ArrayList<>();
        for(HeroInfo heroInfo : heroInfoFromXml) {
            names.add(heroInfo.name);
        }
        return names;
    }

/*
    private void showSimilarityInfo(List<HeroAndSimilarity> similarityList) {
        TextView infoText = (TextView) getActivity().findViewById(R.id.text_debug_similarity_info);
        infoText.setText("");
        infoText.setVisibility(View.VISIBLE);
        HeroAndSimilarity matchingHero = similarityList.get(0);

        infoText.append(matchingHero.hero.name + ", " + matchingHero.similarity);

        // poor result, so lets show some alternatives
        if (matchingHero.similarity < 0.65) {
            infoText.append(". (Alternatives: ");
            for (int i = 1; i < 6; i++) {
                infoText.append(similarityList.get(i).hero.name + ","
                        + similarityList.get(i).similarity + ". ");
            }
            infoText.append(")");
        }

        infoText.append(System.getProperty("line.separator")
                + System.getProperty("line.separator"));
    }
*/

    private void ResetTextViews(List<Integer> ids) {
        for (Integer id : ids) {
            TextView tv = (TextView) getActivity().findViewById(id);
            tv.setText("");
            tv.setVisibility(View.GONE);
        }
    }

    /**
     * Adds the picture of the hero on the left (the one which is currently selected)
     * @param foundPicturesView
     * @param hero
     */
    private void addLeftHeroImage(LinearLayout foundPicturesView,
                                  HeroFromPhoto hero) {
        ImageView leftImage = (ImageView) foundPicturesView.findViewById(R.id.image_left);
        leftImage.setImageBitmap(ImageTools.GetBitmapFromMat(hero.image));
    }

    private void initialiseHeroNameEditText(AutoCompleteTextView heroNameTextView,
                                            int posInList) {
        //heroNameTextView.setText("???");
        mHeroNamesTextViews.add(heroNameTextView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, mAllHeroNames);
        heroNameTextView.setAdapter(adapter);

        heroNameTextView.addTextChangedListener(new HeroTextWatcher(
                heroNameTextView.getText().toString(), mHeroChangedListener, mAllHeroNames,
                posInList));
    }

    /**
     * Adds the AutoCompleteTextView showing the name of the currently selected hero, which is
     * also editable by the user to select a different hero.

     */
/*    private void addHeroNameEditText(AutoCompleteTextView heroNameTextView,
                                     String heroName,
                                     int posInList) {
        mHeroNamesTextViews.add(heroNameTextView);

        heroNameTextView.setText(heroName);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, mAllHeroNames);
        heroNameTextView.setAdapter(adapter);

        heroNameTextView.addTextChangedListener(new HeroTextWatcher(
                heroNameTextView.getText().toString(), mHeroChangedListener, mAllHeroNames,
                posInList));
    }*/

    private void initialiseHeroSelectRecycler(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setAdapter(new HeroImageAdapter());
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Adds the recycler view used for changing the hero
     * @param recyclerView
     * @param hero
     * @param posInList
     */
    private void completeRecycler(RecyclerView recyclerView, HeroFromPhoto hero, int posInList) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.setAdapter(new HeroImageAdapter(hero.getSimilarityList()));

        //TODO-beauty: make the FoundHeroesFragment not depend on the screen width for finding
        // its centre, should instead use the Fragment's width. It also goes wrong if the
        // image on the left hand side is too wide! I don't really understand the math here!
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        // This makes the recyclerView automatically lock on the image which has been
        // scrolled to. Thanks stackoverflow and Github :)
        int center = (11 * metrics.widthPixels / 24);
        CenterLockListener recyclerViewListener = new CenterLockListener(center,
                mHeroChangedListener, layoutManager, hero.getSimilarityList(), posInList);
        recyclerView.addOnScrollListener(recyclerViewListener);
        mHeroRecyclerViewListeners.add(recyclerViewListener);
        mHeroRecyclerViews.add(recyclerView);


        // Animate the recycler view in from the right to indicate that you can slide it to
        // change the hero
        int xPos = (int) recyclerView.getX();
        recyclerView.setX(metrics.widthPixels);
        recyclerView.animate()
//                .setStartDelay(50 * posInList)
                .translationXBy(1 + -1 * xPos)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200);
    }
}

class HeroTextWatcher implements TextWatcher {

    private String mCurrentHeroName;
    private final FoundHeroesFragment.OnHeroChangedListener mHeroChangedListener;
    private final List<String> mAllHeroNames;
    private final int mPosInHeroList;

    HeroTextWatcher(String currentHeroName,
                    FoundHeroesFragment.OnHeroChangedListener heroChangedListener,
                    List<String> allHeroNames,
                    int posInHeroList){
        mCurrentHeroName = currentHeroName;
        mHeroChangedListener = heroChangedListener;
        mAllHeroNames = allHeroNames;
        mPosInHeroList = posInHeroList;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!mCurrentHeroName.equalsIgnoreCase(s.toString())
                && containsIgnoreCase(mAllHeroNames, (s.toString()))) {
            mCurrentHeroName = s.toString();
            mHeroChangedListener.onHeroChanged(mPosInHeroList, s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private boolean containsIgnoreCase(List<String> list, String string) {
        for(String s : list)
            if(s.equalsIgnoreCase(string))
                return true;

        return false;
    }
}

class CenterLockListener extends RecyclerView.OnScrollListener {

    //To avoid recursive calls
    private boolean mAutoSet = true;

    //The pivot to be snapped to
    private int mCenterPivot;

    private final FoundHeroesFragment.OnHeroChangedListener mHeroChangedListener;
    private final LinearLayoutManager mLayoutManager;
    private final int mPosInHeroList;
    private final List<HeroAndSimilarity> mSimilarityList;
    private HeroAndSimilarity mCurrentSelectedHero;

    private final String TAG = "CenterLockListener";

    public CenterLockListener(int center,
                              FoundHeroesFragment.OnHeroChangedListener heroChangedListener,
                              LinearLayoutManager layoutManager,
                              List<HeroAndSimilarity> similarityList,
                              int posInHeroList){
        mCenterPivot = center;
        mHeroChangedListener = heroChangedListener;
        mLayoutManager = layoutManager;
        mSimilarityList = similarityList;
        mPosInHeroList = posInHeroList;
        mCurrentSelectedHero = similarityList.get(0);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();

        if(mCenterPivot == 0) {
            // Default pivot , Its a bit inaccurate .
            // Better pass the center pivot as your Center Indicator view's
            // calculated center on it OnGlobalLayoutListener event
            if(lm.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                mCenterPivot = recyclerView.getLeft() + recyclerView.getRight();
            } else {
                mCenterPivot = recyclerView.getTop() + recyclerView.getBottom();
            }
        }
        if(!mAutoSet) {
            if( newState == RecyclerView.SCROLL_STATE_IDLE ) {
                //ScrollStoppped
                View view = findCenterView(lm);//get the view nearest to center

                int viewCenter;
                if(lm.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    viewCenter = (view.getLeft() + view.getRight()) / 2;
                } else {
                    viewCenter = (view.getTop() + view.getBottom()) / 2;
                }

                //compute scroll from center
                int scrollNeeded = viewCenter - mCenterPivot; // Add or subtract any offsets you need here

                if(lm.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    recyclerView.smoothScrollBy(scrollNeeded, 0);
                }
                else
                {
                    recyclerView.smoothScrollBy(0, scrollNeeded);
                }
                mAutoSet = true;
            }
        }
        if(newState == RecyclerView.SCROLL_STATE_DRAGGING
                || newState == RecyclerView.SCROLL_STATE_SETTLING){
            mAutoSet = false;
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

    }

    //TODO-someday: make hero changes scroll nicely
    public void setHero(String heroName) {
        int newPos = 0;
        for(HeroAndSimilarity hero : mSimilarityList) {
            if(hero.hero.name.equals(heroName)) {
                if(hero != mCurrentSelectedHero) {
                    mLayoutManager.scrollToPositionWithOffset(newPos, 0);
                }
                // recyclerView.smoothScrollBy(scrollNeeded, 0);
                return;
            }
            newPos++;
        }

        Log.e(TAG, "Attempting to scroll to " + heroName + " but can't find a hero with that name");
    }

    private View findCenterView(LinearLayoutManager lm) {
        int minDistance = 0;
        View view = null;
        View returnView = null;
        int foundPos = -1;
        boolean notFound = true;

        for(int i = lm.findFirstVisibleItemPosition();
            i <= lm.findLastVisibleItemPosition() && notFound;
            i++) {

            view=lm.findViewByPosition(i);

            int center;
            if(lm.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                center = (view.getLeft() + view.getRight()) / 2;
            } else {
                center = (view.getTop() + view.getBottom()) / 2;
            }

            int leastDifference = Math.abs(mCenterPivot - center);

            if(leastDifference <= minDistance || i == lm.findFirstVisibleItemPosition()) {
                minDistance = leastDifference;
                returnView = view;
                foundPos = i;
            } else {
                notFound = false;
            }
        }

        if(foundPos != -1) {
            reportHeroChange(foundPos - 1);
        }

        return returnView;
    }

    private void reportHeroChange(int pos) {
        mCurrentSelectedHero = mSimilarityList.get(pos);
        mHeroChangedListener.onHeroChanged(mPosInHeroList, mCurrentSelectedHero.hero.name);
    }
}

class HeroImageAdapter extends RecyclerView.Adapter<HeroImageAdapter.ViewHolder> {
    private List<HeroAndSimilarity> mHeroes;

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.image);
/*            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Log.d("HeroImageAdapter", "Element " + getPosition() + " clicked.");
                }
            });*/
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

    public HeroImageAdapter() {
        mHeroes = new ArrayList<>();
        return;
    }

    public HeroImageAdapter(List<HeroAndSimilarity> heroes) {
        mHeroes = heroes;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HeroImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hero_recycler_image, parent, false);
        // google says that here you set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.getImageView().setImageResource(mHeroes.get(position).hero.getImageResource());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mHeroes.size();
    }
}
