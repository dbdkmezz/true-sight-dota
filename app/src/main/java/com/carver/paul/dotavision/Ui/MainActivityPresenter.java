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
import android.view.View;

import com.carver.paul.dotavision.Ui.AbilityInfo.AbilityInfoPresenter;
import com.carver.paul.dotavision.Models.DataManager;
import com.carver.paul.dotavision.Ui.HeroesDetected.HeroesDetectedPresenter;

import java.io.File;

public class MainActivityPresenter {
    private MainActivity mView;
    private DataManager mDataManager;

    public MainActivityPresenter(MainActivity view) {
        mView = view;
        mDataManager = new DataManager(this);
    }

    public void onDestroy() {
        mDataManager.onDestroy();
    }

    public void doImageRecognition(Bitmap photo,
                                   HeroesDetectedPresenter heroesDetectedPresenter,
                                   AbilityInfoPresenter abilityInfoPresenter) {
        if(!mDataManager.presentersRegistered()) {
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
}
