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

//TODO-now: put presenters and views in one package so that all the UI code only presenters run can be protected?

package com.carver.paul.dotavision.Presenters;

import android.content.Context;
import android.graphics.Bitmap;

import com.carver.paul.dotavision.AbilityInfo.AbilityInfoPresenter;
import com.carver.paul.dotavision.MainActivity;
import com.carver.paul.dotavision.Models.DataManager;

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
        mDataManager.identifyHeroesInPhoto(photo, heroesDetectedPresenter, abilityInfoPresenter);
    }

    public Context getContext() {
        return mView;
    }

    public void startHeroRecognitionLoadingAnimations(Bitmap photo) {
        mView.setTopImage(photo);
        mView.slideDemoButtonsOffScreen();
        mView.hideBackground();
        mView.pulseCameraFab();
    }

    public void stopHeroRecognitionLoadingAnimations() {
        mView.stopHeroRecognitionLoadingAnimations();
    }
}
