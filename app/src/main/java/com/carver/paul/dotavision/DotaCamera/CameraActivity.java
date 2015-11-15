package com.carver.paul.dotavision.DotaCamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.carver.paul.dotavision.ImageRecognition.Variables;
import com.carver.paul.dotavision.MainActivity;
import com.carver.paul.dotavision.R;

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

//TODO: crop camera preview and crop what the mCamera saves
//TODO: make camera pretty
//TODO: enable you to go back from camera without taking photo
//TODO-now: make camera activity send intent back so you can use the photo immediately
//TODO: open the camera in a seperate thread

public class CameraActivity extends Activity {
    private Camera mCamera;
    private SurfaceView mPreview;
    private static final String TAG = "Camera Activity";

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            MainActivity.EnsureMediaDirectoryExists();
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions: ");// + e.getMessage());
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                showPhotoConfirmButtons();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    private static File getOutputMediaFile() {
        return new File(MainActivity.getImagesLocation(), MainActivity.PHOTO_FILE_NAME);
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

    private final ScheduledExecutorService mScheduledExecutorService =
            Executors.newScheduledThreadPool(1);

    public void capturePhoto(View view) {
        //mCamera.takePicture(null, null, mPicture);
        if (mCamera.getParameters().getFocusMode().equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {

//https://stackoverflow.com/questions/6658868/camera-autofocus-callback-not-happening
            final ScheduledFuture<?> focusTimeoutFuture = mScheduledExecutorService.schedule(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Autofocus didn't succeed after 3 seconds, taking photo anyway.");
                    mCamera.takePicture(null, null, mPicture);
                    mCamera.cancelAutoFocus();
                }
            }, 3, TimeUnit.SECONDS);// add a 3 second timeout to autofocus

            Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    // cancel the timeout future if it didn't run already
                    boolean canceledFuture = focusTimeoutFuture.cancel(false);
                    if (canceledFuture) {
                        Log.d(TAG, "Taking pciture with correct autofocus.");
                        CameraActivity.this.mCamera.takePicture(null, null, mPicture);
                        CameraActivity.this.mCamera.cancelAutoFocus();
                    }
                }
            };
            mCamera.autoFocus(autoFocusCallback);
        } else {
            Log.d(TAG, "Taking piture outside focus mode");
            mCamera.takePicture(null, null, mPicture);
        }
    }

    // runs when confirm button is pressed.
    public void confirmPhoto(View view) {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void takePhotoAgain(View view) {
        mCamera.startPreview();
        showCaptureButton();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Create an instance of Camera


        new CameraOpeningTask(this).execute();
/*
        mCamera = getCameraInstance();
        setupCamera();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);*/
    }

/*    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * displayMetrics.density);
    }*/

    @Override
    public void onResume() {
        super.onResume();

        if (mCamera == null) {
            setContentView(R.layout.activity_camera);


            new CameraOpeningTask(this).execute();
            /*
            mCamera = getCameraInstance();
            setupCamera();

            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);*/
        }
        showCaptureButton();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the mCamera immediately on pause event
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the mCamera for other applications
            mCamera = null;
        }
    }

    /**
     * Check if this device has a mCamera
     * Shouldn't be needed because my AndroidManifest requires a mCamera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a mCamera
            return true;
        } else {
            // no mCamera on this device
            throw new RuntimeException("This device doesn't have a mCamera");
            //return false;
        }
    }

    private class CameraOpeningTask extends AsyncTask<Void, Void, Camera> {

        private Context context;

        public CameraOpeningTask(Context context) {
            this.context = context;
        }

        // This is where the hard work happens which needs to be off the UI thread
        protected Camera doInBackground(Void... params) {
            Camera camera = getCameraInstance();
            setupCamera(camera);
            return camera;
        }

        protected void onPostExecute(Camera camera) {
            mCamera = camera;

            mPreview = new CameraPreview(context, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);

            // need to delay setting up the letterbox, because right now the camera hasn't been drawn, so we can't find out its width
            // I worry this will go wrong on slower phones, needs testing
            final ScheduledFuture<?> setupLetterboxFuture = mScheduledExecutorService.schedule(new Runnable() {
                @Override
                public void run() {
                    setupPreviewLetterbox();
                }
            }, 500, TimeUnit.MILLISECONDS);

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

                Camera.Size size = findSmallestGoodCameraSize(parameters.getSupportedPictureSizes());
                if (size != null) {
                    parameters.setPictureSize(size.width, size.height);
                    parameters.setPreviewSize(size.width, size.height);
                }

/*            if(parameters.isAutoExposureLockSupported() == true)
                parameters.setAutoExposureLock(true);*/

                if (parameters.isAutoWhiteBalanceLockSupported() == true) {
                    parameters.setAutoWhiteBalanceLock(true);
                } else {
                    Log.d(TAG, "Oh no, mCamera doesn't support white balance lock.");
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

/*            //TODO: fix mCamera exposure to make it auto
            int maxExposure = parameters.getMaxExposureCompensation();
            if (maxExposure != 0)
                parameters.setExposureCompensation(maxExposure * 3 / 4);*/

/*            if(parameters.getSupportedSceneModes().contains(Camera.Parameters.SCENE_MODE_NIGHT))
                parameters.setSceneMode(Camera.Parameters.SCENE_MODE_NIGHT);*/

                camera.setParameters(parameters);
            }
        }

        private Camera.Size findSmallestGoodCameraSize(List<Camera.Size> sizes) {
            List<Camera.Size> sizesWithGoodWidth = new ArrayList<>();

            //get a list of all the sizes with the smallest width at lest SCALED_IMAGE_WIDTH
            for (Camera.Size currentSize : sizes) {
                if (currentSize.width >= Variables.SCALED_IMAGE_WIDTH) {
                    if (sizesWithGoodWidth.isEmpty()) {
                        sizesWithGoodWidth.add(currentSize);
                    } else if (sizesWithGoodWidth.get(0).width == currentSize.width) {
                        sizesWithGoodWidth.add(currentSize);
                    } else if (sizesWithGoodWidth.get(0).width > currentSize.width) {
                        sizesWithGoodWidth.clear();
                        sizesWithGoodWidth.add(currentSize);
                    }
                }
            }

            Camera.Size result = null;

            // of the sizes with good witdth, find the one with the smallest height at least SCALED_IMAGE_HEIGHT
            for (Camera.Size currentSize : sizesWithGoodWidth) {
                if (currentSize.height >= Variables.SCALED_IMAGE_HEIGHT &&
                        (result == null || result.height > currentSize.height)) {
                    result = currentSize;
                }
            }

            if (result == null) {
                Log.d(TAG, "Failed to find appropriate mCamera size.");
                if (!sizes.isEmpty()) {
                    result = sizes.get(0);
                }
            }

            return result;
        }

        //TODO: replace camera letterbox code with just doing it in xml, this is too unreliable
        private void setupPreviewLetterbox() {
            int cameraPreviewWidth = findViewById(R.id.camera_preview).getWidth();
            int cameraParentHeight = findViewById(R.id.camera_preview_parent).getHeight();
            int targetPreviewHeight = cameraPreviewWidth * Variables.SCALED_IMAGE_HEIGHT / Variables.SCALED_IMAGE_WIDTH;
            int heightOfPreviewCovers = (cameraParentHeight - targetPreviewHeight) / 2;

            View aboveLetterbox = findViewById(R.id.above_camera_preview_letterbox);
            View belowLetterbox = findViewById(R.id.below_camera_preview_letterbox);
            aboveLetterbox.setMinimumHeight(heightOfPreviewCovers);
            belowLetterbox.setMinimumHeight(heightOfPreviewCovers);
/*
            aboveLetterbox.bringToFront();
            belowLetterbox.bringToFront();
*/
        }
    }
}


/**
 * A basic Camera preview class
 */
class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private static final String TAG = "Camera Preview";

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
        try {
            mCamera.setPreviewDisplay(holder);


            // No idea why, but if this is not run then I don't get the bottom edge of the letterbox!
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            setLayoutParams(layoutParams);

/*            Camera.Size cameraSize = mCamera.getParameters().getPictureSize();
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = (getWidth() * cameraSize.height / cameraSize.width);
            setLayoutParams(layoutParams);*/


            mCamera.startPreview();

        } catch (IOException e) {
            Log.d(TAG, "Error setting mCamera preview: " + e.getMessage());
        }
    }

    //TODO-now remove surfaceDestroyed class?
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    //TODO-now remove surfaceChanged class?

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
            Log.d(TAG, "Error starting mCamera preview: " + e.getMessage());
        }
    }
}
