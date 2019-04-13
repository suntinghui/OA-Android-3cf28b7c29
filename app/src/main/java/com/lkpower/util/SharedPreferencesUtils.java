package com.lkpower.util;

import android.content.SharedPreferences;

import com.lkpower.oaandroid.OaApplication;

import static android.content.Context.MODE_PRIVATE;

/**
 * SharedPreferences 操作的工具类
 */
public class SharedPreferencesUtils {

    private static SharedPreferences sp;

    public static SharedPreferences getInstance() {
        if (sp == null) {
            sp = OaApplication.getApplication().getSharedPreferences("LKOA-sp", MODE_PRIVATE);
        }
        return sp;
    }

    public static String getString(String spName) {
        return getInstance().getString(spName, Constants.EMPTY);
    }

    public static String getString(String spName,String defaultValue) {
        return getInstance().getString(spName, defaultValue);
    }

    public static void saveString(String spName, String value) {
        getInstance().edit().putString(spName, value).commit();
    }

    public static int getInt(String spName) {
        return getInstance().getInt(spName, 0);
    }

    public static int getInt(String spName, int defaultValue) {
        return getInstance().getInt(spName, defaultValue);
    }

    public static void saveInt(String spName, int value) {
        getInstance().edit().putInt(spName, value).commit();
    }

    public static boolean getBoolean(String spName) {
        return getInstance().getBoolean(spName, false);
    }

    public static void saveBoolean(String spName, boolean value) {
        getInstance().edit().putBoolean(spName, value).commit();
    }

    //旧代码需要,整合时可以参照修改为其他方法
    public static boolean getBooleanByDefault(String spName, boolean deafu) {
        return getInstance().getBoolean(spName, deafu);
    }

}
