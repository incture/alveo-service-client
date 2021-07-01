package com.ap.menabev.serviceimpl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import java.util.HashSet;
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
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ap.menabev.dto.PoSearchApiDto;
import com.ap.menabev.service.PoSearchApiService;
import com.ap.menabev.util.ServiceUtil;

@Service
public class PoSearchApiServiceImpl implements PoSearchApiService {
	public static ResponseEntity<?> getO() {
		try {
			Map<String, Object> map = getDestination("SD4_DEST");
			return consumingOdataService(
					"/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_SupplierCompany?$format=json&$filter=%20Supplier%20eq%20%271000047%27%20and%20CompanyCode%20eq%20%271010%27",
					null, "GET", map);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
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

		System.err.println("28 destination");
		HttpClient client = HttpClientBuilder.create().build();

		HttpPost httpPost = new HttpPost(
				"https://menabevdev.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials");
		httpPost.addHeader("Content-Type", "application/json");

		// Encoding username and password
		//Dev Credentials
		String auth = encodeUsernameAndPassword(
				"sb-clone4768d4738f4b49498258b8a01b20230a!b3189|destination-xsappname!b2",
				"2af4f4c4-7265-4d95-b544-01e917937a1e$HlHDn__C2aLbv2PqTcyq251kX4P9QZmZDShfUEFw8NQ=");
		//Test Credentials
//		String auth = encodeUsernameAndPassword(
//				"sb-clone4768d4738f4b49498258b8a01b20230a!b3189|destination-xsappname!b2",
//				"f1ea4794-89be-46ef-a92a-4f92e9115c68$k2beHChqU4bzbhfnR9mqhm2S_nUn7z4PnBHJ4izvbtI=");
//		httpPost.addHeader("Authorization", auth);

		HttpResponse res = client.execute(httpPost);

		System.err.println(" 41 rest" + res);

		String data = getDataFromStream(res.getEntity().getContent());
		if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String jwtToken = new JSONObject(data).getString("access_token");

			System.err.println("jwtdestinationToken " + jwtToken);

			HttpGet httpGet = new HttpGet("https://destination-configuration.cfapps.eu20.hana.ondemand.com"
					+ "/destination-configuration/v1/destinations/" + destinationName);

			httpGet.addHeader("Content-Type", "application/json");

			httpGet.addHeader("Authorization", "Bearer " + jwtToken);

			HttpResponse response = client.execute(httpGet);
			String dataFromStream = getDataFromStream(response.getEntity().getContent());
			System.err.println("dataFromStream : " + dataFromStream);
			if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
				System.err.println("60 " + dataFromStream);
				JSONObject json = new JSONObject(dataFromStream);
				Map<String, Object> mapResponse = json.getJSONObject("destinationConfiguration").toMap();
				mapResponse.put("token", jwtToken);
				return mapResponse;

			}
		}

		return null;
	}

	// public static ResponseEntity<String> getOdata() throws
	// URISyntaxException, IOException {
	// String clientid =
	// "sb-cloneb41bf10568ca4499840711bb8a0f2de4!b3189|connectivity!b5";
	// String clientsecret =
	// "cf792fe9-32f6-496c-aeb6-aec065a33512$WhwgyCaocXG__utqLrg1NJjS3mRwCEGW9VxWDTTniK4=";
	// String proxyHost = "10.0.4.5";
	// int proxyPort = 20003;
	// final SimpleClientHttpRequestFactory factory = new
	// SimpleClientHttpRequestFactory();
	// final InetSocketAddress address = new InetSocketAddress(proxyHost,
	// proxyPort);
	// final Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
	// factory.setProxy(proxy);
	// final RestTemplate restTemplate = new
	// RestTemplateBuilder().requestFactory(() -> factory)
	// .defaultHeader(HttpHeaders.PROXY_AUTHORIZATION, "Bearer " +
	// getConectivityProxy()).build();
	//
	// restTemplate.setRequestFactory(factory);
	//
	// URI uri = null;
	// HttpHeaders requestHeaders = null;
	// HttpEntity<String> entity;
	// String plainCreds = "Syuvraj:Incture@12345";
	// byte[] plainCredsBytes = plainCreds.getBytes();
	// byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
	// String base64Creds = new String(base64CredsBytes);
	//
	// uri = new URI(
	// "http://sd4.menabev.com:443/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_SupplierCompany?$format=json&$filter=%20Supplier%20eq%20%271000047%27%20and%20CompanyCode%20eq%20%271010%27");
	//
	// requestHeaders = new HttpHeaders();
	// requestHeaders.add("Authorization", "Basic " + base64Creds);
	// requestHeaders.add("Content-Type", "application/json;charset=utf-8");
	//
	// ResponseEntity<String> response = null;
	// HttpEntity<String> entity1 = new HttpEntity<String>("parameters",
	// requestHeaders);
	//
	// response = restTemplate.exchange(uri, HttpMethod.GET, entity1,
	// String.class);
	//
	// return response;
	//
	// // JSONObject jsonObj = new JSONObject(System.getenv("VCAP_SERVICES"));
	// // JSONArray jsonArr = jsonObj.getJSONArray("connectivity");
	// // JSONObject connectivityCredentials =
	// // jsonArr.getJSONObject(0).getJSONObject("credentials");
	// //
	// //
	// //
	// // JSONObject jsonObjXsuaa = new
	// // JSONObject(System.getenv("VCAP_SERVICES"));
	// // JSONArray jsonArrXsuaa = jsonObjXsuaa.getJSONArray("xsuaa");
	// // JSONObject xsuaaCredentials =
	// // jsonArrXsuaa.getJSONObject(0).getJSONObject("credentials");
	// //
	// //
	// //
	// // String connProxyHost =
	// // connectivityCredentials.getString("onpremise_proxy_host");
	// // System.out.println("conn proxy host.."+connProxyHost);
	// // int connProxyPort =
	// //
	// Integer.parseInt(connectivityCredentials.getString("onpremise_proxy_http_port"));
	// // System.out.println("conn proxy Port.."+connProxyPort);
	// }

	public static String getConectivityProxy() throws URISyntaxException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(
				"https://menabevdev.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials");
		//Dev Cred
		String auth = encodeUsernameAndPassword("sb-cloneb41bf10568ca4499840711bb8a0f2de4!b3189|connectivity!b5",
				"d56e99cf-76a5-4751-b16b-5e912f1483dc$iVWHjYhERnR-9oYc_ffRYWShcnGbdSdLQ4DOnPcpc5I=");
		
		//Test Cred
//		String auth = encodeUsernameAndPassword("sb-cloneb41bf10568ca4499840711bb8a0f2de4!b3189|connectivity!b5",
//						"b0075a12-8c25-4b14-8c46-64ceeac0ce06$dHgSH9hb4cuHRo2uigbB00FGYHFPTyMI1SDJpXWAPXQ=");
//				
//		httpPost.addHeader("Authorization", auth);
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

		System.err.println("com.incture.utils.HelperClass  + Inside consumingOdataService==================");

		String proxyHost = "10.0.4.5";

		System.err.println("proxyHost-- " + proxyHost);
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
		String jsonMessage = null;
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

					System.err.println("entity " + entity);
					input = new StringEntity(entity);
					input.setContentType("application/json");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				System.err.println("inputEntity " + input);
				((HttpPost) httpRequestBase).setEntity(input);
			}
			if (destinationInfo.get("sap-client") != null) {
				httpRequestBase.addHeader("sap-client", (String) destinationInfo.get("sap-client"));
			}
			httpRequestBase.addHeader("accept", "application/json");

			Header[] headers = getAccessToken(
					(String) destinationInfo.get("URL")
							+ "/sap/opu/odata/sap/ZP2P_API_PODETAILS_SRV/HeaderSet?$filter=Number%20eq%20%274900000015%27&$format=json",
					(String) destinationInfo.get("User"), (String) destinationInfo.get("Password"), httpClient,
					proxyHost, proxyPort, (String) destinationInfo.get("sap-client"), jwToken);
			String token = null;
			List<String> cookies = new ArrayList<>();
			if (headers.length != 0) {

				for (Header header : headers) {

					if (header.getName().equalsIgnoreCase("x-csrf-token")) {
						token = header.getValue();
						System.err.println("token --- " + token);
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

				System.err.println("this is requestBase ============" + Arrays.asList(httpRequestBase));
				httpResponse = httpClient.execute(httpRequestBase);
				System.err.println(
						"com.incture.utils.HelperClass ============" + Arrays.asList(httpResponse.getAllHeaders()));
				System.err.println("com.incture.utils.HelperClass ============" + httpResponse);
				System.err.println("STEP 4 com.incture.utils.HelperClass ============StatusCode from odata hit="
						+ httpResponse.getStatusLine().getStatusCode());
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					json = httpResponse.getAllHeaders();
//					String responseFromECC = getDataFromStream(httpResponse.getEntity().getContent());
//					System.out.println("RESPONSEODATA:::::::::::::;" + responseFromECC);
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					InputStream inputStream = httpResponse.getEntity().getContent();
					byte[] data = new byte[1024];
					int length = 0;
					while ((length = inputStream.read(data, 0, data.length)) != -1) {
						bytes.write(data, 0, length);
					}
					String respBody = new String(bytes.toByteArray(), "UTF-8");
					return new ResponseEntity<String>(respBody, HttpStatus.OK);

				} else {
					String responseFromECC = getDataFromStream(httpResponse.getEntity().getContent());

					System.err.println("responseFromEcc" + responseFromECC);

					return new ResponseEntity<String>("Response from odata call " + httpResponse,
							HttpStatus.BAD_REQUEST);
				}

			} catch (IOException e) {
				System.err.print("IOException : " + e);
				throw new IOException(
						"Please Check VPN Connection ......." + e.getMessage() + " on " + e.getStackTrace()[4]);
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

		System.err.println("313 response " + response);

		// HttpResponse response = client.execute(httpGet);

		return response.getAllHeaders();

	}

	@Override
	public List<PoSearchApiDto> poSearch(PoSearchApiDto dto) {
		List<PoSearchApiDto> response = new ArrayList<>();
		try {
			if (!ServiceUtil.isEmpty(dto.getDocumentNumber())) {
				List<String> documentNumber = new ArrayList<>();
				documentNumber.add(dto.getDocumentNumber());
				response = searchByDocumentNumber(documentNumber,dto);
			} else if (!ServiceUtil.isEmpty(dto.getDeliveryNoteNumber())) {
				response = searchByDeliveryNoteNumber(dto.getDeliveryNoteNumber(),dto);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return response;
		}
		return response;
	}

	public List<PoSearchApiDto> searchByDocumentNumber(List<String> documentNumber, PoSearchApiDto dto)
			throws URISyntaxException, IOException {

		List<PoSearchApiDto> responseDto = new ArrayList<>();
		String filter = "";
		if (!ServiceUtil.isEmpty(documentNumber)) {
			for (String obj : documentNumber) {
				filter = filter + "Number%20eq%20%27" + obj + "%27%20or%20";
			}

			String url = "/sap/opu/odata/sap/ZP2P_API_PODETAILS_SRV/HeaderSet?$filter="
					+ filter.substring(0, filter.length() - 11) + "%27&$format=json";
			Map<String, Object> map = getDestination("SD4_DEST");
			ResponseEntity<?> response = consumingOdataService(url, null, "GET", map);
			System.out.println("RESPONSE:::::::::::::::431:::" + response.getBody());
			JSONObject node = new JSONObject(response.getBody().toString());
			System.out.println("NODE:::::::::::::::::"+ node);
			JSONArray resultsArray = node.getJSONObject("d").getJSONArray("results");
			for (int i = 0; i < resultsArray.length(); i++) {
				PoSearchApiDto resultForResponse = new PoSearchApiDto();
				if (!ServiceUtil.isEmpty(resultsArray.getJSONObject(i).getString("Number"))) {
					resultForResponse.setDocumentNumber(resultsArray.getJSONObject(i).getString("Number"));
				}
				if (!ServiceUtil.isEmpty(resultsArray.getJSONObject(i).getString("CompCode"))) {
					resultForResponse.setCompanyCode(resultsArray.getJSONObject(i).getString("CompCode"));
				}
				if (!ServiceUtil.isEmpty(resultsArray.getJSONObject(i).getString("CreatDate"))) {
					resultForResponse.setCreatedAtRange(resultsArray.getJSONObject(i).getString("CreatDate"));
				}
				if (!ServiceUtil.isEmpty(dto.getDeliveryNoteNumber())) {
					resultForResponse.setDeliveryNoteNumber(dto.getDeliveryNoteNumber());
				}
				if (!ServiceUtil.isEmpty(resultsArray.getJSONObject(i).getString("DocCategory"))) {
					resultForResponse.setDocumentCategory(resultsArray.getJSONObject(i).getString("DocCategory"));
				}
				if (!ServiceUtil.isEmpty(resultsArray.getJSONObject(i).getString("PurchOrg"))) {
					resultForResponse.setPurchaseOrganization(resultsArray.getJSONObject(i).getString("PurchOrg"));
				}
				if (!ServiceUtil.isEmpty(resultsArray.getJSONObject(i).getString("Vendor"))) {
					resultForResponse.setVendorId(resultsArray.getJSONObject(i).getString("Vendor"));
				}
//				if (!ServiceUtil.isEmpty(resultsArray.getJSONObject(i).getString("Vendor"))) {
//					String urlVendorName = "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_Supplier?$format=json&$filter=Supplier%20eq%20%27"+resultsArray.getJSONObject(i).getString("Vendor")+"%27";
//					ResponseEntity<?> responseVendorName = consumingOdataService(urlVendorName, null, "GET", map);
//					JSONObject vNameNode = new JSONObject(responseVendorName.getBody().toString());
//					JSONArray vnameResultsArray = vNameNode.getJSONObject("d").getJSONArray("results");
//					for (int j = 0; i < vnameResultsArray.length(); j++) {
//						if (!ServiceUtil.isEmpty(vnameResultsArray.getJSONObject(j).getString("SupplierName"))) {
//							resultForResponse.setVendorName(vnameResultsArray.getJSONObject(j).getString("SupplierName"));
//						}
//
//					}
//					System.out.println("vendorName ::::"+ responseVendorName );
//					
//				}
				responseDto.add(resultForResponse);
			}
		}
		return responseDto;
	}

	public List<PoSearchApiDto> searchByDeliveryNoteNumber(String deliveryNoteNumber, PoSearchApiDto dto)
			throws URISyntaxException, IOException {
		HashSet<String> uniqueDocumentNumber = new HashSet<String>();
		if (!ServiceUtil.isEmpty(deliveryNoteNumber)) {
			Map<String, Object> map = getDestination("SD4_DEST");
			ResponseEntity<?> response = consumingOdataService(
					"/sap/opu/odata/sap/API_MATERIAL_DOCUMENT_SRV/A_MaterialDocumentHeader?$filter=ReferenceDocument%20eq%20%27"
							+ deliveryNoteNumber + "%27&$expand=to_MaterialDocumentItem&$format=json",
					null, "GET", map);
			System.out.println("RESPONSE:::::::::::::::431:::" + response.getBody());

			JSONObject node = new JSONObject(response.getBody().toString());
			JSONArray resultsArray = node.getJSONObject("d").getJSONArray("results");
			for (int i = 0; i < resultsArray.length(); i++) {
				JSONArray resultsArrayInside = resultsArray.getJSONObject(i).getJSONObject("to_MaterialDocumentItem").getJSONArray("results");
				for (int j = 0; j < resultsArrayInside.length(); j++) {
					if (!ServiceUtil.isEmpty(resultsArrayInside.getJSONObject(j).getString("PurchaseOrder"))) {
						uniqueDocumentNumber.add(resultsArrayInside.getJSONObject(j).getString("PurchaseOrder"));
					}
				}
				

			}

		}
		List<String> documnetNumber = new ArrayList<>();
		if (!ServiceUtil.isEmpty(uniqueDocumentNumber)) {
			documnetNumber.addAll(uniqueDocumentNumber);
		}
		List<PoSearchApiDto> responseDto = new ArrayList<>();
		if (!ServiceUtil.isEmpty(documnetNumber)) {
			 responseDto = searchByDocumentNumber(documnetNumber,dto);
		}
		return responseDto;
	}
	// public static void main(String[] args) throws URISyntaxException,
	// IOException {
	// Map<String, Object> map = getDestination("SD4_DEST");
	//// ResponseEntity<?> response = consumingOdataService(
	//// "/sap/opu/odata/sap/ZP2P_API_PODETAILS_SRV/HeaderSet?$filter=Number%20eq%20%274900000015%27&$format=json",
	//// null, "GET", map);
	// URIBuilder builder = new URIBuilder();
	// builder.setHost("/sap/opu/odata/sap/ZP2P_API_PODETAILS_SRV/HeaderSet")
	// .setParameter("$filter", "Number eq '" + 49000000 +
	// "'").setParameter("$format", "json");
	// builder.build();
	// System.out.println(map);
	//
	// }
	

}
