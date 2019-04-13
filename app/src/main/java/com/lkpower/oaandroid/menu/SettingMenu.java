package com.lkpower.oaandroid.menu;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.lkpower.util.Util;

/**
 * 
 * @author linger
 *
 * @since 2016-2-1
 * 
 * 弹出菜单
 *
 */
public class SettingMenu extends PopupWindow {
	
	private static final String tag = SettingMenu.class.getSimpleName();
	
	private LinearLayout layout;
	private GridView grid;
	private MenuNameAdapter menuNameAdapter;
	private int nameIndex = 0;
	
	private Context ctx;
	
	public SettingMenu(Context ctx, List<String> menuName, OnItemClickListener event, 
			OnDismissListener listener) {
		super(ctx);
		this.ctx = ctx;
		
		//布局框架  
        layout = new LinearLayout(this.ctx);       
        layout.setOrientation(LinearLayout.VERTICAL);  
        layout.setLayoutParams(new LayoutParams(  
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        
        nameIndex = 0;  
        grid = new GridView(ctx);  
        menuNameAdapter = new MenuNameAdapter(ctx, menuName);  
        grid.setAdapter(menuNameAdapter);  
        grid.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        grid.setNumColumns(menuName.size());
        grid.setBackgroundColor(Color.WHITE);
        
//        grid.setOnItemClickListener(new OnItemClickListener() {  
//        	  
//            @Override  
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
//                    long arg3) {
//            	//记录当前选中菜单项序号 
//                nameIndex = arg2;  
//                menuNameAdapter.setFocus(arg2);  
//            }
//        });
        grid.setOnItemClickListener(event);
        
		grid.setFocusableInTouchMode(true);
		grid.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((keyCode == KeyEvent.KEYCODE_MENU) && (isShowing())) {
					Util.printLog(tag, "### 退出菜单");
					// 这里写明模拟menu的PopupWindow退出就行
					dismiss();
					return true;
				}
				return false;
			}
		});
        
        layout.addView(grid);
        
//        this.setTouchInterceptor(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				Util.printLog(tag, "### setTouchInterceptor ");
//				return false;
//			}
//		});
        
        this.setOnDismissListener(listener);
//        this.setOnDismissListener(new OnDismissListener() {
//			
//			@Override
//			public void onDismiss() {
//				Util.printLog(tag, "### setOnDismissListener");
//				
//			}
//		});
        
		// 添加菜单视图
		this.setContentView(layout);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// menu菜单获得焦点 如果没有获得焦点menu菜单中的控件事件无法响应
		this.setFocusable(true);
	}
	
	
	
	/**
	 * 获取当前选中菜单项
	 * 
	 * @return 菜单项序号
	 */
	public int getNameIndex() {
		return nameIndex;
	}

}
