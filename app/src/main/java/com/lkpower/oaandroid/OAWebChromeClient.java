package com.lkpower.oaandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;

import com.lkpower.util.Util;

/**
 * 
 * @author linger
 * 
 * @since 2015-2-5
 * 
 */
public class OAWebChromeClient extends WebChromeClient {
	
	private static final String TAG = OAWebChromeClient.class.getSimpleName();
	
	private Context context;
	
	private ValueCallback<Uri> mUploadMessage;  

	public OAWebChromeClient(Context context) {
		this.context = context;
	}	
	
	public ValueCallback<Uri> getmUploadMessage() {
		return mUploadMessage;
	}

	public void setmUploadMessage(ValueCallback<Uri> mUploadMessage) {
		this.mUploadMessage = mUploadMessage;
	}

	// 自动扩充缓存容量
	@Override
	public void onReachedMaxAppCacheSize(long requiredStorage, long quota,
			QuotaUpdater quotaUpdater) {
		quotaUpdater.updateQuota(requiredStorage * 2);
	}

	@Override
	public boolean onJsAlert(WebView view, String url, String message,
			final JsResult result) {
		Util.printLog(TAG, message + ", " + url);
		
		AlertDialog.Builder b2 = new AlertDialog.Builder(context)
				.setTitle("Alert").setMessage(message)
				.setPositiveButton("ok", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
						// MyWebView.this.finish();
					}
				});

		b2.setCancelable(false);
		b2.create();
		b2.show();
		return true;
	}
	
	@Override
	public boolean onJsPrompt(WebView view, String url, String message,
			String defaultValue, JsPromptResult result) {
		Util.printLog(TAG, message + ", " + defaultValue + ", " + url);
		
		return super.onJsPrompt(view, url, message, defaultValue, result);
	}
	
	/*
	public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
		Util.printLog(TAG, "openFileChooser, " + acceptType + ", " + capture);
		mUploadMessage = uploadMsg;  
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
        i.addCategory(Intent.CATEGORY_OPENABLE);  
        i.setType("image/*");
//        i.setType("file/**");
        
        MainActivity activity = (MainActivity) context;
        activity.startActivityForResult(Intent.createChooser( i, "File Chooser" ), 
        		MainActivity.FILECHOOSER_RESULTCODE);

    }
    */

}
