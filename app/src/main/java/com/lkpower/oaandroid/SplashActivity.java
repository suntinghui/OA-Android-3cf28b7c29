package com.lkpower.oaandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

import com.lkpower.oaandroid.task.AsyncGetSessionTask;
import com.lkpower.util.Constants;
import com.tencent.bugly.crashreport.CrashReport;

public class SplashActivity extends Activity {
	
	private static final String tag = SplashActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		JPushInterface.init(getApplicationContext());
		setContentView(R.layout.activity_splash);

		Intent intent = this.getIntent();
		String value = intent.getStringExtra(Constants.KEY_URL);
		Log.d(tag, "传递参数 " + value);
		
		// test url
//		value = "http://192.168.1.81:2800/ui/html/maildetail.html?xid=100012590&mid=100006962&type=0&page=1";
		
		
		if (null == value) {
			new Handler().postDelayed(new Runnable() {
	
				@Override
				public void run() {
					Intent intent = new Intent();
					intent.setClass(SplashActivity.this, MainActivity.class);
					startActivity(intent);
					
					finish();
	
				}
			}, 2000L);
		}
		else {
			new AsyncGetSessionTask(this, value).execute();
		}
		
	}
	@Override
	protected void onResume() {
	    super.onResume();
	    JPushInterface.onResume(this);
	}
	@Override
	protected void onPause() {
	    super.onPause();
	    JPushInterface.onPause(this);
	}
}
