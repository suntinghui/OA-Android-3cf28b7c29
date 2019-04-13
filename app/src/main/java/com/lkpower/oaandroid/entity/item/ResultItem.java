package com.lkpower.oaandroid.entity.item;

public class ResultItem {

	private String result;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(result);

		return sb.toString();
	}

}
