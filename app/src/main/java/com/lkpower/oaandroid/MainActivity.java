package com.lkpower.oaandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lkpower.oaandroid.dialog.ICallBackInterface;
import com.lkpower.oaandroid.menu.SettingDialog;
import com.lkpower.oaandroid.service.UpdateCheckType;
import com.lkpower.oaandroid.task.AsyncCheckUpdateTask;
import com.lkpower.oaandroid.task.AsyncLoginParamTask;
import com.lkpower.oaandroid.task.AsyncUploadFileTask;
import com.lkpower.util.Constants;
import com.lkpower.util.SharedPreferencesUtils;
import com.lkpower.util.Util;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

import static com.lkpower.util.DialogUtil.isDialogShowing;
import static com.lkpower.util.DialogUtil.showAlertDialog;
import static com.lkpower.util.DialogUtil.showConfirmDialog;
import static com.lkpower.util.ToastUtils.toast;

@SuppressWarnings("MissingPermission")
public class MainActivity extends Activity
implements SensorEventListener /*implements TextWatcher*/

{

    private static final String tag = MainActivity.class.getSimpleName();


    //
    public static final int FILECHOOSER_RESULTCODE = 200;
    // 设置网络的返回值
    public static final int NET_WIFI_REQUEST_CODE = 300;
    // 拍照的返回值
    public static final int CAMERA_REQUEST_CODE = 400;
    // Android选择文件返回码
    public static final int UPLOAD_FILE_SELECT_CODE = 402;
    //扫描二维码的返回值
    public static final int SCAN_QRCODE_RESULT_CODE = 201;
    //扫描二维码绑定服务器地址的返回值
    public static final int SCAN_QRCODE_BIND_URL_CODE = 202;


    //	private static String updateUrl = "http://192.168.1.152:2800";
    public static String updateUrl = null;

    protected ProgressBar pb, pb2;
    private String url;
    protected static WebView web;
    private OAWebChromeClient wcc;
    private OAWebViewClient wvc;


    protected SettingDialog setDialog;
    private List<String> menuName;

    // 登录状态
    protected static boolean isLogin = false;
    // 正在运行
//	private static boolean isForeground = true;

    private static boolean isRunning = false;

    // 感应器
    private SensorManager sensorManager;
    private Vibrator vibrator;

    //===================================================
    protected String jsonStr;//上传文件,需要传给服务器的json,原封不动的传给服务器
    protected String funcName;//上传文件,需要的方法名

    //===================================================
    public static boolean isLogin() {
        return isLogin;
    }


    public static boolean isRunning() {
        return isRunning;
    }

    List<String> addItems(String[] values) {

        List<String> list = new ArrayList<String>();
        for (String var : values) {
            list.add(var);
        }

        return list;
    }

    // 定制默认地址
    void initUrl() {
        String value = SharedPreferencesUtils.getString(Constants.SERVICE_URL);
        Util.printLog("<<<<<<<<<<<<<<", value);
        try {
            // 首次安装时设置
            if (Util.checkEmptyOrNull(value)) {
                String defineUrl = getResources().getString(R.string.default_url);
                if (!Util.checkEmptyOrNull(defineUrl) && defineUrl.startsWith("http://")) {
                    SharedPreferencesUtils.saveString(Constants.SERVICE_URL, defineUrl);
                }
            }
        } catch (NotFoundException ex) {
            Util.printLog(tag, "默认地址未设置");
            ex.printStackTrace();
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            getWindow().addFlags(WindowManager.LayoutParams.class.getField("FLAG_NEEDS_MENU_KEY").getInt(null));
        } catch (Exception e) {
            Log.w("feelyou.info",
                    "Could not access FLAG_NEEDS_MENU_KEY in addLegacyOverflowButton()", e);
        }

        isRunning = true;
        String targetVersion = BuildConfig.versionTarget;
        if (targetVersion == "custom") {
            menuName = addItems(new String[]{"用户信息", "更新系统", "版本信息", "清理缓存"});//定制版
        } else {
            menuName = addItems(new String[]{"设置地址", "用户信息", "更新系统", "版本信息", "清理缓存"});//非定制版
        }
        setDialog = new SettingDialog(this, menuName, new ItemClickEvent(this));

        pb = (ProgressBar) this.findViewById(R.id.progressBar);
        pb2 = (ProgressBar) this.findViewById(R.id.progressBar2);

        wvc = new OAWebViewClient(this, pb);
        wcc = new OAWebChromeClient(this);

        web = (WebView) this.findViewById(R.id.show);
        WebSettings set = web.getSettings();
        set.setUserAgentString(set.getUserAgentString() + " mobile");
        set.setJavaScriptEnabled(true);
        web.setWebViewClient(wvc);
        web.setWebChromeClient(wcc);

        //自动化测试
//        web.getSettings().setAllowContentAccess(true);


        // 定制默认地址
        initUrl();

        String user = SharedPreferencesUtils.getString(Constants.USERNAME);
        String passwd = SharedPreferencesUtils.getString(Constants.PASSWORD);
        if (null == user || Constants.EMPTY.equals(user) ||
                null == passwd || Constants.EMPTY.equals(passwd)) {
            SharedPreferencesUtils.saveInt(Constants.CHECKED, Constants.CHECKED_NOT);
        }

        //定制 服务器地址URL
        url = SharedPreferencesUtils.getString(Constants.SERVICE_URL);
//        url = "http://fyoa.fybjy.com:8001";

        updateUrl = SharedPreferencesUtils.getString(Constants.SERVICE_URL);
        Util.printLog(tag, "url = " + url);
        Util.printLog(tag, "updateUrl = " + updateUrl);

        boolean status = Util.isNetAvailable(MainActivity.this);
        Log.d(tag, "网络状态 ==> " + status);
        if (!status) {
            showAlertDialog(this, "网络不通", "退出", "去设置", new ICallBackInterface() {
                @Override
                public void ok() {
                    finish();
                }

                @Override
                public void cancel() {
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    intent.setAction(Settings.ACTION_SETTINGS);
                    startActivityForResult(intent, NET_WIFI_REQUEST_CODE);
                }
            });
        }

        Intent intent = getIntent();
        String value = intent.getStringExtra(Constants.KEY_URL);
        Log.d(tag, "传递参数 " + value);
//		value = null;


//		web.loadUrl("http://115.28.246.218:3366/topology.html");
//		web.loadUrl("http://192.168.1.141:8087/html5/index5.html");
        if (!Util.checkEmptyOrNull(value)) {
            web.loadUrl(value);
        } else {
            if (Util.checkEmptyOrNull(url)) {
                this.setServerUrl();
                web.loadUrl("file:///android_asset/www/html/cache.html");
            } else {
                web.loadUrl(url);
            }
        }
//		web.setDownloadListener(new DownloadListener() {
//
//			@Override
//			public void onDownloadStart(String url, String userAgent,
//					String contentDisposition, String mimetype, long contentLength) {
//				Util.printLog(TAG, "下载监听 ... " + url);
//
//				Uri uri = Uri.parse(url);
//				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//				startActivity(intent);
//				String filename = contentDisposition.substring((contentDisposition.indexOf("filename") + "filename=".length()), contentDisposition.lastIndexOf(";"));
//				filename = URLDecoder.decode(filename);
//
//				AsyncDownloadDocumentTask task = new AsyncDownloadDocumentTask(MainActivity.this, url, pb);
//				task.execute();
//			}
//		});
//		web.loadUrl("file:///android_asset/www/html/404.html");
        Util.printLog(tag, "loading " + url);

        // Android API 17 later
        web.addJavascriptInterface(new JavaScriptInterface(this, web), "callnative");

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }

        // 启动服务
//		PollUtils.startPollingService(this, 30, PollService.class, PollService.ACTION);

        // 检查更新
        String date = SharedPreferencesUtils.getString(Constants.UPDATE_DATE);
        int flag = SharedPreferencesUtils.getInt(Constants.UPDATE_FLAG, Constants.UPDATE_FLAG_FALSE);
        Util.printLog(tag, "date = " + date + ", flag = " + flag);

        String today = Util.formatDate(new Date(), "yyyy-MM-dd");
        Util.printLog(tag, "today = " + today);
        boolean result = false;
        if (null == date || Constants.EMPTY.equals(date) || today.compareTo(date) > 0) {
            SharedPreferencesUtils.saveString(Constants.UPDATE_DATE, today);
            SharedPreferencesUtils.saveInt(Constants.UPDATE_FLAG, Constants.UPDATE_FLAG_FALSE);
            result = true;
        } else if (today.compareTo(date) == 0 && flag == Constants.UPDATE_FLAG_FALSE) {
            result = true;
        }

        // 获取登录所需参数
        int md5Flag = SharedPreferencesUtils.getInt(Constants.IS_MD5_LOGIN, Constants.LOGIN_PASSWD_OFF_DEFAULT);
        if (md5Flag == Constants.LOGIN_PASSWD_OFF_DEFAULT) {
            if (Util.isNetAvailable(this)) {
                new AsyncLoginParamTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        }

        if (result) {
            Timer timer = new Timer();
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (Util.isNetAvailable(MainActivity.this)) {
                                if (null != updateUrl && !Constants.EMPTY.equals(updateUrl)) {
                                    String url = updateUrl + Constants.UPDATE_REQUEST_JSON;
                                    Util.printLog(tag, "auto check update " + url);
                                    AsyncCheckUpdateTask task = new AsyncCheckUpdateTask(
                                            MainActivity.this, url, pb2, UpdateCheckType.Auto);
                                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                }
                            }
                        }
                    });
                }
            };
            timer.schedule(tt, 3000);
        }
        // 初始化极光
        //JPushInterface.setDebugMode(true);
        Util.printLog(tag, "极光 init ");
//        JPushInterface.init(this);
        String registerid = JPushInterface.getRegistrationID(getApplicationContext());
        Util.printLog(tag, "注册极光 ### registerid = " + registerid);

        registerNotifyReceiver();

//		Util.printLog(tag, "### device " + android.os.Build.VERSION.RELEASE);

        // 感应器
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (Util.checkNull(sensors)) {
            Util.printLog(tag, "" + null);
        } else {
            if (sensors.size() == 0) {
                Util.printLog(tag, "没有感应器");
            } else {
                Util.printLog(tag, "支持感应器 " + sensors.size());

                vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

                sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//		Util.printLog(tag, "onSensorChanged ");

        float[] xyz = event.values;
//		for (float f : xyz) {
//			Util.printLog(tag, "" + f);
//		}

        int value = 19;
        int sensorType = event.sensor.getType();
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            if (Math.abs(xyz[0]) > value
                    || Math.abs(xyz[1]) > value
                    || Math.abs(xyz[2]) > value) {
                vibrator.vibrate(1L);
                Util.printLog(tag, "### 显示菜单");
//				if (null != settingMenu) {
//					Util.printLog(tag, "settingMenu ==> " + settingMenu.isShowing());
//					settingMenu.showAtLocation(findViewById(R.id.container), Gravity.BOTTOM, 0,0);
//				}

                if (null != setDialog && !isDialogShowing()) {
                    Util.printLog(tag, "settingMenu ==> " + setDialog.isShowing());
                    setDialog.show();
                }
            }
        }

//		// 读取摇一摇敏感值
//		int shakeSenseValue = Integer.parseInt(getResources().getString(
//				R.string.shakeSenseValue));
//		;
//		// values[0]:X轴，values[1]：Y轴，values[2]：Z轴
//		float[] values = event.values;
//
//		if (sensorType == Sensor.TYPE_ACCELEROMETER) {
//			if ((Math.abs(values[0]) > shakeSenseValue
//					|| Math.abs(values[1]) > shakeSenseValue || Math
//					.abs(values[2]) > shakeSenseValue)) {
//				// 触发事件，执行打开应用行为
//				vibrator.vibrate(500);
//			}
//		}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Util.printLog(tag, "onAccuracyChanged");

    }


    private NotifyReceiver receiver = null;

    public class NotifyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Util.printLog(tag, "接收通知 ");
            if (Constants.NOTIFY_RECEIVED_ACTION.equals(intent.getAction())) {
                String reUrl = intent.getStringExtra(Constants.KEY_REDIRECT_URL);
                Util.printLog(tag, "转向地址 " + reUrl);
                if (null != web) {
                    web.loadUrl(reUrl);
                }
            }
        }
    }

    public void registerNotifyReceiver() {
        receiver = new NotifyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.NOTIFY_RECEIVED_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        Util.printLog(tag, "destroy");
        super.onDestroy();
        isRunning = false;
        if (null != receiver) {
            unregisterReceiver(receiver);
        }
        if (null != sensorManager) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onStop() {
        Util.printLog(tag, "stop");
        super.onStop();
        // 启动服务
//		PollUtils.startPollingService(this, TIME, PollService.class, PollService.ACTION);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Util.printLog(tag, "onNewIntent");
        String redirect = intent.getStringExtra("com.lkpower.oaandroid.url");
        Util.printLog(tag, redirect);
        if (!Util.checkEmptyOrNull(redirect) && null != web) {
            web.loadUrl(redirect);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onStart() {
        Util.printLog(tag, "start");


        super.onRestart();
        // 停止服务
//		PollUtils.stopPollingService(this, PollService.class, PollService.ACTION);
    }

    @Override
    protected void onResume() {
        Util.printLog(tag, "resume");

        isRunning = true;

        JPushInterface.onResume(this);
        if (null != sensorManager) {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        Util.printLog(tag, "pause");

        isRunning = false;

        JPushInterface.onPause(this);
        if (null != sensorManager) {
            sensorManager.unregisterListener(this);
        }
        super.onPause();
    }

//	private EditText et;

    // 设置服务器URL
    void setServerUrl() {
        final EditText et = new EditText(MainActivity.this);
        et.setHint(R.string.login_label_server_url);
        String url = SharedPreferencesUtils.getString(Constants.SERVICE_URL);
        if (null != url && url.length() > 0) {
            et.setText(url);
            et.setSelection(url.length());
        } else {
            et.setText("http://222.134.7.71:2800");
        }

        showAlertDialog(this, getString(R.string.popup_menu_server_tip), et, getString(R.string.save), "取消", "扫码绑定", new ICallBackInterface() {
            @Override
            public void ok() {
                String url = et.getText().toString();
                Util.printLog(tag, url);
                if (null == url || Constants.EMPTY.equals(url)) {
                    toast(MainActivity.this, "服务地址为空");
                    return;
                }

                if (!url.toLowerCase(Locale.getDefault()).startsWith(Constants.HTTP_PREFIX)) {
                    url = Constants.HTTP_PREFIX + url;
                }
                url = Util.formatPathSuffix(url);
                Util.printLog(tag, url);
                SharedPreferencesUtils.saveString(Constants.SERVICE_URL, url);
                web.loadUrl(url);
            }

            @Override
            public void cancel() {

            }

            @Override
            public void middle() {
                startActivityForResult(new Intent(MainActivity.this, ScanActivity.class), SCAN_QRCODE_BIND_URL_CODE);
            }
        });
    }

    protected String mUploadUrl = null;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Util.printLog(tag, "onConfigurationChanged " + newConfig.orientation);
        super.onConfigurationChanged(newConfig);
    }


    // 按两下退出（2秒之内）
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        String curUrl = wvc.getCurUrl();
        // 双击退出的判断
        if (keyCode == KeyEvent.KEYCODE_BACK && !Util.checkEmptyOrNull(curUrl)) {
            Util.printLog(tag, "curUrl = " + curUrl);
            if (curUrl.contains(Constants.EXIT_FLAG_2) || curUrl.contains(Constants.EXIT_FLAG_3)) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序",
                            Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                    System.exit(0);
                }

                return true;
            }
        }

        if (keyCode == KeyEvent.KEYCODE_BACK && web.canGoBack()) {
            Util.printLog(tag, "key == > back");
            if (null != wvc) {
                wvc.hide();
                if (null != wvc.getTimer()) {
                    wvc.getTimer().cancel();
                    wvc.getTimer().purge();
                    wvc.setTimer(null);
                }
            }
            web.goBack();  // goBack()表示返回webView的上一页面
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //	private ValueCallback<Uri> mUploadMessage;
    private Bitmap photo = null;
    protected String saveImgPath = null;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        Util.printLog(tag, "onActivityResult " + requestCode + ", " + resultCode);

//        if (requestCode == FILECHOOSER_RESULTCODE) {
//            if (null == mUploadMessage) return;
//            Uri result = intent == null || resultCode != RESULT_OK ? null
//                    : intent.getData();
//            mUploadMessage.onReceiveValue(result);
//            mUploadMessage = null;
//        }
        // Wifi
        if (requestCode == NET_WIFI_REQUEST_CODE) {

            boolean status = Util.isNetAvailable(MainActivity.this);
            Log.d(tag, "网络状态 2 ==> " + status);
            if (status) {
                web.loadUrl(url);
                // Android API 17 later
            } else {
//    			web.loadUrl("file:///android_asset/www/html/404.html");
                web.loadUrl("file:///android_asset/www/html/cache.html");
            }
            web.addJavascriptInterface(new JavaScriptInterface(this, web), "callnative");
        }
        // Take photo
        else if (requestCode == CAMERA_REQUEST_CODE) {
            if (null == intent) {
                return;
            }
            Toast.makeText(MainActivity.this, Environment.getExternalStorageDirectory() + "", Toast.LENGTH_LONG).show();
            if (null != photo && !photo.isRecycled()) {
                Util.printLog(tag, "回收图片资源");
                photo.recycle();
                photo = null;
            }

            Uri uri = intent.getData();
            if (null != uri) {
                Util.printLog(tag, uri.getPath());
                photo = BitmapFactory.decodeFile(uri.getPath());
            }
            if (null == photo) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    photo = (Bitmap) bundle.get("data");
                } else {
                    Toast.makeText(MainActivity.this, "获取图像失败", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            saveImgPath = this.saveBitmap();
            if (null == saveImgPath) {
                Util.printLog(tag, "保存失败");
                Toast.makeText(MainActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Util.printLog(tag, "已保存到手机");
                Toast.makeText(MainActivity.this, "已保存到手机", Toast.LENGTH_SHORT).show();

                web.loadUrl("javascript:nativeCallJavascriptSavePhotoPath('" + saveImgPath + "')");
            }

        } else if (requestCode == UPLOAD_FILE_SELECT_CODE) {
            Util.printLog(tag, "### UPLOAD_FILE_SELECT_CODE ");

            if (null == intent) {
                Util.printLog(tag, "intent null");
                return;
            }

            Uri uri = intent.getData();
            if (null != uri) {
                String path = uri.getPath();
                String authority = uri.getAuthority();
                String scheme = uri.getScheme();
                Util.printLog(tag, "##路径 " + path + ", " + authority + ", " + scheme);

                File file = new File(path);
                // file
                if ("file".equalsIgnoreCase(scheme)) {
                    Util.printLog(tag, "文件类型 file");

//					String url = "http://192.168.1.113:8087/webstudy/fileUpload2Svlt";
                    String url = mUploadUrl;
                    String filepath = Constants.PREFIX + File.separator + "2images/uploadtestfile.rar";
//    				filepath = Constants.PREFIX + File.separator + "2images/testfile11m.pdf";
                    filepath = uri.getPath();
                    try {
                        new AsyncUploadFileTask(this, url, filepath, web, jsonStr, funcName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (isDownloadsDocument(uri)) {
                    String filepath = getDataColumn(this, uri, null, null);
                    Util.printLog(tag, "文件类型 Download " + filepath + ", " + path);
                    if (!Util.checkEmptyOrNull(path)) {
                        String str = path.substring(path.lastIndexOf("/") + 1);

                        Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(str));

                        filepath = getDataColumn(this, contentUri, null, null);
                        Util.printLog(tag, "文件类型 Download " + filepath + ", " + str);
                        //服务端只要.jpg格式图片,其他不要
                        if (isPicture(filepath)) return;

//	        			String url = "http://192.168.1.113:8087/webstudy/fileUpload2Svlt";
                        String url = mUploadUrl;
//    				String filepath = Constants.PREFIX + File.separator + "2images/uploadtestfile.rar";
                        //    				filepath = Constants.PREFIX + File.separator + "2images/testfile11m.pdf";
                        //    				filepath = uri.getPath();
                        try {
                            new AsyncUploadFileTask(this, url, filepath, web, jsonStr, funcName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (isMediaDocument(uri)) {
                    String filepath = getDataColumn(this, uri, null, null);
                    Util.printLog(tag, "文件类型 Media " + filepath);

                    if (!Util.checkEmptyOrNull(path)) {
                        String str = path.substring(path.lastIndexOf("/") + 1);
                        String[] arr = str.split(":");
                        String type = arr[0];

                        Uri contentUri = null;
                        if ("image".equals(type)) {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(type)) {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(type)) {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        final String selection = "_id=?";
                        final String[] selectionArgs = new String[]{
                                arr[1]
                        };

                        filepath = getDataColumn(this, contentUri, selection, selectionArgs);
                        Util.printLog(tag, "文件类型 Media " + filepath + ", " + str + ", " + type);
                        //服务端只要.jpg格式图片,其他不要
                        if (isPicture(filepath)) return;
//	        			String url = "http://192.168.1.113:8087/webstudy/fileUpload2Svlt";
                        String url = mUploadUrl;
//    				String filepath = Constants.PREFIX + File.separator + "2images/uploadtestfile.rar";
                        //    				filepath = Constants.PREFIX + File.separator + "2images/testfile11m.pdf";
                        //    				filepath = uri.getPath();
                        try {
                            new AsyncUploadFileTask(this, url, filepath, web, jsonStr, funcName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // content
                else if ("content".equalsIgnoreCase(scheme)) {
                    String filepath = getDataColumn(this, uri, null, null);
                    //服务端只要.jpg格式图片,其他不要
                    if (isPicture(filepath)) return;
                    Util.printLog(tag, "文件类型 content " + filepath);
                    if (!Util.checkEmptyOrNull(filepath)) {
//        				String url = "http://192.168.1.113:8087/webstudy/fileUpload2Svlt";
                        String url = mUploadUrl;
//        				String filepath = Constants.PREFIX + File.separator + "2images/uploadtestfile.rar";
//        				filepath = Constants.PREFIX + File.separator + "2images/testfile11m.pdf";
//        				filepath = uri.getPath();
                        try {
                            new AsyncUploadFileTask(this, url, filepath, web, jsonStr, funcName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    String[] projection = {MediaStore.Images.Media.DATA};
                    CursorLoader cLoader = new CursorLoader(getApplicationContext(), uri, projection, null, null, null);
                    Cursor cursor = cLoader.loadInBackground();
                    try {
                        int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        path = cursor.getString(column_index);
                        Util.printLog(tag, "### " + path);
                        file = new File(path);
                        if (file.exists()) {
                            Util.printLog(tag, "else " + path);

//        	            	String url = "http://192.168.1.113:8087/webstudy/fileUpload2Svlt";
                            String url = mUploadUrl;
                            String filepath = Constants.PREFIX + File.separator + "2images/uploadtestfile.rar";
//        					filepath = Constants.PREFIX + File.separator + "2images/testfile11m.pdf";
                            filepath = path;
                            try {
                                new AsyncUploadFileTask(this, url, filepath, web, jsonStr, funcName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        if (null != cursor) {
                            cursor.close();
                        }
                    }
                }
            }
        } else if (requestCode == SCAN_QRCODE_RESULT_CODE) {//扫描二维码
            String result = "";
            if (intent != null) {
                result = intent.getStringExtra("result");
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            }
            Util.printLog(tag, result + "=====================1");
            web.loadUrl("javascript:" + funcName + "('" + result + "')");
        } else if (requestCode == SCAN_QRCODE_BIND_URL_CODE && resultCode == RESULT_OK) {//扫码绑定服务器地址
            String url = "";
            if (intent != null) {
                url = intent.getStringExtra("result");
                Toast.makeText(this, url, Toast.LENGTH_SHORT).show();

                if (!url.toLowerCase(Locale.getDefault()).startsWith(Constants.HTTP_PREFIX)) {
                    url = Constants.HTTP_PREFIX + url;
                }
                url = Util.formatPathSuffix(url);
                Util.printLog(tag, url);
                SharedPreferencesUtils.saveString(Constants.SERVICE_URL, url);
                web.loadUrl(url);
            } else {
                Toast.makeText(this, "扫码错误，请重新进入", Toast.LENGTH_SHORT).show();
            }
            Util.printLog(tag, url + "=================222222222222");
        }
    }

    private boolean isPicture(String filepath) {
        if (TextUtils.isEmpty(funcName)) {
            Toast.makeText(this, "方法名没有传", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(filepath)) {
            Toast.makeText(this, "图片路径不正确", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (funcName.equals("setImgSrc") && !filepath.endsWith(".jpg")) {
            showConfirmDialog(this, "上传文件格式不正确!请选择jpg格式图片");
            return true;
        }
        return false;
    }

    /**
     * 保存照片
     *
     * @return true save success, false failure
     */
    String saveBitmap() {
        if (null != photo) {
            String saveDir = Constants.TMP_PATH;
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String name = Util.getUuid() + ".jpg";
            String imgPath = saveDir + File.separator + name;
            Util.printLog(tag, imgPath);
            File file = new File(imgPath);
            if (file.exists()) {
                file.delete();
            }

            ByteArrayOutputStream baos = null;
            BufferedOutputStream bos = null;
            FileOutputStream fos = null;
            try {
                baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] byteArray = baos.toByteArray();

                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos);
                bos.write(byteArray);
                bos.flush();

                Util.printLog(tag, file.getPath());
                return file.getPath();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            } finally {
                if (null != baos) {
                    try {
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (null != bos) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (null != fos) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Util.printLog(tag, "key == > onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Util.printLog(tag, "key == > onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
//		if (null != settingMenu) {
//			Util.printLog(tag, "settingMenu ==> " + settingMenu.isShowing());
//			settingMenu.showAtLocation(findViewById(R.id.container), Gravity.BOTTOM, 0,0);
//		}
        if (null != setDialog) {
            Util.printLog(tag, "settingMenu ==> " + setDialog.isShowing());
            setDialog.show();
        }
//		hideMenuStart();

//		return super.onMenuOpened(featureId, menu);
        return false;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            return rootView;
        }
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        if (null != uri) {
            return "com.android.providers.downloads.documents".equals(uri
                    .getAuthority());
        }
        return false;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        if (null != uri) {
            return "com.android.providers.media.documents".equals(uri
                    .getAuthority());
        }
        return false;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
//        final String column = "_data";
        final String column = MediaStore.Images.ImageColumns.DATA;
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                String result = uri.getPath();
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static WebView getWeb() {
        return web;
    }

    public void setUrl(String url) {
        this.url = url;
    }



}
