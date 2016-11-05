/**
 * True Sight for Dota 2
 * Copyright (C) 2015 Paul Broadbent
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */

package com.carver.paul.truesight.Ui.DotaCamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.BuildConfig;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.carver.paul.truesight.ImageRecognition.Variables;
import com.carver.paul.truesight.R;
import com.carver.paul.truesight.Ui.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//TODO-now: make DotaCamera MVP

//TODO-soon: fix the crashes in the camera

//TODO-someday: crop what the Camera saves

//TODO-someday: Give the camera preview a visible border and add it to the preview

public class CameraActivity extends Activity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private static final String TAG = "Camera Activity";

    private final ScheduledExecutorService mScheduledExecutorService =
            Executors.newScheduledThreadPool(1);
    private CameraOpeningTask mCameraOpeningTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
    }

    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_camera);

        //TODO: ensure I'm using the camera opening task correctly


        // Setup the camera
        if (mCamera == null) {
            if (mCameraOpeningTask != null) {
                Log.w(TAG, "Running onResume, but the mCameraOpeningTask is already running. " +
                        "I don't think this can happen, so I must have missed something!");
                mCameraOpeningTask.cancel(true);
            }
            mCameraOpeningTask = new CameraOpeningTask(this);
            mCameraOpeningTask.execute();
        }

        showCaptureButton();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScheduledExecutorService.shutdown();
        //TODO: check that when I cancel the camerOpeningTask I want to stop it even if it's running
        mCameraOpeningTask.cancel(true);
        mCameraOpeningTask = null;
        releaseCamera();              // release the mCamera immediately on pause event
    }

    /**
     * called when the capture photo button is pressed
     *
     * @param view
     */
    public void capturePhoto(View view) {
        if (mCameraOpeningTask.getStatus() != AsyncTask.Status.FINISHED
                || mCamera == null
                || mPreview == null
                || !mPreview.isSurfaceCreated())
            return;

        if (mCamera.getParameters().getFocusMode() != null
                && mCamera.getParameters().getFocusMode().equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {

            // add a 3 second timeout to autofocus
            //https://stackoverflow.com/questions/6658868/camera-autofocus-callback-not-happening
            final ScheduledFuture<?> focusTimeoutFuture = mScheduledExecutorService.schedule(new Runnable() {
                @Override
                public void run() {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Autofocus didn't succeed after 3 seconds, taking photo anyway.");
                    }
                    mCamera.takePicture(null, null, mPicture);
                    mCamera.cancelAutoFocus();
                }
            }, 3, TimeUnit.SECONDS);

            Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    // cancel the timeout future if it didn't run already
                    boolean canceledFuture = focusTimeoutFuture.cancel(false);
                    if (canceledFuture) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Taking picture with successful autofocus.");
                        }
                        try {
                            CameraActivity.this.mCamera.takePicture(null, null, mPicture);
                            CameraActivity.this.mCamera.cancelAutoFocus();
                        } catch (RuntimeException re) {
                            Log.w(TAG, "Unexpected exception when taking photo", re);
                        }
                    }
                }
            };
            mCamera.autoFocus(autoFocusCallback);
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Taking picture outside focus mode");
            }
            mCamera.takePicture(null, null, mPicture);
        }
    }


    /**
     * called with the confirm capture photo buttin is pressed
     *
     * @param view
     */
    public void confirmPhoto(View view) {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void takePhotoAgain(View view) {
        mCamera.startPreview();
        showCaptureButton();
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile();
            if(pictureFile == null) {
                Log.e(TAG, "Error creating media file, check storage permissions: ");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                showPhotoConfirmButtons();
            } catch (FileNotFoundException e) {
                Log.w(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    private File getOutputMediaFile() {
        return new File(getApplicationContext().getFilesDir(), MainActivity.PHOTO_FILE_NAME);
    }

    private void showPhotoConfirmButtons() {
        ImageButton captureButton = (ImageButton) findViewById(R.id.button_capture);
        ImageButton confirmButton = (ImageButton) findViewById(R.id.button_confirm);
        ImageButton takeAgainButton = (ImageButton) findViewById(R.id.button_take_again);
        captureButton.setVisibility(View.GONE);
        confirmButton.setVisibility(View.VISIBLE);
        takeAgainButton.setVisibility(View.VISIBLE);
    }

    private void showCaptureButton() {
        ImageButton captureButton = (ImageButton) findViewById(R.id.button_capture);
        ImageButton confirmButton = (ImageButton) findViewById(R.id.button_confirm);
        ImageButton takeAgainButton = (ImageButton) findViewById(R.id.button_take_again);
        captureButton.setVisibility(View.VISIBLE);
        confirmButton.setVisibility(View.GONE);
        takeAgainButton.setVisibility(View.GONE);
    }


    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the mCamera for other applications
            mCamera = null;
        }
    }

    private class CameraOpeningTask extends AsyncTask<Void, Void, Camera> {
        private Context mContext;

        public CameraOpeningTask(Context context) {
            mContext = context;
        }

        // This is where the hard work happens which needs to be off the UI thread
        protected Camera doInBackground(Void... params) {
            Camera camera = getCameraInstance();
            setupCamera(camera);
            return camera;
        }

        protected void onPostExecute(Camera camera) {
            mCamera = camera;

            mPreview = new CameraPreview(mContext, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
            setupPreviewLetterbox();

            // THIS CODE WAS SO DODGY. GONE!
            // need to delay setting up the letterbox, because right now the camera hasn't been drawn, so we can't find out its width
            // I worry this will go wrong on slower phones, needs testing
/*            final ScheduledFuture<?> setupLetterboxFuture = mScheduledExecutorService.schedule(new Runnable() {
                @Override
                public void run() {
                    setupPreviewLetterbox();
                }
            }, 500, TimeUnit.MILLISECONDS);*/

        }

        private Camera getCameraInstance() {
            Camera c = null;
            try {
                c = Camera.open();
            } catch (Exception e) {
                // Camera is not available (in use or does not exist)
                Log.e(TAG, "failed to open Camera");
            }
            return c;
        }

        private void setupCamera(Camera camera) {
            if (camera != null) {
                Camera.Parameters parameters = camera.getParameters();

                Camera.Size size = findSmallestGoodCameraSize(parameters.getSupportedPictureSizes(),
                        parameters.getSupportedPreviewSizes());
                if (size != null) {
                    parameters.setPictureSize(size.width, size.height);
                    parameters.setPreviewSize(size.width, size.height);
                }

/*            if(parameters.isAutoExposureLockSupported() == true)
                parameters.setAutoExposureLock(true);*/

                if (parameters.isAutoWhiteBalanceLockSupported() == true) {
                    parameters.setAutoWhiteBalanceLock(true);
                } else {
                    if (BuildConfig.DEBUG) Log.v(TAG, "Camera doesn't support white balance lock.");
                }

/*            List<String> supportedWhiteBalances = parameters.getSupportedWhiteBalance();
            if(supportedWhiteBalances.contains("daylight")) {
                parameters.setWhiteBalance("daylight");
            } else {
                System.out.println("Oh no, mCamera doesn't support daylight white balance.");
            }*/

                List<String> foc = parameters.getSupportedFocusModes();
                if (foc.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);


                //TODO-someday: make camera exposure auto
/*          int maxExposure = parameters.getMaxExposureCompensation();
            if (maxExposure != 0)
                parameters.setExposureCompensation(maxExposure * 3 / 4);*/

/*            if(parameters.getSupportedSceneModes().contains(Camera.Parameters.SCENE_MODE_NIGHT))
                parameters.setSceneMode(Camera.Parameters.SCENE_MODE_NIGHT);*/

                camera.setParameters(parameters);
            }
        }

        /**
         * Returns the smallest Size which is in both input lists and bigger than
         * SCALED_IMAGE_WIDTH x SCALED_IMAGE_HEIGHT.
         *
         * @param pictureSizes
         * @param previewSizes
         * @return
         */
        private Camera.Size findSmallestGoodCameraSize(List<Camera.Size> pictureSizes,
                                                       List<Camera.Size> previewSizes) {

            if (pictureSizes.isEmpty() || previewSizes.isEmpty()) {
                Log.e(TAG, "Can't set a camera size because there are none supported!");
                return null;
            }

            List<Camera.Size> sizesWithGoodWidth = new ArrayList<>();

            //get a list of all the sizes with the smallest width which is at least SCALED_IMAGE_WIDTH
            for (Camera.Size size : previewSizes) {
                if (pictureSizes.contains(size)
                        && (size.width >= Variables.SCALED_IMAGE_WIDTH)) {
                    if (sizesWithGoodWidth.isEmpty()) {
                        sizesWithGoodWidth.add(size);
                    } else if (sizesWithGoodWidth.get(0).width == size.width) {
                        sizesWithGoodWidth.add(size);
                    } else if (sizesWithGoodWidth.get(0).width > size.width) {
                        sizesWithGoodWidth.clear();
                        sizesWithGoodWidth.add(size);
                    }
                }
            }

            Camera.Size result = null;

            // of the sizes with a good width, find the one with the smallest height which is at
            // least SCALED_IMAGE_HEIGHT
            for (Camera.Size size : sizesWithGoodWidth) {
                if (size.height >= Variables.SCALED_IMAGE_HEIGHT
                        && (result == null || result.height > size.height)) {
                    result = size;
                }
            }

            if (result == null) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Failed to find appropriate camera size.");
                }

                // Can't find a good size, so I'll just return any old one
                for (Camera.Size size : previewSizes) {
                    if (pictureSizes.contains(size)) {
                        return size;
                    }
                }

                //TODO-someday: find out if should ever be the case that there are no common sizes in preview and picture
                Log.e(TAG, "Failed to any camera sizes which I can use for the preview and the picture.");
            }

            return result;
        }

        /**
         * This covers up the top and bottom of the camera preview in order to ensure that what's visible is
         * what will be used in the app and is of the ratio Variables.SCALED_IMAGE_WIDTH : Variables.SCALED_IMAGE_HEIGHT
         */
        private void setupPreviewLetterbox() {
            if (mCamera == null) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Not setting up preview letterbox because camera is null. " +
                            "I don't think that should ever happen!");
                }
                return;
            }

            Camera.Size cameraSize = mCamera.getParameters().getPictureSize();
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            RelativeLayout parent = (RelativeLayout) findViewById(R.id.camera_preview_parent);
            int totalPhotoHeight = parent.getWidth() * cameraSize.height / cameraSize.width;

            ViewGroup.LayoutParams previewParams = preview.getLayoutParams();
            ViewGroup.LayoutParams parentParams = parent.getLayoutParams();

            previewParams.width = parent.getWidth();
            previewParams.height = totalPhotoHeight;
            preview.setLayoutParams(previewParams);

            parentParams.height = totalPhotoHeight;
            parent.setLayoutParams(parentParams);
/*
            previewParent.setLayoutParams(parentParams);
*/
/*            preview.setMinimumHeight(totalPhotoHeight);
            previewParent.setMinimumHeight(totalPhotoHeight);*/

            int targetVisiblePhotoHeight = preview.getWidth() * Variables.SCALED_IMAGE_HEIGHT / Variables.SCALED_IMAGE_WIDTH;
            int heightOfEachLetterBoxCover = (totalPhotoHeight - targetVisiblePhotoHeight) / 2;

            View aboveLetterbox = findViewById(R.id.above_camera_preview_letterbox);
            View belowLetterbox = findViewById(R.id.below_camera_preview_letterbox);

            ViewGroup.LayoutParams aboveLetterboxParams = aboveLetterbox.getLayoutParams();
            aboveLetterboxParams.height = heightOfEachLetterBoxCover;
            aboveLetterbox.setLayoutParams(aboveLetterboxParams);

            ViewGroup.LayoutParams belowLetterboxParams = belowLetterbox.getLayoutParams();
            belowLetterboxParams.height = heightOfEachLetterBoxCover;
            belowLetterbox.setLayoutParams(belowLetterboxParams);

/*            aboveLetterbox.setMinimumHeight(heightOfEachLetterBoxCover);
            belowLetterbox.setMinimumHeight(heightOfEachLetterBoxCover);*/
        }

        //Removed, calling this code from a ScheduledExecutorService caused horrible things to happen
/*
        private void setupPreviewLetterbox() {
            int cameraPreviewWidth = findViewById(R.id.camera_preview).getWidth();
            int cameraParentHeight = findViewById(R.id.camera_preview_parent).getHeight();
            int targetPreviewHeight = cameraPreviewWidth * Variables.SCALED_IMAGE_HEIGHT / Variables.SCALED_IMAGE_WIDTH;
            int heightOfPreviewCovers = (cameraParentHeight - targetPreviewHeight) / 2;

            View aboveLetterbox = findViewById(R.id.above_camera_preview_letterbox);
            View belowLetterbox = findViewById(R.id.below_camera_preview_letterbox);
            aboveLetterbox.setMinimumHeight(heightOfPreviewCovers);
            belowLetterbox.setMinimumHeight(heightOfPreviewCovers);
*/
/*
            aboveLetterbox.bringToFront();
            belowLetterbox.bringToFront();
*//*

        }
*/
    }
}


/**
 * A basic Camera preview class
 */
class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private static final String TAG = "Camera Preview";
    private boolean mSurfaceCreated = false;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the mCamera where to draw the preview.
        if (mCamera == null) {
            return;
        }
        try {
            mCamera.setPreviewDisplay(holder);

            // No idea why, but if this is not run then I don't get the bottom edge of the letterbox!
/*            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            setLayoutParams(layoutParams);*/

/*            Camera.Size cameraSize = mCamera.getParameters().getPictureSize();
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = (getWidth() * cameraSize.height / cameraSize.width);
            setLayoutParams(layoutParams);*/

            mCamera.startPreview();
            mSurfaceCreated = true;

        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Error setting mCamera preview: " + e.getMessage());
            }
        }
    }

    public boolean isSurfaceCreated() {
        return mSurfaceCreated;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed called.");
        //don't need to do anything here, camera is released when the Activity is paused anyway
    }

    /*
        If you want to set a specific size for your mCamera preview,
        set this in the surfaceChanged() method as noted in the comments
        above. When setting preview size, you must use values from
        getSupportedPreviewSizes(). Do not set arbitrary values in
        the setPreviewSize() method.
    */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "Error starting mCamera preview: " + e.getMessage());
            }
        }
    }
}
