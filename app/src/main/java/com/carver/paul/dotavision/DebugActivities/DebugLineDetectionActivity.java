/**
 * True Sight for Dota 2
 * Copyright (C) 2015 Paul Broadbent
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

package com.carver.paul.dotavision.DebugActivities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.carver.paul.dotavision.ImageRecognition.Debug.DebugLineDetection;
import com.carver.paul.dotavision.MainActivity;
import com.carver.paul.dotavision.R;

import java.io.File;

public class DebugLineDetectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_line_detection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void testWithoutNewPhoto(View view) {

        // hide the keyboard
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        File mediaFile = new File(MainActivity.getImagesLocation(), MainActivity.PHOTO_FILE_NAME);
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

/*        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_photo);*/

        //DebugLineDetection.TestLines(bitmap, (ImageView) findViewById(R.id.imageViewMask));
        Bitmap linesBitmap = DebugLineDetection.TestLines(bitmap, hMin, hMax, sMin, sMax, vMin, vMax);
        Bitmap maskBitmap = DebugLineDetection.TestMask(bitmap, hMin, hMax, sMin, sMax, vMin, vMax);//, (ImageView) findViewById(R.id.imageViewLines));

        ImageView mImageView;
        mImageView = (ImageView) findViewById(R.id.imageViewLines);
        mImageView.setImageBitmap(linesBitmap);
        mImageView = (ImageView) findViewById(R.id.imageViewMask);
        mImageView.setImageBitmap(maskBitmap);
    }
}
