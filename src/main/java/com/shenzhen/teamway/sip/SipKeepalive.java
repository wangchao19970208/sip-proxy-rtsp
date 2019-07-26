package com.shenzhen.teamway.sip;



import com.shenzhen.teamway.model.Camera;

import javax.sip.message.Request;
import java.util.List;
import java.util.TimerTask;

/**
 * <p>
 *
 * @author wangc
 * @date 2019/7/1 0001 10:58
 * @Description:
 */
public class SipKeepalive extends TimerTask {

	int count;
	@Override
	public void run() {
		JainSipClient instance = JainSipClient.getInstance();
		List<Camera> cameras = ClientListener.cameras;
		for (Camera camera : cameras) {
			count++;
			instance.sendMessageVideo(ClientListener.getFromName(),ClientListener.getFromIpPort(),ClientListener.getToName(),ClientListener.getToIpPort(),"<?xml version=\"1.0\"?>\n" +
					"<Notify>\n" +
					"<CmdType>Keepalive</CmdType>\n" +
					"<SN>"+count+"</SN>\n" +
					"<DeviceID>"+camera.getDeviceCode()+"</DeviceID>\n" +
					"<Status>OK</Status>\n" +
					"</Notify>\n",Request.MESSAGE,Request.MESSAGE,"application","Xml",5L);
		}

	}
}
