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
import com.carver.paul.dotavision.MainActivity;
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void testWithoutNewPhoto(View view) {
        File mediaFile = new File(MainActivity.getImagesLocation(), "photo.jpg");
        runTest(mediaFile.getPath());
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

        Bitmap bitmap = MainActivity.CreateCroppedBitmap(photoPath);

        //DebugLineDetection.TestLines(bitmap, (ImageView) findViewById(R.id.imageViewMask));
        Bitmap maskBitmap = DebugLineDetection.TestMask(bitmap, hMin, hMax, sMin, sMax, vMin, vMax);//, (ImageView) findViewById(R.id.imageViewLines));

        ImageView mImageView;
        mImageView = (ImageView) findViewById(R.id.imageViewLines);
        mImageView.setImageBitmap(maskBitmap);
    }
}
