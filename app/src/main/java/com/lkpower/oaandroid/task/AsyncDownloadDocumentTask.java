package com.lkpower.oaandroid.task;

import java.io.File;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lkpower.http.Http;
import com.lkpower.oaandroid.MainActivity;
import com.lkpower.oaandroid.R;
import com.lkpower.util.FileUtil;
import com.lkpower.util.Util;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * 下载附件
 * 
 * @author linger
 *
 * @since 2015-8-27
 *
 */
public class AsyncDownloadDocumentTask extends AsyncTask<String, String, String> {
	
	private static final String TAG = AsyncDownloadDocumentTask.class.getSimpleName();
	
	private ProgressBar pb;
	private MainActivity context;
	private String url;
	private String downloadFlag = null;
	
	public AsyncDownloadDocumentTask(MainActivity context, String url, ProgressBar pb) {
		this.pb = pb;
		this.url = url;
		this.context = context;
		
		pb.setVisibility(View.VISIBLE);
	}

	@Override
	protected String doInBackground(String... params) {
		Util.printLog(TAG, "下载附件 doInBackground ..." + url);
		downloadFlag = Http.download(url);
		openFile(this.context, downloadFlag);
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Util.printLog(TAG, "下载附件 onPostExecute ...");
		
		pb.setVisibility(View.GONE);
		
		// 下载附件成功
		if (null != downloadFlag) {
			Util.printLog(TAG, "下载成功");

		}
		else {
			Toast.makeText(context, "下载失败请重试", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		Util.printLog(TAG, "下载附件 onProgressUpdate ...");
	}

	public static void openFile(Context context, String fileName) {

		try {
			File file = new File(fileName);

			if (null != file && file.isFile()) {
				Intent intent = null;
				Resources res = context.getResources();

				if (checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingText))) {
					intent = getTextFileIntent(context, file);
					context.startActivity(intent);

				} else if (checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingPdf))) {
					intent = getPdfFileIntent(context, file);
					context.startActivity(intent);

				} else if (checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingWord))) {
					intent = getWordFileIntent(context, file);
					context.startActivity(intent);

				} else if (checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingExcel))) {
					intent = getExcelFileIntent(context, file);
					context.startActivity(intent);

				} else if (checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingPPT))) {
					intent = getPPTFileIntent(context, file);
					context.startActivity(intent);

				} else if (checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingImage))) {
					intent = getImageFileIntent(context, file);
					context.startActivity(intent);

				} else if (checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingWebText))) {
					intent = getHtmlFileIntent(context, file);
					context.startActivity(intent);

				} else if (checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingPackage))) {
					intent = getApkFileIntent(context, file);
					context.startActivity(intent);

				} else if (checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingAudio))) {
					intent = getAudioFileIntent(context, file);
					context.startActivity(intent);

				} else if (checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingVideo))) {
					intent = getVideoFileIntent(context, file);
					context.startActivity(intent);

				} else if (checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingChm))) {
					intent = getChmFileIntent(context, file);
					context.startActivity(intent);

				} else {
					Toast.makeText(context, "不支持此格式的文件！["+ getFileExtension(fileName)+"]", Toast.LENGTH_LONG).show();
				}

			} else {
				Toast.makeText(context, "文件下载出错，请重新下载!", Toast.LENGTH_LONG).show();
			}
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(context, "您需要先在手机中安装打开[ " + getFileExtension(fileName) + " ]文件的软件。", Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "未知异常，请联系管理员解决。", Toast.LENGTH_LONG).show();
			CrashReport.postCatchedException(e);
		}
	}

	// 定义用于检查要打开的文件的后缀是否在遍历后缀数组中
	private static boolean checkEndsWithInStringArray(String checkItsEnd, String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.toUpperCase().endsWith(aEnd.toUpperCase()))
				return true;
		}
		return false;
	}

	private static String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	//android获取一个用于打开HTML文件的intent
	private static Intent getHtmlFileIntent(Context context, File file) {
		/**
		 Intent intent = new Intent(Intent.ACTION_VIEW);
		 Uri uri = Uri.parse(file.toString()).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(file.toString()).build();
		 intent.setDataAndType(uri, "text/html");
		 return intent;
		 **/
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri uri = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION );
			uri = FileProvider.getUriForFile(context, "com.lkpower.oaandroid.fileprovider", file);
		} else {
			uri = uri = Uri.parse(file.toString()).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(file.toString()).build();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}

		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(uri, "text/html");
		return intent;
	}

	//android获取一个用于打开图片文件的intent
	private static Intent getImageFileIntent(Context context, File file) {
		return getBaseIntent(context, "image/*", file);
		/**
		 Intent intent = new Intent("android.intent.action.VIEW");
		 intent.addCategory("android.intent.category.DEFAULT");
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 Uri uri = Uri.fromFile(file);
		 intent.setDataAndType(uri, "image/*");
		 return intent;
		 **/
	}

	//android获取一个用于打开PDF文件的intent
	private static Intent getPdfFileIntent(Context context, File file) {
		return getBaseIntent(context, "application/pdf", file);
		/**
		 Intent intent = new Intent("android.intent.action.VIEW");
		 intent.addCategory("android.intent.category.DEFAULT");
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 Uri uri = Uri.fromFile(file);
		 intent.setDataAndType(uri, "application/pdf");
		 return intent;
		 **/
	}

	//android获取一个用于打开文本文件的intent
	private static Intent getTextFileIntent(Context context, File file) {
		return getBaseIntent(context, "text/plain", file);
		/**
		 Intent intent = new Intent("android.intent.action.VIEW");
		 intent.addCategory("android.intent.category.DEFAULT");
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 Uri uri = Uri.fromFile(file);
		 intent.setDataAndType(uri, "text/plain");
		 return intent;
		 **/
	}

	//android获取一个用于打开音频文件的intent
	private static Intent getAudioFileIntent(Context context, File file) {
		/**
		 Intent intent = new Intent(Intent.ACTION_VIEW);
		 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 intent.putExtra("oneshot", 0);
		 intent.putExtra("configchange", 0);
		 Uri uri = Uri.fromFile(file);
		 intent.setDataAndType(uri, "audio/*");
		 return intent;
		 **/
		Intent intent = getBaseIntent(context, "audio/*", file);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		return intent;
	}

	//android获取一个用于打开视频文件的intent
	private static Intent getVideoFileIntent(Context context,File file) {
		/**
		 Intent intent = new Intent(Intent.ACTION_VIEW);
		 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 intent.putExtra("oneshot", 0);
		 intent.putExtra("configchange", 0);
		 Uri uri = Uri.fromFile(file);
		 intent.setDataAndType(uri, "video/*");
		 return intent;
		 **/
		Intent intent = getBaseIntent(context, "video/*", file);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		return intent;
	}


	//android获取一个用于打开CHM文件的intent
	private static Intent getChmFileIntent(Context context, File file) {
		return getBaseIntent(context, "application/x-chm", file);
		/**
		 Intent intent = new Intent("android.intent.action.VIEW");
		 intent.addCategory("android.intent.category.DEFAULT");
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 Uri uri = Uri.fromFile(file);
		 intent.setDataAndType(uri, "application/x-chm");
		 return intent;
		 **/
	}

	//android获取一个用于打开Word文件的intent
	private static Intent getWordFileIntent(Context context, File file) {
		return getBaseIntent(context, "application/msword", file);
		/**
		 Intent intent = new Intent("android.intent.action.VIEW");
		 Uri uri = null;
		 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
		 intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
		 uri = FileProvider.getUriForFile(context, "com.lkpower.oaandroid.fileprovider", file);
		 } else {
		 uri = Uri.fromFile(file);
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 }

		 intent.addCategory("android.intent.category.DEFAULT");
		 intent.setDataAndType(uri, "application/msword");
		 return intent;
		 **/
	}

	//android获取一个用于打开Excel文件的intent
	private static Intent getExcelFileIntent(Context context, File file) {
		return getBaseIntent(context, "application/vnd.ms-excel", file);
		/**
		 Intent intent = new Intent("android.intent.action.VIEW");
		 intent.addCategory("android.intent.category.DEFAULT");
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 Uri uri = Uri.fromFile(file);
		 intent.setDataAndType(uri, "application/vnd.ms-excel");
		 return intent;
		 **/
	}

	//android获取一个用于打开PPT文件的intent
	private static Intent getPPTFileIntent(Context context, File file) {
		return getBaseIntent(context, "application/vnd.ms-powerpoint", file);
		/**
		 Intent intent = new Intent("android.intent.action.VIEW");
		 intent.addCategory("android.intent.category.DEFAULT");
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 Uri uri = Uri.fromFile(file);
		 intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		 return intent;
		 **/
	}

	//android获取一个用于打开apk文件的intent
	private static Intent getApkFileIntent(Context context, File file) {
		return getBaseIntent(context, "application/vnd.android.package-archive", file);
		/**
		 Intent intent = new Intent(Intent.ACTION_VIEW);
		 Uri uri = null;
		 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
		 intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
		 uri = FileProvider.getUriForFile(context, "com.lkpower.oaandroid.fileprovider", file);
		 } else {
		 uri = Uri.fromFile(file);
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 }
		 intent.setDataAndType(uri, "application/vnd.android.package-archive");
		 return intent;
		 **/
	}

	private static Intent getBaseIntent(Context context, String type, File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri uri = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
			uri = FileProvider.getUriForFile(context, "com.lkpower.oaandroid.fileprovider", file);
		} else {
			uri = Uri.fromFile(file);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}

		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(uri, type);
		return intent;
	}

}
