package com.lkpower.oaandroid.task;

import java.util.List;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lkpower.http.Http;
import com.lkpower.oaandroid.MainActivity;
import com.lkpower.oaandroid.service.LoginTypeResult;
import com.lkpower.util.Constants;
import com.lkpower.util.Util;

/**
 * 获取登录配置
 * 
 * @author linger
 *
 * @since 2015-10-12
 *
 */
public class AsyncLoginTypeTask extends AsyncTask<String, String, String> {
	
	private static final String TAG = AsyncLoginTypeTask.class.getSimpleName();
	
	private String addr;
	private MainActivity context;
	
	public AsyncLoginTypeTask(MainActivity context, String addr) {
		this.addr = addr;
		this.context = context;
	}

	@Override
	protected String doInBackground(String... params) {
		Util.printLog(TAG, "photo doInBackground ...");
		String value = Http.getStrContent(addr);
		Util.printLog(TAG, value);
		
		if (null != value && !Constants.EMPTY.equals(value)) {
			Gson gson = new Gson();
			try {
				LoginTypeResult type = gson.fromJson(value, new TypeToken<List<LoginTypeResult>>(){}.getType());
				Util.printLog(TAG, type.toString());
				
				
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Util.printLog(TAG, "photo onPostExecute ...");
		
		// 服务器版本高于当前安装的版本
//		if (updateFlag == Update.Result.SUCCESS) {
//			Util.printLog(TAG, "需要更新");
////			LayoutInflater inflater = context.getLayoutInflater();
////			View layout = inflater.inflate(R.layout.dialog_update, null);
////			final ProgressBar progressDownload = (ProgressBar) layout.findViewById(R.id.progressDownload);
//			
//			AlertDialog dialog;
//			AlertDialog.Builder b = new AlertDialog.Builder(context)
//					.setTitle("提示")
////					.setView(layout)
//					.setMessage("有新版本是否更新")
//					.setPositiveButton("更新", new AlertDialog.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							Util.printLog(TAG, "下载");
//							
//							SharedPreferences sp = context.getSharedPreferences();
//							Editor editor = sp.edit();
//							editor.putInt(Constants.UPDATE_FLAG, Constants.UPDATE_FLAG_TRUE);
//							editor.commit();
//							
//							if (Util.isNetAvailable(context)) {
//								AsyncDownloadTask task = new AsyncDownloadTask(context, update, pb);
//								task.execute();
//							}
//							else {
//								Toast.makeText(context, "网络不通", Toast.LENGTH_SHORT).show();
//							}
//							
//							dialog.dismiss();
//						}
//					});
//			b.setCancelable(false);
//			b.setNegativeButton("以后更新", new AlertDialog.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					Util.printLog(TAG, "以后更新");
//					
//					SharedPreferences sp = context.getSharedPreferences();
//					Editor editor = sp.edit();
//					editor.putInt(Constants.UPDATE_FLAG, Constants.UPDATE_FLAG_TRUE);
//					editor.commit();
//				}
//			});
//			b.create();
//			dialog = b.show();
//		}
//		else if (updateFlag == Update.Result.NET_ERROR) {
//			Toast.makeText(context, "网络不通，请稍后重试", Toast.LENGTH_SHORT).show();
//		}
//		else {
////			Toast.makeText(context, "已是最新版本", Toast.LENGTH_SHORT).show();
//		}
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		Util.printLog(TAG, "photo onProgressUpdate ...");
	}

}
