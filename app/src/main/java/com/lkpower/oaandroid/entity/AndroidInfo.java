package com.lkpower.oaandroid.entity;

/**
 * 
 * @author linger
 *
 * @since 2015-12-2
 *
 */
public class AndroidInfo {
	
	private String deviceId;
//	private String androidVer;
	private String version;
	private String jPushId;
	
	@Override
	public String toString() {
		return "[ " + deviceId + ", " + version + ", " + jPushId + " ]";
	}

	public String getjPushId() {
		return jPushId;
	}

	public void setjPushId(String jPushId) {
		this.jPushId = jPushId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
