package com.carver.paul.dotavision.Ui.widget;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.carver.paul.dotavision.Ui.AbilityInfo.AbilityDebuffPresenter;
import com.carver.paul.dotavision.Ui.AbilityInfo.AbilityInfoFragment;
import com.carver.paul.dotavision.Ui.AbilityInfo.AbilityInfoPresenter;
import com.carver.paul.dotavision.Ui.CounterPicker.CounterPickerFragment;
import com.carver.paul.dotavision.Ui.CounterPicker.CounterPickerPresenter;

import java.util.List;

/**
 * Based on code from :
 * http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    static private List<String> mTitles;
    static private final int NUMBER_OF_TABS = 3;

    CounterPickerFragment tab1;
    AbilityInfoFragment<AbilityInfoPresenter> tab2;
    AbilityInfoFragment<AbilityDebuffPresenter> tab3;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, List<String> titles) {
        super(fm);

        mTitles = titles;
        tab1 = new CounterPickerFragment();
        tab2 = new AbilityInfoFragment<>();
        tab2.setPresenter(new AbilityInfoPresenter(tab2));
        tab3 = new AbilityInfoFragment<>();
        tab3.setPresenter(new AbilityDebuffPresenter(tab3));
    }

    public CounterPickerPresenter getCounterPickerPresenter() {
        return tab1.getPresenter();
    }

    public AbilityInfoPresenter getAbilityInfoPresenter() { return tab2.getPresenter(); }

    public AbilityDebuffPresenter getAbilityDebuffPresenter() { return tab3.getPresenter(); }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return tab1;
        } else if(position == 1){
            return tab2;
        } else
            return tab3;

    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NUMBER_OF_TABS;
    }
}