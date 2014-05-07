package com.snail.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import com.snail.camera.service.CameraService;

public class HomeActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

    private SurfaceView cameraView;

    private ImageView image;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//        MainService.statusBarHeight = frame.top;

        Intent service = new Intent(this, CameraService.class);
        this.startService(service);
    }
}
