package com.snail.camera.core;

import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.snail.camera.R;

/**
 * Created by fenghb on 2014/5/7.
 */
public class CameraTouch implements View.OnTouchListener {
    private final WindowManager windowManager;
    private final WindowManager.LayoutParams params;

    public CameraTouch(WindowManager windowManager, WindowManager.LayoutParams params) {
        this.windowManager = windowManager;
        this.params = params;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.layout:
                float x = motionEvent.getRawX();
                float y = motionEvent.getRawY();
                float mTouchStartX = 0;
                float mTouchStartY = 0;
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchStartX = motionEvent.getX();
                        mTouchStartY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        params.x = (int) (x - mTouchStartX);
                        params.y = (int) (y - mTouchStartY);
                        windowManager.updateViewLayout(view, params);

                        break;
                    case MotionEvent.ACTION_UP:
                        params.x = (int) (x - mTouchStartX);
                        params.y = (int) (y - mTouchStartY);
                        windowManager.updateViewLayout(view, params);
                        break;
                }
                break;
            default:
                break;
        }
        return true;
    }
}
