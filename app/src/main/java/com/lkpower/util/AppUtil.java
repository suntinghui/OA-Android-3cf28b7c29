package com.lkpower.util;

import android.content.pm.PackageManager;

import com.lkpower.oaandroid.OaApplication;

/**
 * Created by yw on 17-10-20.
 */

public class AppUtil {
    public static String getVersionName() {
        try {
            return OaApplication.getApplication().getPackageManager().getPackageInfo(
                    OaApplication.getApplication().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Constants.EMPTY;
    }
}
