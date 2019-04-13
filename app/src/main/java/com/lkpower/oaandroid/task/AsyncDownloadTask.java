package com.lkpower.oaandroid.task;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lkpower.oaandroid.MainActivity;
import com.lkpower.oaandroid.service.Update;
import com.lkpower.util.Util;

/**
 * 下载
 * 
 * @author linger
 *
 * @since 2015-8-12
 *
 */
public class AsyncDownloadTask extends AsyncTask<String, String, String> {
	
	private static final String TAG = AsyncDownloadTask.class.getSimpleName();
	
	private ProgressBar pb;
	private MainActivity context;
	private Update update;
	private boolean downloadFlag = false;
	
	public AsyncDownloadTask(MainActivity context, Update update, ProgressBar pb) {
		this.pb = pb;
		this.update = update;
		this.context = context;
		
		pb.setVisibility(View.VISIBLE);
	}

	@Override
	protected String doInBackground(String... params) {
		Util.printLog(TAG, "photo doInBackground ...");
		downloadFlag = update.download();
		
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Util.printLog(TAG, "photo onPostExecute ...");
		
		pb.setVisibility(View.GONE);
		
		// 下载成功
		if (downloadFlag) {
			Util.printLog(TAG, "下载成功");
//			LayoutInflater inflater = context.getLayoutInflater();
//			View layout = inflater.inflate(R.layout.dialog_update, null);
//			final ProgressBar progressDownload = (ProgressBar) layout.findViewById(R.id.progressDownload);
			
			AlertDialog.Builder b2 = new AlertDialog.Builder(context)
					.setTitle("提示")
//					.setView(layout)
					.setMessage("下载成功是否安装")
					.setPositiveButton("安装", new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Util.printLog(TAG, "安装");
							update.install();
						}
					});
			b2.setCancelable(false);
			b2.setNegativeButton("以后安装", null);
			b2.create();
			b2.show();
		}
		else {
			Toast.makeText(context, "下载失败请重试", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		Util.printLog(TAG, "photo onProgressUpdate ...");
	}

}
