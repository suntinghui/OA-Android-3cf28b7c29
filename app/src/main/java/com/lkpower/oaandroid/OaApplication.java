package com.lkpower.oaandroid;

import android.app.Application;

import com.lkpower.crash.CrashHandler;
import com.lkpower.util.Constants;
import com.lkpower.util.FileUtil;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by yw on 17-10-19.
 */

public class OaApplication extends Application {
    private static OaApplication mApplication = null;
    private CrashHandler crashHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        try {
//            crashHandler = CrashHandler.getInstance();
//            crashHandler.init(OaApplication.this);

            CrashReport.initCrashReport(getApplicationContext(), "348dbb1bb5", true);

            // 清除文件
            FileUtil.deleteDirectory(Constants.TMP_PATH);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static OaApplication getApplication() {
        return mApplication;
    }
}
