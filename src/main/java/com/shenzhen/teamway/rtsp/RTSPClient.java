package com.shenzhen.teamway.rtsp;


import com.shenzhen.teamway.db.config.ConfigManager;
import com.shenzhen.teamway.sip.SipProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 *
 * @author wangc
 * @date 2019/7/4 0004 16:15
 * @Description:
 */
public class RTSPClient extends Thread implements IEvent {

	//心跳内容
	private String keepaliveContent;

	private String streamType;

	private Logger log = LoggerFactory.getLogger(RTSPClient.class);

	private static final String VERSION = " RTSP/1.0\r\n";
	private static final String RTSP_OK = "RTSP/1.0 200 OK";

	private String deviceCode;


	/** */
	/**
	 * 远程地址
	 */
	private final InetSocketAddress remoteAddress;

	/** */
	/**
	 * 本地地址
	 */
	private final InetSocketAddress localAddress;

	/** */
	/**
	 * 连接通道
	 */
	private SocketChannel socketChannel;

	/** */
	/**
	 * 发送缓冲区
	 */
	private final ByteBuffer sendBuf;

	/** */
	/**
	 * 接收缓冲区
	 */
	private final ByteBuffer receiveBuf;

	private static final int BUFFER_SIZE = 8192;

	/** */
	/**
	 * 端口选择器
	 */
	private Selector selector;

	private String address;

	private Status sysStatus;

	private String sessionid;

	/** */
	/**
	 * 线程是否结束的标志
	 */
	private AtomicBoolean shutdown;

	private int seq = 1;

	private boolean isSended;
	//视频流接收端口  1025到65535
	private int dstPort = 3306;

	private String trackInfo;
	//RTP发送
	private SipProxyServer sipProxyServer;

	private  String ctype;

	private enum Status {
		init, options, describe, setup, play, pause, teardown
	}

	public RTSPClient(InetSocketAddress remoteAddress,
	                  InetSocketAddress localAddress, String address,SipProxyServer sipProxyServer) {
		this.remoteAddress = remoteAddress;
		this.localAddress = localAddress;
		this.address = address;
		this.sipProxyServer=sipProxyServer;

		// 初始化缓冲区
		sendBuf = ByteBuffer.allocateDirect(BUFFER_SIZE);
		receiveBuf = ByteBuffer.allocateDirect(BUFFER_SIZE);
		if (selector == null) {
			// 创建新的Selector
			try {
				selector = Selector.open();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		startup();
		sysStatus = Status.init;
		shutdown = new AtomicBoolean(false);
		isSended = false;
	}

	public void startup() {
		try {
			// 打开通道
			socketChannel = SocketChannel.open();
			// 绑定到本地端口
			socketChannel.socket().setSoTimeout(30000);
			socketChannel.configureBlocking(false);
			socketChannel.socket().bind(localAddress);
			if (socketChannel.connect(remoteAddress)) {
				log.info("开始建立连接:" + remoteAddress);
			}
			socketChannel.register(selector, SelectionKey.OP_CONNECT
					| SelectionKey.OP_READ | SelectionKey.OP_WRITE, this);
			log.info("端口打开成功");

		} catch (final IOException e1) {
			e1.printStackTrace();
		}

	}

	public void send(byte[] out) {
		if (out == null || out.length < 1) {
			return;
		}
		synchronized (sendBuf) {
			sendBuf.clear();
			sendBuf.put(out);
			sendBuf.flip();
		}

		// 发送出去
		try {
			write();
			isSended = true;
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void write() throws IOException {
		if (isConnected()) {
			try {
				socketChannel.write(sendBuf);
			} catch (final IOException e) {
			}
		} else {
			log.info("通道为空或者没有连接上");
		}
	}

	public byte[] recieve() {
		if (isConnected()) {
			try {
				int len = 0;
				int readBytes = 0;

				synchronized (receiveBuf) {
					receiveBuf.clear();
					try {
						while ((len = socketChannel.read(receiveBuf)) > 0) {
							readBytes += len;
						}
					} catch (SocketException e) {
						e.printStackTrace();
					} finally {
						receiveBuf.flip();
					}
					if (readBytes > 0) {
						final byte[] tmp = new byte[readBytes];
						receiveBuf.get(tmp);
						return tmp;
					} else {
						log.info("接收到数据为空,重新启动连接");
						return null;
					}
				}
			} catch (final IOException e) {
				e.printStackTrace();
				log.error("接收消息错误:" + e.getMessage());
			}
		} else {
			log.info("端口没有连接");
		}
		return null;
	}

	public boolean isConnected() {
		return socketChannel != null && socketChannel.isConnected();
	}

	private void select() {
		int n = 0;
		try {
			if (selector == null) {
				return;
			}
			n = selector.select(1000);

		} catch (final Exception e) {
			e.printStackTrace();
		}

		// 如果select返回大于0，处理事件
		if (n > 0) {
			for (final Iterator<SelectionKey> i = selector.selectedKeys()
					.iterator(); i.hasNext(); ) {
				// 得到下一个Key
				final SelectionKey sk = i.next();
				i.remove();
				// 检查其是否还有效
				if (!sk.isValid()) {
					continue;
				}

				// 处理事件
				final IEvent handler = (IEvent) sk.attachment();
				try {
					if (sk.isConnectable()) {
						handler.connect(sk);
					} else if (sk.isReadable()) {
						handler.read(sk);
					} else {
						// System.err.println("Ooops");
					}
				} catch (final Exception e) {
					e.printStackTrace();
					handler.error(e);
					sk.cancel();
				}
			}
		}
	}

	public void shutdown() {
		if (isConnected()) {
			try {
				socketChannel.close();
				log.info("端口关闭成功");
			} catch (final IOException e) {
				log.error("端口关闭错误:");
			} finally {
				socketChannel = null;
			}
		} else {
			log.info("通道为空或者没有连接");
		}
	}

	@Override
	public void run() {
		// 启动主循环流程
		while (!shutdown.get()) {
			try {
				if (isConnected() && (!isSended)) {
					switch (sysStatus) {
						case init:
							doOption();
							break;
						case options:
							doDescribe();
							break;
						case describe:
							doSetup();
							break;
						case setup:
							if (sessionid == null && sessionid.length() > 0) {
								log.info("setup还没有正常返回");
							} else {
								doPlay();
							}
							break;
						case play:
							break;
						case pause:
//							doTeardown();
							doPause();
							break;
						default:
							break;
					}
				}
				// do select
				select();
				try {
					Thread.sleep(1000);
				} catch (final Exception e) {
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		shutdown();
	}

	public void connect(SelectionKey key) throws IOException {
		if (isConnected()) {
			return;
		}
		// 完成SocketChannel的连接
		socketChannel.finishConnect();
		while (!socketChannel.isConnected()) {
			try {
				Thread.sleep(300);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			socketChannel.finishConnect();
		}

	}

	public void error(Exception e) {
		e.printStackTrace();
	}

	public void read(SelectionKey key) throws IOException {
		// 接收消息
		final byte[] msg = recieve();
		if (msg != null) {
			handle(msg);
		} else {
			key.cancel();
		}
	}


	private void handle(byte[] msg) {

		String tmp = new String(msg);

		log.info("\r\n返回内容：\r\n" + tmp);
		if (tmp.startsWith(RTSP_OK)) {
			switch (sysStatus) {
				case init:
					sysStatus = Status.options;
					break;
				case options:
					String[] split1 = tmp.split("\r\n");
					String substring="";
					for (String s : split1) {
						if (s.indexOf("trackID=")!=-1){
							substring = s.substring(s.indexOf("trackID=") + 8);
							break;
						}
//						else if (s.indexOf("a=control")!=-1) {
//							String[] split = s.split(":");
//							for (String s1 : split) {
//								if (s1.equals("ctype=video")) {
//									ctype=s1;
//								}
//							}
//						}
					}
					trackInfo=substring;
					sysStatus = Status.describe;
					//启动监听视屏流端口
					RTPServer instance = new RTPServer(getDeviceCode(),localAddress.getAddress().getHostAddress(),sipProxyServer);
					dstPort = instance.getPort();
					instance.start();
					streamType=tmp.substring(tmp.indexOf("RTP/"),tmp.indexOf("RTP/")+7);
					break;
				case describe:
					sessionid = tmp.substring(tmp.indexOf("Session") + 9, tmp
							.indexOf(";"));
					if (sessionid != null && sessionid.length() > 0) {
						sysStatus = Status.setup;
					}
					break;
				case setup:
					//发送RTSP心跳
					ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
					RTSPKeepalive rtspKeepalive = new RTSPKeepalive(this, this.address + VERSION);
					scheduledExecutorService.scheduleAtFixedRate(rtspKeepalive, 0, 20, TimeUnit.SECONDS);
					sysStatus = Status.play;
					break;
//				case play:
//					sysStatus = Status.pause;
//					break;
//				case pause:
//					sysStatus = Status.teardown;
//					shutdown.set(true);
//					break;
//				case teardown:
//					sysStatus = Status.init;
//					break;
				default:
					break;
			}
			isSended = false;
		} else {
			log.error("\n返回错误:\n" + tmp);
		}

	}

	private void doTeardown() {
		StringBuilder sb = new StringBuilder();
		sb.append("TEARDOWN ");
		sb.append(this.address);
		sb.append("/");
		sb.append(VERSION);
		sb.append("Cseq: ");
		sb.append(seq++);
		sb.append("\r\n");
		sb.append("User-Agent: RealMedia Player HelixDNAClient/10.0.0.11279 (win32)\r\n");
		sb.append("SipSession: ");
		sb.append(sessionid);
		sb.append("\r\n");
		send(sb.toString().getBytes());
		log.info("\r\n" + sb.toString());
	}

	private void doPlay() {
		StringBuilder sb = new StringBuilder();
		sb.append("PLAY ");
		sb.append(this.address);
		sb.append("/");
		sb.append(VERSION);
		sb.append("Cseq: ");
		sb.append(seq++);
		sb.append("\r\n");
		sb.append("User-Agent: LibVLC/3.0.7.1 (LIVE555 Streaming Media v2016.11.28)");
		sb.append("\r\n");
		sb.append("Session: ");
		sb.append(sessionid);
		sb.append("\r\n");
		sb.append("Range: npt=0.000-");
		sb.append("\r\n");
		sb.append("\r\n");
		log.info("\r\n" + sb.toString());
		send(sb.toString().getBytes());

	}

	private void doSetup() {
		StringBuilder sb = new StringBuilder();
		sb.append("SETUP ");
		if (trackInfo==null){
			sb.append(this.address+"/ctype=video");
		}else{
			sb.append(this.address+"/trackID="+trackInfo);
		}
		//	sb.append("/");
//		sb.append(trackInfo);
		sb.append(VERSION);
		sb.append("CSeq: ");
		sb.append(seq++);
		sb.append("\r\n");
		sb.append("User-Agent: LibVLC/3.0.7.1 (LIVE555 Streaming Media v2016.11.28)");
		sb.append("\r\n");
		// RTP/AVP/TCP;unicast;interleaved=0-1
		sb.append("Transport: "+streamType+";unicast;client_port=" + dstPort + "-" + (dstPort + 1) + "\r\n");
		sb.append("\r\n");
		log.info("\r\n" + sb.toString());
		send(sb.toString().getBytes());
	}

	private void doOption() {
		StringBuilder sb = new StringBuilder();
		sb.append("OPTIONS ");
		sb.append(this.address);
		//摄像机通道号
//		sb.append("/ch01.264");
		sb.append(VERSION);
		sb.append("CSeq: ");
		sb.append(seq++);
		sb.append("\r\n");
		sb.append("User-Agent: LibVLC/3.0.7.1 (LIVE555 Streaming Media v2016.11.28)");
		sb.append("\r\n");
		sb.append("\r\n");
		keepaliveContent = sb.toString();
		log.info("\r\n" + sb.toString());
		send(sb.toString().getBytes());
	}

	private void doDescribe() {
		StringBuilder sb = new StringBuilder();
		sb.append("DESCRIBE ");
		sb.append(this.address);
		sb.append(VERSION);
		sb.append("Cseq: ");
		sb.append(seq++ + "\r\n");
		sb.append("User-Agent: LibVLC/3.0.7.1 (LIVE555 Streaming Media v2016.11.28)\n");
		sb.append("Accept: application/sdp\n");
		sb.append("\r\n");
		sb.append("\r\n");
		log.info("\r\n" + sb.toString());
		send(sb.toString().getBytes());
	}

	private void doPause() {
		StringBuilder sb = new StringBuilder();
		sb.append("PAUSE ");
		sb.append(this.address);
		sb.append("/");
		sb.append(VERSION);
		sb.append("Cseq: ");
		sb.append(seq++);
		sb.append("\r\n");
		sb.append("Session: ");
		sb.append(sessionid);
		sb.append("\r\n");
		send(sb.toString().getBytes());
		log.info("\r\n" + sb.toString());
	}


	public static void main(String[] args) {
		try {
			// RTSPClient(InetSocketAddress remoteAddress,
			// InetSocketAddress localAddress, String address)
			ConfigManager instance = ConfigManager.instance();
			boolean b = instance.readConfigFile();
			String rAddr = instance.getValue("rtsp.remote.addr");
			int rPort = Integer.parseInt(instance.getValue("rtsp.remote.port"));
			String lAddr = instance.getValue("rtsp.local.addr");
			int lPort = Integer.parseInt(instance.getValue("rtsp.local.port"));
			String rtspUrl = instance.getValue("rtsp.url");

//			RTSPClient client = new RTSPClient(
//					new InetSocketAddress(rAddr, rPort),
//					new InetSocketAddress(lAddr, lPort),
//					rtspUrl);
//			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}
}
