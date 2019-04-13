package com.lkpower.oaandroid.entity;

import com.lkpower.oaandroid.entity.item.ResultItem;


/**
 * 
 * @author linger
 *
 * @since 2015-11-17
 *
 */
public class SessionEntity extends Entity {
	
	private ResultItem result;

	public ResultItem getResult() {
		return result;
	}

	public void setResult(ResultItem result) {
		this.result = result;
	}
	
	@Override
	public String toString() {
		return result.toString();
	}

}






