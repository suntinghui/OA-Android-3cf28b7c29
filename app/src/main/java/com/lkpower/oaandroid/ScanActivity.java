package com.lkpower.oaandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.lkpower.util.FileUtil;
import com.lkpower.util.PermissionUtil;
import com.lkpower.util.ToastUtils;
import com.lkpower.util.Util;

import java.io.IOException;

import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * @author YW
 * @date 2016/11/11 14:29
 * @company Beijing BaiCai Management Co.
 * @projectName MyApplication
 * @packageName com.lkpower.oaandroid
 */
public class ScanActivity extends Activity implements QRCodeView.Delegate, View.OnClickListener {
    /*private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1001;
    private static final int MY_PERMISSIONS_REQUEST_STORGE = 1002;*/


    private ZXingView mQRCodeView;
    private String TAG = this.getClass().getSimpleName();
    private CheckBox flashSwitch;
    private int flashIndex = 0;
    private ImageView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
        mQRCodeView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式
        mQRCodeView.setType(BarcodeType.ALL, null); // 识别所有类型的码
        mQRCodeView.startSpot();
        flashSwitch = (CheckBox) findViewById(R.id.flash_switch);
        flashSwitch.setOnClickListener(this);
        close = (ImageView) findViewById(R.id.close);
        close.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mQRCodeView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.1秒后开始识别

        PermissionUtil.requestPermission(this);
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        if(checkPermission(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }else if(checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORGE);
        }
    }*/

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Util.printLog(TAG, "result:" + result);
        mQRCodeView.stopSpot();
        Intent intent = getIntent();
        intent.putExtra("result", result);
        setResult(RESULT_OK, intent);
        finish();
//        vibrate();
//        mQRCodeView.startSpot();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        String tipText = mQRCodeView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mQRCodeView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mQRCodeView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError(Throwable th) {
        Log.e(TAG, "打开相机出错");
        ToastUtils.toast(this, "打开相机出错,请检查您是否已授予本App的相机权限");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StringBuffer exceptionStr = new StringBuffer();
                    exceptionStr.append("Exception: " + th.getMessage() + "\n");
                    StackTraceElement[] elements = th.getStackTrace();
                    for (int i = 0; i < elements.length; i++) {
                        exceptionStr.append(elements[i].toString() + "\n");
                    }
                    exceptionStr.append("\n");
                    FileUtil.writeFile("打开相机失败:"+exceptionStr.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.start_spot:
//                mQRCodeView.startSpot();
//                break;
//            case R.id.stop_spot:
//                mQRCodeView.stopSpot();
//                break;
//            case R.id.start_spot_showrect:
//                mQRCodeView.startSpotAndShowRect();
//                break;
//            case R.id.stop_spot_hiddenrect:
//                mQRCodeView.stopSpotAndHiddenRect();
//                break;
//            case R.id.show_rect:
//                mQRCodeView.showScanRect();
//                break;
//            case R.id.hidden_rect:
//                mQRCodeView.hiddenScanRect();
//                break;
//            case R.id.start_preview:
//                mQRCodeView.startCamera();
//                break;
//            case R.id.stop_preview:
//                mQRCodeView.stopCamera();
//                break;
//            case R.id.open_flashlight://开灯
//                mQRCodeView.openFlashlight();
//                Util.printLog(TAG, "开灯//////////////////");
//                break;
            case R.id.flash_switch://关灯
                if (flashIndex % 2 == 0) {
                    mQRCodeView.openFlashlight();
                } else {
                    mQRCodeView.closeFlashlight();
                }
                flashIndex++;
                break;
//            case R.id.scan_barcode:
//                mQRCodeView.changeToScanBarcodeStyle();
//                break;
//            case R.id.scan_qrcode:
//                mQRCodeView.changeToScanQRCodeStyle();
//                break;
//            case R.id.choose_qrcde_from_gallery:
//                /*
//                从相册选取二维码图片，这里为了方便演示，使用的是
//                https://github.com/bingoogolapple/BGAPhotoPicker-Android
//                这个库来从图库中选择二维码图片，这个库不是必须的，你也可以通过自己的方式从图库中选择图片
//                 */
//                startActivityForResult(BGAPhotoPickerActivity.newIntent(this, null, 1, null, false), REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
//                break;
            case R.id.close:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        mQRCodeView.showScanRect();
//        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
//            final String picturePath = BGAPhotoPickerActivity.getSelectedImages(data).get(0);
//
//            /*
//            这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
//            请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github.com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
//             */
//            new AsyncTask<Void, Void, String>() {
//                @Override
//                protected String doInBackground(Void... params) {
//                    return QRCodeDecoder.syncDecodeQRCode(picturePath);
//                }
//
//                @Override
//                protected void onPostExecute(String result) {
//                    if (TextUtils.isEmpty(result)) {
//                        Toast.makeText(ScanActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(ScanActivity.this, result, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }.execute();
//        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        *//*switch (requestCode){
            case MY_PERMISSIONS_REQUEST_CAMERA:

                break;
            case MY_PERMISSIONS_REQUEST_STORGE:

                break;
        }*//*
        if(grantResults.length>0){
            for(int result : grantResults){
                if(result == PackageManager.PERMISSION_GRANTED){
                    ToastUtils.toast(this , "您必须授予权限才能使用次");
                }
            }
        }
    }

    private  boolean checkPermission(Context context, String permission){
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
                || PermissionChecker.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }*/


}
