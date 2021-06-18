package com.ap.menabev.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class OdataHelperClass {

	private static final Logger logger = LoggerFactory.getLogger(OdataHelperClass.class);

	public static String encodeUsernameAndPassword(String username, String password) {
		String encodeUsernamePassword = username + ":" + password;
		String auth = "Basic " + DatatypeConverter.printBase64Binary(encodeUsernamePassword.getBytes());
		return auth;
	}

	public static String getDataFromStream(InputStream stream) throws IOException {
		StringBuilder dataBuffer = new StringBuilder();
		BufferedReader inStream = new BufferedReader(new InputStreamReader(stream));
		String data = "";

		while ((data = inStream.readLine()) != null) {
			dataBuffer.append(data);
		}
		inStream.close();
		return dataBuffer.toString();
	}

	public static Map<String, Object> getDestination(String destinationName) throws URISyntaxException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(ApplicationConstants.DESTINATION_TOKEN_URL);
		httpPost.addHeader("Content-Type", "application/json");
		// Encoding username and password
		String auth = encodeUsernameAndPassword(ApplicationConstants.DESTINATION_CLIENT_ID,
				ApplicationConstants.DESTINATION_CLIENT_SECRET);
		httpPost.addHeader("Authorization", auth);
		HttpResponse res = client.execute(httpPost);
		String data = getDataFromStream(res.getEntity().getContent());
		if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String jwtToken = new JSONObject(data).getString("access_token");
			logger.error("jwtdestinationToken " + jwtToken);
			HttpGet httpGet = new HttpGet(ApplicationConstants.DESTINATION_BASE_URL + destinationName);
			httpGet.addHeader("Content-Type", "application/json");
			httpGet.addHeader("Authorization", "Bearer " + jwtToken);
			HttpResponse response = client.execute(httpGet);
			String dataFromStream = getDataFromStream(response.getEntity().getContent());
			logger.error("dataFromStream : " + dataFromStream);
			if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
				logger.error("60 " + dataFromStream);
				JSONObject json = new JSONObject(dataFromStream);
				Map<String, Object> mapResponse = json.getJSONObject("destinationConfiguration").toMap();
				mapResponse.put("token", jwtToken);
				return mapResponse;
			}
		}
		return null;
	}

	
	public static ResponseEntity<?> consumingOdataService(String url, String entity, String method,
			Map<String, Object> destinationInfo) throws IOException, URISyntaxException {
		try {
			// have to fetch from Connectivity SK through VCAP //TODO
			String proxyHost = "10.0.4.5";
			int proxyPort = 20003;
			// JSONObject jsonObj = new
			// JSONObject(System.getenv("VCAP_SERVICES"));

			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort), new UsernamePasswordCredentials(
					(String) destinationInfo.get("User"), (String) destinationInfo.get("Password")));

			HttpClientBuilder clientBuilder = HttpClientBuilder.create();

			clientBuilder.setProxy(new HttpHost(proxyHost, proxyPort))
					.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy())
					.setDefaultCredentialsProvider(credsProvider).disableCookieManagement();

			HttpClient httpClient = clientBuilder.build();
			HttpRequestBase httpRequestBase = null;
			String jsonMessage = null;
			HttpResponse httpResponse = null;
			StringEntity input = null;
			Header[] json = null;
			JSONObject obj = null;
			String jwToken = getConectivityProxy();
			if (method.equalsIgnoreCase("GET")) {
				httpRequestBase = new HttpGet(destinationInfo.get("URL") + url);
			} else if (method.equalsIgnoreCase("POST")) {
				httpRequestBase = new HttpPost(url);
				try {

					logger.error("entity " + entity);
					input = new StringEntity(entity);
					input.setContentType("application/json");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				logger.error("inputEntity " + input);
				((HttpPost) httpRequestBase).setEntity(input);
			}
			if (destinationInfo.get("sap-client") != null) {
				httpRequestBase.addHeader("sap-client", (String) destinationInfo.get("sap-client"));
			}
			httpRequestBase.addHeader("accept", "application/json");

			Header[] headers = getAccessToken((String) destinationInfo.get("URL") + url,
					(String) destinationInfo.get("User"), (String) destinationInfo.get("Password"), httpClient,
					proxyHost, proxyPort, (String) destinationInfo.get("sap-client"), jwToken);
			String token = null;
			List<String> cookies = new ArrayList<>();
			if (headers.length != 0) {

				for (Header header : headers) {

					if (header.getName().equalsIgnoreCase("x-csrf-token")) {
						token = header.getValue();
						logger.error("token --- " + token);
					}

					if (header.getName().equalsIgnoreCase("set-cookie")) {
						cookies.add(header.getValue());
					}

				}
			}

			if (destinationInfo.get("User") != null && destinationInfo.get("Password") != null) {
				String encoded = encodeUsernameAndPassword((String) destinationInfo.get("User"),
						(String) destinationInfo.get("Password"));
				httpRequestBase.addHeader("Authorization", encoded);
				httpRequestBase.setHeader("Proxy-Authorization", "Bearer " + jwToken);
				httpRequestBase.addHeader("SAP-Connectivity-SCC-Location_ID",
						(String) destinationInfo.get("CloudConnectorLocationId"));

			}
			
			if (token != null) {
				httpRequestBase.addHeader("X-CSRF-Token", token);
			}
			if (!cookies.isEmpty()) {
				for (String cookie : cookies) {
					String tmp = cookie.split(";", 2)[0];
					httpRequestBase.addHeader("Cookie", tmp);
				}
			}
			httpResponse = httpClient.execute(httpRequestBase);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String dataFromStream = getDataFromStream(httpResponse.getEntity().getContent());
				return new ResponseEntity<String>(dataFromStream, HttpStatus.OK);
			} else if (httpResponse.getStatusLine().getStatusCode() == 201) {
				// logic for 201 //TODO
				return new ResponseEntity<String>("from block 201", HttpStatus.OK);
			} else {
				String responseFromECC = getDataFromStream(httpResponse.getEntity().getContent());
				return new ResponseEntity<String>(responseFromECC, HttpStatus.BAD_REQUEST);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<String>("Error" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public static String getConectivityProxy() throws URISyntaxException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(
				"https://menabevdev.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials");
		//use vcap or change when deploying in dev in dev space
		String auth = encodeUsernameAndPassword("sb-cloneb41bf10568ca4499840711bb8a0f2de4!b3189|connectivity!b5",
				"c1ac511d-74f8-4a64-a28b-c42bb41c2d2b$Md8ZvS__pg9I_Z_qPNhHrXR7p2zhCP9ByfhO3RYVCt8=");
		httpPost.addHeader("Authorization", auth);
		HttpResponse res = client.execute(httpPost);
		String data = getDataFromStream(res.getEntity().getContent());
		if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String jwtToken = new JSONObject(data).getString("access_token");
			return jwtToken;
		}
		return null;
	}

	private static Header[] getAccessToken(String url, String username, String password, HttpClient client,
			String proxyHost, int proxyPort, String sapClient, String token)
			throws ClientProtocolException, IOException {

		HttpGet httpGet = new HttpGet(url);
		String userpass = username + ":" + password;
		httpGet.setHeader("Proxy-Authorization", "Bearer " + token);
		httpGet.setHeader(HttpHeaders.AUTHORIZATION,
				"Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes()));
		// Encoding username and password
		httpGet.addHeader("X-CSRF-Token", "fetch");
		httpGet.addHeader("Content-Type", "application/json");
		httpGet.addHeader("sap-client", sapClient);
		httpGet.addHeader("SAP-Connectivity-SCC-Location_ID", "DEVHEC");
		HttpResponse response = client.execute(httpGet);

		return response.getAllHeaders();

	}
}
