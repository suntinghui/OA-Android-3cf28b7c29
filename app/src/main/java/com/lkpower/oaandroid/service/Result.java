package com.lkpower.oaandroid.service;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author linger
 *
 * @since 2015-8-17
 *
 */
public class Result {
	
//	private String result;
//	private List<NoticePush> list;
	private ResultCount result;

	public ResultCount getResult() {
		return result;
	}

	public void setResult(ResultCount result) {
		this.result = result;
	}
	
	@Override
	public String toString() {
		return result.toString();
	}

}

class ResultCount {
	
	private List<NoticePush> result;

	public List<NoticePush> getResult() {
		return result;
	}

	public void setResult(List<NoticePush> result) {
		this.result = result;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\n{\n");
		for (NoticePush np : result) {
			sb.append(np.toString()).append("\n");
		}
		sb.append("}");
		
		return sb.toString();
	}
	
}

class NoticePush {
	
	private String id;
	private String title;
	private String content;
	private String startDate;	
	@SerializedName("expriseData")
	private String expriseDate;	
	private String type;
	@SerializedName("class")
	private String clazz;
	
	public NoticePush() {}
	
	@Override
	public String toString() {
		return "[" + this.id + ", " + this.title + ", " + this.content + ", " 
				+ this.startDate + ", " + this.expriseDate + ", " + this.type 
				+ ", " + this.clazz + "]";
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getExpriseDate() {
		return expriseDate;
	}

	public void setExpriseDate(String expriseDate) {
		this.expriseDate = expriseDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}


