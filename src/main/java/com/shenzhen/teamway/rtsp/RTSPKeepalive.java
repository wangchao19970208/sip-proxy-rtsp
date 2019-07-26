package com.shenzhen.teamway.rtsp;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.TimerTask;

/**
 * <p>
 *
 * @author wangc
 * @date 2019/7/5 0005 15:24
 * @Description:
 */
public class RTSPKeepalive extends TimerTask {
	private String keepaliveContent;
	int CSeq = 4;
	private Logger log=LoggerFactory.getLogger(getClass());

	private RTSPClient client;

	public RTSPKeepalive(RTSPClient client, String keepaliveContent) {
		this.keepaliveContent = keepaliveContent;
		this.client = client;
	}


	@Override
	public void run() {
		StringBuilder builder = new StringBuilder();
		CSeq++;
		builder.append("GET_PARAMETER "+keepaliveContent);
		builder.append("CSeq: " + CSeq);
		builder.append("\r\n\r\n");
		client.send(builder.toString().getBytes());
		log.info("\r\n发送心跳:\r\n" + builder.toString());
	}
}
