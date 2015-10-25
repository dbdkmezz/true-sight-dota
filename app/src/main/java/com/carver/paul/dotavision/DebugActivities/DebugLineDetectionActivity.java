package com.carver.paul.dotavision.DebugActivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.carver.paul.dotavision.ImageRecognition.Debug.DebugLineDetection;
import com.carver.paul.dotavision.ImageRecognition.Recognition;
import com.carver.paul.dotavision.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DebugLineDetectionActivity extends AppCompatActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_line_detection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.cameraFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void testWithoutNewPhoto(View view) {
        runTest(fileUri.getPath());
    }

    private void runTest(String photoPath) {
        TextView tv = (TextView) findViewById(R.id.minH);
        int hMin = Integer.parseInt(tv.getText().toString());
        tv = (TextView) findViewById(R.id.maxH);
        int hMax = Integer.parseInt(tv.getText().toString());
        tv = (TextView) findViewById(R.id.minS);
        int sMin = Integer.parseInt(tv.getText().toString());
        tv = (TextView) findViewById(R.id.maxS);
        int sMax = Integer.parseInt(tv.getText().toString());
        tv = (TextView) findViewById(R.id.minV);
        int vMin = Integer.parseInt(tv.getText().toString());
        tv = (TextView) findViewById(R.id.maxV);
        int vMax = Integer.parseInt(tv.getText().toString());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        //options.outWidth = 200;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
        final int NEW_WIDTH = 800;
        int newHeight = NEW_WIDTH * bitmap.getHeight() / bitmap.getWidth();
        bitmap = Bitmap.createScaledBitmap(bitmap, NEW_WIDTH, newHeight, false);

        //DebugLineDetection.TestLines(bitmap, (ImageView) findViewById(R.id.imageViewMask));
        DebugLineDetection.TestMask(bitmap, hMin, hMax, sMin, sMax, vMin, vMax, (ImageView) findViewById(R.id.imageViewMask));//, (ImageView) findViewById(R.id.imageViewLines));

/*        ImageView mImageView;
        mImageView = (ImageView) findViewById(R.id.imageViewLines);
        mImageView.setImageBitmap(bitmap);*/
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                runTest(fileUri.getPath());
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

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static final int WRITE_EXTERNAL_STORAGE = 1;

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile() {
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
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("DOTA Vision", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }
}
