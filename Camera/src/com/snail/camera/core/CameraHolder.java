package com.snail.camera.core;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by fenghb on 2014/5/7.
 */
public class CameraHolder implements SurfaceHolder.Callback {
    private static final String TAG = "CameraHolder";

    private final Camera mCamera;


    public CameraHolder(Camera camera) {
        this.mCamera = camera;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(surfaceHolder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();

            Camera.Size csize = mCamera.getParameters().getPreviewSize();
            int mPreviewHeight = csize.height; //
            int mPreviewWidth = csize.width;
            parameters.setPreviewSize(mPreviewWidth, mPreviewHeight);

            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }
}
