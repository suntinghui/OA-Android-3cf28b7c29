package com.lkpower.oaandroid.service;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.lkpower.http.Http;
import com.lkpower.util.Constants;
import com.lkpower.util.Util;

/**
 * 
 * 更新
 * 
 * @author linger
 *
 * @since 2015-8-11
 *
 */
public class Update implements Runnable {
	
	public enum Result {
		SUCCESS,
		NET_ERROR,
		FAIL
	}
	
	private static final String TAG = Update.class.getSimpleName();
	
	private AppBean app;
	
	private Context context;
	private String url;
	
	public Update(Context context, String url) {
		this.context = context;
		this.url = url;
	}
	
	/**
	 * 检查更新
	 * 
	 * @return
	 */
	public Result isUpdate() {
		// 检查网络更新
//		String url = "http://192.168.1.141:8087/webstudy/version.json";
		String value = Http.getStrContent(url);
		Util.printLog(TAG, value);
		
		// 网络问题
		if (null == value || Constants.EMPTY.equals(value)) {
			return Result.NET_ERROR;
		}
		
		Gson gson = new Gson();
		try {
			app = gson.fromJson(value, AppBean.class);
			Util.printLog(TAG, app.toString());
		}
		catch (JsonSyntaxException ex) {
			ex.printStackTrace();
			return Result.FAIL;
		}
		
		// 获取应用版本
		int verCode = getVersionCode();
		Util.printLog(TAG, "服务器版本 = " + app.getVerCode());
		
		if (app.getVerCode() > verCode) {
			return Result.SUCCESS;
		}
		
		return Result.FAIL;
	}
	
	/**
	 * 下载
	 * 
	 * @return
	 */
	public boolean download() {
		boolean result = false;
		if (null != app.getDownloadUrl() && !Constants.EMPTY.equals(app.getDownloadUrl())) {
			Util.printLog(TAG, Constants.SAVE_PATH);
//			String dir = Constants.PREFIX + File.separator + "LKOA";
			File oaDir = new File(Constants.SAVE_PATH);
			if (!oaDir.exists()) {
				oaDir.mkdir();
			}
			Util.printLog(TAG, Constants.SAVE_FILE_PATH);
//			String save = dir + File.separator + app.getApkName();
			File oaApk = new File(Constants.SAVE_FILE_PATH);
			if (oaApk.exists()) {
				oaApk.delete();
			}
			result = Http.download(app.getDownloadUrl(), Constants.SAVE_FILE_PATH);
			Util.printLog(TAG, "下载结果 " + result);
			
			Util.printLog(TAG, "apk version " + checkApkVersion(Constants.SAVE_FILE_PATH));
		}
		return result;
	}
	
	public void install() {
		Util.printLog(TAG, "安装");
		File oaApk = new File(Constants.SAVE_FILE_PATH);
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.fromFile(oaApk), "application/vnd.android.package-archive");
		context.startActivity(i);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	public void checkUpdate() {
		// 检查网络更新
		String url = "http://192.168.1.141:8087/webstudy/version.json";
		String value = Http.getStrContent(url);
		Util.printLog(TAG, value);
		
		// 检查是否有已经下载的版本
//		int apkVerCode = checkApkVersion(Constants.SAVE_FILE_PATH);
//		Util.printLog(TAG, "apkVerCode = " + apkVerCode);
		
		// 获取应用版本
		int verCode = getVersionCode();
		Util.printLog(TAG, "verCode = " + verCode);
		
		Gson gson = new Gson();
		AppBean app = gson.fromJson(value, AppBean.class);
		Util.printLog(TAG, app.toString());
		
		
		
		if (null != app.getDownloadUrl() && !Constants.EMPTY.equals(app.getDownloadUrl())) {
			Util.printLog(TAG, Constants.SAVE_PATH);
//			String dir = Constants.PREFIX + File.separator + "LKOA";
			File oaDir = new File(Constants.SAVE_PATH);
			if (!oaDir.exists()) {
				oaDir.mkdir();
			}
			Util.printLog(TAG, Constants.SAVE_FILE_PATH);
//			String save = dir + File.separator + app.getApkName();
			File oaApk = new File(Constants.SAVE_FILE_PATH);
			if (oaApk.exists()) {
				oaApk.delete();
			}
			boolean result = Http.download(app.getDownloadUrl(), Constants.SAVE_FILE_PATH);
			Util.printLog(TAG, "下载结果 " + result);
			
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setDataAndType(Uri.fromFile(oaApk), "application/vnd.android.package-archive");
			context.startActivity(i);
//			android.os.Process.killProcess(android.os.Process.myPid());
			Util.printLog(TAG, "安装");
		}
		
	}

	@Override
	public void run() {
		this.checkUpdate();
		
	}
	
	// 获得指定apk的版本
	public int checkApkVersion(String apkPath) {
		int versionCode = 0;
		PackageManager pm = context.getPackageManager();  
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);  
        if (info != null) {  
            ApplicationInfo appInfo = info.applicationInfo;  
            appInfo.sourceDir = apkPath;  
            appInfo.publicSourceDir = apkPath;  
            versionCode = info.versionCode;
            Util.printLog(TAG, apkPath + " 版本是 " + versionCode);
        }
        
        return versionCode;
	}
	
	// 获取当前应用的版本
	int getVersionCode() {
		int versionCode = 0;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
//			versionCode = context.getPackageManager().getPackageInfo(
//					"com.lkpower.oaandroid", 0).versionCode;
			versionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
			Util.printLog(TAG, "当前版本是 " + versionCode);
		}
		catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;

	}  

}

class AppBean {
	
	@SerializedName("AppName")
	private String appName;
	
	@SerializedName("ApkName")
	private String apkName;
	
	@SerializedName("VerCode")
	private int verCode;
	
	@SerializedName("VerName")
	private String VerName;
	
	@SerializedName("DownloadUrl")
	private String downloadUrl;
	
	@SerializedName("Content")
	private String content;
	
	public AppBean() {
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	public int getVerCode() {
		return verCode;
	}

	public void setVerCode(int verCode) {
		this.verCode = verCode;
	}

	public String getVerName() {
		return VerName;
	}

	public void setVerName(String verName) {
		VerName = verName;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	@Override
	public String toString() {
		return "[ " + this.apkName + ", " + this.appName + ", " 
				+ this.verCode + ", " + this.VerName + ", " + this.downloadUrl + ", "  
				+ this.content + " ]";
	}
	
}
