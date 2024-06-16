package gob.pe.munisantanita.licencias.presentation.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

public class CameraSourcePreview extends ViewGroup {
    private static final String TAG = "CameraSourcePreview";

    private Context mContext;
    private SurfaceView mSurfaceView;
    private boolean mStartRequested;
    private boolean mSurfaceAvailable;
    private CameraSource mCameraSource;


    public CameraSourcePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mStartRequested = false;
        mSurfaceAvailable = false;

        mSurfaceView = new SurfaceView(context);
        mSurfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(mSurfaceView);
    }

    public void start(CameraSource cameraSource) throws IOException {
        if (cameraSource == null) {
            stop();
        }

        mCameraSource = cameraSource;

        if (mCameraSource != null) {
            mStartRequested = true;
            startIfReady();
        }
    }

    public void stop() {
        if (mCameraSource != null) {
            mCameraSource.stop();
        }
    }

    public void release() {
        if (mCameraSource != null) {
            mCameraSource.release();
            mCameraSource = null;
        }
    }

    private int requestCode = 101;
    private void startIfReady() throws IOException {
        if (mStartRequested && mSurfaceAvailable) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getContext(), new String[] {Manifest.permission.CAMERA}, requestCode);
                return;
            }
            mCameraSource.start(mSurfaceView.getHolder());
            VisionApiCameraFix.cameraFocus(mCameraSource, Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mStartRequested = false;
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surface) {
            mSurfaceAvailable = true;
            try {
                startIfReady();
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source.", e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
            mSurfaceAvailable = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = 320;
        int height = 240;

        if (mCameraSource != null) {
            Size size = mCameraSource.getPreviewSize();
            if (size != null) {
                width = size.getWidth();
                height = size.getHeight();
            }
        }

        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
        if (isPortraitMode()) {
            int tmp = width;
            width = height;
            height = tmp;
        }

        final int layoutWidth = right - left;
        final int layoutHeight = bottom - top;

        // Computes height and width for potentially doing fit width.
        int childWidth = layoutWidth;
        int childHeight = (int) (((float) layoutWidth / (float) width) * height);

        // If height is too tall using fit width, does fit height instead.
        if (childHeight > layoutHeight) {
            childHeight = layoutHeight;
            childWidth = (int)(((float) layoutHeight / (float) height) * width);
        }

        for (int i = 0; i < getChildCount(); ++i) {
            getChildAt(i).layout(0, 0, childWidth, childHeight);
            //getChildAt(i).layout(0, 0, width    , height);
        }

        try {
            startIfReady();
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
        /* Test
        *****************/
//
//        int previewWidth = 320;
//        int previewHeight = 240;
//        if (mCameraSource != null) {
//            Size size = mCameraSource.getPreviewSize();
//            if (size != null) {
//                previewWidth = size.getWidth();
//                previewHeight = size.getHeight();
//            }
//        }
//
//        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
//        if (isPortraitMode()) {
//            int tmp = previewWidth;
//            previewWidth = previewHeight;
//            previewHeight = tmp;
//        }
//
//        final int viewWidth = right - left;
//        final int viewHeight = bottom - top;
//
//        int childWidth;
//        int childHeight;
//        int childXOffset = 0;
//        int childYOffset = 0;
//        float widthRatio = (float) viewWidth / (float) previewWidth;
//        float heightRatio = (float) viewHeight / (float) previewHeight;
//
//        // To fill the view with the camera preview, while also preserving the correct aspect ratio,
//        // it is usually necessary to slightly oversize the child and to crop off portions along one
//        // of the dimensions.  We scale up based on the dimension requiring the most correction, and
//        // compute a crop offset for the other dimension.
//        if (widthRatio > heightRatio) {
//            childWidth = viewWidth;
//            childHeight = (int) ((float) previewHeight * widthRatio);
//            childYOffset = (childHeight - viewHeight) / 2;
//        } else {
//            childWidth = (int) ((float) previewWidth * heightRatio);
//            childHeight = viewHeight;
//            childXOffset = (childWidth - viewWidth) / 2;
//        }
//
//        for (int i = 0; i < getChildCount(); ++i) {
//            // One dimension will be cropped.  We shift child over or up by this offset and adjust
//            // the size to maintain the proper aspect ratio.
//            getChildAt(i).layout(
//                    -1 * childXOffset, -1 * childYOffset,
//                    childWidth - childXOffset, childHeight - childYOffset);
//        }
//
//        try {
//            startIfReady();
//        } catch (IOException e) {
//            Log.e(TAG, "Could not start camera source.", e);
//        }

    }

    private boolean isPortraitMode() {
        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }

        Log.d(TAG, "isPortraitMode returning false by default");
        return false;
    }
}
