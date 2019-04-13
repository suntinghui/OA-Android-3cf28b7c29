package com.lkpower.util;

import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lkpower.oaandroid.R;

/**
 * 浮窗提示工具类
 */
public class ToastUtils {
    //内存泄露,静态变量始终持有context的引用导致
    private static Toast toast;

    public static class Duration {

        public static final int VERY_SHORT = (1500);
        public static final int NORMAL = (2000);
        public static final int MEDIUM = (2750);
        public static final int LONG = (3500);
        public static final int EXTRA_LONG = (4500);

    }

    /****
     * 显示普通的提示
     *
     * @param context
     * @param message
     */
    public static void toast(Context context, final String message) {
        toast(context.getApplicationContext(), message, Duration.NORMAL);
    }

    /****
     * 显示较长时间的提示
     *
     * @param context
     * @param message
     */
    public static void toastLong(Context context, final String message) {
        toast(context.getApplicationContext(), message, Duration.LONG);
    }


    private static void toast(Context context, final String message, final int duration) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            return;
        }
        View view = null;
        if (toast == null) {
            view = LayoutInflater.from(context).inflate(R.layout.toast_bg, null);
            toast = new Toast(context);
            toast.setView(view);
            // setGravity方法用于设置位置，此处为垂直居中
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        } else {
            view = toast.getView();
        }
        TextView tv = (TextView) view.findViewById(R.id.toast_tv);
        tv.setText(message);
        toast.setDuration(duration);
        toast.show();
    }

    public static void cancel() {
        if (toast != null) {
            toast.cancel();
        }
    }

}
