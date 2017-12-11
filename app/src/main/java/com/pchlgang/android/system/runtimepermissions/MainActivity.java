package com.pchlgang.android.system.runtimepermissions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.pchlgang.android.system.runtimepermissions.camera.CameraPreviewActivity;
import com.pchlgang.android.system.runtimepermissions.contact.ContactsActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    /**
     * 请求相机权限的id
     */
    private static final int REQUEST_CAMERA = 0;
    /**
     * 该Activity的根View
     */
    private View rootView;
    /**
     * {@link ContactsActivity}中需要的权限
     */
    private static final String[] PERMISSIONS_CONTACTS = {Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS};
    /**
     * 请求联系人权限的id
     */
    private static final int REQUEST_CONTACTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = findViewById(R.id.main_layout);
    }

    public void openCameraPreview(View view) {
        Log.i(TAG, "Show camera preview button pressed. Checking permission.");
        //检测Camera权限是否赋予
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //Camera权限未授予
            requestCameraPermission();
        } else {
            showCameraPreview();
        }
    }

    /**
     * 请求Camera权限
     * 如果之前用户已经拒绝过改权限，使用SnackBar提醒用户授予权限，否则直接请求授予权限
     */
    private void requestCameraPermission() {
        Log.i(TAG, "CAMERA permission has NOT been granted. Requesting permission.");
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //需要为用户提供解释说明，为什么需要Camera权限
            Log.i(TAG, "Displaying camera permission rationale to provide additional context.");
            Snackbar.make(rootView, R.string.permission_camera_rationale, Snackbar.LENGTH_LONG).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                }
            }).show();
        } else {
            //没有请求过Camera权限，直接请求
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
    }

    /**
     * 跳转到{@link CameraPreviewActivity}页面
     */
    private void showCameraPreview() {
        startActivity(new Intent(this, CameraPreviewActivity.class));
    }

    public void openContact(View view) {
        Log.i(TAG, "Show contacts button pressed. Checking permissions.");
        //验证联系人页面需要的所有权限是否被授予
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Contact permissions has NOT been granted. Requesting permissions.");
            requestContactsPermissions();
        }else {
            Log.i(TAG, "Contact permissions have already been granted. Displaying contact details.");
            showContactDetails();
        }
    }

    private void requestContactsPermissions() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_CONTACTS)) {
            Log.i(TAG, "Displaying contacts permission rationale to provide additional context.");
            Snackbar.make(rootView,R.string.permission_contacts_rationale,Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(MainActivity.this,PERMISSIONS_CONTACTS,REQUEST_CONTACTS);
                        }
                    }).show();
        }else {
            ActivityCompat.requestPermissions(MainActivity.this,PERMISSIONS_CONTACTS,REQUEST_CONTACTS);
        }
    }

    private void showContactDetails() {
        startActivity(new Intent(this,ContactsActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                onRequestCameraPermissionsResult(permissions, grantResults);
                break;
            case REQUEST_CONTACTS:
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void onRequestCameraPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "Received response for Camera permission request.");
        //检测权限是否授予
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Camera权限授予成功，可以查看Camera预览页面
            Log.i(TAG, "CAMERA permission has now been granted. Showing preview.");
            Snackbar.make(rootView, R.string.permission_available_camera, Snackbar.LENGTH_LONG).show();
        } else {
            Log.i(TAG, "CAMERA permission was NOT granted.");
            Snackbar.make(rootView, R.string.permissions_not_granted, Snackbar.LENGTH_LONG).show();
        }
    }

    private void onRequestContactsPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "Received response for contact permissions request.");
        if(PermissionUtil.verifyPermissions(grantResults)) {
            Snackbar.make(rootView, R.string.permission_available_contacts,
                    Snackbar.LENGTH_SHORT)
                    .show();
        }else {
            Log.i(TAG, "Contacts permissions were NOT granted.");
            Snackbar.make(rootView, R.string.permissions_not_granted,
                    Snackbar.LENGTH_SHORT)
                    .show();
        }
    }
}
