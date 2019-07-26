package com.shenzhen.teamway.model;

import java.io.Serializable;

/**
 * <p>
 *
 * @author wangc
 * @date 2019/7/18 0018 17:29
 * @Description:
 */
public class SipSever  implements Serializable {
	private String deviceCode;

	private String password;

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
