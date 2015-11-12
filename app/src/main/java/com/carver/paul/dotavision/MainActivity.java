package com.carver.paul.dotavision;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

//TODO: remove unecessary depedencies

//TODO-beauty: tidy up layout files

//TODO: modifications to layouts for a few phone sizes

//TODO: test all spells, e.g. do the stun summaries show the right information?

//TODO: understand what lines like xmlns:app= do in the layout files

//TODO: change side menu xmls so that I don't use specific values, but they are based on variables (as in the example code from android)

//TODO-essential: make sure I have a legal message saying it's Valve's trademark

//TODO-now: use sample image from package

//TODO-essential: fix info reported on heroes. E.g. Zeus' lightning bolt is only a mini stun but the app reports the sight duration! -- test for other durations and not show them?

//TODO: learn about layout optimisation
// http://developer.android.com/training/improving-layouts/optimizing-layout.html

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static List<HeroInfo> heroInfoList = null;
    private static HistTest histTest = null;
    public static boolean debugMode = true;

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.debug_specific_hue) {
            startDebugLineActivity();
        } else if (id == R.id.debug_whole_process) {
            startDebugWholeProcessActivity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void LoadXML() {
        XmlResourceParser parser = getResources().getXml(R.xml.hero_info_from_web);
        heroInfoList = LoadHeroXml.Load(parser);
    }

    private void loadHistTest() {
        histTest = new HistTest(this);
    }

    public void startDebugLineActivity() {
        Intent intent = new Intent(this, DebugLineDetectionActivity.class);
        startActivity(intent);
    }

    public void startDebugWholeProcessActivity() {
        Intent intent = new Intent(this, DebugWholeProcessActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO change so it saves images in the right location
    public static String getImagesLocation() {
        return new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "DOTA Vision").getPath();
    }

    public static String getPhotoLocation() {
        return new File(getImagesLocation(), "photo.jpg").getPath();
    }

    //TODO-now change useExistingPictureButton back so it uses a saved image, it currently just uses the last photo!
    public void useExistingPictureButton(View view) {
        File mediaFile = new File(getImagesLocation(), "photo.jpg");
        Bitmap bitmap;
        if (mediaFile.exists()) {
            bitmap = CreateCroppedBitmap(mediaFile.getPath());
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_photo);
        }
        testImageRecognition(bitmap);
    }

    static public Bitmap CreateCroppedBitmap(String photoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
        int newHeight = Variables.SCALED_IMAGE_WIDTH * bitmap.getHeight() / bitmap.getWidth();
        if (bitmap.getWidth() != Variables.SCALED_IMAGE_WIDTH)
            bitmap = Bitmap.createScaledBitmap(bitmap, Variables.SCALED_IMAGE_WIDTH, newHeight, false);

        //crop the top and bottom thirds off, if it's tall
        if (newHeight > 190 * 3)
            bitmap = Bitmap.createBitmap(bitmap, 0, newHeight / 3, Variables.SCALED_IMAGE_WIDTH, newHeight / 3);
        return bitmap;
    }

    //TODO: make image recognition threaded
    private void testImageRecognition(Bitmap bitmap) {
        if (heroInfoList == null)
            LoadXML();
        if (histTest == null)
            loadHistTest();

        ImageView topImage = (ImageView) findViewById(R.id.topImage);
        topImage.setImageBitmap(bitmap);

        List<HeroRect> heroes = Recognition.Run(bitmap, histTest); //BitmapFactory.decodeFile(mediaFile.getPath()), hMin, hMax, sMin, sMax, vMin, vMax);

        if (debugMode) {
            TextView imageDebugText = (TextView) findViewById(R.id.imageDebugText);
            imageDebugText.setVisibility(View.VISIBLE);
            imageDebugText.setText(Recognition.debugString);
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int heroIconWidth = metrics.widthPixels * 2 / 6;

        LinearLayout loadedPicturesLayout = (LinearLayout) findViewById(R.id.loadedPicturesLayout);
        loadedPicturesLayout.removeAllViews();

        ResetTextViews();

        for (HeroRect hero : heroes) {
            LinearLayout thisPictureLayout = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            thisPictureLayout.setLayoutParams(params);
            if (heroes.get(0) == hero) // i.e., this is the first hero
                thisPictureLayout.setPadding(0, 16, 0, 16);
            else
                thisPictureLayout.setPadding(0, 0, 0, 16);

            loadedPicturesLayout.addView(thisPictureLayout);

            //TODO: Don't work out the preview width and height this way! or at least don't scale the photo this way
            ImageView imageViewPhoto = new ImageView(this);
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

            ImageView imageViewOriginal = new ImageView(this);
            // imageViewOriginal.setPadding(0, 0, 0, 0);
            HeroHistAndSimilarity matchingHero = hero.getSimilarityList().get(0);
            Bitmap bitmapOriginal = ImageTools.GetBitmapFromMat(matchingHero.hero.image);
            height = heroIconWidth * bitmapOriginal.getHeight() / bitmapOriginal.getWidth();
            bitmapOriginal = Bitmap.createScaledBitmap(bitmapOriginal, heroIconWidth, height, true);
            imageViewOriginal.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageViewOriginal.setImageBitmap(bitmapOriginal);
            thisPictureLayout.addView(imageViewOriginal);

            SetInfoText(hero.getSimilarityList());
        }

        //mRecyclerView.setAdapter(new HeroInfoAdapter(heroesSeen));

        List<HeroWithHist> heroesSeen = new ArrayList<>();
        for (HeroRect heroRect : heroes) {
            heroesSeen.add(heroRect.getSimilarityList().get(0).hero);
        }
        AddAbilityHeading("Stuns");
        AddStunCards(heroesSeen);
        AddAbilityHeading("Ultimates");
        AddUltimateCards(heroesSeen);

        LayoutTransition transition = new LayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);
        transition.setDuration(250);
        LinearLayout parent = (LinearLayout) findViewById(R.id.linearLayout);
        parent.setLayoutTransition(transition);

/*        ImageView mImageView;
        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setImageBitmap(bitmap);*/
    }

    private void AddAbilityHeading(String string) {
        LinearLayout parent = (LinearLayout) findViewById(R.id.linearLayout);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.heading_item, parent, false);
        TextView textView = (TextView) v.findViewById(R.id.textView);
        textView.setText(string);
        parent.addView(v);
    }

    private void AddStunCards(List<HeroWithHist> heroes) {
        List<HeroAbility> stunAbilities = new ArrayList<>();
        for (HeroWithHist hero : heroes) {
            for (HeroAbility ability : MainActivity.FindHeroWithName(hero.name).abilities) {
                if (ability.isStun) {
                    stunAbilities.add(ability);
                }
            }
        }

        AddAbilityCards(stunAbilities, true);
    }

    //TODO: make cards cards with rounded edges
    private void AddUltimateCards(List<HeroWithHist> heroes) {
        List<HeroAbility> ultimates = new ArrayList<>();
        for (HeroWithHist hero : heroes) {
            HeroInfo heroInfo = MainActivity.FindHeroWithName(hero.name);
            ultimates.add(heroInfo.abilities.get(heroInfo.abilities.size() - 1));
        }
        AddAbilityCards(ultimates, false);
    }

    private void AddAbilityCards(List<HeroAbility> abilities, boolean showStunDuration) {
        LinearLayout parent = (LinearLayout) findViewById(R.id.linearLayout);

        for (HeroAbility ability : abilities) {
            AbilityCard card = new AbilityCard(this, ability, showStunDuration);
            parent.addView(card);
        }
    }

    //TODO: move GetDrawableFromString to imagetools?
    public static int GetDrawableFromString(String string) {
        for (Pair<Integer, String> pair : Variables.abilityDrawables) {
            if (pair.second.equals(string))
                return pair.first;
        }
        return -1;
    }

    private void ResetTextViews() {
        List<Integer> ids = Arrays.asList(R.id.infoText);
        for (Integer id : ids) {
            TextView tv = (TextView) findViewById(id);
            tv.setText("");
            tv.setVisibility(View.VISIBLE);
        }
    }

    private void SetInfoText(List<HeroHistAndSimilarity> similarityList) {

        TextView infoText = (TextView) findViewById(R.id.infoText);
        HeroHistAndSimilarity matchingHero = similarityList.get(0);

        infoText.append(matchingHero.hero.name + ", " + matchingHero.similarity);

        //TODO-now make it work with pictures who's names are wrong
        HeroInfo heroWithName = FindHeroWithName(matchingHero.hero.name);
        if (heroWithName != null) {
            infoText.append(". Stuns: " + heroWithName.CountStuns());
        }

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

    private StringBuilder GetStunText(HeroHistAndSimilarity matchingHero) {
        HeroInfo heroWithName = FindHeroWithName(matchingHero.hero.name);
        StringBuilder string = new StringBuilder();
        if (heroWithName == null) return string;

        for (HeroAbility ability : heroWithName.abilities) {
            if (ability.isStun) {
                if (string.length() == 0) {
                    string.append("<p><b>" + heroWithName.name + "</b>");
                } else {
                    string.append("<p>");
                }
                string.append("<br><b>" + ability.name + "</b> " + ability.description);
                String stunDuration = ability.guessStunDuration();
                if (stunDuration != null) {
                    string.append(" <b>" + stunDuration + "</b>");
                }
            }
        }

        return string;
    }

    private StringBuilder GetUltimatesText(HeroHistAndSimilarity matchingHero) {
        HeroInfo heroWithName = FindHeroWithName(matchingHero.hero.name);
        StringBuilder string = new StringBuilder();

        if (heroWithName == null) return string;

        HeroAbility ultimate = heroWithName.abilities.get(heroWithName.abilities.size() - 1);
        string.append("<p><b>" + heroWithName.name + ", " + ultimate.name + "</b><br>" + ultimate.description);

        if (ultimate.cooldown != null) {
            string.append("<br>Cooldown: " + ultimate.cooldown);
        }

        return string;
    }

    // TODO: replace FindHeroWithName to use the drawable id int instead of strings
    public static HeroInfo FindHeroWithName(String name) {
        if (heroInfoList == null)
            throw new RuntimeException("heroInfoList is null");

        for (HeroInfo hero : heroInfoList) {
            if (hero.HasName(name)) {
                return hero;
            }
        }
        return null;
    }


    // TODO: Change permissions so it uses the Android 6 way, then can increase target API
    // TODO: Make it save in the write media location, I think media store wasn't right
    public void takePhoto(View view) {

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.camera_fab);
/*        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(fab, "scaleX", 0.2f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(fab, "scaleY", 0.2f);
        scaleX.setDuration(300);
        scaleY.setDuration(300);
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.start();*/

        TimeInterpolator interpolator = new OvershootInterpolator();

        fab.animate().scaleX(0.2f).scaleY(0.2f).setDuration(300).setInterpolator(interpolator);



/*        EnsureMediaDirectoryExists();
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);*/


/*        EnsureMediaDirectoryExists();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoFileUri = Uri.fromFile(new File(getPhotoLocation()));// getOutputMediaFileUri(); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri); // set the image file name
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);*/
    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                testImageRecognition(getPhotoLocation());
//                testImageRecognition(fileUri.getPath());
*//*                ImageView mImageView;
                mImageView = (ImageView) findViewById(R.id.imageView);
                mImageView.setImageBitmap(BitmapFactory.decodeFile(fileUri.getPath()));*//*

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }*/

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


    *//** Create a File for saving an image or video *//*
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
        // TODO: Change location to Context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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
