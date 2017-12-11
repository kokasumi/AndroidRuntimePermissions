package com.pchlgang.android.system.runtimepermissions;

import android.content.pm.PackageManager;
import android.app.Activity;

/**
 * Created by lart-02 on 2017/12/11.
 */

public class PermissionUtil {
    /**
     * 检测请求权限结果是否全部授权
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     * @param grantResults
     * @return
     */
    public static boolean verifyPermissions(int[] grantResults) {
        if(grantResults == null || grantResults.length < 1)
            return false;
        for(int result : grantResults) {
            if(result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
