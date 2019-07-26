package com.shenzhen.teamway.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>
 *
 * @author wangc
 * @date 2019/7/18 0018 11:05
 * @Description:实时视频会话管理
 */
public class SipSession implements Serializable, Runnable {
	private Logger log=LoggerFactory.getLogger(getClass());
	private String FromCode;

	private String CSeq;

	private String FromIpPort;

	private String toIpPort;

	private String toDeivceCode;

	private String contactCode;

	private String contactIpPort;

	private String callId;

	public String getCallId() {
		return callId;
	}

	public void setCallId(String callId) {
		this.callId = callId;
	}

	public String getFromCode() {
		return FromCode;
	}

	public void setFromCode(String fromCode) {
		FromCode = fromCode;
	}

	public String getCSeq() {
		return CSeq;
	}

	public void setCSeq(String CSeq) {
		this.CSeq = CSeq;
	}

	public String getFromIpPort() {
		return FromIpPort;
	}

	public void setFromIpPort(String fromIpPort) {
		FromIpPort = fromIpPort;
	}

	public String getToIpPort() {
		return toIpPort;
	}

	public void setToIpPort(String toIpPort) {
		this.toIpPort = toIpPort;
	}

	public String getToDeivceCode() {
		return toDeivceCode;
	}

	public void setToDeivceCode(String toDeivceCode) {
		this.toDeivceCode = toDeivceCode;
	}

	public String getContactCode() {
		return contactCode;
	}

	public void setContactCode(String contactCode) {
		this.contactCode = contactCode;
	}

	public String getContactIpPort() {
		return contactIpPort;
	}

	public void setContactIpPort(String contactIpPort) {
		this.contactIpPort = contactIpPort;
	}


	@Override
	public String toString() {
		return "SipSession{" +
				"FromCode='" + FromCode + '\'' +
				", CSeq='" + CSeq + '\'' +
				", FromIpPort='" + FromIpPort + '\'' +
				", toIpPort='" + toIpPort + '\'' +
				", toDeivceCode='" + toDeivceCode + '\'' +
				", contactCode='" + contactCode + '\'' +
				", contactIpPort='" + contactIpPort + '\'' +
				", callId='" + callId + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SipSession that = (SipSession) o;
		return Objects.equals(log, that.log) &&
				Objects.equals(FromCode, that.FromCode) &&
				Objects.equals(CSeq, that.CSeq) &&
				Objects.equals(FromIpPort, that.FromIpPort) &&
				Objects.equals(toIpPort, that.toIpPort) &&
				Objects.equals(toDeivceCode, that.toDeivceCode) &&
				Objects.equals(contactCode, that.contactCode) &&
				Objects.equals(contactIpPort, that.contactIpPort) &&
				Objects.equals(callId, that.callId);
	}

	@Override
	public int hashCode() {

		return Objects.hash(log, FromCode, CSeq, FromIpPort, toIpPort, toDeivceCode, contactCode, contactIpPort, callId);
	}

	@Override
	public void run() {
		log.info("会话信息："+this.toString());
	}
}
