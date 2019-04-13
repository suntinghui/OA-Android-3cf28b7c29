package com.lkpower.oaandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lkpower.oaandroid.R;
import com.lkpower.oaandroid.task.AsyncUploadFileTask;
import com.lkpower.util.Util;

/**
 * 
 * @author linger
 *
 * @since 2015-9-23
 *
 */
public class RealProgressDialog extends Dialog {
	
	private static final String tag = RealProgressDialog.class.getSimpleName();

	private int len;
	private int start;
	
	
	public RealProgressDialog(Context context, int len, int start) {
		super(context, R.style.define_dialog_2);
		
		this.setTitle("上传文件");
		this.setCancelable(false);
		this.len = len;
		this.start = start;
	}
	
	private TextView numberProgress;
	private ProgressBar realProgressBar;
	private Button cancel;
	
	private AsyncUploadFileTask task;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_real_progress_layout);
		
		numberProgress = (TextView) findViewById(R.id.numberProgress);
		numberProgress.setText("0%");
		realProgressBar = (ProgressBar) findViewById(R.id.dialogRealProgressBarh);
		realProgressBar.setMax(len);
		realProgressBar.setProgress(start);
		
		cancel = (Button) findViewById(R.id.cancelProgress);
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Util.printLog(tag, "取消上传");
				boolean result = task.cancel(true);
				Util.printLog(tag, "取消上传结果 " + result);
				
				dismiss();
			}
		});
	}

	public AsyncUploadFileTask getTask() {
		return task;
	}

	public void setTask(AsyncUploadFileTask task) {
		this.task = task;
	}

	public ProgressBar getRealProgressBar() {
		return realProgressBar;
	}

	public void setRealProgressBar(ProgressBar realProgressBar) {
		this.realProgressBar = realProgressBar;
	}

	public TextView getNumberProgress() {
		return numberProgress;
	}

	public void setNumberProgress(TextView numberProgress) {
		this.numberProgress = numberProgress;
	}

}
