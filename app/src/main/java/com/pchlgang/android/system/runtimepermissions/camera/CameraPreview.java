package com.pchlgang.android.system.runtimepermissions.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * 显示一个{@link Camera}的相机预览
 * <p>处理显示和停止预览等基本生命周期方法
 * <p>基于http://developer.android.com/guide/topics/media/camera.html文档实现
 * <p>使用过时的android.hardware.Camera支持{14 < API < 21}.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.CameraInfo mCameraInfo;
    private int mDisplayOrientation;

    public CameraPreview(Context context) {
        this(context, null, null, 0);
    }

    public CameraPreview(Context context, Camera camera, Camera.CameraInfo cameraInfo, int mDisplayOrientation) {
        super(context);
        if (camera == null || cameraInfo == null)
            return;
        mCamera = camera;
        mCameraInfo = cameraInfo;
        this.mDisplayOrientation = mDisplayOrientation;
        //初始化SurfaceHolder.Callback以便底层SurfaceView创建和销毁时能得到通知
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    /**
     * 计算{@link Camera}在屏幕上的正确显示方向
     * <p>基于{@link Camera#setDisplayOrientation(int)}中的代码实现
     *
     * @param cameraInfo
     * @param rotation
     * @return
     */
    public static int calculatePreviewOrientation(Camera.CameraInfo cameraInfo, int rotation) {
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (cameraInfo.orientation - degrees + 360) % 360;
        }

        return result;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //SurfaceView创建了，告诉相机的预览位置
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            Log.d(TAG, "Camera preview started.");
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //预览改变或者旋转会调用该回调，确保停止预览后再调整大小或者重新格式化

        if(holder.getSurface() == null) {
            //预览SurfaceView不存在
            Log.d(TAG,"Preview surface does not exist");
            return;
        }
        //改变之前先停止预览
        try {
            mCamera.stopPreview();
            Log.d(TAG, "Preview stopped.");
        }catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
        int orientation = calculatePreviewOrientation(mCameraInfo,mDisplayOrientation);
        mCamera.setDisplayOrientation(orientation);
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            Log.d(TAG, "Camera preview started.");
        } catch (IOException e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 注意在Activity中释放Camera预览
    }
}
