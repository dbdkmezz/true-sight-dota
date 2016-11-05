/**
 * True Sight for Dota 2
 * Copyright (C) 2016 Paul Broadbent
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

//TODO-now: finish off the details in making MainActivity MVP

package com.carver.paul.truesight.Ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.carver.paul.truesight.R;
import com.carver.paul.truesight.Ui.AbilityInfo.AbilityDebuffPresenter;
import com.carver.paul.truesight.Ui.AbilityInfo.AbilityInfoPresenter;
import com.carver.paul.truesight.Ui.CounterPicker.CounterPickerPresenter;
import com.carver.paul.truesight.Ui.DotaCamera.CameraActivity;
import com.carver.paul.truesight.Ui.HeroesDetected.HeroesDetectedFragment;
import com.carver.paul.truesight.Ui.HeroesDetected.HeroesDetectedPresenter;
import com.carver.paul.truesight.Ui.widget.ExpandingViewPager;
import com.carver.paul.truesight.Ui.widget.SlidingTabLayout;
import com.carver.paul.truesight.Ui.widget.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO-beauty: Write tests to check if all hero images load, and if all hero ability icons draw and
// all hero abilities have text

//TODO-now: Check all new ability icons, I think that when I updated them the ones online were not
// right. Also Loan Druid's new ability Savage Roar wasn't even there
// http://www.dota2.com/hero/Lone_Druid/

//TODO-next: test much more for crashes, there is something wrong somewhere. The camera threading??

//TODO-beauty: support rotating the screen!

//TODO-someday: add tab view so you can slide to change hero rather them all being piled up in one place

//TODO-someday: Make card borders 0 on small displays

//TODO-next: Detect empty box for when no hero picked yet (note, this isn't as easy as it may seem!)

//TODO-next: Add rate this app button

//TODO-someday: Add a way to keep both dire and radiant photos

//TODO-next: Add map with creep camp stack timings

//TODO-someday: Add details for escape and invisibility abilities:
// http://dota2.gamepedia.com/Teleport
// http://dota2.gamepedia.com/Invisibility

//TODO-next: find out whether it's ok to have Valve's images on github

//TODO-next: make screenshots in the Google Play Store higher definition

//TODO-someday: learn about layout optimisation
// http://developer.android.com/training/improving-layouts/optimizing-layout.html

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // sDebugMode is true if I want to show extra debug information. It is ignored when
    // BuildConfig.DEBUG is false (i.e. the app is compiled for release)
    public static boolean sDebugMode = false;
    public static final String PHOTO_FILE_NAME = "photo.jpg";
    private static final String TAG = "MainActivity";
    private static final int CAMERA_ACTIVITY_REQUEST_CODE = 100;
    protected static final int MY_PERMISSIONS_REQUEST_CAMERA = 101;
    private static final List<Integer> TAB_TITLE_RES_IDS = Arrays.asList(R.string.counter_picker,
            R.string.hero_abilities, R.string.dispells);

    private MainActivityPresenter mPresenter;

    static {
        // Ensure this library isn't loaded when running robolectric tests, it makes them crash
        if (System.getenv("ROBOLECTRIC") == null) {
            System.loadLibrary("opencv_java3");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ViewPagerAdapter adapter =  createViewPager();

        HeroesDetectedFragment heroesDetectedFragment = (HeroesDetectedFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_found_heroes);
        HeroesDetectedPresenter heroesDetectedPresenter = heroesDetectedFragment.getPresenter();

        CounterPickerPresenter counterPickerPresenter = adapter.getCounterPickerPresenter();
        AbilityInfoPresenter abilityInfoPresenter = adapter.getAbilityInfoPresenter();
        AbilityDebuffPresenter abilityDebuffPresenter = adapter.getAbilityDebuffPresenter();
        mPresenter = new MainActivityPresenter(this, heroesDetectedPresenter,
                abilityInfoPresenter, abilityDebuffPresenter, counterPickerPresenter);

        // Hide the keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCameraActivity();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.about:
                startAboutActivity();
                break;
            case R.id.demo:
                mPresenter.demoPhotoRecognition();
                break;
            case R.id.use_last_photo:
                mPresenter.useLastPhoto();
                break;
        }

/*        } else if (id == R.id.debug_specific_hue) {
            startDebugLineActivity();
        }*/
        /*
        } else if (id == R.id.debug_whole_process) {
            startDebugWholeProcessActivity();*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Handle the buttons in the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_take_photo:
                mPresenter.takePhotoButton();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

/*
    public void startDebugLineActivity() {
        Intent intent = new Intent(this, DebugLineDetectionActivity.class);
        startActivity(intent);
    }

    public void startDebugWholeProcessActivity() {
        Intent intent = new Intent(this, DebugWholeProcessActivity.class);
        startActivity(intent);
    }
*/

    public void startAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void clearButton(View view) {
        mPresenter.clearButton();
    }

    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected void hideTip() {
        View view = findViewById(R.id.text_opening_tip);
        view.setVisibility(View.GONE);
    }

    protected void startCameraActivity() {
        if(checkForCameraPermission()) {
            Intent intent = new Intent(this, CameraActivity.class);
            startActivityForResult(intent, CAMERA_ACTIVITY_REQUEST_CODE);
        }
    }

    protected void showClearFab() {
        View fab = findViewById(R.id.fab_clear);
        fab.setVisibility(View.VISIBLE);
        if (fab.getAlpha() != 1f) {
            fab.animate().alpha(1f).setDuration(150).setStartDelay(50);
        }
    }

    protected void hideClearFab() {
        View fab = findViewById(R.id.fab_clear);
        if(fab.getAlpha() != 0f) {
            fab.animate().alpha(0f).setDuration(150);
        }
    }

    /**
     * Starts the animations to show that photo recognition is running in the background.
     * @param photo
     */
    protected void startHeroRecognitionLoadingAnimations(Bitmap photo) {
        setTopImage(photo);
        findViewById(R.id.text_opening_tip).setVisibility(View.GONE);
        findViewById(R.id.tabs).setVisibility(View.GONE);
        pulseCameraImage();
    }

    /**
     * stopHeroRecognitionLoadingAnimations shows makes the the camera do one final pulse, and
     * then fades it away
     */
    protected void stopHeroRecognitionLoadingAnimations() {
        View processingText = findViewById(R.id.text_processing_image);
        processingText.animate().alpha(0).setDuration(150);

        View cameraImage = findViewById(R.id.image_pulsing_camera);
        Animation animation = cameraImage.getAnimation();
        if (animation != null) {
            animation.setRepeatCount(0);
        }

        cameraImage.animate().alpha(0).setDuration(150);
    }

    protected void showPager(){
        findViewById(R.id.tabs).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mPresenter.doImageRecognitionOnPhoto();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    protected Bitmap getSamplePhoto() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.sample_photo);
    }

    protected void hidePhoto() {
        findViewById(R.id.image_top).setVisibility(View.GONE);
    }

    protected void scrollToTop() {
        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    private boolean checkForCameraPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Based on code from:
     * http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html
     */
    private ViewPagerAdapter createViewPager() {
        List<String> tabTitles = new ArrayList<>();
        for(Integer resId : TAB_TITLE_RES_IDS) {
            tabTitles.add(getString(resId));
        }
        ViewPagerAdapter adapter =  new ViewPagerAdapter(getSupportFragmentManager(), tabTitles);
        ExpandingViewPager pager = (ExpandingViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        return adapter;
    }

    private void setTopImage(Bitmap photoBitmap) {
        ImageView topImage = (ImageView) findViewById(R.id.image_top);
        topImage.setVisibility(View.VISIBLE);
        topImage.setImageBitmap(photoBitmap);
    }

    /**
     * Makes the camera pulse infinitely (will be stopped when loading completes)
     */
    //TODO-nextversion: Make camera do something other than pulse - it implies you should press
    // it!
    private void pulseCameraImage() {
        //Code using the old Animation class, rather than the new ViewPropertyAnimator
        //Infinite repeat is easier to implement this way

        View cameraImage = findViewById(R.id.image_pulsing_camera);
        cameraImage.setVisibility(View.VISIBLE);
        cameraImage.setAlpha(1f);

        ScaleAnimation pulse = new ScaleAnimation(1f, 0.8f, 1f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        pulse.setDuration(250);
        pulse.setRepeatCount(Animation.INFINITE);
        pulse.setRepeatMode(Animation.REVERSE);
        cameraImage.startAnimation(pulse);

        View processingText = findViewById(R.id.text_processing_image);
        processingText.setAlpha(1f);
        processingText.setVisibility(View.VISIBLE);
    }
}
