package com.lkpower.util;

import java.io.File;

import android.os.Build;
import android.os.Environment;

/**
 * 
 * @author linger
 *
 * @since 2015-5-29
 *
 */
public class Constants {
	
	public static final String EMPTY = "";
	
	// notice, urgent, work, mail
	public static final String NOTICE_TYPE_NOTICE = "notice";
	
	public static final String NOTICE_TYPE_URGENT = "urgent";
	
	public static final String NOTICE_TYPE_WORK = "work";
	
	public static final String NOTICE_TYPE_MAIL = "mail";
	
	// 对应链接 
	public static final String REQUEST_NOTICE = "/ui/html/noticelist.html";
	
	public static final String REQUEST_URGENT = "/ui/html/first.html";
	
	public static final String REQUEST_WORK = "/ui/html/wklist.html";
	
	public static final String REQUEST_MAIL = "/ui/html/maillist.html";
	
	public static final String KEY_URL = "com.lkpower.oaandroid.url";
	
	public static final String NOTIFY_RECEIVED_ACTION = "com.lkpower.oaandroid.NOTIFY_RECEIVED_ACTION";
	
	public static final String KEY_REDIRECT_URL = "key_redirect_url";
	
	
	
	public static final String SERVICE_URL = "ServiceUrl";
	public static final String SESSIONID = "SessionUrl";
	
	// 定制地址 
	public static final String DEFINE_URL = "http://61.136.78.43:9004";
	
	// 测试地址 
	public static final String DEFINE_URL_TEST = "http://192.168.1.95:8087/html5/index5.html";
	
//	public static final String DEFAULT_URL = "http://115.28.246.218:3366/topology.html";
	
//	public static final String DEFAULT_URL_2 = "http://www.lkpower.com/index.html";
	
	public static final String UPDATE_REQUEST_JSON = "/download/?name=version.json";
	
	public static final String REQUEST_NOTICE_PUSH = "/notice/push";
	
	public static final String REQUEST_GET_SESSION_BY_POST = "/loginu";
	
	// 0 MD5; 1 NOT MD5;
	public static final String REQUEST_ENCRYPT_PARAM = "/encrypt/flag";
	
	// 0 表示开启MD5
	// 1 表示明码
	public static final String IS_MD5_LOGIN = "is_md5_login";
	
	// -1 表示这个值不存在，需要去获取
	public static final int LOGIN_PASSWD_OFF_DEFAULT = -1;
	
	// 0 表示获取失败，使用默认方式
	public static final int LOGIN_PASSWD_ON_DEFAULT = 0;
	
	public static final String USERNAME = "Username";
	
	public static final String PASSWORD = "Password";
	
	public static final String PASSWORD_MD5 = "PasswordMD5";
	
	public static final String USERINFO_SHA1 = "Username_Password_SHA1";
	
	public static final String LOGIN_TYPE = "Login_Type";
	
	
	public static final String PREFIX = Environment.getExternalStorageDirectory().getPath();
	
	@SuppressWarnings("deprecation")
	public static final String ANDROID_TYPE = Build.VERSION.SDK;
	
	public static final String HTTP_PREFIX = "http://";
	
	
	public static final String CHECKED = "checked";
	
	public static final int CHECKED_DEFAULT = 1;
	
	public static final int CHECKED_NOT = 0;
	
	// 退出标识
	public static final String EXIT_FLAG_2 = "/ui/html/first.html";
	
	public static final String EXIT_FLAG_3 = "/ui/html/pltfrm.html";
	
	
	// 存储目录
	public static final String SAVE_PATH = PREFIX + File.separator + "LKOA";
	
	// 下载文件目录
	public static final String TMP_PATH = SAVE_PATH + File.separator + "tmp";
	
	// 存储文件
	public static final String SAVE_FILE_PATH = SAVE_PATH + File.separator + "OAAndroid.apk";
	
	// 更新标识 
	public static final String UPDATE_DATE = "update_date";
	
	public static final String UPDATE_FLAG = "update_flag";
	
	public static final int UPDATE_FLAG_TRUE = 1;
	
	public static final int UPDATE_FLAG_FALSE = 0;
	
	static {
		File save = new File(SAVE_PATH);
		if (!save.exists()) {
			save.mkdirs();
		}
		
		File tmp = new File(TMP_PATH);
		if (!tmp.exists()) {
			tmp.mkdirs();
		}
	}

}
