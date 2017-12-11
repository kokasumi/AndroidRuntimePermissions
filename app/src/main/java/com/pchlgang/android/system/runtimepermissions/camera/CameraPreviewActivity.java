package com.pchlgang.android.system.runtimepermissions.camera;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.pchlgang.android.system.runtimepermissions.R;

/**
 * Created by lart-02 on 2017/12/11.
 */

public class CameraPreviewActivity extends AppCompatActivity {
    private static final String TAG = "CameraPreview";
    /**
     * 访问的Camera id，0代表第一个Camera
     */
    private static final int CAMERA_ID = 0;
    private CameraPreview mCameraPreview;
    private Camera mCamera;

    /**
     * 获取Camera对象的一个安全方式
     * @param cameraId
     * @return
     */
    public static Camera getCameraInstance(int cameraId) {
        Camera camera = null;
        try {
            camera = Camera.open(cameraId);//获取Camera实例
        }catch (Exception e) {
            // Camera不可用(已经在使用中或者不存在)
            Log.d(TAG, "Camera " + cameraId + " is not available: " + e.getMessage());
        }
        return camera;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取Camera实例
        mCamera = getCameraInstance(CAMERA_ID);
        Camera.CameraInfo cameraInfo = null;
        if(mCamera != null) {
            //Camera可用时获取CameraInfo
            cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(CAMERA_ID,cameraInfo);
        }
        if(mCamera == null || cameraInfo == null) {
            //Camera不可用
            Toast.makeText(this, "Camera is not available.", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_camera_unavailable);
        }else {
            setContentView(R.layout.activity_camera_preview);
            //获取屏幕的旋转方向，正确适配预览图片
            int displayOrientation = getWindowManager().getDefaultDisplay().getRotation();
            mCameraPreview = new CameraPreview(this,mCamera,cameraInfo,displayOrientation);
            FrameLayout cameraPreviewContainer = findViewById(R.id.camera_preview);
            cameraPreviewContainer.addView(mCameraPreview);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    private void releaseCamera() {
        if(mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void onBackClick(View view) {
        finish();
    }
}
