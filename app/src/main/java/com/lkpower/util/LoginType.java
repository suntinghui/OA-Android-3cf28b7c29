package com.lkpower.util;

/**
 * 
 * @author linger
 *
 * @since 2015-10-12
 *
 */
public enum LoginType {
	
	/**
	 * 登录密码使用md5方式
	 */
	LOGIND_MD5_ON(0),
	
	/**
	 * 登录密码使用明码
	 */
	LOGIND_PASSWD(1);
	
	private int value;
	
	LoginType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	

}
