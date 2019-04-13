package com.lkpower.oaandroid.service;

import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lkpower.http.Http;
import com.lkpower.oaandroid.MainActivity;
import com.lkpower.oaandroid.R;
import com.lkpower.util.Constants;
import com.lkpower.util.Util;

public class PollService extends Service {
	
	private static final String TAG = PollService.class.getSimpleName();

	public static final String ACTION = "com.lkpower.oaandroid.service.PollService";
	
	public static final int NOTIFICATION_ID_NOTICE = 2002;
	
	public static final int NOTIFICATION_ID_URGENT = 2003;
	
	public static final int NOTIFICATION_ID_WORK = 2004;
	
	public static final int NOTIFICATION_ID_MAIL = 2005;
	
	private Notification.Builder builder;
	private NotificationManager mManager;
	
	private WakeLock wakeLock = null;
	
	

	public void acquireWakeLock() {
		if (null == wakeLock) {
			PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
			if (null != wakeLock) {
				wakeLock.acquire();
			}
		}
	}
	
	public void releaseWakeLock() {
		if (null != wakeLock) {
			wakeLock.release();
			wakeLock = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
//		initNotifiManager();
//		acquireWakeLock();
		
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		new PollThread().start();
	}

	//初始化通知栏配置
	private void initNotifiManager() {
		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		builder = new Notification.Builder(this.getBaseContext());
		builder.setSmallIcon(R.drawable.icon);
		builder.setTicker("移动办公");
	}
	
	//弹出Notification
	private void showNotification(String title, String msg, int nId, String url) {
		initNotifiManager();

		builder.setWhen(System.currentTimeMillis());
		Intent intent = new Intent(this, MainActivity.class);
		intent.setAction("" + nId);
		intent.putExtra("com.lkpower.oaandroid.url", url);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, nId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pendingIntent);
		Notification notification = builder.build();
		mManager.notify(nId, notification);
	}

	/**
	 * Polling thread
	 * 模拟向Server轮询的异步线程
	 */
	int count = 0;
	
	private String url = null; 
	private String sessionId = null; 
	
	class PollThread extends Thread {
		
		@Override
		public void run() {
			SharedPreferences sp = getSharedPreferences("LKOA-sp", MODE_PRIVATE);
			url = sp.getString(Constants.SERVICE_URL, Constants.EMPTY);
			sessionId = sp.getString(Constants.SESSIONID, Constants.EMPTY);
			printLog("Poll ... " + count + ", " + url + ", " + sessionId);
			count ++;
			
//			String url = "http://192.168.1.141:8087/webstudy/checkNewMessageSvlt?param=7&type=" + Constants.ANDROID_TYPE;
//			String url = "http://192.168.1.246:2800/notice/push";
			
			boolean available = Util.isNetAvailable(getApplicationContext());
			if (available && !Util.checkEmptyOrNull(url)) {
				String pushUrl = url + Constants.REQUEST_NOTICE_PUSH;
				printLog(pushUrl);
				
				String result = null;
				result = Http.getStrContent2(pushUrl, sessionId);
				printLog(result);
				
				if (!Util.checkEmptyOrNull(result)) {
					Gson gson = new Gson();
					try {
						Result list = gson.fromJson(result, new TypeToken<Result>(){}.getType());
						printLog(list.toString());
						List<NoticePush> notices = list.getResult().getResult();
//						int size = notices.size();
//						String msg = "您总共有" + size + "条消息待处理";
//						printLog(msg);
						
						judge(notices);
						
//						showNotification(msg);
//						count++;
//						showNotification("Test++");
//						Log.d(TAG, "New message! " + count + " " + msg);
					}
					// 异常表示没有通知数据 
					catch (JsonSyntaxException ex) {
						printLog("通知异常");
						ex.printStackTrace();
					}
					catch (Exception ex) {
						printLog("通知异常 发生错误");
						ex.printStackTrace();
					}
				}
			}
		}
	}
	
	void printLog(String msg) {
		Util.printLog(TAG, msg);
	}
	
	/*
	 *  判断类型 
	 *  notice, urgent, work, mail 四种类型
	 *  
	 */
	void judge(List<NoticePush> notices) {
		if (null != notices && notices.size() > 0) {
			int notice = 0, urgent = 0, work = 0, mail = 0;
			for (NoticePush np : notices) {
				if (Constants.NOTICE_TYPE_NOTICE.equals(np.getClazz())) {
					notice = notice + 1;
				}
				else if (Constants.NOTICE_TYPE_URGENT.equals(np.getClazz())) {
					urgent = urgent + 1;
				}
				else if (Constants.NOTICE_TYPE_WORK.equals(np.getClazz())) {
					work = work + 1;
				}
				else if (Constants.NOTICE_TYPE_MAIL.equals(np.getClazz())) {
					mail = mail + 1;
				}
			}
			
			// sessionid 单独保存
			// 通知　紧急通知　事务　邮箱
			// 查看　查看　处理　邮件处理
			printLog("通知总数 " + notices.size());
			printLog("notice = " + notice + ", urgent = " + urgent + ", work = " 
					+ work + ", mail = " + mail);
			
			String msg = null, redirectUrl = null;
			if (notice > 0) {
				count++;
				msg = "您总共有" + notice + "条通知";
				redirectUrl = url + Constants.REQUEST_NOTICE;
				showNotification("通知", msg, NOTIFICATION_ID_NOTICE, redirectUrl);
			}
			if (urgent > 0) {
				count++;
				msg = "您总共有" + urgent + "条紧急通知";
				redirectUrl = url + Constants.REQUEST_URGENT;
				showNotification("紧急通知", msg, NOTIFICATION_ID_URGENT, redirectUrl);
			}
			if (work > 0) {
				count++;
				msg = "您总共有" + work + "条事务待处理";
				redirectUrl = url + Constants.REQUEST_WORK;
				showNotification("事务", msg, NOTIFICATION_ID_WORK, redirectUrl);
			}
			if (mail > 0) {
				count++;
				msg = "您总共有" + mail + "封邮件未读";
				redirectUrl = url + Constants.REQUEST_MAIL;
				showNotification("邮箱", msg, NOTIFICATION_ID_MAIL, redirectUrl);
			}
			
		}
	}
	
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "Service:onDestroy");
	}

}
