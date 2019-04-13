package com.lkpower.sign;

/**
 * 
 * @author linger
 *
 * @since 2015-6-16
 *
 */
public class SignCore {
	
	static {
		System.loadLibrary("lkoa");
	}
	
	private SignCore() {}
	
	private static final class SignCoreInner {
		private static final SignCore instance = new SignCore();
	}
	
	public static SignCore getInstance() {
		return SignCoreInner.instance;
	}
	
	/**
	 * 签章
	 * @param prikey
	 * @param digest
	 * @return
	 */
	public native String sign(String prikey, String digest, String passwd);
	
	/**
	 * 验章
	 * @param pubkey
	 * @param digest
	 * @param sign
	 * @return
	 */
	public native int verify(String pubkey, String digest, String sign);
	

}
