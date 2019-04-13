package com.lkpower.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;

import com.lkpower.oaandroid.dialog.ICallBackInterface;

import static com.lkpower.util.ToastUtils.toast;

/**
 * Created by yw on 17-10-20.
 */

public class DialogUtil {
    private DialogUtil() {
    }

    private static boolean isShowing = false;

    //确认提示匡

    public static void showConfirmDialog(Context context, String title, String message
            , String positiveText, ICallBackInterface callBack) {
        showDialog(context, -1, title, message, null, positiveText, null, null, false, callBack);
    }

    public static void showConfirmDialog(Context context, String message, String positiveText, ICallBackInterface callBack) {
        showDialog(context, -1, null, message, null, positiveText, null, null, false, callBack);
    }

    public static void showConfirmDialog(Context context, String message, ICallBackInterface callBack) {
        showDialog(context, -1, null, message, null, "确定", null, null, false, callBack);
    }

    public static void showConfirmDialog(Context context, String message) {
        showDialog(context, -1, null, message, null, "确定", null, null, false, null);
    }

    public static void showConfirmDialog(Context context, String title, String message) {
        showDialog(context, -1, title, message, null, "确定", null, null, false, null);
    }


    //alert
    public static void showAlertDialog(Context context, String title, View view, String positiveText, ICallBackInterface callBackInterface) {
        showDialog(context, -1, title, null, view, TextUtils.isEmpty(positiveText) ? "确定" : positiveText, "取消", null, false, callBackInterface);
    }

    public static void showAlertDialog(Context context, String message, String positiveText, String negativeText, ICallBackInterface callBackInterface) {
        showDialog(context, -1, null, message, null, TextUtils.isEmpty(positiveText) ? "确定" : positiveText, null,
                TextUtils.isEmpty(positiveText) ? "取消" : negativeText, false, callBackInterface);
    }

    public static void showAlertDialog(Context context, String title, View view, String positiveText, String negativeText, String neutralText, ICallBackInterface callBackInterface) {
        showDialog(context, -1, title, null, view,
                TextUtils.isEmpty(positiveText) ? "确定" : positiveText,
                TextUtils.isEmpty(negativeText) ? "取消" : negativeText,
                neutralText, false, callBackInterface);
    }


    private static void showDialog(final Context context, int iconId, String title, String message, View view
            , String positiveText, String negativeText, String neutralText, boolean isTouchCancel, final ICallBackInterface callBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (!isShowing) {
            isShowing = true;
            if (iconId != -1) {
                builder.setIcon(iconId);
            }
            builder.setTitle(TextUtils.isEmpty(title) ? "提示" : title);
            if (view != null) {
                builder.setView(view);
            }
            if (!TextUtils.isEmpty(message)) {
                builder.setMessage(message);
            }
            if (!TextUtils.isEmpty(positiveText)) {
                builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (callBack != null) {
                            callBack.ok();
                        }
                        isShowing = false;
                    }
                });
            }
            if (!TextUtils.isEmpty(negativeText)) {
                builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (callBack != null) {
                            callBack.cancel();
                        }
                        isShowing = false;
                    }
                });
            }
            builder.setCancelable(isTouchCancel);
            if (!TextUtils.isEmpty(neutralText)) {
                builder.setNeutralButton(neutralText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (callBack != null) {
                            callBack.middle();
                        }
                        isShowing = false;
                    }
                });
            }
            builder.create().show();
        } else {
            toast(context, "清先取消之前对话框");
        }
    }

    public static boolean isDialogShowing() {
        return isShowing;
    }
}
