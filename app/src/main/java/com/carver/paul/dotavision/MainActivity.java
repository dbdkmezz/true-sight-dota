package com.carver.paul.dotavision;

import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carver.paul.dotavision.DebugActivities.DebugLineDetectionActivity;
import com.carver.paul.dotavision.DebugActivities.DebugWholeProcessActivity;
import com.carver.paul.dotavision.DotaCamera.CameraActivity;
import com.carver.paul.dotavision.ImageRecognition.HeroHistAndSimilarity;
import com.carver.paul.dotavision.ImageRecognition.HeroRect;
import com.carver.paul.dotavision.ImageRecognition.HeroWithHist;
import com.carver.paul.dotavision.ImageRecognition.HistTest;
import com.carver.paul.dotavision.ImageRecognition.ImageTools;
import com.carver.paul.dotavision.ImageRecognition.Recognition;
import com.carver.paul.dotavision.ImageRecognition.Variables;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO: fix bug where preview moving when processing

//TODO-beauty: remove unecessary depedencies

//TODO-now: fix crash on verity phone

//TODO-now: put source on github and make it compile on new installs, and add OSS code headings

//TODO-now: reduce package size. Smaller images? Crop test image

//TODO-now: make new icon and screenshots for the play store

//TODO-prebeta: add tab view so you can see the spells of all the heroes

//TODO-beauty: tidy up layout files

//TODO-beauty: modifications to layouts for a few phone sizes

//TODO-prebeta: test all spells, e.g. do the stun summaries show the right information? Also, some of the ultimates aren't ultimates!

//TODO-someday: change side menu xmls so that I don't use specific values, but they are based on variables (as in the example code from android)

//TODO: slide to change hero!!

//TODO-someday: learn about layout optimisation
// http://developer.android.com/training/improving-layouts/optimizing-layout.html

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean debugMode = false;
    private static final int CAMERA_ACTIVITY_REQUEST_CODE = 100;
    public static final String PHOTO_FILE_NAME = "photo.jpg";

    static {
        System.loadLibrary("opencv_java3");
    }
    //private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/*
        createRecyclerView();
*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

/*
    private void createRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.infoRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(null);
    }
*/


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

   // @SuppressWarnings("StatementWithEmptyBody")
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

    public void demoButton(View view) {
        Bitmap sampleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_photo);
        doImageRecognition(sampleBitmap);
    }

    public void useLastPhotoButton(View view) {
        doImageRecognitionOnPhoto();
    }

    private void doImageRecognitionOnPhoto() {
        File mediaFile = new File(getImagesLocation(), PHOTO_FILE_NAME);
        if (!mediaFile.exists()) {
            throw new RuntimeException("Trying to recognise photo, but I can't find file at " + getImagesLocation() + PHOTO_FILE_NAME);
        }

        Bitmap bitmap = CreateCroppedBitmap(mediaFile.getPath());
        doImageRecognition(bitmap);
    }

    //TODO-beauty: move image recognition into separate class. Make MainActivity class tiny
    private void doImageRecognition(Bitmap bitmap) {
        ImageView topImage = (ImageView) findViewById(R.id.topImage);
        topImage.setImageBitmap(bitmap);

        new RecognitionTask(this).execute(bitmap);
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

    // TODO-beauty: Change permissions so it uses the Android 6 way, then can increase target API
    // TODO-prebeta: Make it save in the write media location, I think media store wasn't right
    public void takePhoto(View view) {
        EnsureMediaDirectoryExists();
        Intent intent = new Intent(this, CameraActivity.class);
        //TODO-someday: make it possible to specify image save location by sending camera activity an intent
        startActivityForResult(intent, CAMERA_ACTIVITY_REQUEST_CODE);
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

    public static void EnsureMediaDirectoryExists() {
        File mediaStorageDir = new File(getImagesLocation());
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("DOTA Vision", "failed to create directory");
            }
        }
    }

    /*

    *//** Create a file Uri for saving an image or video *//*
    private static Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
    }

    private static final int WRITE_EXTERNAL_STORAGE = 1;


    */

    /**
     * Create a File for saving an image or video
     *//*
    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

   *//*     ActivityCompat.requestPermissions(MainActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                WRITE_EXTERNAL_STORAGE);
*//*

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "DOTA Vision");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // https://developer.android.com/guide/topics/media/camera.html#saving-media

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("DOTA Vision", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }*/

    //TODO-prebeta: make it so RecognitionTask gets passed heroInfoList and histTest by MainActivity so I don't have to load them every time.
    private class RecognitionTask extends AsyncTask<Bitmap, Void, List<HeroRect>> {

        private List<HeroInfo> heroInfoList = null;
        private HistTest histTest = null;
        private Context context;

        public RecognitionTask(Context context) {
            this.context = context;
        }

        // work to do in the UI thread before doing the hard work
        protected void onPreExecute() {
            resetInfo();
            slideDemoButtonsOffScreen();
            pulseCameraFab();
        }

        private void resetInfo() {
            LinearLayout layout = (LinearLayout) findViewById(R.id.resultsInfoLayout);
            layout.removeAllViews();
            layout = (LinearLayout) findViewById(R.id.loadedPicturesLayout);
            layout.removeAllViews();

            if (debugMode) {
                List<Integer> textViewIds = Arrays.asList(R.id.similarityInfoText, R.id.imageDebugText);
                ResetTextViews(textViewIds);
            }
        }

        /**
         * If the demo and use last photo buttons haven't been moved yet, then slide them off the left of the screen
         */
        private void slideDemoButtonsOffScreen() {
            View view = findViewById(R.id.demoAndsLastPhotoButtonsLayout);
            if (view.getTranslationX() == 0) {
                view.animate().x(-1f * view.getWidth()).setDuration(150).setInterpolator(new AccelerateInterpolator());
            }
        }

        /**
         * Makes the camera FAB pulse infinitely (will be stopped when loading complete)
         */
        private void pulseCameraFab() {
            //Code using the old Animation class, rather than the new ViewPropertyAnimator
            //Infinite repeat is easier to implement this way
            View view = findViewById(R.id.cameraFab);
            moveViewBackToStartingPosAndScale(view);

            ScaleAnimation pulse = new ScaleAnimation(1f, 0.8f, 1f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            pulse.setDuration(250);
            pulse.setRepeatCount(Animation.INFINITE);
            pulse.setRepeatMode(Animation.REVERSE);
            view.startAnimation(pulse);

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
        fab.animate().scaleX(0.2f).scaleY(0.2f).setDuration(300).setInterpolator(interpolator);*/

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

        // This is where the hard work happens which needs to be off the UI thread
        protected List<HeroRect> doInBackground(Bitmap... bitmaps) {
            if (heroInfoList == null)
                LoadXML();
            if (histTest == null)
                loadHistTest();

            return Recognition.Run(bitmaps[0], histTest);
        }

        // work to do in the UI thread after the hard work
        protected void onPostExecute(final List<HeroRect> heroes) {
            // Stop the camera button pulsing by making it finish the current animation and then run the code in onAnimationEnd
            ImageView imageview = (ImageView) findViewById(R.id.cameraFab);
            Animation animation = imageview.getAnimation();
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

        private void showResult(final List<HeroRect> heroes) {

            //TODO-beauty: tidy up all the UI code that's run after image recognition

            if (debugMode) {
                TextView imageDebugText = (TextView) findViewById(R.id.imageDebugText);
                imageDebugText.setVisibility(View.VISIBLE);
                imageDebugText.setText(Recognition.debugString);
            }

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int heroIconWidth = metrics.widthPixels * 2 / 6;

            LinearLayout loadedPicturesLayout = (LinearLayout) findViewById(R.id.loadedPicturesLayout);

            for (HeroRect hero : heroes) {
                LinearLayout thisPictureLayout = new LinearLayout(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                thisPictureLayout.setLayoutParams(params);
                if (heroes.get(0) == hero) // i.e., this is the first hero
                    thisPictureLayout.setPadding(0, 16, 0, 16);
                else
                    thisPictureLayout.setPadding(0, 0, 0, 16);

                loadedPicturesLayout.addView(thisPictureLayout);

                //TODO: Don't work out the preview width and height this way! or at least don't scale the photo this way
                ImageView imageViewPhoto = new ImageView(context);
                Bitmap bitmapPhoto = ImageTools.GetBitmapFromMat(hero.image);
                int height = heroIconWidth * bitmapPhoto.getHeight() / bitmapPhoto.getWidth();
                bitmapPhoto = Bitmap.createScaledBitmap(bitmapPhoto, heroIconWidth, height, true);
                imageViewPhoto.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageViewPhoto.setImageBitmap(bitmapPhoto);
                imageViewPhoto.setPadding(0, 0, 16, 0);
                thisPictureLayout.addView(imageViewPhoto);

/*            imageViewPhoto.setMinimumWidth(thisPictureLayout.getWidth() * 2 / 6);
            imageViewPhoto.setMaxWidth(thisPictureLayout.getWidth() * 2 / 6);
            imageViewPhoto.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageViewPhoto.setAdjustViewBounds(true);
       //     imageViewPhoto.setMaxWidth(thisPictureLayout.getWidth() * 2 / 6);
            imageViewPhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageViewPhoto.setImageBitmap(ImageTools.GetBitmapFromMap(hero.image));*/

                ImageView imageViewOriginal = new ImageView(context);
                // imageViewOriginal.setPadding(0, 0, 0, 0);
                HeroHistAndSimilarity matchingHero = hero.getSimilarityList().get(0);
                Bitmap bitmapOriginal = ImageTools.GetBitmapFromMat(matchingHero.hero.image);
                height = heroIconWidth * bitmapOriginal.getHeight() / bitmapOriginal.getWidth();
                bitmapOriginal = Bitmap.createScaledBitmap(bitmapOriginal, heroIconWidth, height, true);
                imageViewOriginal.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageViewOriginal.setImageBitmap(bitmapOriginal);
                thisPictureLayout.addView(imageViewOriginal);

                if (debugMode)
                    showSimilarityInfo(hero.getSimilarityList());
            }

            //mRecyclerView.setAdapter(new HeroInfoAdapter(heroesSeen));

            List<HeroWithHist> heroesSeen = new ArrayList<>();
            for (HeroRect heroRect : heroes) {
                if (!heroesSeen.contains(heroRect.getSimilarityList().get(0).hero)) {
                    heroesSeen.add(heroRect.getSimilarityList().get(0).hero);
                }
            }

            //TODO-prebeta: add "no silences found" text when none found
            AddAbilityHeading("Stuns");
            AddAbilityCards(heroesSeen, HeroAbility.STUN);
            AddAbilityHeading("Silences");
            AddAbilityCards(heroesSeen, HeroAbility.SILENCE);
            AddAbilityHeading("Ultimates");
            AddAbilityCards(heroesSeen, HeroAbility.ULTIMATE);

            LayoutTransition transition = new LayoutTransition();
            transition.enableTransitionType(LayoutTransition.CHANGING);
            transition.setDuration(250);
            LinearLayout resultsInfoLayout = (LinearLayout) findViewById(R.id.resultsInfoLayout);
            resultsInfoLayout.setLayoutTransition(transition);
        }

        private void moveCameraFabToBottomRight() {
            //TODO-beauty: seriously, sort out the camera fab button animation code. It's a mess and goes to the wrong place! Ideally put the animation into an XML
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.cameraFab);
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainCoordinatorLayout);
            //     FloatingActionButton otherFab = (FloatingActionButton) findViewById(R.id.useExistingPictureButton);
            float finalWidth = dpToPx(60);
//            float finalMargin = (float) getResources().getDimension(R.dimen.fab_margin);

//            int finalMargin = otherFab.getLayoutParams().
            float currentWidth = fab.getWidth();
            AnimatorSet animatorSet = new AnimatorSet();
            float farRight = coordinatorLayout.getRight();
            float startX = fab.getX();

//            float marginPx = dpToPx(getResources().getDimension(R.dimen.fab_margin));
            float marginPx = dpToPx(16);


            float endX = coordinatorLayout.getWidth() - (currentWidth + finalWidth + marginPx) / 2f;
            float endY = coordinatorLayout.getHeight() - (currentWidth + finalWidth + marginPx) / 2f;
            //ObjectAnimator translationX = ObjectAnimator.ofFloat(fab, "translationX", 100f + farRight - startX );//coordinatorLayout.getRight() - finalWidth - fab.getX());
            ObjectAnimator x = ObjectAnimator.ofFloat(fab, "x", endX);
            ObjectAnimator y = ObjectAnimator.ofFloat(fab, "y", endY);
            // ObjectAnimator translationY = ObjectAnimator.ofFloat(fab, "translationY", coordinatorLayout.getHeight() - finalWidth - fab.getY());
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(fab, "scaleX", finalWidth / currentWidth);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(fab, "scaleY", finalWidth / currentWidth);
            animatorSet.playTogether(x, y, scaleX, scaleY);
            //animatorSet.play(x);
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.start();
        }

        //TODO-prebeta: I don't trust the dpToPx function, test it on other screen sizes
        private float dpToPx(float dp) {

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return dp * displayMetrics.density;

/*            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float px = dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
            return px;*/
        }

        private void LoadXML() {
            XmlResourceParser parser = getResources().getXml(R.xml.hero_info_from_web);
            heroInfoList = LoadHeroXml.Load(parser);
        }

        private void loadHistTest() {
            histTest = new HistTest(context);
        }

        private void AddAbilityHeading(String string) {
            LinearLayout parent = (LinearLayout) findViewById(R.id.resultsInfoLayout);
            LayoutInflater inflater = getLayoutInflater();
            View v = inflater.inflate(R.layout.heading_item, parent, false);
            TextView textView = (TextView) v.findViewById(R.id.textView);
            textView.setText(string);
            parent.addView(v);
        }

        private boolean AddAbilityCards(List<HeroWithHist> heroes, int abilityType) {
            List<HeroAbility> abilities = new ArrayList<>();
            for (HeroWithHist hero : heroes) {
                for (HeroAbility ability : FindHeroWithName(hero.name).abilities) {
                    if(abilityType == HeroAbility.STUN && ability.isStun)
                        abilities.add(ability);
                    else if(abilityType == HeroAbility.SILENCE && ability.isSilence)
                        abilities.add(ability);
                    else if(abilityType == HeroAbility.ULTIMATE && ability.isUltimate)
                        abilities.add(ability);
                }
            }

            LinearLayout parent = (LinearLayout) findViewById(R.id.resultsInfoLayout);
            boolean cardsAdded = false;

            for (HeroAbility ability : abilities) {
                AbilityCard card = new AbilityCard(context, ability, abilityType);
                parent.addView(card);
                cardsAdded = true;
            }

            return cardsAdded;
        }

        private void ResetTextViews(List<Integer> ids) {
            for (Integer id : ids) {
                TextView tv = (TextView) findViewById(id);
                tv.setText("");
                tv.setVisibility(View.GONE);
            }
        }

        private void showSimilarityInfo(List<HeroHistAndSimilarity> similarityList) {
            TextView infoText = (TextView) findViewById(R.id.similarityInfoText);
            infoText.setText("");
            infoText.setVisibility(View.VISIBLE);
            HeroHistAndSimilarity matchingHero = similarityList.get(0);

            infoText.append(matchingHero.hero.name + ", " + matchingHero.similarity);

            // poor result, so lets show some alternatives
            if (matchingHero.similarity < 0.65) {
                infoText.append(". (Alternatives: ");
                for (int i = 1; i < 6; i++) {
                    infoText.append(similarityList.get(i).hero.name + "," + similarityList.get(i).similarity + ". ");
                }
                infoText.append(")");
            }

            infoText.append(System.getProperty("line.separator") + System.getProperty("line.separator"));
        }

        // TODO-prebeta: replace FindHeroWithName to use the drawable id int instead of strings
        private HeroInfo FindHeroWithName(String name) {
            for (HeroInfo hero : heroInfoList) {
                if (hero.HasName(name)) {
                    return hero;
                }
            }
            return null;
        }

    }

}




/*
class HeroInfoAdapter extends RecyclerView.Adapter<HeroInfoAdapter.ViewHolder> {
    private List<HeroAbility> stunAbilities;

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Log.d("HeroInfoAdapter", "Element " + getPosition() + " clicked.");
                }
            });
            textView = (TextView) v.findViewById(R.id.textView);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public HeroInfoAdapter() {
        stunAbilities = new ArrayList<>();
        return;
    }

    public HeroInfoAdapter(List<HeroWithHist> heroes) {
        stunAbilities = new ArrayList<>();
        for(HeroWithHist hero : heroes) {
            for (HeroAbility ability : MainActivity.FindHeroWithName(hero.name).abilities) {
                if (ability.isStun) {
                    stunAbilities.add(ability);
                }
            }
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HeroInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stun_info_item, parent, false);
        // google says that here you set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.getTextView().setText(stunAbilities.get(position).description);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return stunAbilities.size();
    }
}
*/
