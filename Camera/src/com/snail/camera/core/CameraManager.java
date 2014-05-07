package com.snail.camera.core;

/**
 * Created by fenghb on 2014/5/7.
 */
public class CameraManager {
    private Callback callback;

    private static CameraManager manager;


    public interface Callback {
        void startRecoder();

        void stopRecoder();

        void takePhoto();
    }


    public static CameraManager getInstance() {
        if (manager == null)
            manager = new CameraManager();
        return manager;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void startRecoder() {
        callback.startRecoder();
    }

    public void stopRecoder() {
        callback.stopRecoder();
    }

    public void takePhoto() {
        callback.takePhoto();
    }
}
