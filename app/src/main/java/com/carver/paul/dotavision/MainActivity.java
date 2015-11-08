package com.carver.paul.dotavision;

import android.Manifest;
import android.app.Fragment;
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
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

//TODO: change side menu xmls so that I don't use specific values, but they are based on variables (as in the example code from android)

//TODO-essential: make sure I have a legal message saying it's Valve's trademark

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<HeroInfo> heroInfoList = null;
    private HistTest histTest = null;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    public static boolean debugMode = true;

    static{ System.loadLibrary("opencv_java3"); }
    //private Uri fileUri;

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
        testImageRecognition(mediaFile.getPath());
    }

    static public Bitmap CreateCroppedBitmap(String photoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
        int newHeight = Variables.SCALED_IMAGE_WIDTH * bitmap.getHeight() / bitmap.getWidth();
        if(bitmap.getWidth() != Variables.SCALED_IMAGE_WIDTH)
            bitmap = Bitmap.createScaledBitmap(bitmap, Variables.SCALED_IMAGE_WIDTH, newHeight, false);

        //crop the top and bottom thirds off, if it's tall
        if(newHeight > 190 * 3)
            bitmap = Bitmap.createBitmap(bitmap, 0, newHeight / 3, Variables.SCALED_IMAGE_WIDTH, newHeight / 3);
        return bitmap;
    }

    private void testImageRecognition(String photoPath) {
        if (heroInfoList == null)
            LoadXML();
        if (histTest == null)
            loadHistTest();

        Bitmap bitmap = CreateCroppedBitmap(photoPath);

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
        StringBuilder ultimatesString = new StringBuilder();
        StringBuilder stunString = new StringBuilder();

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
            ultimatesString.append(GetUltimatesText(hero.getSimilarityList().get(0)));
            stunString.append(GetStunText(hero.getSimilarityList().get(0)));
        }

        TextView tv = (TextView) findViewById(R.id.ultimatesText);
        tv.setText(Html.fromHtml(ultimatesString.toString()));
        tv = (TextView) findViewById(R.id.stunsText);
        tv.setText(Html.fromHtml(stunString.toString()));

/*        ImageView mImageView;
        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setImageBitmap(bitmap);*/
    }

    private void ResetTextViews() {
        List<Integer> ids = Arrays.asList(R.id.infoText, R.id.ultimatesText, R.id.stunsText);
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

        // poor result, so lets show some alteratives
        if (matchingHero.similarity < 0.65) {
            infoText.append(". (Alternatives: ");
            for (int i = 1; i < 6; i++) {
                infoText.append(similarityList.get(i).hero.name + "," + similarityList.get(i).similarity + ". ");
            }
            infoText.append(")");
        }

        infoText.append(System.getProperty("line.separator") + System.getProperty("line.separator"));
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

    private StringBuilder GetStunText(HeroHistAndSimilarity matchingHero) {
        HeroInfo heroWithName = FindHeroWithName(matchingHero.hero.name);
        StringBuilder string = new StringBuilder();
        if (heroWithName == null) return string;

        for (HeroAbility ability : heroWithName.abilities) {
            if (ability.isStun) {
                if (string.length() == 0) {
                    string.append("<p><b>" + heroWithName.name + "</b>");
                }
                else {
                    string.append("<p>");
                }
                string.append("<br><b>" + ability.name + "</b>" + ability.description);
                String stunDuration = ability.guessStunDuration();
                if (stunDuration != null) {
                    string.append(" " + stunDuration);
                }
            }
        }

        return string;
    }

    private HeroInfo FindHeroWithName(String name) {
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
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);


/*        EnsureMediaDirectoryExists();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoFileUri = Uri.fromFile(new File(getPhotoLocation()));// getOutputMediaFileUri(); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri); // set the image file name
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                testImageRecognition(getPhotoLocation());
//                testImageRecognition(fileUri.getPath());
/*                ImageView mImageView;
                mImageView = (ImageView) findViewById(R.id.imageView);
                mImageView.setImageBitmap(BitmapFactory.decodeFile(fileUri.getPath()));*/

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }


    public static void EnsureMediaDirectoryExists() {
        File mediaStorageDir = new File(getPhotoLocation());
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
