/**
 * True Sight for Dota 2
 * Copyright (C) 2016 Paul Broadbent
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

package com.carver.paul.dotavision.Ui.HeroesDetected.HeroesDetectedItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.carver.paul.dotavision.R;
import com.carver.paul.dotavision.Ui.HeroesDetected.HeroesDetectedPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class shows a hero which has been found in the image.
 *
 * For the hero we show:
 *
 *   1) The image of the hero we found in the photo.
 *
 *   2) The name of the hero (this is editable by the user to change the hero identified)
 *
 *   3) A horizontal RecyclerView showing all the images of the heroes in the game, in order of how
 *   similar we think they are to the image of the hero in the photo. The user can scroll through
 *   these to select a different hero.
 */
public class HeroDetectedItemView {

    private HeroDetectedItemPresenter mPresenter;

    private Context mActivityContext;
    private LinearLayout mLinearLayout;
    private ImageView mImageUpperHero;
    private LinearLayout mLayoutBottom;
    private AutoCompleteTextView mNameTextView;
    private CenterLockListener mRecyclerViewListener;
    // mRecyclerView holds images of all heroes in the game, so the user can scroll through them
    // to choose a different hero.
    private RecyclerView mRecyclerView;
    private int mScreenWidth;

    public HeroDetectedItemView(Context activityContext,
                                LayoutInflater inflater,
                                LinearLayout parentView,
                                HeroesDetectedPresenter parentPresenter,
                                int screenWidth,
                                boolean showRecycler) {
        mActivityContext = activityContext;
        mScreenWidth = screenWidth;

        mPresenter = new HeroDetectedItemPresenter(this, parentPresenter);

        mLinearLayout =
                (LinearLayout) inflater.inflate(R.layout.item_found_hero_picture, parentView, false);

        mImageUpperHero = (ImageView) mLinearLayout.findViewById(R.id.image_upper_hero);
        mLayoutBottom = (LinearLayout) mLinearLayout.findViewById(R.id.layout_bottom);
        mNameTextView = (AutoCompleteTextView) mLinearLayout.findViewById(R.id.text_hero_name);

        ImageButton clearNameButton =
                (ImageButton) mLinearLayout.findViewById(R.id.button_clear_name);
        clearNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNameTextView.setText("");

                mPresenter.updateFromNameChange("");
            }
        });

        if(showRecycler) {
            mImageUpperHero.setVisibility(View.GONE);
            initialiseHeroSelectRecycler();
        } else {
            mLayoutBottom.setVisibility(View.GONE);
        }
    }

    public LinearLayout getView() {
        return mLinearLayout;
    }

    public HeroDetectedItemPresenter getPresenter() {
        return mPresenter;
    }

    protected void setName(String name) {
        mNameTextView.setText(name);
    }

    protected void setHeroInRecycler(int posInSimilarityList) {
        mRecyclerViewListener.setHero(posInSimilarityList);

        // Give the relevant RecyclerView focus. This ensures none of the text views have focus
        // after setting the name of a hero
        mRecyclerView.requestFocus();
    }

    /**
     * Adds the picture of the hero on the left (the one which is currently selected)
     */
    protected void setHeroImage(Bitmap image) {
        ImageView leftImage = (ImageView) mLinearLayout.findViewById(R.id.image_lower_left);
        leftImage.setImageBitmap(image);
    }

    protected void setupTextAutoCompleteAndChangeListener(String currentHeroName,
                                                      List<String> allHeroNames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivityContext,
                android.R.layout.simple_dropdown_item_1line, allHeroNames);
        mNameTextView.setAdapter(adapter);

        // There's a chance the text in the textview will have changed already, which would be
        // missed by the text changed listener. So we change it back to its initialised value and
        // then after setting up the TextChangedListener change it's value now.
        String currentText = mNameTextView.getText().toString();
        mNameTextView.setText(currentHeroName);

        mNameTextView.addTextChangedListener(new HeroTextWatcher(
                mPresenter,
                currentHeroName,
                allHeroNames));

        mNameTextView.setText(currentText);
    }

    protected boolean recyclerInitialised() {
        return mRecyclerView != null;
    }

    //TODO-beauty, remove the need to intialise recycler

    /**
     * If we don't set the layoutmanager for the recycler immediately then the app crashes
     */
    private void initialiseHeroSelectRecycler() {
        mRecyclerView =
                (RecyclerView) mLinearLayout.findViewById(R.id.recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivityContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setAdapter(new HeroImageAdapter());
        mRecyclerView.setLayoutManager(layoutManager);
    }

/*
    private void showRecycler() {
        mLayoutBottom.setVisibility(View.VISIBLE);
        mImageUpperHero.setVisibility(View.GONE);
    }

    private void hideRecycler() {
        mLayoutBottom.setVisibility(View.GONE);
        mImageUpperHero.setVisibility(View.VISIBLE);
    }
*/

    /**
     * Adds the recycler view used for changing the hero
     * @param similarHeroImages the R.ids for the images to be shown in the recycler (ordered by
     *                          how similar they are to to this hero)
     */
    protected void completeRecycler(List<Integer> similarHeroImages) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.setAdapter(new HeroImageAdapter(similarHeroImages));

        //TODO-beauty: make the HeroesDetectedFragment not depend on the screen width for finding
        // its centre, should instead use the Fragment's width. It also goes wrong if the
        // image on the left hand side is too wide! I don't really understand the math here!

        // This makes the recyclerView automatically lock on the image which has been
        // scrolled to. Thanks stackoverflow and Github :)
        int center = (11 * mScreenWidth / 24);
        mRecyclerViewListener = new CenterLockListener(mPresenter, center, layoutManager);
        mRecyclerView.addOnScrollListener(mRecyclerViewListener);

        // Animate the recycler view in from the right to indicate that you can slide it to
        // change the hero
        mRecyclerView.setX(mScreenWidth);
        mRecyclerView.animate()
                .translationXBy(-1 * mRecyclerView.getWidth())
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200);
    }
}

/**
 * Watches the name of the hero. If the user changes it to the name of another hero this will be
 * reported back to the presenter so that the hero can be changed.
 */
class HeroTextWatcher implements TextWatcher {

    private String mCurrentHeroName;
    private final List<String> mAllHeroNames;
    private final HeroDetectedItemPresenter mPresenter;

    HeroTextWatcher(HeroDetectedItemPresenter presenter,
                    String currentHeroName,
                    List<String> allHeroNames) {
        mCurrentHeroName = currentHeroName;
        mAllHeroNames = allHeroNames;
        mPresenter = presenter;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!mCurrentHeroName.equalsIgnoreCase(s.toString())
                && containsIgnoreCase(mAllHeroNames, (s.toString()))) {
            mCurrentHeroName = s.toString();

            mPresenter.updateFromNameChange(s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private boolean containsIgnoreCase(List<String> list, String string) {
        for (String s : list)
            if (s.equalsIgnoreCase(string))
                return true;

        return false;
    }
}

/**
 * This makes the recyclerView automatically lock on the image which has been scrolled to.
 *
 * Based on code (highly modified now!) from github, via stackoverflow
 * https://github.com/humblerookie/centerlockrecyclerview
 */
class CenterLockListener extends RecyclerView.OnScrollListener {


    private final HeroDetectedItemPresenter mPresenter;
    private final LinearLayoutManager mLayoutManager;
    private final static String TAG = "CenterLockListener";

    //To avoid recursive calls
    private boolean mAutoSet = true;

    //The pivot to be snapped to
    private int mCenterPivot;

    // The position in the recycler view of the hero currently being shown
    private int mCurrentPosition;

    public CenterLockListener(HeroDetectedItemPresenter presenter,
                              int center,
                              LinearLayoutManager layoutManager) {
        mPresenter = presenter;
        mCenterPivot = center;
        mLayoutManager = layoutManager;
        mCurrentPosition = 0;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();

        if (mCenterPivot == 0) {
            // Default pivot , Its a bit inaccurate .
            // Better pass the center pivot as your Center Indicator view's
            // calculated center on it OnGlobalLayoutListener event
            if (lm.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                mCenterPivot = recyclerView.getLeft() + recyclerView.getRight();
            } else {
                mCenterPivot = recyclerView.getTop() + recyclerView.getBottom();
            }
        }
        if (!mAutoSet) {
            //The scrolling of the recycler view has stopped (i.e. the user has stopped scrolling)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                View view = findCenterView(lm);//get the view nearest to center

                int viewCenter;
                if (lm.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    viewCenter = (view.getLeft() + view.getRight()) / 2;
                } else {
                    viewCenter = (view.getTop() + view.getBottom()) / 2;
                }

                //compute scroll from center
                int scrollNeeded = viewCenter - mCenterPivot; // Add or subtract any offsets you need here

                if (lm.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    recyclerView.smoothScrollBy(scrollNeeded, 0);
                } else {
                    recyclerView.smoothScrollBy(0, scrollNeeded);
                }
                mAutoSet = true;
            }
        }
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING
                || newState == RecyclerView.SCROLL_STATE_SETTLING) {
            mAutoSet = false;
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
    }

    /**
     * Scrolls the RecyclerView so that the hero with this position in the list is visible.
     * @param newPosition   The position in the list of hero images to scroll to
     */
    public void setHero(int newPosition) {
        if (mCurrentPosition != newPosition) {
            mLayoutManager.scrollToPositionWithOffset(newPosition, 0);
            mCurrentPosition = newPosition;
        }
    }

    private View findCenterView(LinearLayoutManager lm) {
        int minDistance = 0;
        View view = null;
        View returnView = null;
        Integer newPosition = null;
        boolean notFound = true;

        for (int i = lm.findFirstVisibleItemPosition();
             i <= lm.findLastVisibleItemPosition() && notFound;
             i++) {

            view = lm.findViewByPosition(i);

            int center;
            if (lm.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                center = (view.getLeft() + view.getRight()) / 2;
            } else {
                center = (view.getTop() + view.getBottom()) / 2;
            }

            int leastDifference = Math.abs(mCenterPivot - center);

            if (leastDifference <= minDistance || i == lm.findFirstVisibleItemPosition()) {
                minDistance = leastDifference;
                returnView = view;
                newPosition = i - 1;
            } else {
                notFound = false;
            }
        }

        if (newPosition != null) {
            reportHeroChange(newPosition);
        }

        return returnView;
    }

    private void reportHeroChange(int newPosition) {
        if (mCurrentPosition != newPosition) {
            mCurrentPosition = newPosition;
            mPresenter.updateFromSimilarityListChange(newPosition);
        }
    }
}

class HeroImageAdapter extends RecyclerView.Adapter<HeroImageAdapter.ViewHolder> {
    private List<Integer> mHeroImages;

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.image);
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

    public HeroImageAdapter() {
        mHeroImages = new ArrayList<>();
    }

    public HeroImageAdapter(List<Integer> heroImages) {
        mHeroImages = heroImages;
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
        holder.getImageView().setImageResource(mHeroImages.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mHeroImages.size();
    }
}
