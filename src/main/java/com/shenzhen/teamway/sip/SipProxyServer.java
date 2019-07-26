package com.shenzhen.teamway.sip;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author wangc
 * @date 2019/7/23 0023 16:08
 * @Description:
 */
public class SipProxyServer  {
	private Logger log = LoggerFactory.getLogger(getClass());

	private  int port=3000;

	private  String address;

	private  DatagramSocket ds;

	private  String remoteAddr;

	private  int remotePort;

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public SipProxyServer(String address) {
		this.address = address;
	}


	public void sendData(byte[]bytes,String deviceCode){
		try {
			DatagramPacket dp = new DatagramPacket(bytes, bytes.length,InetAddress.getByName("slave35"),remotePort);
			ds.send(dp);
			log.info("sip代理发送RTP包成功,RTP包大小为:"+dp.getLength());
		} catch (IOException e) {
			log.error("sip代理发送RTP包失败,设备为"+deviceCode);
		}
	}

	/**
	 * 返回一个可用的UDP端口
	 * @return
	 */
   public int getPort(){
	   while (true) {
		   try {
			   if (port % 2 == 0) {
				   ds = new DatagramSocket(new InetSocketAddress(address, port));
			   }
			   log.info("启用SipProxy发送端口：" + port);
			   break;
		   } catch (IOException e) {
			   log.info("启用SipProxy发送端口占用：" + port);
			   port += 2;
		   }
	   }
	   return port;
   }

}
