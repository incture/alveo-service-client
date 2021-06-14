package com.ap.menabev.soap.service;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.ap.menabev.dto.InvoiceHeaderCheckDto;


public class Odata {
	
	private static final Logger logger = LoggerFactory.getLogger(Odata.class);
	public static ResponseEntity<?> getPaymentTerms(InvoiceHeaderCheckDto invoiceHeaderCheckDto) {
		try {
			Map<String, Object> map = getDestination("SD4_DEST");
			return consumingOdataService(
					"/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_SupplierCompany?$format=json&$filter=%20Supplier%20eq%20%27"+invoiceHeaderCheckDto.getVendorID()+"%27%20and%20CompanyCode%20eq%20%27"+invoiceHeaderCheckDto.getCompanyCode()+"%27",
					null, "GET", map);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

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

		logger.error("28 destination");
		HttpClient client = HttpClientBuilder.create().build();

		HttpPost httpPost = new HttpPost(
				"https://menabevdev.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials");
		httpPost.addHeader("Content-Type", "application/json");

		// Encoding username and password
		String auth = encodeUsernameAndPassword(
				"sb-clone4768d4738f4b49498258b8a01b20230a!b3189|destination-xsappname!b2",
				"f845b3d5-4cc9-443e-8d29-fb6bf26d5dbc$kZHRRKM05NdJ6i-B6QqdOyM7UuN-qvyiVkMpY0dBqnY=");

		httpPost.addHeader("Authorization", auth);

		HttpResponse res = client.execute(httpPost);

		logger.error(" 41 rest" + res);

		String data = getDataFromStream(res.getEntity().getContent());
		if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String jwtToken = new JSONObject(data).getString("access_token");

			logger.error("jwtdestinationToken " + jwtToken);

			HttpGet httpGet = new HttpGet("https://destination-configuration.cfapps.eu20.hana.ondemand.com"
					+ "/destination-configuration/v1/destinations/" + destinationName);

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

	public static ResponseEntity<?> getOdata() throws URISyntaxException, IOException {
		String clientid = "sb-cloneb41bf10568ca4499840711bb8a0f2de4!b3189|connectivity!b5";
		String clientsecret = "cf792fe9-32f6-496c-aeb6-aec065a33512$WhwgyCaocXG__utqLrg1NJjS3mRwCEGW9VxWDTTniK4=";
		String proxyHost = "10.0.4.5";
		int proxyPort = 20003;
		final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		final InetSocketAddress address = new InetSocketAddress(proxyHost, proxyPort);
		final Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
		factory.setProxy(proxy);
		final RestTemplate restTemplate = new RestTemplateBuilder().requestFactory(() -> factory)
				.defaultHeader(HttpHeaders.PROXY_AUTHORIZATION, "Bearer " + getConectivityProxy()).build();

		restTemplate.setRequestFactory(factory);

		URI uri = null;
		HttpHeaders requestHeaders = null;
		HttpEntity<String> entity;
		String plainCreds = "Syuvraj:Incture@12345";
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		uri = new URI(
				"http://sd4.menabev.com:443/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_SupplierCompany?$format=json&$filter=%20Supplier%20eq%20%271000047%27%20and%20CompanyCode%20eq%20%271010%27");

		requestHeaders = new HttpHeaders();
		requestHeaders.add("Authorization", "Basic " + base64Creds);
		requestHeaders.add("Content-Type", "application/json;charset=utf-8");

		ResponseEntity<String> response = null;
		HttpEntity<String> entity1 = new HttpEntity<String>("parameters", requestHeaders);

		response = restTemplate.exchange(uri, HttpMethod.GET, entity1, String.class);

		return response;

		// JSONObject jsonObj = new JSONObject(System.getenv("VCAP_SERVICES"));
		// JSONArray jsonArr = jsonObj.getJSONArray("connectivity");
		// JSONObject connectivityCredentials =
		// jsonArr.getJSONObject(0).getJSONObject("credentials");
		//
		//
		//
		// JSONObject jsonObjXsuaa = new
		// JSONObject(System.getenv("VCAP_SERVICES"));
		// JSONArray jsonArrXsuaa = jsonObjXsuaa.getJSONArray("xsuaa");
		// JSONObject xsuaaCredentials =
		// jsonArrXsuaa.getJSONObject(0).getJSONObject("credentials");
		//
		//
		//
		// String connProxyHost =
		// connectivityCredentials.getString("onpremise_proxy_host");
		// System.out.println("conn proxy host.."+connProxyHost);
		// int connProxyPort =
		// Integer.parseInt(connectivityCredentials.getString("onpremise_proxy_http_port"));
		// System.out.println("conn proxy Port.."+connProxyPort);
	}

	public static String getConectivityProxy() throws URISyntaxException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(
				"https://menabevdev.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials");
		String auth = encodeUsernameAndPassword("sb-cloneb41bf10568ca4499840711bb8a0f2de4!b3189|connectivity!b5",
				"cf792fe9-32f6-496c-aeb6-aec065a33512$WhwgyCaocXG__utqLrg1NJjS3mRwCEGW9VxWDTTniK4=");
		httpPost.addHeader("Authorization", auth);
		HttpResponse res = client.execute(httpPost);
		String data = getDataFromStream(res.getEntity().getContent());
		if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String jwtToken = new JSONObject(data).getString("access_token");
			return jwtToken;
		}
		return null;
	}
	
	public static ResponseEntity<?> consumingOdataService(String url, String entity, String method,
			Map<String, Object> destinationInfo) throws IOException, URISyntaxException {

		logger.error("com.incture.utils.HelperClass  + Inside consumingOdataService==================");

		String proxyHost = "10.0.4.5";

		logger.error("proxyHost-- " + proxyHost);
		int proxyPort = 20003;
		Header[] jsonResponse = null;
		String objresult = null;

		// JSONObject jsonObj = new JSONObject(System.getenv("VCAP_SERVICES"));

		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort), new UsernamePasswordCredentials(
				(String) destinationInfo.get("User"), (String) destinationInfo.get("Password")));

		HttpClientBuilder clientBuilder = HttpClientBuilder.create();

		clientBuilder.setProxy(new HttpHost(proxyHost, proxyPort))
				.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy())
				.setDefaultCredentialsProvider(credsProvider).disableCookieManagement();

		HttpClient httpClient = clientBuilder.build();
		HttpRequestBase httpRequestBase = null;
		String jsonMessage = "Empty BODY";
		HttpResponse httpResponse = null;
		StringEntity input = null;
		Header[] json = null;
		JSONObject obj = null;
		String jwToken = getConectivityProxy();
		if (url != null) {
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

			Header[] headers = getAccessToken(
					(String) destinationInfo.get("URL")
							+ url,
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
			// if (tenantctx != null) {
			// httpRequestBase.addHeader("SAP-Connectivity-ConsumerAccount",
			// tenantctx.getTenant().getAccount().getId());
			// }
			try {

				httpResponse = httpClient.execute(httpRequestBase);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					String dataFromStream = getDataFromStream(httpResponse.getEntity().getContent());
					logger.error("dataFromStream" + dataFromStream);
					return new ResponseEntity<String>(dataFromStream,
							HttpStatus.OK);

				} else {
					String responseFromECC = httpResponse.getEntity().toString();

					logger.error("responseFromEcc" + responseFromECC);

					return new ResponseEntity<String>("Response from odata call " + httpResponse,
							HttpStatus.BAD_REQUEST);
				}


			} catch (IOException e) {
				e.printStackTrace();
				return  new ResponseEntity<String>("Exception in ODATA consumtion block" + e.getMessage(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}

			
		}

		return new ResponseEntity<String>(jsonMessage, HttpStatus.OK);

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
		httpGet.addHeader("X-CSRF-Token", "Fetch");
		httpGet.addHeader("Content-Type", "application/json");
		httpGet.addHeader("sap-client", sapClient);
		httpGet.addHeader("SAP-Connectivity-SCC-Location_ID", "DEVHEC");

		HttpResponse response = client.execute(httpGet);
		// HttpResponse response = client.execute( httpGet);

		logger.error("313 response " + response);

		// HttpResponse response = client.execute(httpGet);

		return response.getAllHeaders();

	}

}
