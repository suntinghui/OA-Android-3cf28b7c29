package com.lkpower.oaandroid.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lkpower.oaandroid.MainActivity;
import com.lkpower.oaandroid.SplashActivity;
import com.lkpower.oaandroid.entity.JPushExtraEntity;
import com.lkpower.oaandroid.task.AsyncGetSessionTask_2;
import com.lkpower.util.Constants;
import com.lkpower.util.Util;


/**
 * 
 * @author linger
 * 
 * @since 2015-10-21
 * 
 *        推送信息接收
 * 
 */
public class JPushReceiver extends BroadcastReceiver {

	private static final String tag = JPushReceiver.class.getSimpleName();
	
	static int count = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		count++;
		Util.printLog(tag, "接收推送信息 " + count);

		Bundle bundle = intent.getExtras();
//		Util.printLog(tag, "[MyReceiver] onReceive - " + intent.getAction()
//				+ ", extras: " + printBundle(bundle));

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Util.printLog(tag, "[MyReceiver] 接收Registration Id : " + regId);
			// send the Registration Id to your server...
//			Toast.makeText(context,"regid is:" + regId, Toast.LENGTH_SHORT).show();
		}
		else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			Util.printLog(tag, "[MyReceiver] 接收到推送下来的自定义消息: ");
//			Util.printLog(tag,
//					"[MyReceiver] 接收到推送下来的自定义消息: "
//							+ bundle.getString(JPushInterface.EXTRA_MESSAGE) + ", " + 
//							bundle.getString(JPushInterface.EXTRA_EXTRA));
//			processCustomMessage(context, bundle);

		}
		else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			Util.printLog(tag, "[MyReceiver] 接收到推送下来的通知: " + 
					bundle.getString(JPushInterface.EXTRA_MESSAGE) + ", " 
					+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			
		}
		else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			Util.printLog(tag, "[MyReceiver] 用户点击打开了通知");
			
//			String url = "http://www.baidu.com";
//			url = "http://192.168.1.81:2800/ui/html/maildetail.html?xid=100012590&mid=100006962&type=0&page=1";
			
//			Intent i = new Intent();
//			i.putExtra(Constants.KEY_URL, url);
//			i.setClass(context, SplashActivity.class);
//			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			context.startActivity(i);
			
			
			String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Util.printLog(tag, "extra " + extra);
			Util.printLog(tag, "run " + MainActivity.isRunning() + ", login " + MainActivity.isLogin());
			if (!Util.checkEmptyOrNull(extra)) {
			
				Gson gson = new Gson();
				JPushExtraEntity entity = null;
				try {
					entity = gson.fromJson(extra, JPushExtraEntity.class);
					Util.printLog(tag, "Entity: " + entity.toString());
				}
				catch (JsonSyntaxException ex) {
					ex.printStackTrace();
					entity = null;
				}
				
				if (null != entity) {
					SharedPreferences sp = context.getSharedPreferences("LKOA-sp", Context.MODE_PRIVATE);
					String url = sp.getString(Constants.SERVICE_URL, Constants.EMPTY);
					url = url + entity.getUrl();
					Util.printLog(tag, "### url = " + url);
					
					// 运行而且已经登录
					if (MainActivity.isRunning() && MainActivity.isLogin()) {
		//				processCustomMessage(context, url);
						Intent msgIntent = new Intent(Constants.NOTIFY_RECEIVED_ACTION);
						msgIntent.putExtra(Constants.KEY_REDIRECT_URL, url);
		
						context.sendBroadcast(msgIntent);
					}
					// 运行未登录
					else if (MainActivity.isRunning() && !MainActivity.isLogin()) {
//						Intent i = new Intent();
//						i.putExtra(Constants.KEY_URL, url);
//						i.setClass(context, MainActivity.class);
//						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//						context.startActivity(i);
						
						new AsyncGetSessionTask_2(context, url).execute();
					}
					// 未运行 
					else {
						Intent i = new Intent();
						i.putExtra(Constants.KEY_URL, url);
						i.setClass(context, SplashActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						context.startActivity(i);
					}
				}
			}

		}
		else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
				.getAction())) {
			Util.printLog(tag,
					"[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
							+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..

		}
		else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
				.getAction())) {
			boolean connected = intent.getBooleanExtra(
					JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			Util.printLog(tag, "[MyReceiver]" + intent.getAction()
					+ " connected state change to " + connected);
		}
		else {
			Util.printLog(tag, "[MyReceiver] Unhandled intent - " + intent.getAction());
		}
	}
	
	//send msg to Activity
	void processCustomMessage(Context context, String url) {
		Intent msgIntent = new Intent(Constants.NOTIFY_RECEIVED_ACTION);
		msgIntent.putExtra(Constants.KEY_REDIRECT_URL, url);

		context.sendBroadcast(msgIntent);
	}
	
	
	
	
	// 打印所有的 intent extra 数据
	static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n[" + count + "\n");
		for (String key : bundle.keySet()) {
			
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("EXTRA_NOTIFICATION_ID:" + key + ", value:" + bundle.getInt(key) + "\n");
			}
			else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("EXTRA_CONNECTION_CHANGE:" + key + ", value:" + bundle.getBoolean(key) + "\n");
			}
			else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				sb.append("EXTRA_EXTRA: " + key + ", " + bundle.getString(key) + "\n");
			}
			else if (key.equals(JPushInterface.EXTRA_TITLE)) {
				sb.append("EXTRA_TITLE: " + key + ", " + bundle.getString(key) + "\n");
			}
			else if (key.equals(JPushInterface.EXTRA_MESSAGE)) {
				sb.append("EXTRA_MESSAGE: " + key + ", " + bundle.getString(key) + "\n");
			}
			else if (key.equals(JPushInterface.EXTRA_CONTENT_TYPE)) {
				sb.append("EXTRA_CONTENT_TYPE: " + key + ", " + bundle.getString(key) + "\n");
			}
			else if (key.equals(JPushInterface.EXTRA_APP_KEY)) {
				sb.append("EXTRA_APP_KEY: " + key + ", " + bundle.getString(key) + "\n");
			}
			else if (key.equals(JPushInterface.EXTRA_ALERT)) {
				sb.append("EXTRA_ALERT: " + key + ", " + bundle.getString(key) + "\n");
			}
			else if (key.equals(JPushInterface.EXTRA_MSG_ID)) {
				sb.append("EXTRA_MSG_ID: " + key + ", " + bundle.getString(key) + "\n");
			}
			else if (key.equals(JPushInterface.EXTRA_STATUS)) {
				sb.append("EXTRA_STATUS: " + key + ", " + bundle.getString(key) + "\n");
			}
			else {
				sb.append("key:" + key + ", value:" + bundle.getString(key) + "\n");
			}
		}
		sb.append("\n]");
		
		return sb.toString();
	}
	

}




