package com.lkpower.oaandroid;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.lkpower.oaandroid.task.AsyncDownloadDocumentTask;
import com.lkpower.oaandroid.view.WheelProgressDialog;
import com.lkpower.util.Constants;
import com.lkpower.util.PermissionUtil;
import com.lkpower.util.SharedPreferencesUtils;
import com.lkpower.util.Util;
import com.tencent.bugly.crashreport.CrashReport;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * 
 * @author linger
 *
 */
public class OAWebViewClient extends WebViewClient {
	
	private static final String tag = OAWebViewClient.class.getSimpleName();
	
	private MainActivity activity;
	private ProgressBar pb;
	// 超时时间 7 秒 12 秒 60s
	private long timeout = 30 * 1000L;
	private Timer timer;

	private WheelProgressDialog wheelProgressDialog = null;
	
	public static String sessionId = null;
	
	private String curUrl = null;
//	private String prefixUrl = null;
	
	// 测试任务
	private static int count = 0;
	
	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	public OAWebViewClient() {
	}
	
	public String getCurUrl() {
		return curUrl;
	}

//	public String getPrefixUrl() {
//		return prefixUrl;
//	}

	public OAWebViewClient(MainActivity activity, ProgressBar pb) {
		this.activity = activity;
		this.pb = pb;
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		Util.printLog(tag, "shouldOverrideUrlLoading Loading Url ... " + url);
		
		if(url.contains("/attch/detail?id=") || url.contains("/work/wdoc?workId=")
				|| url.contains("/xxzx/DocFiles/") || url.contains("/PduOA5_NotifyMaager/DocFiles/")
				|| url.contains("/Editor/uploadfile/")) {

			Util.printLog(tag, "### 打开附件 ... ");

//			AsyncDownloadDocumentTask task = new AsyncDownloadDocumentTask(activity, url, pb);
//			task.execute();

			try{
				Util.printLog(tag, url);

				FileDownloader.setup(this.activity);
				FileDownloader.getImpl().create(url)
						.setPath(Constants.TMP_PATH, true)
						.setAutoRetryTimes(3)
						.addHeader("Cookie", sessionId)
						.setListener(new FileDownloadListener() {
							@Override
							protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
								super.connected(task, etag, isContinue, soFarBytes, totalBytes);
							}

							@Override
							protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
								wheelProgressDialog=new WheelProgressDialog(activity);
								wheelProgressDialog.message("开始下载").show();
							}

							@Override
							protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
								if (null != wheelProgressDialog) {
									wheelProgressDialog.progress(100*soFarBytes/totalBytes).message("正在下载，请稍候...");
								}
							}

							@Override
							protected void completed(BaseDownloadTask task) {
								Util.printLog(tag, "文件下载完成");


								if (null != wheelProgressDialog) {
									wheelProgressDialog.progress(100).message("下载完成");
									wheelProgressDialog.dismiss();
								}

								// filedownloader取的filename需要进行转码才能显示成中文
								// 在未转码时可能会造成文件名过长的异常发生，所以只能一开始就将文件用中文命名
								// 所以只能将filedownloader由gradle引用的方式改为直接引用源码
								// 并在com.liulishuo.filedownloader.util.FileDownloadUtils类的findFilename()方法中加入如下代码：
								// URLDecoder.decode(task.getFilename().replace(";filename*=",""));

								// 进行文件重命名
								String filepath = Constants.TMP_PATH + File.separator + task.getFilename();
								AsyncDownloadDocumentTask.openFile(activity, filepath);
							}

							@Override
							protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
							}

							@Override
							protected void error(BaseDownloadTask task, Throwable e) {
								Util.printLog(tag, "文件下载失败");

								if (null != wheelProgressDialog) {
									wheelProgressDialog.dismiss();
								}

								if (e instanceof SocketException) {
									Toast.makeText(activity, "文件因网络异常下载失败，请稍候重试", Toast.LENGTH_LONG).show();
								} else {
									Toast.makeText(activity, "文件下载失败["+e.getMessage()+"]", Toast.LENGTH_LONG).show();
								}

								CrashReport.postCatchedException(e);
							}

							@Override
							protected void warn(BaseDownloadTask task) {

							}
						}).start();

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return true;
		}
		
		return super.shouldOverrideUrlLoading(view, url);
	}
	
	@Override
	public void onPageStarted(WebView view, final String url, Bitmap favicon) {
		Util.printLog(tag, "pagestarted " + url);
//		activity.getMenuStart().setVisibility(View.GONE);
		
		
//		pb.setVisibility(View.VISIBLE);
//		if (!"file:///android_asset/www/404.html".equals(url)) {
//			show();
//		}
		
		if (null != timer) {
			Util.printLog(tag, "### 取消任务 onPageStarted " + count);
			timer.cancel();
	        timer.purge();
	        timer = null;
		}
		
		count++;
		
		show();
		timer = new Timer();  
        TimerTask tt = new TimerTask() {
            @Override  
            public void run() {
            	Util.printLog(tag, "### 执行任务 onPageStarted " + count);

//            	 if (null != timer) {
//            		 Util.printLog(TAG, "取消任务 onPageStarted");
//	            	 timer.cancel();
//	                 timer.purge();
//            	 }
//                 hide();
            	 activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						activity.setUrl(url);
//						activity.getWeb().loadUrl("file:///android_asset/www/html/404.html");
						activity.getWeb().loadUrl("file:///android_asset/www/html/cache.html");
//						hide();
					}
				});
            }  
        };  
        timer.schedule(tt, timeout);
        Util.printLog(tag, "### 创建任务 " + count);
//        timer.schedule(tt, timeout, 1); 
	}
	
	public void show() {
//		Util.printLog(TAG, "显示进度条");
		pb.setVisibility(View.VISIBLE);
	}
	
	public void hide() {
//		Util.printLog(TAG, "隐藏进度条");
		pb.setVisibility(View.GONE);
	}
	
	@Override
	public void onPageFinished(WebView view, String url) {
		Util.printLog(tag, "pagefinished " + url);
		
		CookieManager cm = CookieManager.getInstance();
		String cookie = cm.getCookie(url);
		Util.printLog(tag, cookie);
		
		// LkMobileSrv
		if (null != cookie && cookie.contains("LkMobileSrv=")) {
			sessionId = cookie.substring(cookie.indexOf("LkMobileSrv="));
			
			saveSession();
		}
		
		// connect.sid
		if (null != cookie && cookie.contains("connect.sid=")) {
			sessionId = cookie.substring(cookie.indexOf("connect.sid="));
			
//			saveSession();
		}
		
//		pb.setVisibility(View.GONE);
//		if (!"file:///android_asset/www/404.html".equals(url)) {
//			hide();
//		}
		
		hide();
		if (null != timer) {
			Util.printLog(tag, "### 取消任务 onPageFinished " + count);
			timer.cancel();
	        timer.purge();
	        timer = null;
		}
		
		// 记录当前 url
//		this.prefixUrl = curUrl;
		this.curUrl = url;
//		Util.printLog(tag, "### 当前URL " + curUrl + ", \n前一个URL " + prefixUrl);
		Util.printLog(tag, "### 当前URL " + curUrl);;
		
//		activity.getMenuStart().setVisibility(View.VISIBLE);


		PermissionUtil.requestPermission(activity);
	}


	
	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		Util.printLog(tag, "receivederror " + failingUrl);
//		pb.setVisibility(View.GONE);

		if (null != timer) {
			Util.printLog(tag, "### 取消任务 onReceivedError " + count);
			timer.cancel();
	        timer.purge();
	        timer = null;
		}
		
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				hide();
//				activity.getWeb().loadUrl("file:///android_asset/www/html/404.html");
				activity.getWeb().loadUrl("file:///android_asset/www/html/cache.html");
			}
		});
	}
	
	void saveSession() {
		SharedPreferencesUtils.saveString(Constants.SESSIONID, sessionId);
	}

}
