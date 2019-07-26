package com.shenzhen.teamway.sip;

import com.shenzhen.teamway.db.config.ConfigManager;
import com.shenzhen.teamway.db.config.DBConfig;
import com.shenzhen.teamway.mapper.CameraMapper;
import com.shenzhen.teamway.model.Camera;
import com.shenzhen.teamway.model.SipSession;
import com.shenzhen.teamway.rtsp.RTSPClient;
import com.shenzhen.teamway.sip.utils.MD5Utils;

import gov.nist.javax.sip.header.Subject;
import org.apache.ibatis.session.SqlSession;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.io.IOException;
import java.net.*;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 *
 * @author wangc
 * @date 2019/7/10 0010 11:31
 * @Description:
 */
public class ClientListener implements SipListener {
	private Logger log = LoggerFactory.getLogger(getClass());
	private static String ip;
	private static int port;
	private AddressFactory addressFactory;

	private HeaderFactory headerFactory;

	private MessageFactory messageFactory;

	private static CameraMapper cameraMapper;

	private SipProvider sipProvider;

	static List<Camera> cameras;

	private static JainSipClient instance;

	private static String fromIpPort;

	private static String toIpPort;

	private static String fromName;

	private static String toName;

	private String ipVideoServer;

	//视频请求当前摄像机
	private Camera camera;

	private SipProxyServer sipProxyServer;

	private static String toPassword;

	private int index;


	int serverVideoPort;


	public static String getFromIpPort() {
		return fromIpPort;
	}

	public static void setFromIpPort(String fromIpPort) {
		ClientListener.fromIpPort = fromIpPort;
	}

	public static String getToIpPort() {
		return toIpPort;
	}

	public static void setToIpPort(String toIpPort) {
		ClientListener.toIpPort = toIpPort;
	}

	public static String getFromName() {
		return fromName;
	}

	public static void setFromName(String fromName) {
		ClientListener.fromName = fromName;
	}

	public static String getToName() {
		return toName;
	}

	public static void setToName(String toName) {
		ClientListener.toName = toName;
	}

	static {
		ConfigManager configManager = ConfigManager.instance();
		fromIpPort = configManager.getValue("from.ip.port");
		toIpPort = configManager.getValue("to.ip.port");
		fromName = configManager.getValue("from.user.name");
		toName = configManager.getValue("to.user.name");
		int i = fromIpPort.indexOf(":");
		ip = fromIpPort.substring(0, i);
		port = Integer.parseInt(fromIpPort.substring(i + 1));
		toPassword = configManager.getValue("to.password");
		instance = JainSipClient.getInstance();
		DBConfig dbConfig = DBConfig.getInstance();
		boolean init = dbConfig.init();
		if (init) {
			SqlSession sqlSession = dbConfig.getSession();
			cameraMapper = sqlSession.getMapper(CameraMapper.class);
			cameras = cameraMapper.selectAll();
		}
	}


	public ClientListener(AddressFactory addressFactory, HeaderFactory headerFactory, MessageFactory messageFactory,
	                      SipProvider sipProvider) {
		super();
		this.addressFactory = addressFactory;
		this.headerFactory = headerFactory;
		this.messageFactory = messageFactory;
		this.sipProvider = sipProvider;
	}

	@Override
	public void processRequest(RequestEvent requestEvent) {
		log.info("processRequest执行");
		Request request = requestEvent.getRequest();
		CSeqHeader cSeqHeader = (CSeqHeader) request.getHeader("CSeq");
		if (null == request) {
			log.info("requestEvent.getRequest() is null.");
			return;
		}
		String method = request.getMethod();
		if ("ACK".equals(method)) {
			log.info("\nACK内容是:\n " + request);
			return;
		} else if ("SUBSCRIBE".equals(method)) {
			byte[] content = (byte[]) request.getContent();
			String strXml = new String(content);
			org.dom4j.Document document = null;
			try {
				document = DocumentHelper.parseText(strXml);
			} catch (DocumentException e) {
				e.printStackTrace();
			}
			Element root = document.getRootElement();
			Iterator iter = root.elementIterator("CmdType");
			while (iter.hasNext()) {
				Element element = (Element) iter.next();
				String cmdType = element.getTextTrim();
				Response response = null;
				if ("Catalog".equals(cmdType)) {
					try {
						response = messageFactory.createResponse(200, request);
						EventHeader eventHeader = headerFactory.createEventHeader("presence");
						ExpiresHeader expiresHeader = headerFactory.createExpiresHeader(90);
						ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("Application", "MANSCDP+XML");
						response.setExpires(expiresHeader);
						response.setHeader(eventHeader);
						response.setHeader(contentTypeHeader);
						ServerTransaction st = sipProvider.getNewServerTransaction(request);
						st.sendResponse(response);
						log.info("\n目录订阅消息:\n" + response);
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (TransactionAlreadyExistsException e) {
						e.printStackTrace();
					} catch (InvalidArgumentException e) {
						e.printStackTrace();
					} catch (TransactionUnavailableException e) {
						e.printStackTrace();
					} catch (SipException e) {
						e.printStackTrace();
					}
				}
			}
		} else if ("MESSAGE".equals(method)) {
			StringBuilder builder = new StringBuilder();
			builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
					"<Response>\n");
			builder.append("<CmdType>Catalog</CmdType>\n" +
					"<SN>" + (index + 1) + "</SN>\n");
			builder.append("<DeviceID>" + fromName + "</DeviceID>\n" +
					"<SumNum>" + cameras.size() + "</SumNum>\n" +
					"<DeviceList Num=");
			builder.append("\"" + cameras.size() + "\"" + ">\n");
			for (Camera camera : cameras) {
				builder.append("<Item>\n" + "<DeviceID>" + camera.getDeviceCode() + "</DeviceID>\n" +
						"<Name>" + camera.getName() + "</Name>    \n" +
						"<Manufacturer>" + camera.getManufacturer() + "</Manufacturer>\n" +
						"<Owner>" + camera.getOwner() + "</Owner>\n" +
						"<Model>" + camera.getModel() + "</Model>\n" +
						"<CivilCode>" + camera.getCivilCode() + "</CivilCode>\n");
				builder.append("<Address>" + camera.getAddress() + "</Address>\n" +
						"<Parental>" + camera.getParental() + "</Parental>\n" +
						"<CertNum>" + camera.getCertNum() + "</CertNum>\n" +
						"<Certifiable>" + camera.getCertifiable() + "</Certifiable>\n" +
						"<ParentID>" + camera.getCivilCode() + "</ParentID>\n" +
						"<RegisterWay>" + camera.getRegisterWay() + "</RegisterWay>\n" +
						"<Secrecy>" + camera.getSecrecy() + "</Secrecy>\n" +
						"<IPAddress>" + camera.getIpAddress() + "</IPAddress>\n" +
						"<Port>" + camera.getdPort() + "</Port> \n" +
						"<Status>" + camera.getStatus() + "</Status> \n" +
						"</Item>  \n");

			}
			builder.append("</DeviceList> \n" +
					"</Response>\n");
			index++;
			instance.sendMessageVideo(fromName, fromIpPort, toName, toIpPort, builder.toString(), Request.MESSAGE, Request.MESSAGE, "application", "MANSCDP+XML", cSeqHeader.getSeqNumber());
			log.info("\n目录发送:\n" + request);
			//告诉发送端不要再次发送目录查询消息
			Response response = null;
			try {
				response = messageFactory.createResponse(Response.TRYING, request);
				ServerTransaction st = sipProvider.getNewServerTransaction(request);
				st.sendResponse(response);
				log.info("\n目录上报请求收到trying:\n" + response);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (TransactionAlreadyExistsException e) {
				e.printStackTrace();
			} catch (InvalidArgumentException e) {
				e.printStackTrace();
			} catch (TransactionUnavailableException e) {
				e.printStackTrace();
			} catch (SipException e) {
				e.printStackTrace();
			}
		} else if ("INVITE".equals(method)) {
			Subject subject = ((Subject) request.getHeader("Subject"));
			String s = subject.encodeBody();
			camera = getCamera(s);
			if (camera == null) {
				log.error("ServerRequest实时视频请求，查不到此device");
				return;
			}
			String localIp = getLocalIp();
			sipProxyServer = new SipProxyServer(localIp);
			int port = sipProxyServer.getPort();
			log.info("SipProxy发送视频流地址:"+localIp+":"+port);
			try {
				Response response = messageFactory.createResponse(Response.TRYING, request);
				ContactHeader contact = (ContactHeader) request.getHeader("Contact");
				Address addr = contact.getAddress();
				CSeqHeader seqHeader = (CSeqHeader) request.getHeader("CSeq");
				FromHeader from = (FromHeader) request.getHeader("From");
				SipURI sipURIFrom = (SipURI) from.getAddress().getURI();
				SipURI toUri = (SipURI) ((ToHeader) request.getHeader("To")).getAddress().getURI();
				CallIdHeader callIdHeader = (CallIdHeader) request.getHeader("Call-ID");
				FromHeader fromHeader = headerFactory.createFromHeader(addr, null);
				ServerTransaction st = sipProvider.getNewServerTransaction(request);
				response.removeHeader("From");
				response.addHeader(fromHeader);
				st.sendResponse(response);
				log.info("\n实时视频请求收到trying:\n" + response);
				Response response2 = messageFactory.createResponse(Response.OK, request);
				response2.removeHeader("From");
				response2.addHeader(fromHeader);
				SipSession sipSession = new SipSession();
				SipURI contactUri = (SipURI) contact.getAddress().getURI();
				sipSession.setContactCode(contactUri.getUser());
				sipSession.setContactIpPort(contactUri.getHost() + ":" + contactUri.getPort());
				sipSession.setCSeq(seqHeader.getSeqNumber() + " " + cSeqHeader.getMethod());
				sipSession.setFromIpPort(sipURIFrom.getHost() + ":" + sipURIFrom.getPort());
				sipSession.setFromCode(sipURIFrom.getUser());
				sipSession.setToDeivceCode(toUri.getUser());
				sipSession.setCallId(callIdHeader.getCallId());
				sipSession.setToIpPort(toUri.getHost() + ":" + toUri.getPort());
				Thread thread = new Thread(sipSession);
				thread.start();
				ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp");
				ToHeader toHeader = (ToHeader) response.getHeader("To");
				Address toAddress = toHeader.getAddress();
				SipURI sipURI = (SipURI) toAddress.getURI();
				String host = sipURI.getHost();
				int p = sipURI.getPort();
				SipURI sipURI1 = addressFactory.createSipURI("", host + ":" + p);
				Address address = addressFactory.createAddress(sipURI1);
				StringBuilder builder = new StringBuilder();
				ContactHeader contactHeader = headerFactory.createContactHeader(address);
				response2.addHeader(contactHeader);
				builder.append("v=0 \n" +
						"o=huawei 1375083193016081 0 IN IP4 " + host + " \n" +
						"s=Play\n" +
						"c=IN IP4 " +
						"t=0 0 \n" +
						"m=video " + port + " RTP/AVP 96\n" +
						"a=rtpmap:96 PS/90000 \n" +
						"a=sendonly \n" +
						"a=username:admin \n" +
						"a=password:123456 \n" +
						"y=0999999999 \n" +
						"f=v/2/1/0/1/0a///");
				response2.setContent(builder.toString(), contentTypeHeader);
				st.sendResponse(response2);
				log.info("\n实时视频200 OK:\n" + response);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (TransactionAlreadyExistsException e) {
				e.printStackTrace();
			} catch (InvalidArgumentException e) {
				e.printStackTrace();
			} catch (TransactionUnavailableException e) {
				e.printStackTrace();
			} catch (SipException e) {
				e.printStackTrace();
			}
		} else if ("BYE".equals(method)) {

		}
	}

	@Override
	public void processResponse(ResponseEvent responseEvent) {
		log.info("processResponse执行");
		Response response = responseEvent.getResponse();
		if (null == response) {
			log.info("sip响应为空.");
			return;
		}

		log.info("返回码:" + response.getStatusCode());
		log.info("\nResponse is :" + response);
		WWWAuthenticateHeader wwwHeader = (WWWAuthenticateHeader) response.getHeader(WWWAuthenticateHeader.NAME);
		if (null != wwwHeader) {
			String realm = wwwHeader.getRealm();
			String nonce = wwwHeader.getNonce();
			String A1 = MD5Utils.md5(toName + ":" + realm + ":" + toPassword, "");
			String A2 = MD5Utils.md5("REGISTER:sip:" + toName + "@" + ip + ":" + port, "");
			String resStr = MD5Utils.md5(A1 + ":" + nonce + ":" + A2, "");

			try {
				//requestURI
				SipURI requestSipURI = addressFactory.createSipURI("sip:" + toName, toIpPort);
				requestSipURI.setTransportParam("udp");
				//from
				SipURI fromSipURI = addressFactory.createSipURI(fromName, fromIpPort);
				Address fromAddress = addressFactory.createAddress(fromSipURI);
				FromHeader fromHeader = headerFactory.createFromHeader(fromAddress, "mytag2");
				//to
				SipURI toSipURI = addressFactory.createSipURI(fromName, fromIpPort);
				Address toAddress = addressFactory.createAddress(toSipURI);
				ToHeader toHeader = headerFactory.createToHeader(toAddress, null);
				//via
				ViaHeader viaHeader = headerFactory.createViaHeader(ip, port, "udp", "branchingbranching");
				List<ViaHeader> viaHeaderList = new ArrayList<>();
				viaHeaderList.add(viaHeader);
				//callid,cseq,maxforwards
				CallIdHeader callIdHeader = sipProvider.getNewCallId();
				CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(2L, Request.REGISTER);
				MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);

				Request request = messageFactory.createRequest(requestSipURI, Request.REGISTER, callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaderList, maxForwardsHeader);
				//contant
				SipURI contantURI = addressFactory.createSipURI(fromName, fromIpPort);
				contantURI.setPort(port);
				Address contantAddress = addressFactory.createAddress(contantURI);
				ContactHeader contactHeader = headerFactory.createContactHeader(contantAddress);
				request.addHeader(contactHeader);

				//expires
				ExpiresHeader expiresHeader = headerFactory.createExpiresHeader(3600);
				request.addHeader(expiresHeader);

//				ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("text","plain");
//				request.setContent("",contentTypeHeader);

				AuthorizationHeader aHeader = headerFactory.createAuthorizationHeader("Degist");
				aHeader.setUsername(getToName());
				aHeader.setRealm(realm);
				aHeader.setNonce(nonce);
				aHeader.setURI(fromSipURI);
				aHeader.setResponse(resStr);
				aHeader.setAlgorithm("MD5");
				request.addHeader(aHeader);

				log.info("\n注册请求:\n" + request);
				sipProvider.sendRequest(request);
				ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
				SipKeepalive sipKeepalive = new SipKeepalive();
				scheduledExecutorService.scheduleAtFixedRate(sipKeepalive, 0, 30, TimeUnit.SECONDS);
			} catch (ParseException | InvalidArgumentException | SipException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void processTimeout(TimeoutEvent timeoutEvent) {
		// TODO Auto-generated method stub
		log.info("请求超时：" + timeoutEvent);

	}

	@Override
	public void processIOException(IOExceptionEvent exceptionEvent) {
		// TODO Auto-generated method stub
		log.info("io异常:" + exceptionEvent);
	}

	@Override
	public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
		Request request = transactionTerminatedEvent.getServerTransaction().getRequest();
		log.info("processTransactionTerminated执行:\n" + request);
		Object content = request.getContent();
		if ("INVITE".equals(request.getMethod())) {

			if (content instanceof byte[]) {
				byte[] bytes = (byte[]) content;
				String s = new String(bytes);
				String video_ = s.substring(s.indexOf("video ") + 5, s.indexOf("/AVP"));
				String[] split = video_.split(" ");
				for (String s1 : split) {
					try {
						serverVideoPort = Integer.parseInt(s1);
					} catch (Exception e) {

					}

				}
				log.info("SipServer接收视频流地址:"+ipVideoServer+":"+serverVideoPort);
				String[] split1 = s.split("\r\n");
				for (String s1 : split1) {
					int ip = s1.indexOf("IP4");
					if (ip != -1) {
						ipVideoServer = s1.substring(ip + 3);
					}
				}
			}
			Map<String, Object> remoteUrl = getRemoteUrl(camera);
			Object remoteIpObj = remoteUrl.get("remoteIp");
			Object remotePortObj = remoteUrl.get("remotePort");
			String remoteIp = "";
			int remotePort = 0;
			if (remoteIpObj instanceof String) {
				remoteIp = (String) remoteIpObj;
			}
			if (remotePortObj instanceof Integer) {
				remotePort = (Integer) remotePortObj;
			}
			sipProxyServer.setRemoteAddr(ipVideoServer);
			sipProxyServer.setRemotePort(serverVideoPort);
			createRtspClient(remoteIp,remotePort,getLocalIp(),sipProxyServer,camera);
		}
	}

	@Override
	public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
		log.info("processDialogTerminated执行");
	}

	public Camera getCamera(String s) {
		String deviceCode = s.substring(0, s.indexOf(",")).substring(0, s.indexOf(":"));
		Camera camera = cameraMapper.findCameraByCode(deviceCode);
		return camera;
	}

	public Map<String, Object> getRemoteUrl(Camera camera) {
		Pattern p = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\:(\\d+)");
		Matcher m = p.matcher(camera.getRtspUrl());
		String remoteIp = "";
		int remotePort = 0;
		Map<String, Object> map = new HashMap<>();
		//将符合规则的提取出来
		while (m.find()) {
			remoteIp = m.group(1);
			remotePort = Integer.parseInt(m.group(2));
		}
		map.put("remoteIp", remoteIp);
		map.put("remotePort", remotePort);
		return map;
	}

	public String getLocalIp() {
		String localIp = "";
		String[] split = fromIpPort.split(":");
		if (split[0].length() > split[1].length()) {
			localIp = split[0];
		} else {
			localIp = split[1];
		}
		return localIp;
	}


	public void createRtspClient(String remoteIp, int remotePort, String localIp, SipProxyServer sipProxyServer, Camera camera) {
		int port = 1000;
		while (true) {
			try {
				Socket socket = new Socket();
				socket.bind(new InetSocketAddress(localIp,port));
				log.info("RTSP通信端口被启用:" + port);
				socket.close();
				break;
			} catch (IOException e) {
				log.info("RTSP通信端口被占用:" + port);
				port += 1;
			}
		}
		RTSPClient rtspClient = new RTSPClient(new InetSocketAddress(remoteIp, remotePort), new InetSocketAddress(localIp, port), camera.getRtspUrl(), sipProxyServer);
		if (camera != null) {
			rtspClient.setDeviceCode(camera.getDeviceCode());
		}
		rtspClient.start();
	}

}
