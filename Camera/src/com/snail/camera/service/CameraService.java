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
import com.snail.camera.core.CameraHolder;
import com.snail.camera.core.CameraManager;
import com.snail.camera.core.CameraTouch;

import java.io.IOException;
import java.util.List;

/**
 * Created by fenghb on 2014/5/7.
 */
public class CameraService extends Service implements CameraManager.Callback {
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

        wmParams = new WindowManager.LayoutParams();
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


    private void initViews() {
        layout = LayoutInflater.from(this).inflate(R.layout.camera, null);

        image = (ImageView) layout.findViewById(R.id.image);
        cameraView = (SurfaceView) layout.findViewById(R.id.camera_view);
        cameraView.setFocusableInTouchMode(true);
        cameraView.setOnTouchListener(new CameraTouch(wm, wmParams));

        mHolder = cameraView.getHolder();
        mCamera = Camera.open();
        mHolder.addCallback(new CameraHolder(mCamera));

    }


    //create camera view
    private void createCameraView() {


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
    public void startRecoder() {
        
    }

    @Override
    public void stopRecoder() {

    }

    @Override
    public void takePhoto() {

    }
}
