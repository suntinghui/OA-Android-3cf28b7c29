package com.lkpower.oaandroid.task;

import android.content.Context;
import android.os.AsyncTask;
import android.webkit.WebView;

import com.lkpower.oaandroid.dialog.RealProgressDialog;
import com.lkpower.util.SendFileUtil;
import com.lkpower.util.Util;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 
 * @author linger
 *
 * @since 2015-9-23
 * 
 * 上传文件
 *
 */
public class AsyncUploadFileTask extends AsyncTask<String, String, String> {
	
	private static final String tag = AsyncUploadFileTask.class.getSimpleName();
	private String funcName;
	private String json;
	private WebView web;


	private Context ctx;
	private String url;
	private String filepath;
	
	private RealProgressDialog dialog = null;
	private int sum;
	
	public AsyncUploadFileTask(Context ctx, String url, String filepath, WebView web,String json,String funcName) throws FileNotFoundException {
		this.ctx = ctx;
		this.url = url;
		this.filepath = filepath;
		this.web = web;
		this.json = json;
		this.funcName = funcName;

		File file = new File(filepath);
		if (!file.exists()) {
			throw new FileNotFoundException("文件不存在");
		}
		
		long len = file.length();
		Util.printLog(tag, "文件长度 " + len);
		int iLen = (int) len;
		Util.printLog(tag, "文件长度 " + iLen);
		sum = iLen;
		dialog = new RealProgressDialog(ctx, iLen, 0);
		dialog.setTask(this);
		
		dialog.show();
	}

	@Override
	protected String doInBackground(String... params) {
		
		try {
//			SendFileUtil.send(url, filepath);
			SendFileUtil.sendForProgress(url, filepath, this,ctx);
			Util.printLog(tag, "### 上传完成");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		dialog.dismiss();
//		web.loadUrl("javascript:refresh()");
		web.loadUrl("javascript:"+funcName+"('" + json + "')");
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
//		Util.printLog(tag, "##@ " + values + ", " + sendLen + ", " + sum + ", " + (float)sendLen/sum + ", " + (float)(sendLen/sum));
		float pro = (float)sendLen/sum;
		dialog.getNumberProgress().setText(String.format("%.2f", pro * 100) + "%");
		dialog.getRealProgressBar().setProgress(sendLen);
	}

//	@Override
//	protected void onPreExecute() {
//		super.onPreExecute();
//
//
//	}

	private int sendLen;
	
	public void updateProgress(int sendLen) {
		this.sendLen = sendLen;
		
		publishProgress();
	}

}
