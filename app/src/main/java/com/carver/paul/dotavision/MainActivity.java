/**
 * True Sight for Dota 2
 * Copyright (C) 2015 Paul Broadbent
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */

package com.carver.paul.dotavision;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.carver.paul.dotavision.DebugActivities.DebugLineDetectionActivity;
import com.carver.paul.dotavision.DebugActivities.DebugWholeProcessActivity;
import com.carver.paul.dotavision.DotaCamera.CameraActivity;
import com.carver.paul.dotavision.ImageRecognition.HeroFromPhoto;
import com.carver.paul.dotavision.ImageRecognition.Recognition;
import com.carver.paul.dotavision.ImageRecognition.Variables;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//TODO-now: Check all new ability icons, I think that when I updated them the ones online were not
// right. Also Loan Druid's new ability Savage Roar wasn't even there
// http://www.dota2.com/hero/Lone_Druid/

//TODO-next: test much more for crashes, there is something wrong somewhere. The camera threading??

//TODO-next: reduce package size. Smaller images? Crop test image

//TODO-beauty: support rotating the screen!

//TODO-next: make the heroes info separated somehow. Just a dividing line for now? It's a mess.
// Perhaps just draw their image?

//TODO-someday: add tab view so you can slide to change hero rather them all being piled up in one place

//TODO-someday: Make card borders 0 on small displays

//TODO-next: Detect empty box for when no hero picked yet (note, this isn't as easy as it may seem!)

//TODO-next: Can hit use last photo multiple times! Fix all buttons

//TODO-next: Add rate this app button

//TODO-next: Add a way to keep both dire and radiant photos

//TODO-next: Add map with creep camp stack timings

//TODO-someday: Add details for escape and invisibility abilities:
// http://dota2.gamepedia.com/Teleport
// http://dota2.gamepedia.com/Invisibility

//TODO-next: find out whether it's ok to have Valve's images on github

//TODO-next: make screenshots in the Google Play Store higher definition

//TODO-someday: learn about layout optimisation
// http://developer.android.com/training/improving-layouts/optimizing-layout.html

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FoundHeroesFragment.OnHeroChangedListener {

    // sDebugMode is true if I want to show extra debug information. It is ignored when
    // BuildConfig.DEBUG is false (i.e. the app is compiled for release)
    public static boolean sDebugMode = false;
    public static final String PHOTO_FILE_NAME = "photo.jpg";
    private static final int CAMERA_ACTIVITY_REQUEST_CODE = 100;
    private static final String TAG = "MainActivity";

    private List<HeroInfo> mHeroesSeen = null;

    private RecognitionWithRx mRecognitionWithRx;

    static {
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

        mRecognitionWithRx = new RecognitionWithRx(this);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.about) {
            startAboutActivity();
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

    public void onHeroChanged(int posInList, String newHero) {
        if (mHeroesSeen == null || mRecognitionWithRx.getXmlInfo() == null) return;

        HeroInfo newHeroInfo = FindHeroWithName(newHero, mRecognitionWithRx.getXmlInfo());
        if (newHeroInfo == null) {
            Log.e(TAG, "Attempting to change hero to " + newHero + ", but I can't find a hero with that name.");
            return;
        }

        if (mHeroesSeen.get(posInList) == newHeroInfo) return;

        mHeroesSeen.set(posInList, newHeroInfo);

        AbilityInfoFragment abilityInfoFragment = (AbilityInfoFragment) getFragmentManager().findFragmentById(R.id.fragment_ability_info);
        abilityInfoFragment.reset();
        abilityInfoFragment.showHeroAbilities(mHeroesSeen);

        FoundHeroesFragment foundHeroesFragment = (FoundHeroesFragment) getFragmentManager().findFragmentById(R.id.fragment_found_heroes);
        foundHeroesFragment.changeHero(posInList, newHeroInfo.name, newHeroInfo.imageName);
    }

    public void startDebugLineActivity() {
        Intent intent = new Intent(this, DebugLineDetectionActivity.class);
        startActivity(intent);
    }

    public void startDebugWholeProcessActivity() {
        Intent intent = new Intent(this, DebugWholeProcessActivity.class);
        startActivity(intent);
    }

    public void startAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public static String getImagesLocation() {
        return new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "DOTA Vision").getPath();
    }

    public static String getPhotoLocation() {
        return new File(getImagesLocation(), PHOTO_FILE_NAME).getPath();
    }

    /**
     * Called when the demo buttin is pressed
     * Runs the image recognition on a sample photo which is part of the app
     *
     * @param view
     */
    public void demoButton(View view) {
        Bitmap sampleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_photo);
        doImageRecognition(sampleBitmap);
    }

    /**
     * Runs the image recognition code on the last photo which was taken by the camera
     *
     * @param view
     */
    public void useLastPhotoButton(View view) {
        File mediaFile = new File(getImagesLocation(), PHOTO_FILE_NAME);
        if (mediaFile.exists()) {
            doImageRecognitionOnPhoto();
        } else { // If there isn't a previous photo, then just do the demo
            demoButton(view);
        }
    }

    static public Bitmap CreateCroppedBitmap(String photoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
        int newHeight = Variables.SCALED_IMAGE_WIDTH * bitmap.getHeight() / bitmap.getWidth();
        if (bitmap.getWidth() != Variables.SCALED_IMAGE_WIDTH)
            bitmap = Bitmap.createScaledBitmap(bitmap, Variables.SCALED_IMAGE_WIDTH, newHeight, false);

        //crop the top and bottom thirds off, if it's tall
        if (newHeight > Variables.SCALED_IMAGE_HEIGHT)
            bitmap = Bitmap.createBitmap(bitmap, 0, newHeight / 3, Variables.SCALED_IMAGE_WIDTH, newHeight / 3);
        return bitmap;
    }

    // TODO-beauty: Change permissions so I use the Android 6 way, then can increase target API
    // TODO-next: Make takePhoto save in the write media location, I think media store wasn't right
    public void takePhoto(View view) {
        EnsureMediaDirectoryExists();
        Intent intent = new Intent(this, CameraActivity.class);
        //TODO-beauty: make it possible to specify image save location by sending camera activity an intent
        startActivityForResult(intent, CAMERA_ACTIVITY_REQUEST_CODE);
    }

    public static void EnsureMediaDirectoryExists() {
        File mediaStorageDir = new File(getImagesLocation());
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "failed to create media directory.");
            }
        }
    }

    // TODO-beauty: replace FindHeroWithName to use the drawable id int instead of strings
    public static HeroInfo FindHeroWithName(String name, List<HeroInfo> heroInfoList) {
        if (heroInfoList == null)
            throw new RuntimeException("Called FindHeroWithName when mHeroInfoFromXml is not initialised.");

        for (HeroInfo hero : heroInfoList) {
            if (hero.HasName(name)) {
                return hero;
            }
        }
        return null;
    }

    /**
     * Tasks to be run on the UI thread before doing the background work
     */
    public void preRecognitionUiTasks(Bitmap photoBitmap) {
        setTopImage(photoBitmap);
        resetInfo();
        slideDemoButtonsOffScreen();
        hideBackground();
        pulseCameraFab();
    }

    /**
     * This is where you do the work in the UI thread after the hard work of image recognition.
     * <p/>
     * This lets the cameraFab do one final pulse, and the moves it to the bottom right and
     * shows the result of the recognition.
     *
     * @param heroes
     */
    public void postRecognitionUiTasks(final List<HeroFromPhoto> heroes) {
        View view = findViewById(R.id.button_fab_take_photo);
        Animation animation = view.getAnimation();
        if (animation == null) {
            // I don't understand how this happens, but it does
            //TODO-beauty: fix null animation issue
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Animation is null, I don't understand how this can happen, but it does!");
            }
            moveCameraFabToBottomRight();
            showResult(heroes);
        } else {
            animation.setRepeatCount(0);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    moveCameraFabToBottomRight();
                    showResult(heroes);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                doImageRecognitionOnPhoto();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    private void doImageRecognitionOnPhoto() {
        File mediaFile = new File(getImagesLocation(), PHOTO_FILE_NAME);
        if (!mediaFile.exists()) {
            throw new RuntimeException("Trying to recognise photo, but I can't find file at " + getImagesLocation() + PHOTO_FILE_NAME);
        }

        Bitmap bitmap = CreateCroppedBitmap(mediaFile.getPath());
        doImageRecognition(bitmap);
    }

    private void doImageRecognition(Bitmap bitmap) {
//        RecognitionWithRx recognitionWithRx = new RecognitionWithRx();
        mRecognitionWithRx.Run(this, bitmap);
    }

    private void setTopImage(Bitmap photoBitmap) {
        ImageView topImage = (ImageView) findViewById(R.id.image_top);
        topImage.setImageBitmap(photoBitmap);
    }

    private void resetInfo() {
        FoundHeroesFragment foundHeroesFragment = (FoundHeroesFragment) getFragmentManager().findFragmentById(R.id.fragment_found_heroes);
        foundHeroesFragment.reset();

        AbilityInfoFragment abilityInfoFragment = (AbilityInfoFragment) getFragmentManager().findFragmentById(R.id.fragment_ability_info);
        abilityInfoFragment.reset();
    }

    /**
     * If the demo and use last photo buttons haven't been moved yet, then slide them off the left of the screen
     */
    private void slideDemoButtonsOffScreen() {
        View view = findViewById(R.id.layout_demo_and_last_photo_buttons);
        if (view.getTranslationX() == 0) {
            view.animate()
                    .x(-1f * view.getWidth())
                    .setDuration(150)
                    .setInterpolator(new AccelerateInterpolator());
        }
    }

    private void hideBackground() {
        View view = findViewById(R.id.image_main_background);
        view.animate()
                .alpha(0f)
                .setDuration(150);
    }


    /**
     * Makes the camera FAB pulse infinitely (will be stopped when loading completes)
     */
    //TODO-nextversion: Make camera do something other than pulse - it implies you should press
    // it! When done I should stop disabling the button when animating too.
    private void pulseCameraFab() {
        //Code using the old Animation class, rather than the new ViewPropertyAnimator
        //Infinite repeat is easier to implement this way

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_fab_take_photo);
        fab.setEnabled(false);
        moveViewBackToStartingPosAndScale(fab);

        ScaleAnimation pulse = new ScaleAnimation(1f, 0.8f, 1f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        pulse.setDuration(250);
        pulse.setRepeatCount(Animation.INFINITE);
        pulse.setRepeatMode(Animation.REVERSE);
        fab.startAnimation(pulse);

        View processingText = findViewById(R.id.text_processing_image);
        processingText.setVisibility(View.VISIBLE);

/*            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.cameraFab);
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(fab, "scaleX", 0.8f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(fab, "scaleY", 0.8f);
            animatorSet.playTogether(scaleX, scaleY);
            animatorSet.setDuration(250);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animation.start();
                }
            });
            animatorSet.start();*/


/*            RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                    0.5f,  Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setRepeatCount(Animation.INFINITE);
            rotate.setInterpolator(context, R.anim.linear_interpolator);
            imageview.startAnimation(rotate);*/

/*                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.cameraFab);
        TimeInterpolator interpolator = new OvershootInterpolator();
        fab.animate().
                scaleX(0.2f).
                scaleY(0.2f).
                setDuration(300).
                setInterpolator(interpolator);*/

            /*        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(fab, "scaleX", 0.2f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(fab, "scaleY", 0.2f);
        scaleX.setDuration(300);
        scaleY.setDuration(300);
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.start();*/
    }

    private void moveViewBackToStartingPosAndScale(View view) {
        view.setTranslationX(0f);
        view.setTranslationY(0f);
        view.setScaleX(1f);
        view.setScaleY(1f);
    }

    private void moveCameraFabToBottomRight() {
        // First hide the text to say that the image is being processed
        View processingText = findViewById(R.id.text_processing_image);
        processingText.setVisibility(View.GONE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_fab_take_photo);
        fab.setEnabled(true);
        View fabEndLocation = findViewById(R.id.button_fab_take_photo_final_location);

        int xTrans = (int) ((fabEndLocation.getX() + fabEndLocation.getWidth() / 2) - (fab.getX() + fab.getWidth() / 2));
        int yTrans = (int) ((fabEndLocation.getY() + fabEndLocation.getHeight() / 2) - (fab.getY() + fab.getHeight() / 2));

        fab.animate()
                .translationX(xTrans)
                .translationY(yTrans)
                .scaleX((float) fabEndLocation.getWidth() / (float) fab.getWidth())
                .scaleY((float) fabEndLocation.getHeight() / (float) fab.getHeight())
                .setInterpolator(new AccelerateDecelerateInterpolator());
    }


    /**
     * showResult shows all the information about the heroes I've seen in the photo
     *
     * @param heroes
     */
    private void showResult(final List<HeroFromPhoto> heroes) {

        if (BuildConfig.DEBUG && sDebugMode) {
            TextView imageDebugText = (TextView) findViewById(R.id.text_image_debug);
            if (imageDebugText != null) {
                imageDebugText.setVisibility(View.VISIBLE);
                imageDebugText.setText(Recognition.mDebugString);
            }
        }

        //A list of the heroes we've seen, for use when adding the ability cards
        mHeroesSeen = new ArrayList<>();

        for (HeroFromPhoto hero : heroes) {
            HeroInfo heroInfo = FindHeroWithName(hero.getSimilarityList().get(0).hero.name,
                    mRecognitionWithRx.getXmlInfo());
            mHeroesSeen.add(heroInfo);
        }

        FoundHeroesFragment foundHeroesFragment = (FoundHeroesFragment) getFragmentManager().findFragmentById(R.id.fragment_found_heroes);
        if (foundHeroesFragment != null) {
            foundHeroesFragment.showFoundHeroes(heroes, mHeroesSeen, mRecognitionWithRx.getXmlInfo());
        }

        AbilityInfoFragment abilityInfoFragment = (AbilityInfoFragment) getFragmentManager().findFragmentById(R.id.fragment_ability_info);
        if (abilityInfoFragment != null) {
            abilityInfoFragment.showHeroAbilities(mHeroesSeen);
        }

        //TODO-someday: bring back animation when loading the hero images and abilities?
/*            LayoutTransition transition = new LayoutTransition();
            transition.enableTransitionType(LayoutTransition.CHANGING);
            transition.setDuration(250);
            LinearLayout resultsInfoLayout = (LinearLayout) findViewById(R.id.layout_results_info);
            resultsInfoLayout.setLayoutTransition(transition);*/
    }
}