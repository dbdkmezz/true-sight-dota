package com.carver.paul.dotavision;

import android.view.View;
import android.widget.ImageButton;

import com.carver.paul.dotavision.DotaCamera.CameraActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//TODO-beauty: add test for taking pictures (will have to stop the code needing to write a file)

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21,
        shadows = {CustomShadowParameters.class, CustomShadowCamera.class})
public class CameraActivityTest {

    private CameraActivity activity;

    @Before
    public void setUp() throws Exception
    {
        activity = Robolectric.buildActivity(CameraActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void shouldNotBeNull() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void shouldHaveCaptureButton() {
        ImageButton capture = (ImageButton) activity.findViewById(R.id.button_capture);
        assertNotNull(capture);
        assertTrue(capture.getVisibility() == View.VISIBLE);
    }

    @Test
    public void shouldHaveInvisibleConfirmButton() {
        ImageButton confirm = (ImageButton) activity.findViewById(R.id.button_confirm);
        assertNotNull(confirm);
        assertTrue(confirm.getVisibility() != View.VISIBLE);
    }

    @Test
    public void shouldHaveInvisibleTakeAgainButton() {
        ImageButton takeAgain = (ImageButton) activity.findViewById(R.id.button_take_again);
        assertNotNull(takeAgain);
        assertTrue(takeAgain.getVisibility() != View.VISIBLE);
    }

/*    @Test
    public void clickingCapture_shouldShowOtherButtons() {
        Robolectric.flushBackgroundThreadScheduler();
        activity.findViewById(R.id.button_capture).performClick();

        Robolectric.flushBackgroundThreadScheduler();

        assertTrue(activity.findViewById(R.id.button_capture).getVisibility() != View.VISIBLE);
        assertTrue(activity.findViewById(R.id.button_take_again).getVisibility() == View.VISIBLE);
        assertTrue(activity.findViewById(R.id.button_confirm).getVisibility() == View.VISIBLE);
    }*/
}