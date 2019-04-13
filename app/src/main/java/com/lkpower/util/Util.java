package com.lkpower.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.lkpower.oaandroid.BuildConfig;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.logging.Logger;


/**
 * 
 * @author sevenzero
 *
 * @since 2012-6-8
 *
 */
public class Util {
	
	static Logger log = Logger.getLogger(Util.class.getName());
	
	static boolean debug = BuildConfig.DEBUG;
	
	public static void printLog(String tag, String msg) {
		if (debug) {
			if (null != tag) {
				Log.d(tag, String.valueOf(msg));
			}
		}
	}
	
	/**
	 * 格式化路径结束
	 * 
	 * @param path
	 * @return
	 */
	public static String formatPathSuffix(String path) {
		if (null != path) {
			if (path.endsWith("/") || path.endsWith("\\")) {
				return path.substring(0, path.length() - 1);
			}
		}
		return path;
	}

	/** 
	 * 格式化路径开始
	 * @param input 待格式化的路径
	 * */
	public static String formatPathPrefix(String input) {
		if (input != null) {
			if (!input.startsWith("\\") && !input.startsWith("/")) {
				return "/" + input;
			}
		}
		return input;
	}

	/** 
	 * 合并路径
	 * @param  prefix 路径前缀
	 * @param  suffix 路径后缀
	 * @return 合并后路径
	 * */
	public static String joinPath(String prefix, String suffix) {
		if (prefix == null || suffix == null) {
			return null;
		}
		if (prefix.endsWith("/") || prefix.endsWith("\\")) {
			prefix = prefix.substring(0, prefix.length() - 1);
		}
		if (suffix.startsWith("/") || suffix.startsWith("\\")) {
			suffix = suffix.substring(1, suffix.length());
		}
		
		return prefix + "/" + suffix;
	}
	
	/**
	 * Check empty or null
	 * @param str
	 * @return true if empty or null; <br/> else false;
	 */
	public static boolean checkEmptyOrNull(String str) {
		if (null == str || "".equals(str.trim())) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Check Object whether is null
	 * @param obj
	 * @return true if null; <br/> else false;
	 */
	public static boolean checkNull(Object obj) {
		if (null == obj) {
			return true;
		}
		
		return false;
	}
	
	public static String replace(String str) {
		if (checkEmptyOrNull(str)) {
			return null;
		}		
		
		return str.replaceAll(":|/", "");
	}
	
	public static String convertFileSize(String size) {
//		log.info("Util-Size-" + size);
		if (checkEmptyOrNull(size)) {
			return null;
		}
		
		long fileSize = 0L;
		try {
			fileSize = Long.parseLong(size);
		}
		catch (Exception ex) {
//			log.info("Util-convert file size exception.");
			ex.printStackTrace();
			return null;
		}
		
		String unit = "", result = "-";
		float fSize = fileSize * 1.0F;
		float tmp = 0.0F;

		if ((tmp = fSize / (1024 * 1024 * 1024)) > 0.5) {
			unit = "GB";
		}
		else if ((tmp = fSize / (1024 * 1024)) > 0.5) {
			unit = "MB";
		}
		else if ((tmp = fSize / (1024)) > 0.5) {
			unit = "KB";
		}
		else {
			unit = "B";
			tmp = fileSize;
		}
//		log.info("Util-" + tmp);
		
		if (String.valueOf(tmp).endsWith("0")) {
			NumberFormat nf = NumberFormat.getIntegerInstance();
			result = nf.format(tmp) + unit;
		}
		else {
			DecimalFormat df = new DecimalFormat("0.00");
			result = df.format(tmp) + unit;
		}
		
		return result;
	}
	
	public static String getStringFromInputStream(InputStream input) {
		if (null == input) {
			return null;
		}
		
		BufferedReader br = null;
		try {			
			br = new BufferedReader(new InputStreamReader(input));
			StringBuffer sb = new StringBuffer(2048);
			String line = null;
			
			while (null != br && null != (line = br.readLine())) {
				sb.append(line).append("\n");
			}
			
			return sb.toString();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		finally {
			if (null != br) {
				try {
					br.close();
				}
				catch (IOException e) {
				}
			}
		}
	}
	
	public static byte[] streamToByte(InputStream is) {
		if (null == is) {
			return null;
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] data = null;
		int ch;
		try {
			while ((ch = is.read()) != -1) {
				baos.write(ch);
			}
			data = baos.toByteArray();
		}
		catch (IOException e) {
			e.printStackTrace();
			data = null;
		}
		
		return data;
	}
	
	public static InputStream byteToStream(byte[] data) {
		if (null == data) {
			return null;
		}
		
		return new ByteArrayInputStream(data);
	}
	
	public static final String ENCODING = "UTF-8";
	
	/**
	 * 
	 * @param param
	 * @return null if param is empty or null; <br/>  the translated string;
	 * 
	 */
	public static String encodeParam(String param) {
		if (checkEmptyOrNull(param)) {
			return null;
		}
		
		String temp = null;
		try {
			temp = URLEncoder.encode(param, ENCODING);
		}
		catch (UnsupportedEncodingException e) {
			temp = URLEncoder.encode(param);
		}
		
		return temp;
	}
	
	/**
	 * md5
	 * @param data
	 * @return null if exception occur; <br/>
	 */
	public static String md5(String data) {
		if (null == data) {
			return null;
		}
		byte[] bytes = data.getBytes();
		StringBuilder sb = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(bytes);			
			byte[] md5Bytes = md.digest();
			
			sb = new StringBuilder();
			final int length = md5Bytes.length;
			for (int i=0; i<length; i++) {
				int val = ((int) md5Bytes[i]) & 0xFF;
				if (val < 16) {
					sb.append(0);
				}
				sb.append(Integer.toHexString(val));
			}
			
			return sb.toString();
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String sha1(String data) {
		return digest(data, "SHA1");
	}
	
	
	public static String digest(String data, String type) {
		byte[] bytes = data.getBytes();
		StringBuilder sb = null;
		try {
			MessageDigest md = MessageDigest.getInstance(type);
			md.update(bytes);			
			byte[] md5Bytes = md.digest();
			
			sb = new StringBuilder();
			final int length = md5Bytes.length;
			for (int i=0; i<length; i++) {
				int val = ((int) md5Bytes[i]) & 0xFF;
				if (val < 16) {
					sb.append(0);
				}
				sb.append(Integer.toHexString(val));
			}
			
			return sb.toString();
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getUuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	public static String format(int n) {
		DecimalFormat df = new DecimalFormat("00000000");
		return df.format(n);
	}
	
	/**
	 * 补足8位字符串
	 * @param binary
	 * @return
	 */
	public static String format(String binary) {
		if (null != binary && !"".equals(binary)) {
			while (binary.length() < 8) {
				StringBuilder sb = new StringBuilder();
				sb.append("0");
				sb.append(binary);
				
				binary = sb.toString();
			}
			return binary;
		}
		return "00000000";
	}
	
	public static final int I_255 = 0xFF;
	
	/**
	 * bytes to hex string
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		if (null == src) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		final int len = src.length;
		for (int i=0; i<len; i++) {
			int value = src[i] & 0xFF;
			String hex = Integer.toHexString(value);
			if (hex.length() < 2) {
				sb.append("0");
			}
			sb.append(hex);
		}
		return sb.toString();
	}
	
	/**
	 * 字节数据转字符串专用集合
	 */
	private static final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 字节数据转十六进制字符串
	 * 
	 * @param data
	 *            输入数据
	 * @return 十六进制内容
	 */
	public static String byteArrayToString(byte[] data) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			// 取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移
			stringBuilder.append(HEX_CHAR[(data[i] & 0xf0) >>> 4]);
			// 取出字节的低四位 作为索引得到相应的十六进制标识符
			stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);
			if (i < data.length - 1) {
//				stringBuilder.append(' ');
			}
		}
		return stringBuilder.toString();
	}
	
	public static Date getGMTTime() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Calendar day = Calendar.getInstance();
		day.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		day.set(Calendar.MONTH, cal.get(Calendar.MONTH));
		day.set(Calendar.DATE, cal.get(Calendar.DATE));
		day.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
		day.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
		day.set(Calendar.SECOND, cal.get(Calendar.SECOND));
		day.set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND));
		
		return day.getTime();
	}
	

	
	public static String formatDate(Date date, String format) {
		if (null != date) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
//			df = new SimpleDateFormat("yyyyMM");
			df = new SimpleDateFormat(format);
			return df.format(date);
		}
		return null;
	}
	
	public static String formatDate(Date date) {
		if (null != date) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
//			df = new SimpleDateFormat("yyyyMM");
			df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			return df.format(date);
		}
		return null;
	}
	

	
	/**
	 * Indicates whether network connectivity is possible.
	 * @param activity
	 * @return true is available; false is unavailable.
	 */
	public static boolean isNetAvailable(Activity activity) {
//    	ConnectivityManager cManager=(ConnectivityManager) activity.getSystemService(
//    			Context.CONNECTIVITY_SERVICE);
//		NetworkInfo info = cManager.getActiveNetworkInfo();
//		if (info != null && info.isAvailable()) {
//			return true;
//		}
//		else {
//			return false;
//		}
		
		return isNetAvailable((Context)activity);
    }
	
	public static boolean isNetAvailable(Context context) {
		if (null != context) {
	    	ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(
	    			Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cManager.getActiveNetworkInfo();
			if (info != null && info.isAvailable()) {
				return true;
			}
		}
		
		return false;
    }
	
	public static boolean isWifi(Activity activity) {
		ConnectivityManager cManager=(ConnectivityManager) activity.getSystemService(
    			Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			if (ConnectivityManager.TYPE_WIFI == info.getType()) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 检查扩展名
	 * 
	 * @param filename
	 * @param fileExts
	 * @return
	 */
	public static boolean checkExtNameWithInStringArray(String filename,  String[] fileExts) {
		for (String ext : fileExts) {
			if (filename.toUpperCase().endsWith(ext.toUpperCase()))
				return true;
		}
		return false;
	}
	
	public static String getFileExtension(String fileName){
		if (null != fileName && !"".equals(fileName)) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		}
		return null;
	}
	
	/**
	 * 判断sdcard
	 * @return
	 */
	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		return true;
	}
	
	public static String getDeviceId (Context ctx) {
		return "999999";
	}
	
//	public static String base64(String str) {
//		BASE64Encoder encoder = new BASE64Encoder();
//		return encoder.encode(str.getBytes());
//	}
	
//	public static void main(String[] args) {
//		log.info(getUuid());
//		System.out.println(format(-20));
//		log.info(encodeParam("@"));
		
//		System.out.println(getGMTTime());
//		System.out.println(formatDate(getGMTTime()));
		
		// YWJj
//		System.out.println(base64("abc"));
		// YWJjCg==
//		System.out.println(base64("abc\n"));
		
//		String str = "ffffff97 ffffffa6 ffffffb5 fffffff6 ffffffc9 6b 5a 60 76 ffffff85 59 ffffffb0 6d fffffff7 57 17 2b 36 67 ffffffca 35 ffffffb3 7d ffffffe8 ffffffcc 72 75 ffffffcb ffffffbe ffffffa9 4a 1b 14 ffffff85 34 ffffffef 4f ffffff99 c ffffffff ffffffa8 ffffff9c ffffff99 13 8 ffffffc3 ffffffcb fffffffd 3c fffffff5 fffffff8 fffffff9 b 7e 72 29 59 ffffffe1 ffffffd9 ffffffad fffffffb ffffffb1 77 ffffffbb ffffff98 fffffffc ffffffe0 5a 5a ffffffbd ffffffb7 b 2a 4 ffffffd8 19 19 61 ffffffe6 43 77 ffffff96 ffffffad 10 59 ffffffd9 fffffffe ffffff9e 3d 46 54 ffffff96 33 fffffff6 61 7d 37 ffffff80 ffffff80 61 5c ffffff81 ffffffa2 ffffffe4 ffffffa5 6e ffffffda c ffffffd6 ffffffec fffffff1 ffffff93 46 ffffffc0 3d 40 ffffffdb 4a ffffffb1 c ffffffce 7d ffffffa7 ffffffb9 ffffffb5 ffffff88 31 e ";
//		log.info(str.replaceAll("ffffff", ""));
//	}
	
}
