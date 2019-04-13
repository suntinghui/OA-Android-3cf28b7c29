package com.lkpower.oaandroid;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.lkpower.oaandroid.dialog.ICallBackInterface;
import com.lkpower.oaandroid.service.UpdateCheckType;
import com.lkpower.oaandroid.task.AsyncCheckUpdateTask;
import com.lkpower.util.Constants;
import com.lkpower.util.FileUtil;
import com.lkpower.util.SharedPreferencesUtils;
import com.lkpower.util.Util;

import java.io.File;

import static com.lkpower.oaandroid.LoginActivity.tag;
import static com.lkpower.oaandroid.MainActivity.updateUrl;
import static com.lkpower.oaandroid.MainActivity.web;
import static com.lkpower.util.AppUtil.getVersionName;
import static com.lkpower.util.DialogUtil.showAlertDialog;
import static com.lkpower.util.DialogUtil.showConfirmDialog;
import static com.lkpower.util.ToastUtils.toast;

/**
 * Created by yw on 17-10-20.
 * * 菜单选项点击事件
 */

public class ItemClickEvent implements AdapterView.OnItemClickListener {

    private final MainActivity activity;

    public ItemClickEvent(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // 显示点击的是哪个菜单哪个选项。
        String targetVersion = BuildConfig.versionTarget;
        if (targetVersion == "custom") {
            //定制版需要=============开始============================================
            switch (arg2) {
                case 0://设置用户信息
                    LayoutInflater inflater = activity.getLayoutInflater();
                    View layout = inflater.inflate(R.layout.dialog_userinfo, null);
                    final EditText etUsername = (EditText) layout.findViewById(R.id.et_username);
                    final EditText etPassword = (EditText) layout.findViewById(R.id.et_password);
                    String user = SharedPreferencesUtils.getString(Constants.USERNAME);
                    if (null != user && user.length() > 0) {
                        etUsername.setText(user);
                        etUsername.setSelection(user.length());
                    }
                    showAlertDialog(activity, activity.getString(R.string.popup_menu_userinfo_tip), layout, "保存", new ICallBackInterface() {
                        @Override
                        public void ok() {
                            String username = etUsername.getText().toString();
                            String password = etPassword.getText().toString();
                            if (TextUtils.isEmpty(username)) {
                                toast(activity, "用户名为空");
                                return;
                            }
                            if (TextUtils.isEmpty(password)) {
                                toast(activity, "密码为空");
                                return;
                            }

                            String md5Password = Util.md5(password);
                            Util.printLog(tag, password + ", " + md5Password);
                            StringBuffer sb = new StringBuffer();
                            sb.append(username).append(password);
                            String sha1 = Util.sha1(sb.toString());
                            Util.printLog(tag, sha1);
                            SharedPreferencesUtils.saveString(Constants.USERNAME, username);
                            SharedPreferencesUtils.saveString(Constants.PASSWORD, password);
                            SharedPreferencesUtils.saveString(Constants.PASSWORD_MD5, md5Password);
                            SharedPreferencesUtils.saveString(Constants.USERINFO_SHA1, sha1);
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                    break;
                case 1://检查更新
                    Util.printLog(tag, "333 检查更新");
                    if (Util.isNetAvailable(activity)) {
                        String url = updateUrl + Constants.UPDATE_REQUEST_JSON;
                        Util.printLog(tag, "manual check update " + url);
                        AsyncCheckUpdateTask task = new AsyncCheckUpdateTask(activity, url, activity.pb2, UpdateCheckType.Manual);
                        task.execute();
                    } else {
                        toast(activity, "网络不通");
                    }
                    break;
                case 2://查看新版本
                    showConfirmDialog(activity, "版本", getVersionName());
                    break;
                case 3://清理缓存
                    String cachFilePath = activity.getFilesDir().getAbsolutePath();
                    File file = new File(cachFilePath);
                    FileUtil.deleteFile(file);
                    web.clearCache(true);
                    web.clearHistory();
                    web.clearFormData();
                    activity.deleteDatabase("webview.db");
                    activity.deleteDatabase("webviewCache.db");
                    break;
            }
        }
        //定制版需要=============结束============================================
        //非定制版==============开始======================================
        else {
            switch (arg2) {
                case 0:// 设置服务地址
                    activity.setServerUrl();
                    break;
                case 1:// 设置用户信息
                    LayoutInflater inflater = activity.getLayoutInflater();
                    View layout = inflater.inflate(R.layout.dialog_userinfo, null);
                    final EditText etUsername = (EditText) layout.findViewById(R.id.et_username);
                    final EditText etPassword = (EditText) layout.findViewById(R.id.et_password);
                    String user = SharedPreferencesUtils.getString(Constants.USERNAME);
                    if (null != user && user.length() > 0) {
                        etUsername.setText(user);
                        etUsername.setSelection(user.length());
                    }
                    showAlertDialog(activity, activity.getString(R.string.popup_menu_userinfo_tip), layout, "保存", new ICallBackInterface() {
                        @Override
                        public void ok() {
                            String username = etUsername.getText().toString();
                            String password = etPassword.getText().toString();
                            if (TextUtils.isEmpty(username)) {
                                toast(activity, "用户名为空");
                                return;
                            }
                            if (TextUtils.isEmpty(password)) {
                                toast(activity, "密码为空");
                                return;
                            }

                            String md5Password = Util.md5(password);
                            Util.printLog(tag, password + ", " + md5Password);
                            StringBuffer sb = new StringBuffer();
                            sb.append(username).append(password);
                            String sha1 = Util.sha1(sb.toString());
                            Util.printLog(tag, sha1);
                            SharedPreferencesUtils.saveString(Constants.USERNAME, username);
                            SharedPreferencesUtils.saveString(Constants.PASSWORD, password);
                            SharedPreferencesUtils.saveString(Constants.PASSWORD_MD5, md5Password);
                            SharedPreferencesUtils.saveString(Constants.USERINFO_SHA1, sha1);
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                    break;
                case 2:// 检查更新
                    Util.printLog(tag, "333 检查更新");
                    if (Util.isNetAvailable(activity)) {
                        String url = updateUrl + Constants.UPDATE_REQUEST_JSON;
                        Util.printLog(tag, "manual check update " + url);
                        AsyncCheckUpdateTask task = new AsyncCheckUpdateTask(activity, url, activity.pb2, UpdateCheckType.Manual);
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        toast(activity, "网络不通");
                    }
                    break;
                case 3:// 查看版本
                    String versionName = getVersionName();
                    showConfirmDialog(activity, "版本", versionName);
                    break;
                case 4://清理缓存
                    String cachFilePath = activity.getFilesDir().getAbsolutePath();
                    File file = new File(cachFilePath);
                    FileUtil.deleteFile(file);
                    web.clearCache(true);
                    web.clearHistory();
                    web.clearFormData();
                    activity.deleteDatabase("webview.db");
                    activity.deleteDatabase("webviewCache.db");
                    break;
            }
        }
        //非定制版==============结束======================================
        activity.setDialog.dismiss();
    }
}
