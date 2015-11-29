/**
 * This code is not licensed under the GNU GPL v3 license which governs the rest of this project.
 *
 * It is based on code from humblerookie's github:
 * https://github.com/humblerookie/centerlockrecyclerview/blob/master/CenterLockListener.java
 * And discussed on stackoverflow:
 * http://stackoverflow.com/a/29832723/438013
 */

package com.carver.paul.dotavision;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.carver.paul.dotavision.ImageRecognition.HeroAndSimilarity;

import java.util.List;

public class CenterLockListener extends RecyclerView.OnScrollListener {

    //To avoid recursive calls
    private boolean mAutoSet = true;

    //The pivot to be snapped to
    private int mCenterPivot;

    FoundHeroesFragment.OnHeroChangedListener mHeroChangedListener;
    HeroAndSimilarity mHero;
    List<HeroAndSimilarity> mSimilarityList;
    TextView mHeroNameTextView;

    public CenterLockListener(int center,
                              FoundHeroesFragment.OnHeroChangedListener heroChangedListener,
                              List<HeroAndSimilarity> similarityList,
                              HeroAndSimilarity hero,
                              TextView heroNameTextView){
        mCenterPivot = center;
        mHeroChangedListener = heroChangedListener;
        mSimilarityList = similarityList;
        mHero = hero;
        mHeroNameTextView = heroNameTextView;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();

        if( mCenterPivot == 0 ) {

            // Default pivot , Its a bit inaccurate .
            // Better pass the center pivot as your Center Indicator view's
            // calculated center on it OnGlobalLayoutListener event
            mCenterPivot = lm.getOrientation() == LinearLayoutManager.HORIZONTAL ? ( recyclerView.getLeft() + recyclerView.getRight() ) : ( recyclerView.getTop() + recyclerView.getBottom() );
        }
        if( !mAutoSet ) {

            if( newState == RecyclerView.SCROLL_STATE_IDLE ) {
                //ScrollStoppped
                View view = findCenterView(lm);//get the view nearest to center
                int viewCenter = lm.getOrientation() == LinearLayoutManager.HORIZONTAL ? ( view.getLeft() + view.getRight() )/2 :( view.getTop() + view.getBottom() )/2;
                //compute scroll from center
                int scrollNeeded = viewCenter - mCenterPivot; // Add or subtract any offsets you need here

                if( lm.getOrientation() == LinearLayoutManager.HORIZONTAL ) {

                    recyclerView.smoothScrollBy(scrollNeeded, 0);
                }
                else
                {
                    recyclerView.smoothScrollBy(0, (int) (scrollNeeded));

                }
                mAutoSet =true;
            }
        }
        if( newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING ){
            mAutoSet =false;
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

    }
    private View findCenterView(LinearLayoutManager lm) {

        int minDistance = 0;
        View view = null;
        View returnView = null;
        int foundPos = -1;
        boolean notFound = true;

        for(int i = lm.findFirstVisibleItemPosition(); i <= lm.findLastVisibleItemPosition() && notFound ; i++ ) {

            view=lm.findViewByPosition(i);

            int center = lm.getOrientation() == LinearLayoutManager.HORIZONTAL ? ( view.getLeft() + view.getRight() )/ 2 : ( view.getTop() + view.getBottom() )/ 2;
            int leastDifference = Math.abs(mCenterPivot - center);

            if( leastDifference <= minDistance || i == lm.findFirstVisibleItemPosition())
            {
                minDistance = leastDifference;
                returnView=view;
                foundPos = i;
            }
            else
            {
                notFound=false;

            }
        }

        if(foundPos != -1) {
            reportHeroChange(foundPos - 1);
        }

        return returnView;
    }

    private void reportHeroChange(int pos) {
        HeroAndSimilarity newHero = mSimilarityList.get(pos);
        mHeroChangedListener.onHeroChanged(mHero, newHero);
        mHero = newHero;
    }
}