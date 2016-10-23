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

package com.carver.paul.truesight.Ui.DebugActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.carver.paul.truesight.R;
import com.carver.paul.truesight.Ui.MainActivity;

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
        File mediaFile = new File(getApplicationContext().getFilesDir(), MainActivity.PHOTO_FILE_NAME);
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
