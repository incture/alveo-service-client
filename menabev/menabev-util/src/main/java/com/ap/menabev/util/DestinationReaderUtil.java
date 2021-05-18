package com.ap.menabev.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * @author Arun Gauda This class is used to read destinations from SAP
 */
@Component
public class DestinationReaderUtil {

	private static Logger logger = LoggerFactory.getLogger(DestinationReaderUtil.class);

	public static Map<String, Object> getDestination(String destinationName) throws URISyntaxException, IOException {

		System.err.println("28 destination");
		HttpClient client = HttpClientBuilder.create().build();

		HttpPost httpPost = new HttpPost(MenabevApplicationConstant.DESTINATION_TOKEN_URL);
		httpPost.addHeader("Content-Type", "application/json");

		// Encoding username and password
		String auth = HelperClass.encodeUsernameAndPassword(MenabevApplicationConstant.DESTINATION_CLIENT_ID,
				MenabevApplicationConstant.DESTINATION_CLIENT_SECRET);
		
		
		httpPost.addHeader("Authorization", auth);

		HttpResponse res = client.execute(httpPost);
		
		System.err.println( " 41 rest" + res);

		String data = HelperClass.getDataFromStream(res.getEntity().getContent());
		if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String jwtToken = new JSONObject(data).getString("access_token");
			
			System.err.println("jwtdestinationToken "+jwtToken);

			HttpGet httpGet = new HttpGet(MenabevApplicationConstant.DESTINATION_BASE_URL
					+ "/destination-configuration/v1/destinations/" + destinationName);

			httpGet.addHeader("Content-Type", "application/json");

			httpGet.addHeader("Authorization", "Bearer " + jwtToken);

			HttpResponse response = client.execute(httpGet);
			String dataFromStream = HelperClass.getDataFromStream(response.getEntity().getContent());
			System.err.println("dataFromStream : " + dataFromStream);
			if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
               System.err.println("60 "+dataFromStream);
				JSONObject json = new JSONObject(dataFromStream);
				Map<String, Object> mapResponse = json.getJSONObject("destinationConfiguration").toMap();
				mapResponse.put("token",jwtToken );
				return mapResponse;

			}
		}
		

		return null;
	}
	
	public static String getConectivityProxy() throws URISyntaxException, IOException {

		System.err.println("77 destination");
		HttpClient client = HttpClientBuilder.create().build();

		HttpPost httpPost = new HttpPost(MenabevApplicationConstant.DESTINATION_TOKEN_URL);
		httpPost.addHeader("Content-Type", "application/json");

		// Encoding username and password
		String auth = HelperClass.encodeUsernameAndPassword(MenabevApplicationConstant.CONECTIVITY_CLIENT_ID,
				MenabevApplicationConstant.CONECTIVITY_CLIENT_SECRET);
		
		
		httpPost.addHeader("Authorization", auth);

		HttpResponse res = client.execute(httpPost);
		
		System.err.println( " 92 rest" + res);

		String data = HelperClass.getDataFromStream(res.getEntity().getContent());
		if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String jwtToken = new JSONObject(data).getString("access_token");
			
			System.err.println("jwtProxyToken "+jwtToken);

			
				return jwtToken;

			}
		
		

		return null;
	}
	
	public static String getJwtTokenForAuthenticationForSapApi() throws URISyntaxException, IOException {
		System.err.println("77 destination");
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(MenabevApplicationConstant.WORKFLOW_TOKEN_URL);
		httpPost.addHeader("Content-Type", "application/json");
		// Encoding username and password
		String auth = HelperClass.encodeUsernameAndPassword(MenabevApplicationConstant.WORKFLOW_CLIENT_ID,
				MenabevApplicationConstant.WORKFLOW_CLIENT_SECRETE);
		httpPost.addHeader("Authorization", auth);
		HttpResponse res = client.execute(httpPost);
		System.err.println( " 92 rest" + res);
		String data = HelperClass.getDataFromStream(res.getEntity().getContent());
		if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String jwtToken = new JSONObject(data).getString("access_token");
			System.err.println("jwtProxyToken "+jwtToken);
				return jwtToken;
			}
		return null;
	}
	
	

}
