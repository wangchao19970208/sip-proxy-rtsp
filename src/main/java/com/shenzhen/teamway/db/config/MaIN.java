package com.shenzhen.teamway.db.config;

import java.net.*;

/**
 * <p>
 *
 * @author wangc
 * @date 2019/7/8 0008 15:57
 * @Description:
 */
public class MaIN {

	public static void main(String[] args) {

//		String s = "INVITE sip:62010501001310005076@192.168.12.57:5061 SIP/2.0\n" +
//				"Via: SIP/2.0/UDP 192.168.12.166:7060;rport=7060;received=192.168.12.166;branch=z9hG4bK-1FEE6B1D-2A67308B-yHZIWgUu\n" +
//				"From: <sip:11010000002000000001@1101000000>;tag=1419661215\n" +
//				"To: <sip:62010501001310005076@192.168.12.57:5061>\n" +
//				"Call-ID: 2113795424\n" +
//				"CSeq: 20 INVITE\n" +
//				"Contact: <sip:11010000002000000001@192.168.12.166:7060>\n" +
//				"Content-Type: application/sdp\n" +
//				"Max-Forwards: 70\n" +
//				"User-Agent: 28181sdk 1.0\n" +
//				"Subject: 62010501001310005076:1,11010000002000000001:1\n" +
//				"Content-Length: 249\n" +
//				"\n" +
//				"v=0\n" +
//				"o=62010501001310005076 0 0 IN IP4 192.168.12.166\n" +
//				"s=Play\n" +
//				"c=IN IP4 192.168.12.166\n" +
//				"t=0 0\n" +
//				"m=video 31000 TCP/RTP/AVP 96 98 97\n" +
//				"a=recvonly\n" +
//				"a=rtpmap:96 PS/90000\n" +
//				"a=rtpmap:98 H264/90000\n" +
//				"a=rtpmap:97 MPEG4/90000\n" +
//				"a=setup:passive\n" +
//				"a=connection:new\n";
//		String[] split = s.split("\n");
//		for (String s1 : split) {
//			int ip = s1.indexOf("IP4");
//			if (ip != -1) {
//				String substring = s1.substring(ip + 3);
//				System.out.println(substring);
//			}
//		}
//
//
//		RTSPClient client1 = new RTSPClient(new InetSocketAddress("192.168.12.41", 554), new InetSocketAddress("192.168.12.57", 6661), "rtsp://192.168.12.41:554/cam/realmonitor?channel=1&subtype=0&unicast=true&proto=Onvif", null);
//		RTSPClient client2 = new RTSPClient(new InetSocketAddress("192.168.12.40", 554), new InetSocketAddress("192.168.12.57", 6662), "rtsp://192.168.12.40:554/Streaming/Channels/101?transportmode=unicast&profile=Profile_1", null);
//		RTSPClient client3 = new RTSPClient(new InetSocketAddress("192.168.12.42", 554), new InetSocketAddress("192.168.12.57", 6663), "rtsp://192.168.12.42:554/ch01.264", null);
//		RTSPClient client4 = new RTSPClient(new InetSocketAddress("192.168.12.20", 554), new InetSocketAddress("192.168.12.57", 1000), "rtsp://192.168.12.20:554/user=admin_password=tlJwpbo6_channel=1_stream=0.sdp?real_stream", null);

		InetAddress byName = null;
		try {
			byName = InetAddress.getByName("192.168.12.166");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		System.out.println(byName);

// client1.setName("wangchao1");
//		client2.setName("wangchao2");
//		client1.setDeviceCode("wangchao1");
//
//		client2.setDeviceCode("wangchao2");
//		client1.start();
//		client2.start();
//		client3.start();
//		client4.start();


//		String str="1,23,4,55,8,19";
//		String[] split = str.split(",");
//		List<Integer> list=new ArrayList<>();
//		for (int i = 0; i < split.length; i++) {
//              list.add(Integer.parseInt(split[i]));
//		}
//		list.stream().sorted().collect(Collectors.toList()).forEach(System.out::println);
//		int a;
//		List<Integer>newArr=new ArrayList<>();
//		for (int i = 0; i < list.size(); i++) {
//			for (int j = i+1; j <list.size() ; j++) {
//				if (j-1==list.size()){
//					j=list.size();
//				}
//				if (list.get(i)>=list.get(j)){
//                     a=list.get(i);
//                     list.remove(i);
//                     list.add(i,list.get(j));
//                     list.remove(j);
//                     list.add(j,a);
//
//				}
//			}
//		}
//		list.forEach(System.out::println);
	}
}
