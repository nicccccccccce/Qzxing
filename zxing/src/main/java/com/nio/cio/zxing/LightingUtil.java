package com.nio.cio.zxing;

import android.hardware.Camera;
import android.util.Log;

/**
 * Created by user on 2017/7/5.
 */

public class LightingUtil {

    private static LightingUtil mLightingUtil;

    public static LightingUtil getInstance() {
        if (mLightingUtil == null) {
            mLightingUtil = new LightingUtil();
        }
        return mLightingUtil;
    }

    private Camera camera = null;

    private boolean isFlashlightOn() {
        try {
            Camera.Parameters parameters = camera.getParameters();
            String flashMode = parameters.getFlashMode();
            if (flashMode.equals(android.hardware.Camera.Parameters.FLASH_MODE_TORCH)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public void onToggle() {

        if (isFlashlightOn()) {
            onClose();
            camera = null;
        } else {
            onOpen();
        }
    }

    public void onOpen() {
        //异常处理一定要加，否则Camera打开失败的话程序会崩溃
        try {
            Log.d("smile", "camera打开");
            camera = Camera.open();
        } catch (Exception e) {
            Log.d("smile", "Camera打开有问题");
        }

        if (camera != null) {
            //打开闪光灯
            camera.startPreview();
            Camera.Parameters parameter = camera.getParameters();
            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameter);
            Log.d("smile", "闪光灯打开");


        }
    }

    public void onClose() {
        if (camera != null) {
            //关闭闪光灯
            Log.d("smile", "closeCamera()");
            camera.getParameters().setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(camera.getParameters());
            camera.stopPreview();
            camera.release();
            camera = null;


        }
    }

}