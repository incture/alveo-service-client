package com.ap.menabev.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HelperClass {
	
	
	public static ResponseEntity<?> consumingOdataService(String url, String entity, String method,
			Map<String, Object> destinationInfo) throws IOException, URISyntaxException {


		System.err.println("com.incture.utils.HelperClass  + Inside consumingOdataService==================");
		
		String proxyHost = "10.0.4.5";
		
		System.err.println("proxyHost-- " + proxyHost);
		int proxyPort = 20003;
		Header[] jsonResponse = null;
		String objresult = null;
		
//		JSONObject jsonObj = new JSONObject(System.getenv("VCAP_SERVICES"));
		
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort),
			    new UsernamePasswordCredentials( (String) destinationInfo.get("User"), (String) destinationInfo.get("Password"))); 
		
		HttpClientBuilder clientBuilder =  HttpClientBuilder.create();
		
		clientBuilder.setProxy(new HttpHost(proxyHost, proxyPort))
		   .setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy())
		   .setDefaultCredentialsProvider(credsProvider) 
		   .disableCookieManagement();
		
		HttpClient httpClient = clientBuilder.build();
		HttpRequestBase httpRequestBase = null;
		String  jsonMessage=null;
		HttpResponse httpResponse = null;
		StringEntity input = null;
		Header [] json = null;
		JSONObject obj = null;
		 String jwToken = DestinationReaderUtil.getConectivityProxy();
		if (url != null) {
			if (method.equalsIgnoreCase("GET")) {
				httpRequestBase = new HttpGet(url);
			} else if (method.equalsIgnoreCase("POST")) {
				httpRequestBase = new HttpPost(url);
				try {
					
					System.err.println("entity "+entity);
					input = new StringEntity(entity);
					input.setContentType("application/json");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				System.err.println("inputEntity "+ input);
				((HttpPost) httpRequestBase).setEntity(input);
			}
			if (destinationInfo.get("sap-client") != null) {
				httpRequestBase.addHeader("sap-client", (String) destinationInfo.get("sap-client"));
			}
			httpRequestBase.addHeader("accept", "application/json");
			
			Header[] headers = getAccessToken((String) destinationInfo.get("URL") + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_SupplierCompany?$format=json&$filter= Supplier eq '1000047' and CompanyCode eq '1010'", (String) destinationInfo.get("User"),
					(String) destinationInfo.get("Password"),httpClient,  proxyHost, proxyPort,
					(String) destinationInfo.get("sap-client"),jwToken);
			String token = null;
			List<String> cookies = new ArrayList<>();
			if(headers.length != 0){
			
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
				String encoded = HelperClass.encodeUsernameAndPassword((String) destinationInfo.get("User"),
						(String) destinationInfo.get("Password"));
				httpRequestBase.addHeader("Authorization", encoded);
				httpRequestBase.setHeader("Proxy-Authorization","Bearer " +jwToken);
				httpRequestBase.addHeader("SAP-Connectivity-SCC-Location_ID",(String) destinationInfo.get("CloudConnectorLocationId"));
				
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
//			if (tenantctx != null) {
//				httpRequestBase.addHeader("SAP-Connectivity-ConsumerAccount",
//						tenantctx.getTenant().getAccount().getId());
//			}
			try {
				
				
				System.err.println("this is requestBase ============" + Arrays.asList(httpRequestBase));
				httpResponse = httpClient.execute(httpRequestBase);
				System.err.println(
						"com.incture.utils.HelperClass ============" + Arrays.asList(httpResponse.getAllHeaders()));
				System.err.println(
						"com.incture.utils.HelperClass ============" + httpResponse);
				System.err.println("STEP 4 com.incture.utils.HelperClass ============StatusCode from odata hit="
						+ httpResponse.getStatusLine().getStatusCode());
				if (httpResponse.getStatusLine().getStatusCode() == 201) {
					json = httpResponse.getAllHeaders();
					for (Header header : json) {
						if (header.getName().equalsIgnoreCase("sap-message")) {
							String message  = header.getValue();
							System.err.println("message --- " + message);
							JSONObject jsonOutput = new JSONObject(message);
							  jsonMessage  = jsonOutput.getString("message");
							System.err.println("jsonMessage "+jsonMessage);
						}
					}
			
				} else {
					String responseFromECC = httpResponse.getEntity().toString();
					
					System.err.println("responseFromEcc"+responseFromECC);
					
					
					return new ResponseEntity<String>("Response from odata call "+httpResponse,HttpStatus.BAD_REQUEST);
				}

				System.err.println("STEP 5 Result from odata hit ============" + json);

			} catch (IOException e) {
				System.err.print("IOException : " + e);
				throw new IOException(
						"Please Check VPN Connection ......." + e.getMessage() + " on " + e.getStackTrace()[4]);
			}

			try {
				
				System.err.println("jsonOutput"+json);

				System.err.println("jsonHeaderResponse"+jsonResponse);
			
				
			} catch (JSONException e) {
				System.err.print("JSONException : check " + e + "JSON Object : " + json);
				
				throw new JSONException(
						"Exception occured during json conversion" + e.getMessage() + " on " + e.getStackTrace()[4]);
			}

		}

		return new ResponseEntity<String>(jsonMessage,HttpStatus.OK);

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
	
	
	private static Header[] getAccessToken(String url, String username, String password, HttpClient client,
			String proxyHost, int proxyPort, String sapClient,String token)
			throws ClientProtocolException, IOException {

		
  
		HttpGet httpGet = new HttpGet(url);
		
       String userpass = username + ":" + password;
       
       httpGet.setHeader("Proxy-Authorization","Bearer " +token);
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,
                                        "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes()));
		// Encoding username and password
		httpGet.addHeader("X-CSRF-Token", "Fetch");
		httpGet.addHeader("Content-Type", "application/json");
		httpGet.addHeader("sap-client", sapClient);
		httpGet.addHeader("SAP-Connectivity-SCC-Location_ID","incture");


		HttpResponse response = client.execute(httpGet);
		//HttpResponse response = client.execute( httpGet);
		
		System.err.println("313 response "+ response);

		// HttpResponse response = client.execute(httpGet);

		return response.getAllHeaders();

	}
	
	public static boolean checkString(String s) {
		if (s == null || s.equals("") || s.trim().isEmpty() || s.matches("") || s.equals(null)) {
			return true;
		}
		return false;
	}
	
	
	public ResponseEntity<?>  taskCompleteWorkflowApi(String payload,String taskid) {
		try {
			
			System.err.println("Input payload"+ payload);
			String token = DestinationReaderUtil.getJwtTokenForAuthenticationForSapApi();
			HttpClient client = HttpClientBuilder.create().build();
			String url = MenabevApplicationConstant.WORKFLOW_REST_BASE_URL + "/v1/task-instances/" + taskid;
				// if user is not empty and not null task will be claimed.
				//payload = "{\"processor\":\"" + dto.getUserId() + "\"}";
				System.err.println("payload=" + payload);
			HttpPatch httpPatch = new HttpPatch(url);
			httpPatch.addHeader("Authorization", "Bearer " + token);
			httpPatch.addHeader("Content-Type", "application/json");
			try {
				StringEntity entity = new StringEntity(payload);
				System.err.println("inputEntity " + entity);
				entity.setContentType("application/json");
				httpPatch.setEntity(entity);
				HttpResponse response = client.execute(httpPatch);
				if (response.getStatusLine().getStatusCode() == HttpStatus.NO_CONTENT.value()) {
					
						return new ResponseEntity<String>( getDataFromStream(response.getEntity().getContent()),HttpStatus.CREATED);
					} 
				 else {
					return new ResponseEntity<String>(
							"Failed due " + getDataFromStream(response.getEntity().getContent()),
							HttpStatus.BAD_REQUEST);
				}
			} catch (IOException e) {
				System.err.println("Exception "+e.getMessage());
				return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			System.err.println("Exception "+e.getMessage());
			return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
