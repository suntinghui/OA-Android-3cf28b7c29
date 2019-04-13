package com.lkpower.oaandroid.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author linger
 *
 * @since 2016-1-11
 * 
 * JPush 推送返回的 Extra 数据 
 *
 */
public class JPushExtraEntity extends Entity {
	
	private int count;
	private String startDate;
	private String title;
	private String url;
	@SerializedName("class")
	private String clazz;
	private String content;
	private String expriseData;
	private String type;
	private String id;
	
	@Override
	public String toString() {
		return super.toString() + "[ " + count + ", " + startDate + ", " 
				+ title + ", " + url + ", " + clazz + ", " + content + ", " 
				+ expriseData + ", " + type + ", " + id + " ]";
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getExpriseData() {
		return expriseData;
	}

	public void setExpriseData(String expriseData) {
		this.expriseData = expriseData;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
