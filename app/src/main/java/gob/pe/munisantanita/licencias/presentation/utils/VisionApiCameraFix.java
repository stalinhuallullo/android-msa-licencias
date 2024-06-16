package gob.pe.munisantanita.licencias.presentation.utils;

import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import com.google.android.gms.vision.CameraSource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

public class VisionApiCameraFix {

    @StringDef({
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO,
            Camera.Parameters.FOCUS_MODE_AUTO,
            Camera.Parameters.FOCUS_MODE_EDOF,
            Camera.Parameters.FOCUS_MODE_FIXED,
            Camera.Parameters.FOCUS_MODE_INFINITY,
            Camera.Parameters.FOCUS_MODE_MACRO
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface FocusMode {

    }

    public static boolean cameraFocus(@NonNull CameraSource cameraSource, @FocusMode @NonNull String focusMode) {
        Field[] declaredFields = CameraSource.class.getDeclaredFields();

        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        Camera.Parameters params = camera.getParameters();

                        if (!params.getSupportedFocusModes().contains(focusMode)) {
                            return false;
                        }

                        params.setFocusMode(focusMode);
                        camera.setParameters(params);
                        return true;
                    }

                    return false;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                break;
            }
        }

        return false;
    }
}