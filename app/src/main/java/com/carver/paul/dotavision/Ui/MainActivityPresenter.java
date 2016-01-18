/**
 * True Sight for Dota 2
 * Copyright (C) 2016 Paul Broadbent
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

package com.carver.paul.dotavision.Ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.carver.paul.dotavision.ImageRecognition.Variables;
import com.carver.paul.dotavision.Models.DataManager;
import com.carver.paul.dotavision.Ui.AbilityInfo.AbilityInfoPresenter;
import com.carver.paul.dotavision.Ui.HeroesDetected.HeroesDetectedPresenter;

import java.io.File;

public class MainActivityPresenter {
    private MainActivity mView;
    private DataManager mDataManager;

    public MainActivityPresenter(MainActivity view) {
        mView = view;
        mDataManager = new DataManager(this);
    }

    public void doImageRecognition(Bitmap photo,
                                   HeroesDetectedPresenter heroesDetectedPresenter,
                                   AbilityInfoPresenter abilityInfoPresenter) {
        if (!mDataManager.presentersRegistered()) {
            mDataManager.registerPresenters(heroesDetectedPresenter, abilityInfoPresenter);
        }

        mDataManager.identifyHeroesInPhoto(photo);
    }

    public Context getContext() {
        return mView;
    }

    public void startHeroRecognitionLoadingAnimations(Bitmap photo) {
        mView.startHeroRecognitionLoadingAnimations(photo);
    }

    public void stopHeroRecognitionLoadingAnimations() {
        mView.stopHeroRecognitionLoadingAnimations();
    }

    /**
     * Runs the image recognition code on the last photo which was taken by the camera
     */
    protected void useLastPhotoButtonPressed() {
        File mediaFile = new File(mView.getImagesLocation(), MainActivity.PHOTO_FILE_NAME);
        if (mediaFile.exists()) {
            doImageRecognitionOnPhoto();
        } else { // If there isn't a previous photo, then just do the demo
            demoButtonPressed();
        }
    }

    /**
     * Runs image recognition on the sample photo
     */
    protected void demoButtonPressed() {
        Bitmap SamplePhoto = mView.getSamplePhoto();
        mView.doImageRecognition(SamplePhoto);
    }

    protected void doImageRecognitionOnPhoto() {
        File mediaFile = new File(mView.getImagesLocation(), MainActivity.PHOTO_FILE_NAME);
        if (!mediaFile.exists()) {
            throw new RuntimeException("Trying to recognise photo, but I can't find file at "
                    + mView.getImagesLocation() + MainActivity.PHOTO_FILE_NAME);
        }

        Bitmap bitmap = CreateCroppedBitmap(mediaFile.getPath());
        mView.doImageRecognition(bitmap);
    }

    /**
     * Crops the photo at photoPath to the size needed by image recognition, returning the resulting
     * Bitmap
     * @param photoPath
     * @return
     */
    static private Bitmap CreateCroppedBitmap(String photoPath) {
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
}
