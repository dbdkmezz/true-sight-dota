package com.carver.paul.dotavision.DotaCamera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.carver.paul.dotavision.ImageRecognition.Variables;
import com.carver.paul.dotavision.MainActivity;
import com.carver.paul.dotavision.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

//TODO: fix alt-tabbing back into camera. It gets released and never restarted so crashes

/**
 * Checking camera features
 *
 * Once you obtain access to a camera, you can get further information about
 * its capabilities using the Camera.getParameters() method and checking the
 * returned Camera.Parameters object for supported capabilities. When using API
 * Level 9 or higher, use the Camera.getCameraInfo() to determine if a camera
 * is on the front or back of the device, and the orientation of the image.
 */

 public class CameraActivity extends Activity {
    private Camera mCamera;
    private SurfaceView mPreview;
    private static final String TAG = "Camera Activity";

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            MainActivity.EnsureMediaDirectoryExists();
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null){
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
        return new File(MainActivity.getImagesLocation(), "photo.jpg");
    }

    private void showPhotoConfirmButtons() {
        Button captureButton = (Button) findViewById(R.id.button_capture);
        Button confirmButton = (Button) findViewById(R.id.button_confirm);
        Button takeAgainButton = (Button) findViewById(R.id.button_take_again);
        captureButton.setVisibility(View.GONE);
        confirmButton.setVisibility(View.VISIBLE);
        takeAgainButton.setVisibility(View.VISIBLE);
    }

    private void showCaptureButton() {
        Button captureButton = (Button) findViewById(R.id.button_capture);
        Button confirmButton = (Button) findViewById(R.id.button_confirm);
        Button takeAgainButton = (Button) findViewById(R.id.button_take_again);
        captureButton.setVisibility(View.VISIBLE);
        confirmButton.setVisibility(View.GONE);
        takeAgainButton.setVisibility(View.GONE);
    }

    public void capturePhoto(View view) {
        //mCamera.takePicture(null, null, mPicture);
        if(mCamera.getParameters().getFocusMode().equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {

            Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    System.out.println("Auto focus success.");
                    mCamera.takePicture(null, null, mPicture);
                    mCamera.cancelAutoFocus();

/*                        Camera.Parameters params = camera.getParameters();
                        if (params.isAutoExposureLockSupported()) {
                            params.setAutoExposureLock(true);
                            camera.setParameters(params);

                            params = camera.getParameters();
                            params.setAutoExposureLock(false);
                            camera.setParameters(params);
                        }*//*
*/
/*
*//*
*/
/*
                        if (success)
                        {

                            globalFocusedBefore = true;
                            takePicture();
                        }*//*
*/


                }
            };//end
            mCamera.autoFocus(autoFocusCallback);
        }

    }

    //TODO-soon: send intent back after taking photo
    public void confirmPhoto(View view) {
        super.onBackPressed();
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
        mCamera = getCameraInstance();
        setupCamera();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        showCaptureButton();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mCamera == null) {
            setContentView(R.layout.activity_camera);

            mCamera = getCameraInstance();
            setupCamera();

            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
        }
    }

    private void setupCamera() {
        if(mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            String wb = parameters.getWhiteBalance();

            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            int smallestAllowableHeight = 220;
            Camera.Size newSize = null;
            for(Camera.Size currentSize : sizes) {
                if(currentSize.width == Variables.SCALED_IMAGE_WIDTH && currentSize.height > smallestAllowableHeight) {
                    if(newSize == null || currentSize.height < newSize.height)
                        newSize = currentSize;
                }
            }
            if(newSize != null) {
                parameters.setPictureSize(newSize.width, newSize.height);
                parameters.setPreviewSize(newSize.width, newSize.height);
            }

            List<String> foc = parameters.getSupportedFocusModes();
            if(foc.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

            //TODO: fix camera exposure to make it auto
            int maxExposure = parameters.getMaxExposureCompensation();
            if(maxExposure != 0)
                parameters.setExposureCompensation(maxExposure * 3 / 4);

/*            if(parameters.getSupportedSceneModes().contains(Camera.Parameters.SCENE_MODE_NIGHT))
                parameters.setSceneMode(Camera.Parameters.SCENE_MODE_NIGHT);*/

            mCamera.setParameters(parameters);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    /** Check if this device has a camera
     * Shouldn't be needed because my AndroidManifest requires a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            throw new RuntimeException("This device doesn't have a camera");
            //return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    private static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}


/** A basic Camera preview class */
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
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);




        Camera.Size cameraSize = mCamera.getParameters().getPictureSize();
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = (getWidth() * cameraSize.height / cameraSize.width);
        setLayoutParams(layoutParams);


            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    //TODO-now remove surfaceDestroyed class?
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    //TODO-now remove surfaceChanged class?

/*
    If you want to set a specific size for your camera preview,
    set this in the surfaceChanged() method as noted in the comments
    above. When setting preview size, you must use values from
    getSupportedPreviewSizes(). Do not set arbitrary values in
    the setPreviewSize() method.
*/
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}
