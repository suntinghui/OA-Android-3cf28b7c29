package com.lkpower.oaandroid.task;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lkpower.http.Http;
import com.lkpower.oaandroid.MainActivity;
import com.lkpower.oaandroid.entity.SessionEntity;
import com.lkpower.util.Constants;
import com.lkpower.util.Util;

/**
 * 获取Session
 * 
 * @author linger
 *
 * @since 2015-10-12
 *
 */
public class AsyncGetSessionTask extends AsyncTask<String, String, String> {
	
	private static final String tag = AsyncGetSessionTask.class.getSimpleName();
	
	private Activity context;
	private String url;
	
	private String value = null;
	private SessionEntity session = null;
	
	public AsyncGetSessionTask(Activity context, String url) {
		this.context = context;
		this.url = url;
	}

	@Override
	protected String doInBackground(String... params) {
//		try {
//			Thread.sleep(12 * 1000);
//		}
//		catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		SharedPreferences sp = context.getSharedPreferences("LKOA-sp", Context.MODE_PRIVATE);
		String server = sp.getString(Constants.SERVICE_URL, Constants.EMPTY);
		String user = sp.getString(Constants.USERNAME, Constants.EMPTY);
		String passwd = sp.getString(Constants.PASSWORD, Constants.EMPTY);
		Util.printLog(tag, server + ", " + user + ", " + passwd);
		
		if (Util.checkEmptyOrNull(server) || Util.checkEmptyOrNull(user) || Util.checkEmptyOrNull(passwd)) {
			return null;
		}
		
		String url = server + Constants.REQUEST_GET_SESSION_BY_POST;
		Util.printLog(tag, url);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", user);
		map.put("passwd", passwd);
		
		value = Http.getStrContentByPostWithNoSession(url, map);
		Util.printLog(tag, value);
		
		if (!Util.checkEmptyOrNull(value)) {
			Gson gson = new Gson();
			try {
				session = gson.fromJson(value, 
						new TypeToken<SessionEntity>(){}.getType());
				Util.printLog(tag, session.toString());
			}
			catch (JsonSyntaxException ex) {
				ex.printStackTrace();
				session = null;
			}
		}
		
		
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		if (Util.checkEmptyOrNull(value)) {
			Intent intent = new Intent();
			intent.setClass(context, MainActivity.class);
			context.startActivity(intent);
			context.finish();
			
			return;
		}
		
		if (null == session) {
			Intent intent = new Intent();
			intent.setClass(context, MainActivity.class);
			context.startActivity(intent);
			context.finish();
			
			return;
		}
		
		if (null != session) {
			// 成功失败分别处理
			Intent intent = new Intent();
			if (null != url) {
				if (url.indexOf("?") > -1) {
					url = url + "&";
				}
				else {
					url = url + "?";
				}
				
				url = url + "sessionid=" + session.getResult().getResult();
				Util.printLog(tag, url);
				intent.putExtra(Constants.KEY_URL, url);
				intent.setClass(context, MainActivity.class);
				context.startActivity(intent);
			}
			context.finish();
		}
		
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		Util.printLog(tag, "photo onProgressUpdate ...");
	}

}
