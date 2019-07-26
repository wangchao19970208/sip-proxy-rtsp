package com.shenzhen.teamway.sip.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * <p>
 *
 * @author wangc
 * @date 2019/6/27 0027 11:53
 * @Description:
 */
public class MD5Utils {
	/**
	 * MD5方法
	 *
	 * @param text 明文
	 * @param key 密钥
	 * @return 密文
	 * @throws Exception
	 */
	public static String md5(String text, String key) {
		//加密后的字符串
		String encodeStr=DigestUtils.md5Hex(text + key);
		return encodeStr;
	}

	/**
	 * MD5验证方法
	 *
	 * @param text 明文
	 * @param key 密钥
	 * @param md5 密文
	 * @return true/false
	 * @throws Exception
	 */
	public static boolean verify(String text, String key, String md5) throws Exception {
		//根据传入的密钥进行验证
		String md5Text = md5(text, key);
		if(md5Text.equalsIgnoreCase(md5)) {
			System.out.println("MD5验证通过");
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		String a="RTSP/1.0 200 OK\n" +
				"CSeq: 3\n" +
				"Server: Ants Rtsp Server/1.0\n" +
				"Date: 05 Jul 2019 14:22:29 GMT\n" +
				"SipSession: 1561866763; timeout=60\n" +
				"Transport: RTP/AVP;unicast;client_port=58268-58269;server_port=6970-6971;ssrc=B4E70C96\n" +
				"\n";
		int indexPrefix=a.indexOf("SipSession");
		int indexSufix=a.indexOf("; timeout");

		System.out.println(a.substring(indexPrefix+9,indexSufix));


	}
}
