package com.lkpower.oaandroid.task;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lkpower.oaandroid.MainActivity;
import com.lkpower.oaandroid.service.Update;
import com.lkpower.oaandroid.service.UpdateCheckType;
import com.lkpower.util.Constants;
import com.lkpower.util.SharedPreferencesUtils;
import com.lkpower.util.Util;

/**
 * 检查更新
 * 
 * @author linger
 *
 * @since 2015-8-12
 *
 */
public class AsyncCheckUpdateTask extends AsyncTask<String, String, String> {
	
	private static final String TAG = AsyncCheckUpdateTask.class.getSimpleName();
	
	private ProgressBar pb;
	private String addr;
	private MainActivity context;
	private UpdateCheckType type; // = UpdateCheckType.Auto;
	
	private Update update;
	private Update.Result updateFlag; 
	
	public AsyncCheckUpdateTask(MainActivity context, String addr, ProgressBar pb, UpdateCheckType type) {
		this.pb = pb;
		this.addr = addr;
		this.context = context;
		this.type = type;
		
		if (type == UpdateCheckType.Manual) {
			pb.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected String doInBackground(String... params) {
		Util.printLog(TAG, "photo doInBackground ...");
		update = new Update(context, addr);
		updateFlag = update.isUpdate();
		
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Util.printLog(TAG, "photo onPostExecute ...");
		
		if (type == UpdateCheckType.Manual) {
			pb.setVisibility(View.GONE);
		}
		
		// 服务器版本高于当前安装的版本
		if (updateFlag == Update.Result.SUCCESS) {
			Util.printLog(TAG, "需要更新");
//			LayoutInflater inflater = context.getLayoutInflater();
//			View layout = inflater.inflate(R.layout.dialog_update, null);
//			final ProgressBar progressDownload = (ProgressBar) layout.findViewById(R.id.progressDownload);
			
			AlertDialog dialog;
			AlertDialog.Builder b = new AlertDialog.Builder(context)
					.setTitle("提示")
//					.setView(layout)
					.setMessage("有新版本是否更新")
					.setPositiveButton("更新", new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Util.printLog(TAG, "下载");
							
							SharedPreferencesUtils.saveInt(Constants.UPDATE_FLAG, Constants.UPDATE_FLAG_TRUE);
							if (Util.isNetAvailable(context)) {
								AsyncDownloadTask task = new AsyncDownloadTask(context, update, pb);
								task.execute();
							}
							else {
								Toast.makeText(context, "网络不通", Toast.LENGTH_SHORT).show();
							}
							
							dialog.dismiss();
						}
					});
			b.setCancelable(false);
			b.setNegativeButton("以后更新", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Util.printLog(TAG, "以后更新");
					SharedPreferencesUtils.saveInt(Constants.UPDATE_FLAG, Constants.UPDATE_FLAG_TRUE);
				}
			});
			b.create();
			dialog = b.show();
		}
		else if (updateFlag == Update.Result.NET_ERROR) {
//			Toast.makeText(context, "网络不通，请稍后重试", Toast.LENGTH_SHORT).show();
		}
		else {
			if (type == UpdateCheckType.Manual) {
				Toast.makeText(context, "已是最新版本", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		Util.printLog(TAG, "photo onProgressUpdate ...");
	}

}
