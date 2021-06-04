package com.ap.menabev.sftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.slf4j.LoggerFactory;

import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SocketFactory;

public class SFTPChannelUtil {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SFTPChannelUtil.class);

	public static Session getSession() {

		JSch jsch = new JSch();
		Session session = null;
		try {
			session = jsch.getSession(ApplicationConstants.SFTP_USER, ApplicationConstants.SFTP_HOST,
					ApplicationConstants.SFTP_PORT);
			session.setConfig(ApplicationConstants.STRICT_HOST_KEY_CHECKING_KEY,
					ApplicationConstants.STRICT_HOST_KEY_CHECKING_VALUE);
			session.setPassword(ApplicationConstants.SFTP_PASSWORD);
			session.setSocketFactory(new SocketFactory() {

				@Override
				public OutputStream getOutputStream(Socket socket) throws IOException {
					// TODO Auto-generated method stub
					return socket.getOutputStream();
				}

				@Override
				public InputStream getInputStream(Socket socket) throws IOException {
					// TODO Auto-generated method stub
					return socket.getInputStream();
				}

				@Override
				public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
					// TODO Auto-generated method stub
					ConnectivitySocks5ProxySocket proxySocket = null;
					try {
						proxySocket = new ConnectivitySocks5ProxySocket(ApplicationConstants.SOCKS_LOCATION_ID);
						SocketAddress endpoint = new InetSocketAddress(host, port);
						proxySocket.connect(endpoint, ApplicationConstants.SESSION_TIME_OUT);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					return proxySocket;
				}
			});
			session.connect();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("While Creating session " + "SFTPChannelUtil.getSession()" + e.getMessage());

		}
		return session;
	}

	public static ChannelSftp getJschChannel(Session session) throws JSchException {
		ChannelSftp channelSftp = null;
		try {
			if (!ServiceUtil.isEmpty(session)) {
				channelSftp = (ChannelSftp) session.openChannel(ApplicationConstants.SFTP_CHANNEL);
			} else {
				session = getSession();
				channelSftp = (ChannelSftp) session.openChannel(ApplicationConstants.SFTP_CHANNEL);
			}
			return channelSftp;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("SFTPChannelUtil.getJschChannel() while creating channel:::" + e.getMessage());
			return channelSftp;
		}
	}

	public static boolean disconnect(Session session, ChannelSftp channelSftp) {
		boolean flag = false;
		if (!ServiceUtil.isEmpty(channelSftp)) {
			channelSftp.disconnect();
			if (!ServiceUtil.isEmpty(session)) {
				session.disconnect();
				flag = true;
			}
			return flag;
		} else {
			session.disconnect();
			flag = true;
			return flag;
		}
	}
}
