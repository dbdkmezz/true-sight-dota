
package com.carver.paul.dotavision;

import android.hardware.Camera;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowCamera;

@Implements(Camera.class)
public class CustomShadowCamera extends ShadowCamera {
    @Implementation
    public void takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw,
                            Camera.PictureCallback jpeg) {
        jpeg.onPictureTaken(null, null);
    }
}

/*
// The Robolectric shadow for Camera.Parameters doesn't have a isAutoWhiteBalanceLockSupported
// method, so I've added it myself.
@Implements(value = Camera.class, inheritImplementationMethods = true)
public class CustomShadowCamera extends ShadowCamera {
    private Camera.Parameters parameters;

    @Implementation
    public Camera.Parameters getParameters() {
        if (null == parameters) {
            parameters = newInstanceOf(Camera.Parameters.class);
        }
        return parameters;
    }

    @Implements(Camera.Parameters.class)
    public class CustomShadowParameters extends ShadowCamera.ShadowParameters {
        @Implementation
        public boolean isAutoWhiteBalanceLockSupported() {
            return false;
        }
    }
}*/
