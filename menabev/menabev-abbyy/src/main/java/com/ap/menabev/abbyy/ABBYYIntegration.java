package com.ap.menabev.abbyy;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ap.menabev.sftp.SFTPChannelUtil;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.Session;

@Service
public class ABBYYIntegration {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ABBYYIntegration.class);

	// public ChannelSftp setupJschChannel() throws JSchException {
	// JSch jsch = new JSch();
	// Session jschSession = jsch.getSession(ApplicationConstants.SFTP_USER,
	// ApplicationConstants.SFTP_HOST,
	// ApplicationConstants.SFTP_PORT);
	// jschSession.setConfig(ApplicationConstants.STRICT_HOST_KEY_CHECKING_KEY,
	// ApplicationConstants.STRICT_HOST_KEY_CHECKING_VALUE);
	// jschSession.setPassword(ApplicationConstants.SFTP_PASSWORD);
	// jschSession.setSocketFactory(new SocketFactory() {
	//
	// @Override
	// public OutputStream getOutputStream(Socket socket) throws IOException {
	// // TODO Auto-generated method stub
	// return socket.getOutputStream();
	// }
	//
	// @Override
	// public InputStream getInputStream(Socket socket) throws IOException {
	// // TODO Auto-generated method stub
	// return socket.getInputStream();
	// }
	//
	// @Override
	// public Socket createSocket(String host, int port) throws IOException,
	// UnknownHostException {
	// // TODO Auto-generated method stub
	// ConnectivitySocks5ProxySocket proxySocket = null;
	// try {
	// proxySocket = new
	// ConnectivitySocks5ProxySocket(ApplicationConstants.SOCKS_LOCATION_ID);
	// SocketAddress endpoint = new InetSocketAddress(host, port);
	// proxySocket.connect(endpoint, ApplicationConstants.SESSION_TIME_OUT);
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// return proxySocket;
	// }
	// });
	// jschSession.connect();
	// return (ChannelSftp)
	// jschSession.openChannel(ApplicationConstants.SFTP_CHANNEL);
	// }
	//
	// public String uploadFileUsingJsch(File file, String
	// abbyyRemoteInputDirectory) {
	// ChannelSftp channelSftp = null;
	// String message = null;
	// try {
	// if (!ServiceUtil.isEmpty(channelSftp)) {
	// channelSftp = setupJschChannel();
	// channelSftp.connect();
	// // String localFile = File.createTempFile("sample",
	// // "txt").getAbsolutePath();
	// for (int i = 0; i < 10; i++) {
	// String inputFilePath = file.getAbsolutePath();
	// channelSftp.put(inputFilePath, abbyyRemoteInputDirectory + i +
	// file.getName());
	// }
	// // channelSftp.put(localFile,
	// // ApplicationConstants.ABBYY_REMOTE_INPUT_FILE_DIRECTORY +
	// // "sample.txt");
	// message = "File uploaded";
	// } else {
	// message = "ChannelSftp Is null or empty";
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// logger.error("Error in ABBYYIntegration.uploadFileUsingJsch()");
	// message = "Error While Getting ChannelSftp :: " + e.getMessage();
	// } finally {
	// channelSftp.exit();
	// }
	// return message;
	// }

	/*
	 * public String putInvoiceInAbbyy(List<File> files, String
	 * abbyyRemoteInputDirectory, ChannelSftp channelSftp) { String message =
	 * null; try { for (File file : files) { String inputFilePath =
	 * file.getAbsolutePath();
	 * System.err.println("inputFilePath "+file.getAbsolutePath());
	 * channelSftp.cd("\\Input\\");
	 * channelSftp.put(inputFilePath,file.getName());
	 * System.err.println("fileName putInvoiceInAbby= "+file.getName()); }
	 * message = "File uploaded";
	 * 
	 * } catch (Exception e) { e.printStackTrace();
	 * logger.error("Error in ABBYYIntegration.uploadFileUsingJsch()");
	 * 
	 * message = "Error While Getting ChannelSftp :: " + e.getMessage(); }
	 * return message; }
	 */

	public String putInvoiceInAbbyy(List<File> files, String abbyyRemoteInputDirectory, ChannelSftp channelSftp) {
		String message = null;
		try {
			//channelSftp.cd("C:\\Program Files (x86)\\freeFTPd\\sftproot\\SYuvraj@menabev.com\\Input\\");
			channelSftp.cd("\\Input\\");
			for (File file : files) {
				String inputFilePath = file.getAbsolutePath();
				System.err.println("inputFilePath " + file.getAbsolutePath());
				// channelSftp.cd("\\Input\\");
				try {
					channelSftp.put(inputFilePath, file.getName());
				} catch (Exception e) {

					logger.error("inside for loop catch block");

					Session session = SFTPChannelUtil.getSession();
					channelSftp = SFTPChannelUtil.getJschChannel(session);
					channelSftp.connect();
					channelSftp.put(inputFilePath, file.getName());
					logger.error("after put in catch inside for loop");
				}
				System.err.println("fileName putInvoiceInAbby= " + file.getName());
				
			}
			message = "File uploaded";

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in ABBYYIntegration.uploadFileUsingJsch()");
			message = "Error While Getting ChannelSftp :: " + e.getMessage();
		}
		return message;
	}

	public List<JSONObject> getJsonOutputFromSFTP(ChannelSftp channelSftp) {
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		try {
			logger.error("Errorrrrrrrrrrrr" + ServiceUtil.isEmpty(channelSftp));
			Vector<?> filelist = channelSftp.ls(".json");
			for (int i = 0; i < filelist.size(); i++) {
				LsEntry entry = (LsEntry) filelist.get(i);
				logger.error("INSIDE JSON " + entry.getFilename());
				if (entry.getFilename().contains(".json")) {
					logger.error("INSIDE JSON ");
					InputStream is = channelSftp.get(entry.getFilename());
					ObjectMapper mapper = new ObjectMapper();
					Map<String, Object> jsonMap = mapper.readValue(is, Map.class);
					JSONObject js = new JSONObject(jsonMap);
					logger.error("Errorrrrrrrrrrrr" + js);
					jsonList.add(js);
					is.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonList;
	}
}
