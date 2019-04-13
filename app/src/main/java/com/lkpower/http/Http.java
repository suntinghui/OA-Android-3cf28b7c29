package com.lkpower.http;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.xml.sax.InputSource;

import com.lkpower.oaandroid.OAWebViewClient;
import com.lkpower.util.Constants;
import com.lkpower.util.Util;


/**
 * 
 * @author sevenzero
 *
 * @since 2012-6-5
 *
 */
public class Http {
	
	static Logger log = Logger.getLogger(Http.class.getSimpleName());
	
	private static final int RECYCLE_COUNT = 3;
	private static final long SLEEP_TIME   = 1000L;
	
	public static final int TIME_OUT           = 30 * 000;
	private static final String REQUEST_METHOD = "POST";
	private static final String ENCODING       = "UTF-8";
	
//	private static final String FOXIT_SERVICE_URL = "http://service.foxitcloud.com";
	
//	static String sessionId2 = "connect.sid=s%3A0Jex7z5RotHBUlWWzXKlG2CFARTzGy6s.Mm6gqe4lysDApVNhF742E%2B%2BOtdDTZEDZ6zAwIvwwU7k";
	
	public static String download(String url) {
		URL httpUrl = null;
		HttpURLConnection urlConn = null;
		BufferedOutputStream bos = null;;
		String save = null;
		try {
			httpUrl = new URL(url);
			urlConn = (HttpURLConnection) httpUrl.openConnection();
			log.info("download session " + OAWebViewClient.sessionId);
			urlConn.setRequestProperty("Cookie", OAWebViewClient.sessionId);
//			Map<String, List<String>> map = urlConn.getHeaderFields();
			InputStream input = urlConn.getInputStream();
//			map = urlConn.getHeaderFields();
			
//			log.info("测试输出");
//			Set<String> keys = map.keySet();
//			Iterator<String> it = keys.iterator();
//			while (it.hasNext()) {
//				String key = it.next();
//				List<String> list = map.get(key);
//				log.info("key " + key);
//				for (String str : list) {
//					log.info(str);
//				}
//			}
			
			
//			int length = urlConn.getContentLength();
			// Content-Disposition
			String contentDis = urlConn.getHeaderField("Content-Disposition");
//			contentDis = "attachment; filename=URL%E7%89%B9%E6%AE%8A%E5%AD%97%E7%AC%A6.txt";
			log.info("" + contentDis);
			String fileName = null;
			if (null != contentDis) {
//				return null;
				fileName = URLDecoder.decode(contentDis.substring(contentDis.indexOf("filename") + 9, contentDis.lastIndexOf(";")));
			}
			else {
				int index = url.lastIndexOf("/");
				if (-1 == index) {
					log.info("url 不正确");
					return null;
				}
				fileName = url.substring(index + 1);
			}
			log.info(fileName);
			
			if (Util.checkEmptyOrNull(fileName)) {
				return null;
			}
			save = Constants.TMP_PATH + File.separator + fileName;
			log.info(save);
			
			File tmpDir = new File(Constants.TMP_PATH);
			if (!tmpDir.exists()) {
				boolean result = tmpDir.mkdirs();
				log.info("create dir " + result);
			}
			
			bos = new BufferedOutputStream(new FileOutputStream(save));
			byte[] buffer = new byte[2048];
			int size;
			while ((size = input.read(buffer)) != -1) {
				bos.write(buffer, 0, size);
			}
			bos.flush();

			return save;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if (null != bos) {
				try {
					bos.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static boolean download(String url, String save) {
		URL httpUrl = null;
		HttpURLConnection urlConn = null;
		BufferedOutputStream bos = null;
		try {
			httpUrl = new URL(url);
			urlConn = (HttpURLConnection) httpUrl.openConnection();
			InputStream input = urlConn.getInputStream();
			
			bos = new BufferedOutputStream(new FileOutputStream(save));
			byte[] buffer = new byte[2048];
			int size;
			while ((size = input.read(buffer)) != -1) {
				bos.write(buffer, 0, size);
			}
			bos.flush();

			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		finally {
			if (null != bos) {
				try {
					bos.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	static InputStream getInputStream(String url, String param) {
		if (Util.checkEmptyOrNull(url) || Util.checkEmptyOrNull(param)) {
			return null;
		}
		
		String message = "?key=" + param + "&site=";
		
		URL httpUrl = null;
		HttpURLConnection urlConn = null;
		try {
			httpUrl = new URL(url);
			urlConn = (HttpURLConnection) httpUrl.openConnection();
			urlConn.setConnectTimeout(TIME_OUT);
			urlConn.setReadTimeout(TIME_OUT);
			urlConn.setRequestMethod(REQUEST_METHOD);
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			
			urlConn.addRequestProperty("Content-Type", 
					"application/x-www-form-urlencoded; charset=" + ENCODING);
			urlConn.addRequestProperty("Content-Length", String.valueOf(message.length()));
			
			OutputStream output = urlConn.getOutputStream();
			output.write(message.getBytes());
			output.flush();
			output.close();
			
			return urlConn.getInputStream();
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static String getStrContent(String url, String param) {
		
		if (Util.checkEmptyOrNull(url) || Util.checkEmptyOrNull(param)) {
			return null;
		}
		
		BufferedReader br = null;
		try {
			InputStream input = getInputStream(url, param);
			if (null == input) {
				return null;
			}
			
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
	
//	static String sessionId = "connect.sid=s%3AhOh5ZrNwYweEdXksgKg1-sWTrWk-9t8L.9yZUONmYGt0tCnQFccqYA%2F1647XITRQk5Of947n%2Bipo; path=/; domain=192.168.1.152; HttpOnly";
	static String sessionId = null;
	/**
	 * Get stream object from url （HTTP GET METHOD）
	 * @param url
	 * @return InputStream
	 */
	public static InputStream getInputStream(String url) {
		if (Util.checkEmptyOrNull(url)) {
			log.info("Http url is null.");
			return null;
		}
		
		URL httpUrl = null;
		HttpURLConnection urlConn = null;
		
		try {
			httpUrl = new URL(url);
			urlConn = (HttpURLConnection) httpUrl.openConnection();
			urlConn.setConnectTimeout(TIME_OUT);
			urlConn.setReadTimeout(TIME_OUT);
			urlConn.setRequestProperty("Cookie", OAWebViewClient.sessionId);
			
//			if (null != sessionId) {
//				urlConn.setRequestProperty("Cookie", sessionId);
//			}
			
			long start = System.currentTimeMillis();
			InputStream input = urlConn.getInputStream();
			long end = System.currentTimeMillis();
			log.info("Http costs " + (end - start) + "ms");

			return input;
		}
		catch (MalformedURLException e) {
			log.info("Http Url 不合法");
			e.printStackTrace();
			return null;
		}
		catch (SocketTimeoutException e) {
			log.info("Http 连接主机超时");
			e.printStackTrace();
			return null;
		}
		catch (IOException e) {
			log.info("Http IO exception.");
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Get content from url （HTTP GET METHOD）
	 * @param url
	 * @return String
	 */
	public static String getStrContent(String url) {
		if (Util.checkEmptyOrNull(url)) {
			log.info("Url is null");
			return null;
		}
		
		BufferedReader br = null;
		try {
			InputStream input = getInputStream(url);
			if (null == input) {
				return null;
			}
			
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
	
	public static InputStream getInputStream2(String url, String sessionId) {
		if (Util.checkEmptyOrNull(url)) {
			log.info("Http url is null.");
			return null;
		}
		
		URL httpUrl = null;
		HttpURLConnection urlConn = null;
		
		try {
			httpUrl = new URL(url);
			urlConn = (HttpURLConnection) httpUrl.openConnection();
			urlConn.setConnectTimeout(TIME_OUT);
			urlConn.setReadTimeout(TIME_OUT);
			urlConn.setRequestProperty("Cookie", sessionId);
			
//			if (null != sessionId) {
//				urlConn.setRequestProperty("Cookie", sessionId);
//			}
			
			long start = System.currentTimeMillis();
			InputStream input = urlConn.getInputStream();
			long end = System.currentTimeMillis();
			log.info("Http costs " + (end - start) + "ms");

			return input;
		}
		catch (MalformedURLException e) {
			log.info("Http Url 不合法");
			e.printStackTrace();
			return null;
		}
		catch (SocketTimeoutException e) {
			log.info("Http 连接主机超时");
			e.printStackTrace();
			return null;
		}
		catch (IOException e) {
			log.info("Http IO exception.");
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Get content from url （HTTP GET METHOD）
	 * @param url
	 * @return String
	 */
	public static String getStrContent2(String url, String sessionId) {
		if (Util.checkEmptyOrNull(url)) {
			log.info("Url is null");
			return null;
		}
		
		BufferedReader br = null;
		try {
			InputStream input = getInputStream2(url, sessionId);
			if (null == input) {
				return null;
			}
			
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
	
	/**
	 * Get InputSource from url 
	 * 
	 * @param url
	 * @return
	 */
	public static InputSource getInputSource(String url) {
		if (Util.checkEmptyOrNull(url)) {
			return null;
		}
		
		InputStream input = getInputStream(url);
		if (null == input) {
			int n = 0;
			do {
				try {
					Thread.sleep(SLEEP_TIME);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
					log.info("Thread-Exception.");
				}
				
				input = getInputStream(url);
				
			} while (null == input && n++ < RECYCLE_COUNT);
			
			if (null == input) {
				return null;
			}
			log.info("NET n = " + n);
		}
		
		return new InputSource(input);
	}
	
	/**
	 * Http POST 请求
	 * 
	 * @param url
	 * @param map
	 * @return
	 */
	public static  String getStrContentByPostWithNoSession(String url, Map<String, String> map) {
		if (Util.checkEmptyOrNull(url) || null == map) {
			return null;
		}
		
		BufferedReader br = null;
		try {
			InputStream input = getInputStreamByPostWithNoSession(url, map);
			if (null == input) {
				return null;
			}
			
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
	
	/**
	 * Http POST 请求
	 * 
	 * @param url
	 * @param map
	 * @return
	 */
	public static InputStream getInputStreamByPostWithNoSession(String url, Map<String, String> map) {
		if (Util.checkEmptyOrNull(url) || null == map) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		if (map.size() > 0) {
			Set<String> set = map.keySet();
			Iterator <String> it = set.iterator();
			while(it.hasNext()) {
				String key = it.next();
				String value = map.get(key);
				sb.append(key).append("=").append(value).append("&");
			}
			
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
		}
		log.info("url = " + url);
		log.info("prame = " + sb.toString());
		
		String message = sb.toString();
		
		URL httpUrl = null;
		HttpURLConnection urlConn = null;
		try {
			httpUrl = new URL(url);
			urlConn = (HttpURLConnection) httpUrl.openConnection();
			urlConn.setConnectTimeout(TIME_OUT);
			urlConn.setReadTimeout(TIME_OUT);
			urlConn.setRequestMethod(REQUEST_METHOD);
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			
			urlConn.addRequestProperty("Content-Type", 
					"application/x-www-form-urlencoded; charset=" + ENCODING);
			urlConn.addRequestProperty("Content-Length", String.valueOf(message.length()));
			
			OutputStream output = urlConn.getOutputStream();
			output.write(message.getBytes());
			output.flush();
			output.close();
			
			return urlConn.getInputStream();
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		
		// http://VM10/exponentJobs/109201319_02_09_342f632e398-5000-48b4-b0e6-367b20a135fa/Input/doc3-18号-寿险废止.doc
		
		String s = Util.encodeParam("http://VM10/exponentJobs/109201319_02_09_342f632e398-5000-48b4-b0e6-367b20a135fa/Input");
		String s2 = Util.encodeParam("doc3-18号-寿险废止.doc");
		
		String url = "https://apis.live.net/v5.0/folder.a85db1edcc89e9a2/files?access_token=EwA4Aq1DBAAUlbRWyAJjK5w968Ru3Cyt/6GvwXwAAR52QB5mfDlpyd36K1Bln9agMjC0m1eus8pGd20agiolIwo24+vEwaN6YL76219TAbx3XyAwGMH8PvLQf59kZTEvz3HubAqqalxXaGW9zeBzH4ZKtSaBfbAfQyLhYFiU33XxoQYuVq/zWG6/RPoT9N2uFJ/OTr9BeG6XgVxMXV/cWq8X+V9LjysAdEfBaCjT68Hw602j8ryizJSHgdpMRl4C3jyWCWXfChGfZLaqlYB2J35L1uGe99n9Q8KgJQfZWtaKzJ++73NeqrGq7Dc91d6kLgdAETsGUy5OeqkAWbdwGwATpXnIxBNPJFsGPpIxOpyJ2LV+87GInzcTmePmksEDZgAACFwLgkZyiVG4CAEsQpGt71ayrDIk49k75kvNJkhGm2T8k/4yN/zkoLwYb4VkAT1vo2mxJxIf4G/XUIlCYGTqcn42S4N6BWWCLqQ6dJNzzvRTfDqlXCT5gv2WUaHfKihigAFVipyD1jXkw/cV2Nl295zLUqrAFrNwD5FeA7pHCCujLhc/neIu8tgebnpdC7/0ESF7/GZ2+LpcY5t/wCuKKWdl5cFuClKnAgnRPHVEwcUdj8neAX3eLDdR7W9pq2AzT0WTun70o1YgQKaIf+lzkxeIjmfrnhuhedSBX1/ZWenJ3iJo8r/Lm9VY+5lmgJWsl1SDzrtShV6gyu/fAydQdcoLfiZ8FEVTVcO84pfQtzEpe7EAAA==";
		url = "https://graph.facebook.com/me?access_token=123";
		url = "http://fs01.foxitsoftware.cn/ExponentWSA/ExponentWSA.asmx/GetJobFilesAsStream?JobFolder=" + s + "&JobFiles=" + s2;
		url = "http://192.168.1.141:8087/webstudy/checkNewMessageSvlt?param=7";
		String result = null;
		result = getStrContent(url);
//		result = getStrContent(url, "2");
		log.info(result);
		
	}
	
}
