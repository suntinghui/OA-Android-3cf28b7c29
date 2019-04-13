package com.lkpower.oaandroid.task;

import java.io.FileNotFoundException;

import android.os.AsyncTask;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.lkpower.util.SendFileUtil;
import com.lkpower.util.Util;

/**
 * 上传图片
 * 
 * @author linger
 *
 * @since 2015-7-30
 *
 */
public class AsyncUploadPhotoTask extends AsyncTask<String, String, String> {
	
	private static final String TAG = AsyncUploadPhotoTask.class.getSimpleName();
	
	private ProgressBar pb;
	private String addr, filepath;
	private WebView web;
	
	
	public AsyncUploadPhotoTask(String addr, String filepath, ProgressBar pb, WebView web) {
		this.pb = pb;
		this.web = web;
		this.addr = addr;
		this.filepath = filepath;
		
		pb.setVisibility(View.VISIBLE);
	}

	@Override
	protected String doInBackground(String... params) {
		Util.printLog(TAG, "photo doInBackground ...");
		try {
			SendFileUtil.send(addr, filepath);
			
			Thread.sleep(5000L);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Util.printLog(TAG, "photo onPostExecute ...");
		
		web.loadUrl("javascript:nativeCallJavascriptClearPath()");
		pb.setVisibility(View.GONE);
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		Util.printLog(TAG, "photo onProgressUpdate ...");
	}

}
