package com.snail.camera.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import com.snail.camera.R;

import java.io.IOException;
import java.util.List;

/**
 * Created by fenghb on 2014/5/7.
 */
public class CameraService extends Service implements SurfaceHolder.Callback, View.OnTouchListener {
    private static final String TAG = "CameraService";

    //camera height
    public static int STATUSBAR_HEIGHT = 25;

    private WindowManager wm;
    private WindowManager.LayoutParams wmParams;

    //SurfaceHolder
    private SurfaceHolder mSurfaceHolder;

    //views
    private View layout;
    private ImageView image;
    private SurfaceView cameraView;

    //camera
    private Camera mCamera;

    //
    private MediaRecorder recorder;

    //
    private SurfaceHolder mHolder;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getIntExtra("MSG", 0) == 0x01) {
                if (cameraView != null) {
                    wm.removeView(cameraView);
                    cameraView = null;
                    stopSelf();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        initViews();
        createCameraView();
        initCamera();
    }

    @Override
    public void onDestroy() {
        if (cameraView != null) {
            wm.removeView(cameraView);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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


    private void initViews() {
        layout = LayoutInflater.from(this).inflate(R.layout.camera, null);
        image = (ImageView) layout.findViewById(R.id.image);
        cameraView = (SurfaceView) layout.findViewById(R.id.camera_view);
        mHolder = cameraView.getHolder();
        mHolder.addCallback(this);

    }


    //create camera view
    private void createCameraView() {

        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = 100;
        wmParams.height = 100;
        wm.addView(layout, wmParams);
    }


    //init camera
    private void initCamera() {
        mCamera = Camera.open();
        recorder = new MediaRecorder();
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        recorder.setVideoSize(176, 144);
        recorder.setVideoFrameRate(20);
        recorder.setPreviewDisplay(cameraView.getHolder().getSurface());

    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.layout:
                float x = event.getRawX();
                float y = event.getRawY();
                float mTouchStartX = 0;
                float mTouchStartY = 0;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        wmParams.x = (int) (x - mTouchStartX);
                        wmParams.y = (int) (y - mTouchStartY);
                        wm.updateViewLayout(layout, wmParams);

                        break;
                    case MotionEvent.ACTION_UP:
                        wmParams.x = (int) (x - mTouchStartX);
                        wmParams.y = (int) (y - mTouchStartY);
                        wm.updateViewLayout(layout, wmParams);
                        mTouchStartX = mTouchStartY = 0;
                        break;
                }
                break;
            default:
                break;


        }

        return true;
    }
}
