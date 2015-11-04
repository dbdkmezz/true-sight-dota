package com.carver.paul.dotavision.DebugActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.carver.paul.dotavision.MainActivity;
import com.carver.paul.dotavision.R;

import java.io.File;

public class DebugWholeProcessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_whole_process);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void testWithoutNewPhoto(View view) {
        File mediaFile = new File(MainActivity.getImagesLocation(), "photo.jpg");
        runTest(mediaFile.getPath());
    }

    private void runTest(String photoPath) {
/*
        Bitmap bitmap = MainActivity.CreateCroppedBitmap(photoPath);

        Bitmap rectsBitmap = DebugLineDetection.TestRects(bitmap);

        //DebugLineDetection.TestLines(bitmap, (ImageView) findViewById(R.id.imageViewMask));
       // Bitmap linesBitmap = DebugLineDetection.TestLines(bitmap, hMin, hMax, sMin, sMax, vMin, vMax);
       // Bitmap maskBitmap = DebugLineDetection.TestMask(bitmap, hMin, hMax, sMin, sMax, vMin, vMax);//, (ImageView) findViewById(R.id.imageViewLines));

        ImageView mImageView;
        mImageView = (ImageView) findViewById(R.id.imageDebugRects);
        mImageView.setImageBitmap(rectsBitmap);
        mImageView = (ImageView) findViewById(R.id.imageViewMask);
        mImageView.setImageBitmap(maskBitmap);*/
    }
}
