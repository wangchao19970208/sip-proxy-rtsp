package com.shenzhen.teamway.rtsp;


import com.shenzhen.teamway.sip.SipProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author wangc
 * @date 2019/7/5 0005 09:40
 * @Description:实现客户端套接字UDP
 */
public class RTPServer extends Thread {
    private Logger log=LoggerFactory.getLogger(getClass());

	private DatagramSocket ds;

	private String deviceCode;

	private  int port=3306;

	private  String address;

	private  SipProxyServer sipProxyServer;


	public RTPServer(String deviceCode,String address,SipProxyServer sipProxyServer){
	 	 this.address=address;
	 	 this.deviceCode=deviceCode;
	 	 this.sipProxyServer=sipProxyServer;

	}


	@Override
	public void run() {
		try {
			// 确定数据报接受的数据的数组大小
			byte[] buf = new byte[1500];
			while (true) {
				// 创建接受类型的数据报，数据将存储在buf中
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				ds.receive(dp);
				log.info("deviceCode:"+deviceCode+",rtsp流收到大小："+dp.getLength());
				sipProxyServer.sendData(buf,deviceCode);
			}
		} catch (IOException e) {
			e.printStackTrace();
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
				log.info("RTP包接收端口：" + port);
				break;
			} catch (IOException e) {
				log.info("RTP包接收端口占用:" + port);
				port += 2;
				continue;
			}
		}
		return port;
	}
}
