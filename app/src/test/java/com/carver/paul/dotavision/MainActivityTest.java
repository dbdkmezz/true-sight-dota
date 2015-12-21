package com.carver.paul.dotavision;

import android.content.Intent;

import com.carver.paul.dotavision.DotaCamera.CameraActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MainActivityTest {
    @Test
    public void clickingLogin_shouldStartLoginActivity() {
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);

        activity.findViewById(R.id.button_fab_take_photo).performClick();

        Intent expectedIntent = new Intent(activity, CameraActivity.class);
        assertTrue(shadowOf(activity).getNextStartedActivity().equals(expectedIntent));
    }
}

/*class MainActivityWithoutOpenCV extends MainActivity {
    static {
        sNeedToLoadOpenCV = false;
    }
}*/

/*
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.carver.paul.dotavision.DotaCamera.CameraActivity;

public class MainActivityTest extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final View button = findViewById(R.id.button_fab_take_photo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
            }
        });
    }
}*/
