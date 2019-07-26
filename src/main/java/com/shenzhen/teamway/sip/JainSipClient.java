package com.shenzhen.teamway.sip;

import com.shenzhen.teamway.rtsp.RTSPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.x509.IPAddressName;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * @author wangc
 * @date 2019/6/27 0027 16:42
 * @Description:
 */
public class JainSipClient {
	private SipStack sipStack;
	private static JainSipClient jainSipClient;

	private SipFactory sipFactory;

	private Logger log = LoggerFactory.getLogger(getClass());

	private AddressFactory addressFactory;

	private HeaderFactory headerFactory;

	private MessageFactory messageFactory;

	private SipProvider sipProvider;




	private static String ip;
	private static int port;

	static {
		String fromIpPort = ClientListener.getFromIpPort();
		int i = fromIpPort.indexOf(":");
		ip = fromIpPort.substring(0, i);
		port =Integer.parseInt( fromIpPort.substring(i+1));
	}


	public static void main(String[] args) {
		JainSipClient client = getInstance();
		client.init();
		client.sendMessage(ClientListener.getFromName(), ClientListener.getFromIpPort(), ClientListener.getToName(), ClientListener.getToIpPort(), "", Request.REGISTER);
	}

	private JainSipClient() {

	}

	public static JainSipClient getInstance() {
		if (jainSipClient == null) {
			synchronized (JainSipClient.class) {
				if (jainSipClient == null) {
					jainSipClient = new JainSipClient();
				}
			}
		}
		return jainSipClient;
	}

	public void init() {
		try {
			Properties prop = new Properties();
			prop.setProperty("javax.sip.STACK_NAME", "teststackname");
			prop.setProperty("javax.sip.IP_ADDRESS", ip);
			prop.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
			prop.setProperty("gov.nist.javax.sip.DEBUG_LOG", "sipclientdebug.txt");
			prop.setProperty("gov.nist.javax.sip.SERVER_LOG", "sipclientlog.txt");

			sipFactory = SipFactory.getInstance();
			sipFactory.setPathName("gov.nist");

			sipStack = sipFactory.createSipStack(prop);

			headerFactory = sipFactory.createHeaderFactory();
			addressFactory = sipFactory.createAddressFactory();
			messageFactory = sipFactory.createMessageFactory();

			ListeningPoint listeningpoint_udp = sipStack.createListeningPoint(port, "udp");
//			ListeningPoint listeningponit_tcp =sipStack.createListeningPoint(port, "tcp");

			sipProvider = sipStack.createSipProvider(listeningpoint_udp);
			ClientListener listener = new ClientListener(addressFactory, headerFactory, messageFactory, sipProvider);
			sipProvider.addSipListener(listener);
//			sipProvider = sipStack.createSipProvider(listeningponit_tcp);
			sipProvider.addSipListener(listener);
			log.info("sip客户端初始化成功.");
		} catch (PeerUnavailableException | TransportNotSupportedException | ObjectInUseException
				| InvalidArgumentException | TooManyListenersException e) {
			e.printStackTrace();
		}

	}

	public void sendMessage(String fromUserName, String fromIpPort, String toUserName, String toIpPort, String message, String messageType) {
		try {
			//requestURI
			SipURI requestSipURI = addressFactory.createSipURI("sip:" + toUserName, toIpPort);
			requestSipURI.setTransportParam("udp");
			//from
			SipURI fromSipURI = addressFactory.createSipURI(fromUserName, fromIpPort);
			Address fromAddress = addressFactory.createAddress(fromSipURI);
			FromHeader fromHeader = headerFactory.createFromHeader(fromAddress, "mytag");
			//to
			SipURI toSipURI = addressFactory.createSipURI(toUserName, toIpPort);
			Address toAddress = addressFactory.createAddress(toSipURI);
			ToHeader toHeader = headerFactory.createToHeader(toAddress, null);
			//via
			ViaHeader viaHeader = headerFactory.createViaHeader(ip, port, "udp", "branchingbranching");
			List<ViaHeader> viaHeaderList = new ArrayList<>();
			viaHeaderList.add(viaHeader);
			//callid,cseq,maxforwards
			CallIdHeader callIdHeader = sipProvider.getNewCallId();
			CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L, messageType);
			MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
			//
			Request request = messageFactory.createRequest(requestSipURI, messageType, callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaderList, maxForwardsHeader);
			//contact
			SipURI contactURI = addressFactory.createSipURI(fromUserName, fromIpPort);
			contactURI.setPort(port);
			Address contactAddress = addressFactory.createAddress(contactURI);
			ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
			request.addHeader(contactHeader);
			//expires
			ExpiresHeader expiresHeader = headerFactory.createExpiresHeader(3600);
			request.addHeader(expiresHeader);
			ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "Xml");
			request.setContent(message, contentTypeHeader);

			log.info("sip发送请求内容:\r\n" + request);
			sipProvider.sendRequest(request);
//			//
//			ClientTransaction trans = sipProvider.getNewClientTransaction(request);
//			dialog = trans.getDialog();
//			trans.sendRequest();
//			//
//			request = dialog.createRequest(Request.MESSAGE);
//			request.setHeader(contactHeader);
//			request.setContent(message, contentTypeHeader);
//			ClientTransaction ctrans = sipProvider.getNewClientTransaction(request);
//			ctrans.sendRequest();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		} catch (SipException e) {
			e.printStackTrace();
		}
	}

	public void sendMessageVideo(String fromUserName, String fromIpPort, String toUserName, String toIpPort, String message, String messageType, String cSeqType, String contentHeaderTypePrefix, String contentHeaderTypeSuxfix, long Cesq) {
		try {
			//requestURI
			SipURI requestSipURI = addressFactory.createSipURI("sip:" + toUserName, toIpPort);
			requestSipURI.setTransportParam("udp");
			//from
			SipURI fromSipURI = addressFactory.createSipURI(fromUserName, fromIpPort);
			Address fromAddress = addressFactory.createAddress(fromSipURI);
			FromHeader fromHeader = headerFactory.createFromHeader(fromAddress, "mytag");
			//to
			SipURI toSipURI = addressFactory.createSipURI(toUserName, toIpPort);
			Address toAddress = addressFactory.createAddress(toSipURI);
			ToHeader toHeader = headerFactory.createToHeader(toAddress, null);
			//via
			ViaHeader viaHeader = headerFactory.createViaHeader(ip, port, "udp", "branchingbranching");
			List<ViaHeader> viaHeaderList = new ArrayList<>();
			viaHeaderList.add(viaHeader);
			//callid,cseq,maxforwards
			CallIdHeader callIdHeader = sipProvider.getNewCallId();
			CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(Cesq, cSeqType);
			MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
			//
			Request request = messageFactory.createRequest(requestSipURI, messageType, callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaderList, maxForwardsHeader);
			//contact
			SipURI contactURI = addressFactory.createSipURI(fromUserName, fromIpPort);
			contactURI.setPort(port);
			Address contactAddress = addressFactory.createAddress(contactURI);
			ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
			request.addHeader(contactHeader);
			//expires
			ExpiresHeader expiresHeader = headerFactory.createExpiresHeader(3600);
			request.addHeader(expiresHeader);
			ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader(contentHeaderTypePrefix, contentHeaderTypeSuxfix);
			request.setContent(message, contentTypeHeader);

			log.info("sip发送请求内容:\r\n" + request);
			sipProvider.sendRequest(request);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		} catch (SipException e) {
			e.printStackTrace();
		}
	}
}


