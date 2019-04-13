package com.lkpower.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Looper;

import com.lkpower.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 全局异常捕获处理
 * Created by wandyer on 16-10-14.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler{

    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    // CrashHandler实例
    private static CrashHandler INSTANCE;
    // 程序的Context对象
    private Context mContext;

    /****
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {

    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        if (INSTANCE == null){
            synchronized (CrashHandler.class){
                if(null == INSTANCE){
                    INSTANCE = new CrashHandler();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;

        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
        Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果自定义的没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     *            异常信息
     * @return true 如果处理了该异常信息;否则返回false.
     */
    public boolean handleException(Throwable ex) {
        if (ex == null || mContext == null)
            return false;
        final String crashReport = getCrashReport(mContext, ex);

        save2File(crashReport);
        new Thread() {
            public void run() {
                Looper.prepare();
//	                File file = save2File(crashReport);
//                sendAppCrashReport(mContext, crashReport);
//                sendCrashBrocast();
                Looper.loop();
            }

        }.start();
//	        Intent intent = new Intent(mContext,CrashActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            mContext.startActivity(intent);
        //退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        return true;
    }

    private File save2File(String crashReport) {
        String fileName = "crash-" + System.currentTimeMillis() + ".txt";
        if (FileUtil.isExternalStorageMounted()) {
            try {
                File dir = new File(FileUtil.getSDCard() + File.separator + "lkoa"+File.separator+"log");
                if (!dir.exists())
                    dir.mkdirs();
                File file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(crashReport.toString().getBytes());
                fos.close();
                return file;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取APP崩溃异常报告a
     *
     * @param ex
     * @return
     */
    private String getCrashReport(Context context, Throwable ex) {
        StringBuffer exceptionStr = new StringBuffer();


        exceptionStr.append("Exception: " + ex.getMessage() + "\n");
        StackTraceElement[] elements = ex.getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            exceptionStr.append(elements[i].toString() + "\n");
        }
        exceptionStr.append("\n");
        return exceptionStr.toString();
    }
}
