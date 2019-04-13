package com.lkpower.util;

import android.content.Context;

import com.lkpower.oaandroid.task.AsyncUploadFileTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author sevenzero
 * @since 2012-11-21
 */
public class SendFileUtil {

    private static Logger log = Logger.getLogger(SendFileUtil.class.getSimpleName());

    private static final String BOUNDARY = "---------sevenzero"; // 数据分隔线
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";

    /**
     * 发送多个文件
     */
    public static void send() {
        List<String> list = new ArrayList<String>();
        list.add("/home/linger/pdf/jstl.pdf");
        list.add("/home/linger/linux.txt");

        try {
            String uri = "http://localhost:8087/webstudy/fileUpload2Svlt";
            uri = "http://localhost:8087/webstudy/multFileUploadSvlt";
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);        // 允许输入
            conn.setDoOutput(true);        // 允许输出
            conn.setUseCaches(false);    // 不使用Cache
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);

            OutputStream out = new DataOutputStream(conn.getOutputStream());

            byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();  // 数据分割线

            final int len = list.size();
            for (int i = 0; i < len; i++) {
                String name = list.get(i);
                File file = new File(name);
                StringBuilder sb = new StringBuilder();
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("\r\n");
                sb.append("Content-Disposition: form-data; name=\"file" + i
                        + "\"; filename=\"" + file.getName() + "\"\r\n");
                sb.append("Content-Type: application/octet-stream \r\n\r\n");

                byte[] data = sb.toString().getBytes();
                out.write(data);
                DataInputStream in = new DataInputStream(new FileInputStream(file));
                int bytes = 0;
                byte[] bufferOut = new byte[1024];
                while ((bytes = in.read(bufferOut)) != -1) {
                    out.write(bufferOut, 0, bytes);
                }
                out.write("\r\n".getBytes()); // 多个文件时，二个文件之间加入这个
                in.close();
            }
            out.write(end_data);
            out.flush();
            out.close();


            // 定义BufferedReader输入流来读取URL的响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

//			Map<String, List<String>> map = conn.getHeaderFields();
//			Set<String> sets = map.keySet();
//			for (String str : sets) {
//				System.out.print(str + ", ");
//				List<String> list2 = (List<String>) map.get(str);
//				for (String s : list2) {
//					System.out.print(s + ", ");
//				}
//				System.out.println();
//			}
//			
//			System.out.println(conn.getHeaderField("Set-Cookie"));

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

        }
    }

    public static void send(String addr, String filename) throws FileNotFoundException {
        File file = new File(filename);
        send(addr, file);
    }

    /**
     * 发送单个文件
     *
     * @param addr
     * @throws FileNotFoundException
     */
    public static void send(String addr, File file) throws FileNotFoundException {
        if (null == addr || "".equals(addr)) {
            throw new NullPointerException(addr + " is null.");
        }
        if (!file.exists()) {
            throw new FileNotFoundException(file.getPath() + " doesnot exist.");
        }
        OutputStream out = null;
        DataInputStream in = null;
        BufferedReader reader = null;
        try {
//			String uri = "http://localhost:8087/webstudy/fileUpload2Svlt";
//			uri = "http://localhost:8087/webstudy/multFileUploadSvlt";
            URL url = new URL(addr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);        // 允许输入
            conn.setDoOutput(true);        // 允许输出
            conn.setUseCaches(false);    // 不使用Cache
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);


            out = new DataOutputStream(conn.getOutputStream());

            byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();  // 数据分割线

            StringBuilder sb = new StringBuilder();
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data; name=\"" + file.getName()
                    + "\"; filename=\"" + file.getName() + "\"\r\n");
            sb.append("Content-Type: application/octet-stream \r\n\r\n");

            byte[] data = sb.toString().getBytes();
            out.write(data);
            in = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
//			out.write("\r\n".getBytes()); // 多个文件时，二个文件之间加入这个
//			in.close();

            out.write(end_data);
            out.flush();
//			out.close();


            // 定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

//			Map<String, List<String>> map = conn.getHeaderFields();
//			Set<String> sets = map.keySet();
//			for (String str : sets) {
//				System.out.print(str + ", ");
//				List<String> list2 = (List<String>) map.get(str);
//				for (String s : list2) {
//					System.out.print(s + ", ");
//				}
//				System.out.println();
//			}
//			
//			System.out.println(conn.getHeaderField("Set-Cookie"));

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendForProgress(String addr, String filename, AsyncUploadFileTask task, Context ctx) throws FileNotFoundException {
        File file = new File(filename);
//        sendForProgress(addr, file, task, ctx);
        javaUpload(addr, file, task, ctx);
    }

    private static final String CR_LF = "\r\n";
    private static final String FIELD_SEP = ": ";
    private static final int BUFFER_SIZE = 1024;

    private static void javaUpload(String addr, File file, AsyncUploadFileTask task, Context context) {
        Util.printLog("java##", addr);
        Util.printLog("java##", file.getPath());
        URL url = null;
        try {
            url = new URL(addr + "&filename=" + URLEncoder.encode(file.getName(), "utf-8") + "&size=" + file.length());
        } catch (Exception e) {
            return;
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
//            textViewInfo.setText(e.getMessage());
            return;
        }
        try {
            urlConnection.setRequestMethod("POST");
        } catch (ProtocolException e) {
        }
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY + "; charset=UTF-8");

        String sessionId = context.getSharedPreferences("LKOA-sp",
                Context.MODE_PRIVATE).getString(Constants.SESSIONID, "");
        Util.printLog("sessionId", sessionId);
        urlConnection.setRequestProperty("Cookie", sessionId);


        OutputStream out = null;
        try {
            out = new BufferedOutputStream(urlConnection.getOutputStream());//请求
        } catch (IOException e) {
            urlConnection.disconnect();
//            textViewInfo.setText(e.getMessage());
            return;
        }

        FileInputStream fStream = null;
        StringBuilder form = new StringBuilder();

        form.append("--" + BOUNDARY + CR_LF);
        form.append("Content-Disposition" + FIELD_SEP + "form-data; name=\"name\"" + CR_LF);
        form.append("Content-Type" + FIELD_SEP + "text/plain; charset=UTF-8" + CR_LF);
        form.append("Content-Transfer-Encoding" + FIELD_SEP + "8bit" + CR_LF);
        form.append(CR_LF);
        form.append("testImage抓哇");
        form.append(CR_LF);

        form.append("--" + BOUNDARY + CR_LF);
        form.append("Content-Disposition" + FIELD_SEP + "form-data; name=\"images\";filename=\"test抓哇.jpg\"" + CR_LF);
        form.append("Content-Type" + FIELD_SEP + "image/jpeg; charset=UTF-8" + CR_LF);
        form.append("Content-Transfer-Encoding" + FIELD_SEP + "binary" + CR_LF);
        form.append(CR_LF);

        try {
//            out.write(form.toString().getBytes("UTF-8"));

            fStream = new FileInputStream(file);
            byte[] buffer = new byte[BUFFER_SIZE];
            int length = -1;
            int sendLen = 0;

            while ((length = fStream.read(buffer)) != -1) {
                out.write(buffer, 0, length);
                sendLen = sendLen + length;
                task.updateProgress(sendLen);
            }
            log.info("已发送 " + sendLen);


            out.write(CR_LF.getBytes("UTF-8"));

            out.write(("--" + BOUNDARY + "--" + CR_LF).getBytes("UTF-8"));
            out.flush();
        } catch (IOException e) {
            try {
                out.close();
            } catch (IOException e1) {
            }
            urlConnection.disconnect();
//            textViewInfo.setText(e.getMessage());
            return;
        } finally {
            try {
                if (fStream != null) {
                    fStream.close();
                }
            } catch (IOException e) {
            }
        }
        getResponseJava(urlConnection);
    }

    private static void getResponseJava(HttpURLConnection urlConnection) {
        InputStream in = null;
        try {
            in = new BufferedInputStream(urlConnection.getInputStream());//响应
        } catch (IOException e) {
            urlConnection.disconnect();
//            textViewInfo.setText(e.getMessage());
            return;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
        }
        StringBuilder result = new StringBuilder();
        String tmp = null;
        try {
            while ((tmp = reader.readLine()) != null) {
                Util.printLog("###", tmp);
                result.append(tmp);
            }
            Util.printLog("###", result.toString());

        } catch (IOException e) {
//            textViewInfo.setText(e.getMessage());
            return;
        } finally {
            try {
                reader.close();
                urlConnection.disconnect();
            } catch (IOException e) {
            }
        }

//        webViewResult.loadDataWithBaseURL(null, result.toString(), "text/html", "UTF-8", null);
    }

    /**
     * 发送单个文件
     * 显示实时进度条
     *
     * @param addr
     * @param file
     * @throws FileNotFoundException
     */
    public static void sendForProgress(String addr, File file, AsyncUploadFileTask task, Context context)
            throws
            FileNotFoundException {
        Util.printLog("#####上传地址   =====", addr);

        if (null == addr || "".equals(addr)) {
            throw new NullPointerException(addr + " is null.");
        }
        if (!file.exists()) {
            throw new FileNotFoundException(file.getPath() + " doesnot exist.");
        }

        OutputStream out = null;
        DataInputStream in = null;
        BufferedReader reader = null;
        try {
//			String uri = "http://localhost:8087/webstudy/fileUpload2Svlt";
//			uri = "http://localhost:8087/webstudy/multFileUploadSvlt";
            URL url = new URL(addr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);        // 允许输入
            conn.setDoOutput(true);        // 允许输出
            conn.setUseCaches(false);    // 不使用Cache
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);
            String sessionId = context.getSharedPreferences("LKOA-sp",
                    Context.MODE_PRIVATE).getString(Constants.SESSIONID, "");
            conn.setRequestProperty("Cookie", sessionId);
            sessionId = sessionId.substring(sessionId.indexOf("=") + 1);

            Util.printLog("##########", "sessionid = " + context.getSharedPreferences("LKOA-sp",
                    Context.MODE_PRIVATE).getString(Constants.SESSIONID, ""));
            Util.printLog("##########", "sessionid 00000 = " + conn.getRequestProperty
                    ("LkMobileSrv"));


            String endStr = "\r\n--" + BOUNDARY + "--\r\n";  // 数据分割线
            byte[] end_data = endStr.getBytes();  // 数据分割线

            StringBuilder sb = new StringBuilder();
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data; name=\"" + file.getName()
                    + "\"; filename=\"" + file.getName() + "\"\r\n");
            sb.append("Content-Type: application/octet-stream \r\n\r\n");

            byte[] data = sb.toString().getBytes();
            int fileLen = (int) file.length();
            int sum = sb.toString().length() + fileLen + endStr.length();
            log.info("数据总长度 " + sum);

            conn.setRequestProperty("Content-length", "" + sum);
            conn.setFixedLengthStreamingMode((int) sum);

            out = new DataOutputStream(conn.getOutputStream());
            out.write(data);

            int sendLen = 0;
            in = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);

                sendLen = sendLen + bytes;
                task.updateProgress(sendLen);
            }
            log.info("已发送 " + sendLen);
//			out.write("\r\n".getBytes()); // 多个文件时，二个文件之间加入这个
//			in.close();

            out.write(end_data);
            out.flush();
//			out.close();


            // 定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                log.info("上传结果值 " + line);
            }

//			Map<String, List<String>> map = conn.getHeaderFields();
//			Set<String> sets = map.keySet();
//			for (String str : sets) {
//				System.out.print(str + ", ");
//				List<String> list2 = (List<String>) map.get(str);
//				for (String s : list2) {
//					System.out.print(s + ", ");
//				}
//				System.out.println();
//			}
//			
//			System.out.println(conn.getHeaderField("Set-Cookie"));

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param args
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {

//		send();

        String addr = "http://localhost:8087/webstudy/fileUpload2Svlt";
        String filename = "/home/linger/Pictures/th.jpeg";
        send(addr, filename);

    }

}
