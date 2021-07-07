package com.ap.menabev.serviceimpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ap.menabev.util.OdataHelperClass;
import com.ap.menabev.util.ServiceUtil;

@Service
public class ThreeWay {
	private static final Logger logger = LoggerFactory.getLogger(ThreeWay.class);

	public ResponseEntity<?> threeWayMatchOdataCall() {
		try {
	           
            ResponseEntity<?> responsePost  = new ResponseEntity<>("Failed",HttpStatus.BAD_REQUEST);
             // call Destination Service
           Map<String, Object> destinationInfo = OdataHelperClass.getDestination("SD4_DEST");
          
           String endPointurl = "/sap/opu/odata/sap/ZP2P_API_THREEWAYMATCH_SRV/MatchHeaderSet";
           String entity ="{\"d\":{\"Vendor\":\"1000031\",\"ToMatchInput\":{\"results\":[{\"Vendor\":\"1000031\",\"InvoiceItem\":\"000001\",\"PurchasingDocumentNumber\":\"4100000127\",\"PurchasingDocumentItem\":\"00010\",\"BaseUnit\":\"KG\",\"DntrConvOpuOu\":\"1\",\"NmtrConvOpuOu\":\"1\",\"QtyOrdPurReq\":\"100\",\"PostingDate\":\"20.05.2021\",\"CompanyCode\":\"1010\",\"ReferenceDocument\":\"5000000267\",\"Quantity\":\"200\",\"NetValDC\":\"400.00\",\"NoQuantityLogic\":\"\",\"ItemCategory\":\"0\",\"QtyInvoiced\":\"0\",\"ReturnsItem\":\"\",\"DrCrInd\":\"S\",\"SubDrCrInd\":\"\",\"CurrencyKey\":\"SAR\",\"GrBasedIvInd\":\"X\",\"QtyGoodsReceived\":\"100\",\"GoodsReceiptInd\":\"X\",\"TranslationDate\":\"20.05.2021\",\"UpdatePODelCosts\":\"\",\"PostInvInd\":\"X\",\"DelItemAllocInd\":\"X\",\"RetAllocInd\":\"X\",\"IvOrigin\":\"\",\"QtyOpu\":\"200\",\"InvValFC\":\"0.00\",\"EstPriceInd\":\"\",\"GrNonValInd\":\"\",\"NetValSrvFC\":\"0\",\"AmtDC\":\"400\",\"ValGrFC\":\"400\",\"NewInputVal\":\"\"},{\"Vendor\":\"1000031\",\"InvoiceItem\":\"000002\",\"PurchasingDocumentNumber\":\"4100000127\",\"PurchasingDocumentItem\":\"00010\",\"BaseUnit\":\"KG\",\"DntrConvOpuOu\":\"1\",\"NmtrConvOpuOu\":\"1\",\"QtyOrdPurReq\":\"100\",\"PostingDate\":\"20.05.2021\",\"CompanyCode\":\"1010\",\"ReferenceDocument\":\"5000000267\",\"Quantity\":\"10\",\"NetValDC\":\"400.00\",\"NoQuantityLogic\":\"\",\"ItemCategory\":\"0\",\"QtyInvoiced\":\"0\",\"ReturnsItem\":\"\",\"DrCrInd\":\"S\",\"SubDrCrInd\":\"\",\"CurrencyKey\":\"SAR\",\"GrBasedIvInd\":\"X\",\"QtyGoodsReceived\":\"100\",\"GoodsReceiptInd\":\"X\",\"TranslationDate\":\"20.05.2021\",\"UpdatePODelCosts\":\"\",\"PostInvInd\":\"X\",\"DelItemAllocInd\":\"X\",\"RetAllocInd\":\"X\",\"IvOrigin\":\"\",\"QtyOpu\":\"10\",\"InvValFC\":\"0.00\",\"EstPriceInd\":\"\",\"GrNonValInd\":\"\",\"NetValSrvFC\":\"0\",\"AmtDC\":\"400\",\"ValGrFC\":\"400\",\"NewInputVal\":\"\"}]},\"ToMatchOutput\":{\"results\":[]}}}";
           // call odata service
       // ResponseEntity<?>     header =  OdataHelperClass.fetchAccessToken(endPointurl,null,"GET", destinationInfo);
         // System.err.println("headers =" + header);
         ResponseEntity<?> responseFromOdata =   OdataHelperClass.getOdataServiceToken(endPointurl,null,"GET", destinationInfo);   
          if(responseFromOdata.getStatusCodeValue()==200){   
         Header[] header = (Header[]) responseFromOdata.getBody();
        
            responsePost   =  OdataHelperClass.postOdataServiceToken(endPointurl, entity,"POST", destinationInfo,header );
          }
           return responsePost;
           //return header;
          
       } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>("Error" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public static Header[] getAccessToken(String url, String username, String password, HttpClient client,
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

	public static String encodeUsernameAndPassword(String username, String password) {
		String encodeUsernamePassword = username + ":" + password;
		String auth = "Basic " + DatatypeConverter.printBase64Binary(encodeUsernamePassword.getBytes());
		return auth;
	}
	
	public static String getConectivityProxy() throws URISyntaxException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(
				"https://menabevdev.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials");
		String auth = encodeUsernameAndPassword("sb-cloneb41bf10568ca4499840711bb8a0f2de4!b3189|connectivity!b5",
				"b0075a12-8c25-4b14-8c46-64ceeac0ce06$dHgSH9hb4cuHRo2uigbB00FGYHFPTyMI1SDJpXWAPXQ=");
		httpPost.addHeader("Authorization", auth);
		HttpResponse res = client.execute(httpPost);
		String data = getDataFromStream(res.getEntity().getContent());
		if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String jwtToken = new JSONObject(data).getString("access_token");
			return jwtToken;
		}
		return null;
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

	private String threeWayOdataPosting(HttpClient client, String url, String payload, Header[] headers)
			throws UnsupportedEncodingException, IOException, ClientProtocolException, ParseException {
		if (headers.length != 0) {

			HttpPost httpPost = new HttpPost(url);

			String token = null;
			List<String> cookies = new ArrayList<>();
			for (Header header : headers) {

				if (header.getName().equalsIgnoreCase("x-csrf-token")) {
					token = header.getValue();
				}

				if (header.getName().equalsIgnoreCase("set-cookie")) {
					cookies.add(header.getValue());
				}

			}
			httpPost.addHeader("Accept", "application/json");
			if (token != null) {

				httpPost.addHeader("x-csrf-token", token);

				httpPost.addHeader("Content-Type", "application/json");

				if (!cookies.isEmpty()) {
					for (String cookie : cookies) {

						String tmp = cookie.split(";", 2)[0];
						httpPost.addHeader("Cookie", tmp);

					}
				}

				if (payload != null) {
					StringEntity jsonEntity = new StringEntity(payload);
					jsonEntity.setContentType("application/json");
					httpPost.setEntity(jsonEntity);
					System.err.println(jsonEntity + "********");

					HttpResponse response = client.execute(httpPost);
					logger.error("response_CODE" + response.getStatusLine().getStatusCode());
					logger.error("Response from ODATA-." + response);

					if (201 == response.getStatusLine().getStatusCode()
							|| 200 == response.getStatusLine().getStatusCode()) {
						String responseFromECC = ServiceUtil.getDataFromStream(response.getEntity().getContent());
						logger.error("responseFromECC:::::" + responseFromECC);
						return responseFromECC;

					} else {
						logger.error("responseFromECC:::::getStatusCode" + response.getStatusLine().getStatusCode());
						return "internal server error";
					}

				} else {
					return "PAYLOAD_IS_INVALID";
				}
			} else {
				return "TOKEN_GEN_FAILED";
			}
		} else {
			return "NO_HEADERS_ADDED";
		}
	}
}
