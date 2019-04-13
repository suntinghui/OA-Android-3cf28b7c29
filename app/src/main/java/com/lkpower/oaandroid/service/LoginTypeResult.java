package com.lkpower.oaandroid.service;

/**
 * 
 * @author linger
 *
 * @since 2015-10-12
 *
 */
public class LoginTypeResult {
	
	private Encrypt result;
	
	@Override
	public String toString() {
		return result.toString();
	}

	public Encrypt getResult() {
		return result;
	}

	public void setResult(Encrypt result) {
		this.result = result;
	}

}

class Encrypt {
	
	private int encrypt;
	
	@Override
	public String toString() {
		return "[ " + encrypt + " ]";
	}

	public int getEncrypt() {
		return encrypt;
	}

	public void setEncrypt(int encrypt) {
		this.encrypt = encrypt;
	}
	
}
