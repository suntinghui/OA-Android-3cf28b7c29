package com.lkpower.util;

import android.content.Context;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

public class PermissionUtil {

    public static void requestPermission(Context context) {
        AndPermission.with(context)
                .runtime()
                .permission(Permission.Group.STORAGE, Permission.Group.CAMERA)
                .onGranted(permissions -> {
                    Util.printLog("","得到用户权限");
                })
                .onDenied(permissions -> {
                    // Storage permission are not allowed.
                    Util.printLog("","未得到用户权限");
                    if (AndPermission.hasAlwaysDeniedPermission(context, permissions)) {
                        //true，弹窗再次向用户索取权限
                        Toast.makeText(context, "没有权限，请到设置中为应用添加权限", Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
    }
}
