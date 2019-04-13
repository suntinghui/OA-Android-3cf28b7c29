package com.lkpower.oaandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lkpower.util.PermissionUtil;

public class LoginActivity extends Activity {
	
	static final String tag = LoginActivity.class.getSimpleName();
	
	private EditText username, password, serverurl;
	private Button btnLogin, btnSettingIp;
	private ImageView helpImageView;
	private LinearLayout setIpLayout, rootLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		username = (EditText) this.findViewById(R.id.username);
		password = (EditText) this.findViewById(R.id.pwd);
		serverurl = (EditText) this.findViewById(R.id.serverurl);
		
		btnLogin = (Button) this.findViewById(R.id.login);
		btnLogin.setOnClickListener(listener);
		btnSettingIp = (Button) this.findViewById(R.id.setting_ip);
		btnSettingIp.setOnClickListener(listener);
		
		helpImageView = (ImageView) this.findViewById(R.id.helpImageView);
		helpImageView.setOnClickListener(listener);
		
		setIpLayout = (LinearLayout) this.findViewById(R.id.settingIPLayout);
		rootLayout = (LinearLayout) this.findViewById(R.id.rootLayout);
		rootLayout.setOnClickListener(listener);
		
	}

	@Override
	protected void onResume() {
		super.onResume();

		PermissionUtil.requestPermission(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == btnLogin.getId()) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, MainActivity.class);
				startActivity(intent);
			}
			else if (id == btnSettingIp.getId()) {
				if (View.GONE == setIpLayout.getVisibility()) {
					setIpLayout.setVisibility(View.VISIBLE);
				}
				else {
					setIpLayout.setVisibility(View.GONE);
				}
			}
			else if (id == helpImageView.getId()) {
				
//				LoginActivity.this.showDialog(Dialog.BUTTON_POSITIVE, "请勿随意修改服务器地址，否则会导致无法登录系统。\n服务器地址格式为 http://124.205.53.178:8008/Wap/");
			}
			else if (id == rootLayout.getId()) {
				setIpLayout.setVisibility(View.GONE);
			}
		}
	};

}
