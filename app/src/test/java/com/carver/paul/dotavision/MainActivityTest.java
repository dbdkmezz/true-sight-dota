package com.carver.paul.dotavision;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carver.paul.dotavision.DotaCamera.CameraActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MainActivityTest {

    private MainActivity activity;

    @Before
    public void setUp() throws Exception
    {
        activity = Robolectric.buildActivity(MainActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void shouldNotBeNull() {
        assertNotNull(activity);
    }

    @Test
    public void shouldHaveDemoButton() {
        Button demoButton = (Button) activity.findViewById(R.id.button_demo);
        assertNotNull(demoButton);
        assertThat(demoButton.getText().toString(),
                equalTo (RuntimeEnvironment.application.getString(R.string.demo)));
        assertTrue(demoButton.getVisibility() == View.VISIBLE);
    }

    @Test
    public void shouldHaveUseLastPhotoButton() {
        Button lastPhotoButton = (Button) activity.findViewById(R.id.button_use_last_photo);
        assertNotNull(lastPhotoButton);
        assertThat(lastPhotoButton.getText().toString(),
                equalTo (RuntimeEnvironment.application.getString(R.string.use_last_photo)));
        assertTrue(lastPhotoButton.getVisibility() == View.VISIBLE);
    }

    @Test
    public void shouldHaveTakePhotoFab() {
        FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.button_fab_take_photo);
        assertNotNull(fab);
        assertTrue(fab.getVisibility() == View.VISIBLE);
    }

    @Test
    public void shouldHaveBackground() {
        ImageView backgroundImage = (ImageView) activity.findViewById(R.id.image_main_background);
        assertNotNull(backgroundImage);
        assertTrue(backgroundImage.getVisibility() == View.VISIBLE);
    }

    @Test
    public void shouldHaveToobarAndNavView() {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        assertNotNull(toolbar);
        assertTrue(toolbar.getVisibility() == View.VISIBLE);

        NavigationView navView = (NavigationView) activity.findViewById(R.id.nav_view);
        assertNotNull(navView);
        assertTrue(navView.getVisibility() == View.VISIBLE);
    }

    @Test
    public void shouldHaveTopImage() {
        ImageView topImage = (ImageView) activity.findViewById(R.id.image_top);
        assertNotNull(topImage);
    }

    @Test
    public void hiddenViewsNotVisible() {
        TextView processingText = (TextView) activity.findViewById(R.id.text_processing_image);
        assertNotNull(processingText);
        assertTrue(processingText.getVisibility() != View.VISIBLE);
    }

    @Test
    public void clickingTakePhoto_shouldStartCameraActivity() {
        activity.findViewById(R.id.button_fab_take_photo).performClick();
        Intent expectedIntent = new Intent(activity, CameraActivity.class);
        assertTrue(shadowOf(activity).getNextStartedActivity().equals(expectedIntent));
    }
}