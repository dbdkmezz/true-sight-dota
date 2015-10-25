package com.carver.paul.dotavision;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carver.paul.dotavision.DebugActivities.DebugLineDetectionActivity;
import com.carver.paul.dotavision.ImageRecognition.HeroHistAndSimilarity;
import com.carver.paul.dotavision.ImageRecognition.HeroRect;
import com.carver.paul.dotavision.ImageRecognition.ImageTools;
import com.carver.paul.dotavision.ImageRecognition.Recognition;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static{ System.loadLibrary("opencv_java3"); }

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
/*                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();  */
            }
        });

        //System.out.println("Welcome to OpenCV " + Core.VERSION);
    }

    public void startDebugLineActivity(View view) {
        Intent intent = new Intent(this, DebugLineDetectionActivity.class);
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

    public void useExistingPictureButton(View view) {
        File mediaFile = new File(getImagesLocation(), "dota.jpg");
        testImageRecognition(mediaFile.getPath());
    }

    private void testImageRecognition(String photoPath) {

/*        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "DOTA Vision");
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "dota.jpg");*/

/*        TextView tv = (TextView)findViewById(R.id.minH);
        int hMin = Integer.parseInt(tv.getText().toString());

        tv = (TextView)findViewById(R.id.maxH);
        int hMax = Integer.parseInt(tv.getText().toString());

        tv = (TextView)findViewById(R.id.minS);
        int sMin = Integer.parseInt(tv.getText().toString());

        tv = (TextView)findViewById(R.id.maxS);
        int sMax = Integer.parseInt(tv.getText().toString());

        tv = (TextView)findViewById(R.id.minV);
        int vMin = Integer.parseInt(tv.getText().toString());

        tv = (TextView)findViewById(R.id.maxV);
        int vMax = Integer.parseInt(tv.getText().toString());*/

//        System.out.println(mediaFile.getPath());

/*        String fname = new File(getImagesLocation(), "dota.jpg").getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(fname);*/

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        //options.outWidth = 200;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
        final int NEW_WIDTH = 800;
        int newHeight = NEW_WIDTH * bitmap.getHeight() / bitmap.getWidth();
        bitmap = Bitmap.createScaledBitmap(bitmap, NEW_WIDTH, newHeight, false);


        List<HeroRect> heroes = Recognition.Run(bitmap); //BitmapFactory.decodeFile(mediaFile.getPath()), hMin, hMax, sMin, sMax, vMin, vMax);

        LinearLayout loadedPicturesLayout = (LinearLayout) findViewById(R.id.loadedPicturesLayout);
        TextView infoText = (TextView) findViewById(R.id.infoText);
        for (HeroRect hero : heroes) {
            LinearLayout thisPictureLayout = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            thisPictureLayout.setLayoutParams(params);

            ImageView imageViewPhoto = new ImageView(this);
            imageViewPhoto.setImageBitmap(ImageTools.GetBitmapFromMap(hero.image));
            thisPictureLayout.addView(imageViewPhoto);

            ImageView imageViewOriginal = new ImageView(this);
            HeroHistAndSimilarity matchingHero = hero.getSimilarityList().get(0);
            imageViewOriginal.setImageBitmap(ImageTools.GetBitmapFromMap(matchingHero.hero.image));
            thisPictureLayout.addView(imageViewOriginal);

            loadedPicturesLayout.addView(thisPictureLayout);

            infoText.setText(infoText.getText() + matchingHero.hero.name + ", " + matchingHero.similarity + System.getProperty("line.separator"));
        }

/*        ImageView mImageView;
        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setImageBitmap(bitmap);*/
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoFileUri = Uri.fromFile(new File(getPhotoLocation()));// getOutputMediaFileUri(); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri); // set the image file name
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
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

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
    }

    private static final int WRITE_EXTERNAL_STORAGE = 1;

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

   /*     ActivityCompat.requestPermissions(MainActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                WRITE_EXTERNAL_STORAGE);
*/

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
    }

}
