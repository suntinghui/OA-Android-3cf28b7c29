package com.lkpower.oaandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lkpower.oaandroid.entity.AndroidInfo;
import com.lkpower.oaandroid.task.AsyncUploadPhotoTask;
import com.lkpower.sign.SignCore;
import com.lkpower.util.Constants;
import com.lkpower.util.LoginType;
import com.lkpower.util.SharedPreferencesUtils;
import com.lkpower.util.Util;

import java.io.File;

import cn.jpush.android.api.JPushInterface;

import static com.lkpower.oaandroid.LoginActivity.tag;
import static com.lkpower.oaandroid.MainActivity.CAMERA_REQUEST_CODE;
import static com.lkpower.oaandroid.MainActivity.SCAN_QRCODE_RESULT_CODE;
import static com.lkpower.oaandroid.MainActivity.UPLOAD_FILE_SELECT_CODE;
import static com.lkpower.oaandroid.MainActivity.isLogin;

/**
 * Created by yw on 17-10-20.
 */

public class JavaScriptInterface {
    private final MainActivity activity;
    private final WebView web;

    public JavaScriptInterface(MainActivity activity, WebView webView) {
        this.activity = activity;
        this.web = webView;
    }

    // 计算SHA1摘要
    @JavascriptInterface
    public void jsCallNativeWithSHA1() {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String sha1 = SharedPreferencesUtils.getString(Constants.USERINFO_SHA1, "This is null .");
                Util.printLog(tag, "sha1 = " + sha1);
                web.loadUrl("javascript:nativeCallJavascriptSHA1('" + sha1 + "')");
            }
        });

    }

    // 签名
    @JavascriptInterface
    public void jsCallNativeWithSign(final String digest) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Util.printLog(tag, digest);
                SignCore sign = SignCore.getInstance();
                String prikey = Constants.PREFIX + "/test/client_pkcs8_nocrypt.key";
                Util.printLog(tag, prikey);
                String value = sign.sign(prikey, digest, "c222222");
                Util.printLog(tag, value);

                web.loadUrl("javascript:nativeCallJavascriptSign('" + value + "')");
            }
        });
    }

    // 验证签名
    @JavascriptInterface
    public void jsCallNativeWithVerify(final String digest, final String sign) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Util.printLog(tag, digest);
                Util.printLog(tag, sign);

                String pubkey = Constants.PREFIX + "/test/client.crt";
                SignCore verify = SignCore.getInstance();
                int value = verify.verify(pubkey, digest, sign);
                Util.printLog(tag, "verify result = " + value);

                if (1 == value) {
                    web.loadUrl("javascript:nativeCallJavascriptSign('验签成功')");
                } else {

                }
            }
        });
    }

    // Alert
    @JavascriptInterface
    public void jsCallNativeWithAlert(final String title,
                                      final String msg) {
        Util.printLog(tag, "jsCallNativeWithAlert " + title + ", "
                + msg);
        new AlertDialog.Builder(activity).setTitle(title)
                .setMessage(msg).setPositiveButton("确定", null).show();
    }

    // Confirm
    @JavascriptInterface
    public void jsCallNativeWithConfirm(final String title,
                                        final String msg, final String callback) {
        Util.printLog(tag, "jsCallNativeWithConfirm " + title + ", "
                + msg + ", " + callback);
        activity.runOnUiThread(new Runnable() {
            public void run() {
                new AlertDialog.Builder(activity)
                        .setTitle(title)
                        .setMessage(msg)
                        .setPositiveButton("确定",
                                new AlertDialog.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        Util.printLog(tag,
                                                "jsCallNativeWithConfirm 确定");
                                        web.loadUrl("javascript:"
                                                + callback);

                                    }
                                }).setNegativeButton("取消", null).show();
            }
        });
    }

    // 打开邮箱
    @JavascriptInterface
    public void jsCallNativeWithMail(final String user) {
        Util.printLog(tag, user);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + user));
        activity.startActivity(intent);
    }

    // 拨号
    @JavascriptInterface
    public void jsCallNativeWithTel(final String tel) {
        Util.printLog(tag, tel);

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
//				Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);
        activity.startActivity(intent);
    }

    // 打开网站
    @JavascriptInterface
    public void jsCallNativeWithWebsite(final String website) {
        Util.printLog(tag, website);

        String web;
        if (!website.startsWith(Constants.HTTP_PREFIX)) {
            web = Constants.HTTP_PREFIX + website;
        } else {
            web = website;
        }

        Uri uri = Uri.parse(web);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }

    // wifi
    @JavascriptInterface
    public void jsCallNativeWithWifi() {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                boolean value = Util.isWifi(activity);
                Util.printLog(tag, "wifi value " + value);
                web.loadUrl("javascript:nativeCallJavascriptWifi('" + value + "')");
            }
        });
    }

    //刷新
    @JavascriptInterface
    public void jsCallNativeWithLoadIndex() {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                boolean status = Util.isNetAvailable(activity);
                Log.d(tag, "网络状态 3 ==> " + status);
                if (status) {
                    web.loadUrl(SharedPreferencesUtils.getString(Constants.SERVICE_URL));
                } else {
//                             web.loadUrl("file:///android_asset/www/404.html");
                }
            }
        });
    }

    // 拍照
    @JavascriptInterface
    public void jsCallNativeWithTakePhoto() {
        Util.printLog(tag, "call camera ... ");

        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
//					Intent i = new Intent(Intent.ACTION_CAMERA_BUTTON, null);
            activity.startActivityForResult(i, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(activity, "SDCard不可用", Toast.LENGTH_LONG).show();
        }
    }

    // 记住用户名，密码，状态
    @JavascriptInterface
    public void jsCallNativeWithRemember(final String username, final String passwd, final int checked) {
        Util.printLog(tag, "记住用户名和密码");
        Util.printLog(tag, username + ", " + passwd + ", " + checked);
        if (checked == Constants.CHECKED_DEFAULT) {
            String md5Password = Util.md5(passwd);
            SharedPreferencesUtils.saveString(Constants.USERNAME, username);
            SharedPreferencesUtils.saveString(Constants.PASSWORD, passwd);
            SharedPreferencesUtils.saveString(Constants.PASSWORD_MD5, md5Password);
        }
        SharedPreferencesUtils.saveInt(Constants.CHECKED, checked);
    }

    // 初始化用户名，密码，记住密码
    @JavascriptInterface
    public void jsCallNativeWithInitUser() {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Util.printLog(tag, "jsCallNativeWithInitUser ... ");

                String user = SharedPreferencesUtils.getString(Constants.USERNAME);
                if (null != user && !Constants.EMPTY.equals(user)) {

                    int md5Flag = SharedPreferencesUtils.getInt(Constants.IS_MD5_LOGIN, Constants.LOGIN_PASSWD_ON_DEFAULT);
                    Util.printLog(tag, "md5Flag = " + md5Flag);
//							int loginMd5 = loginType.getValue();
                    int loginMd5 = md5Flag;
                    String passwd = "", showPasswd = "";
                    int checked = SharedPreferencesUtils.getInt(Constants.CHECKED, Constants.CHECKED_NOT);

                    if (md5Flag == LoginType.LOGIND_MD5_ON.getValue()) {
                        passwd = SharedPreferencesUtils.getString(Constants.PASSWORD_MD5, Constants.EMPTY);
                        showPasswd = passwd.substring(0, 8);
                    } else if (md5Flag == LoginType.LOGIND_PASSWD.getValue()) {
                        passwd = SharedPreferencesUtils.getString(Constants.PASSWORD, Constants.EMPTY);
                    }


                    Util.printLog(tag, user + ", " + passwd + ", " + checked + ", " + showPasswd + ", " + loginMd5);
                    web.loadUrl("javascript:nativeCallJavascriptInitUser('" + user + "', '" + passwd + "', " + checked + ", '" + showPasswd + "', " + loginMd5 + ")");
                }
            }
        });
    }

    // 设置登录状态
    // 整数　１　为登录
    @JavascriptInterface
    public void jsCallNativeWithLoginStatus(final int status) {
        Util.printLog(tag, "登录状态 " + status);
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (status == 1) {
                    isLogin = true;
                    Util.printLog(tag, "登录状态 isLogin " + isLogin);
                }
            }
        });
    }

    // 获取极光注册ID
    @JavascriptInterface
    public void jsCallNativeWithJPushId(final String str) {
        Util.printLog(tag, "get 极光注册ID " + str);
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String jPushId = JPushInterface.getRegistrationID(OaApplication.getApplication());
                Util.printLog(tag, "注册极光 ### jPushId = " + jPushId);

                web.loadUrl("javascript:" + str + "('" + jPushId + "')");
            }
        });
    }

    // 获取设备信息
    @JavascriptInterface
    public void jsCallNativeWithDeviceInfo(final String str) {
        Util.printLog(tag, "获取设备信息 " + str);
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String deviceId = Util.getDeviceId(activity);
                Util.printLog(tag, "device id " + deviceId);
                // api version 21
//						Util.printLog(TAG, "device " + android.os.Build.VERSION.SDK_INT);
                // 5.0
                Util.printLog(tag, "device " + android.os.Build.VERSION.RELEASE);

                String jPushId = JPushInterface.getRegistrationID(OaApplication.getApplication());
                Util.printLog(tag, "注册极光 ### jPushId = " + jPushId);

                AndroidInfo aInfo = new AndroidInfo();
                aInfo.setDeviceId(deviceId);
                aInfo.setVersion("Android " + android.os.Build.VERSION.RELEASE);
                aInfo.setjPushId(jPushId);
                Gson gson = new Gson();
                String gStr = gson.toJson(aInfo);
                Util.printLog(tag, "" + gStr);
                web.loadUrl("javascript:" + str + "('" + gStr + "')");
            }
        });
    }

    // 退出
    @JavascriptInterface
    public void jsCallNativeWithExitApp() {
        Util.printLog(tag, "exit app");
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                activity.finish();
            }
        });
    }

    // Touch
    @JavascriptInterface
    public void jsCallNativeWithTouch(final String move, final int x, final int y) {
        Util.printLog(tag, "touch: " + move + ", " + x + ", " + y);
    }

    // 注销
    @JavascriptInterface
    public void jsCallNativeWithLogout() {
        Util.printLog(tag, "调用注销");
        isLogin = false;
        SharedPreferencesUtils.saveString(Constants.USERNAME, Constants.EMPTY);
        SharedPreferencesUtils.saveString(Constants.PASSWORD, Constants.EMPTY);
    }

    // 上传文件
    @JavascriptInterface
    public void androidUploadFile(final String uploadUrl, String funName, String json) {
        Util.printLog(tag, "################################");
        //Toast.makeText(activity, "地址："+ uploadUrl + "方法："  + funName + "参数：" + json, Toast.LENGTH_SHORT).show();
        activity.jsonStr = json;
        activity.funcName = funName;
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Util.printLog(tag, "### androidUploadFile " + uploadUrl);

                activity.mUploadUrl = uploadUrl;
                // Android 上传分两步，先选择文件，然后才能上传文件
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
//                        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
//						activity.startActivity(Intent.createChooser(intent, "选择文件"));
                activity.startActivityForResult(Intent.createChooser(intent, "选择文件"),
                        UPLOAD_FILE_SELECT_CODE);
            }
        });
    }

    //扫描二维码条码方法
    @JavascriptInterface
    public void androidScanQrcode(String name) {
        activity.funcName = name;
        Util.printLog(tag, "#######扫描二维码#########");
        activity.startActivityForResult(new Intent(activity, ScanActivity.class), SCAN_QRCODE_RESULT_CODE);
    }

    // 上传
    @JavascriptInterface
    public void jsCallNativeWithUploadPhoto(final String url, final String path) {
        Util.printLog(tag, "upload photo ... " + url);
        Util.printLog(tag, "upload photo ... " + path);

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (null == activity.saveImgPath) {
                    Util.printLog(tag, "路径为空");
                    Toast.makeText(activity, "路径为空", Toast.LENGTH_SHORT).show();
//							throw new NullPointerException();

                    return;
                }

                File file = new File(activity.saveImgPath);
                if (!file.exists()) {
                    Util.printLog(tag, "该图片不存在");
                    Toast.makeText(activity, "该图片不存在", Toast.LENGTH_LONG).show();
                    return;
                }
                AsyncUploadPhotoTask task = new AsyncUploadPhotoTask(url, path, activity.pb, web);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    @JavascriptInterface
    public void jsCallNative() {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.d(tag, "jsCallNative");
            }
        });
    }

    @JavascriptInterface
    public void jsCallNativeWithParam(final int num, final boolean flag, final String str) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.d(tag, "jsCallNativeWithParam => " + num + ", " + flag + ", " + str);
            }
        });
    }

    @JavascriptInterface
    public String jsCallNativeWithReturnValue(final int num, final boolean flag, final String str) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.d(tag, "jsCallNativeWithReturnValue => " + num + ", " + flag + ", " + str);
            }
        });

        return "Value Returned From Java";
    }
}
