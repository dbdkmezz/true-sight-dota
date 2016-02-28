package com.carver.paul.dotavision.Ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * This is a ViewPager which grows to match the height of its content. Based on code from
 * StackOverflow.
 */
public class ExpandingViewPager extends ViewPager {

    public ExpandingViewPager(Context context) {
        super(context);
    }

    public ExpandingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = 0;
        for(int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if(h > height) height = h;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
