package com.carver.paul.truesight.Ui.widget;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.carver.paul.truesight.Ui.AbilityInfo.AbilityDebuffPresenter;
import com.carver.paul.truesight.Ui.AbilityInfo.AbilityInfoFragment;
import com.carver.paul.truesight.Ui.AbilityInfo.AbilityInfoPresenter;
import com.carver.paul.truesight.Ui.CounterPicker.CounterPickerFragment;
import com.carver.paul.truesight.Ui.CounterPicker.CounterPickerPresenter;

import java.util.List;

/**
 * Based on code from :
 * http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    static private List<String> mTitles;
    static private final int NUMBER_OF_TABS = 3;

    CounterPickerPresenter tab1Presenter;
    AbilityInfoPresenter tab2Presenter;
    AbilityDebuffPresenter tab3Presenter;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, List<String> titles) {
        super(fm);

        mTitles = titles;

        tab1Presenter = new CounterPickerPresenter();
        tab2Presenter = new AbilityInfoPresenter();
        tab3Presenter = new AbilityDebuffPresenter();
    }

    public CounterPickerPresenter getCounterPickerPresenter() {
        return tab1Presenter;
    }

    public AbilityInfoPresenter getAbilityInfoPresenter() { return tab2Presenter; }

    public AbilityDebuffPresenter getAbilityDebuffPresenter() { return tab3Presenter; }

    //This method return the fragment for the every position in the View Pager
    // It is not called when the state is being restored
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            CounterPickerFragment tab1 = new CounterPickerFragment();
            tab1Presenter.setView(tab1);
            tab1.setPresenter(tab1Presenter);
            return tab1;
        } else if (position == 1) {
            AbilityInfoFragment<AbilityInfoPresenter> tab2 = new AbilityInfoFragment<>();
            tab2Presenter.setView(tab2);
            tab2.setPresenter(tab2Presenter);
            return tab2;
        } else {
            AbilityInfoFragment<AbilityDebuffPresenter> tab3 = new AbilityInfoFragment<>();
            tab3Presenter.setView(tab3);
            tab3.setPresenter(tab3Presenter);
            return tab3;
        }
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        // this will load all the fragments again
        super.restoreState(state, loader);

        // since the fragments are now loaded, instantiate can be used because it just returns them
        CounterPickerFragment tab1 = (CounterPickerFragment) instantiateItem(null, 0);
        tab1Presenter.setView(tab1);
        tab1.setPresenter(tab1Presenter);

        AbilityInfoFragment<AbilityInfoPresenter> tab2 = (AbilityInfoFragment) instantiateItem(null, 1);
        tab2Presenter.setView(tab2);
        tab2.setPresenter(tab2Presenter);

        AbilityInfoFragment<AbilityDebuffPresenter> tab3 = (AbilityInfoFragment) instantiateItem(null, 2);
        tab3Presenter.setView(tab3);
        tab3.setPresenter(tab3Presenter);
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