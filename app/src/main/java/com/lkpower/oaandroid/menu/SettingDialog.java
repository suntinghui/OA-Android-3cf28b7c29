package com.lkpower.oaandroid.menu;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.GridView;

import com.lkpower.oaandroid.R;
import com.lkpower.util.Util;

/**
 * 
 * @author lkimac
 *
 * @since 2016-3-7
 * 
 * 自定义设置
 *
 */
public class SettingDialog extends Dialog {
	
	private static final String tag = SettingDialog.class.getSimpleName();
	
	private Context ctx;
	private List<String> menuName;
	private OnItemClickListener event;
	
	private Button cancel;
	private GridView menuGridView;
	private MenuNameAdapter menuNameAdapter;

	public SettingDialog(Context context, List<String> menuName, OnItemClickListener event) {
		super(context, R.style.set_dialog);
		
		this.ctx      = context;
		this.menuName = menuName;
		this.event    = event;
//		super(context);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_set_dialog);
		
	    cancel = (Button) findViewById(R.id.dialog_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Util.printLog(tag, "dialog dismiss .");
				dismiss();
			}
		});
		
		menuGridView = (GridView) findViewById(R.id.dialog_gridView);
		Util.printLog(tag, "### space " + menuGridView.getVerticalSpacing());
//		menuGridView.setVerticalSpacing(10);
		menuNameAdapter = new MenuNameAdapter(ctx, menuName);
		menuGridView.setAdapter(menuNameAdapter);
		menuGridView.setOnItemClickListener(event);
		menuGridView.setFocusableInTouchMode(true);
		menuGridView.setBackgroundColor(Color.WHITE);
//		menuGridView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
	}

}
