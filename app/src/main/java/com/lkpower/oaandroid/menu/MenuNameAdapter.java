package com.lkpower.oaandroid.menu;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lkpower.util.Util;

public class MenuNameAdapter extends BaseAdapter {
	
	private static final String tag = MenuNameAdapter.class.getSimpleName();

	private List<String> menuName;
	private Context context;
	private TextView[] menu;

	public MenuNameAdapter(Context context, List<String> menuName) {
		this.context = context;
		this.menuName = menuName;
		menu = new TextView[menuName.size()];
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return menuName.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/**
	 * 选中后，改变菜单颜色。
	 * 
	 * @param position
	 */
	public void setFocus(int position) {
//		for (int i = 0; i < menuName.size(); i++) {
////			menu[i].setBackgroundColor(Color.WHITE);
//			menu[i].setBackgroundResource(R.drawable.shape_dialog_menu);
//		}
////		menu[position].setBackgroundColor(Color.BLUE);
//		menu[position].setBackgroundResource(R.drawable.shape_dialog_menu_selected);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 菜单栏文字项
		menu[position] = new TextView(context);
		menu[position].setGravity(Gravity.CENTER);
		menu[position].setText(menuName.get(position));
		menu[position].setTextSize(18);
		menu[position].setTextColor(Color.BLACK);
//		menu[position].setBackgroundResource(R.drawable.shape_dialog_menu);
//		menu[position].setLayoutParams(new GridView.LayoutParams(
//				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//		menu[position].setHeight(40);
		
		Util.printLog(tag, "### Height " + menu[position].getLineCount() + ", " + menu[position].getLineHeight());
		int height_in_pixels = 2 * menu[position].getLineHeight(); //approx height text
		menu[position].setHeight(height_in_pixels);

		return menu[position];
	}

}
