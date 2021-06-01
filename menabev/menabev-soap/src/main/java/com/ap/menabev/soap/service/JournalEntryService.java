package com.ap.menabev.soap.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;


import com.ap.menabev.soap.journalcreatebinding.JournalEntryCreateConfirmationBulkMessage;
import com.ap.menabev.soap.journalcreatebinding.JournalEntryCreateRequestBulkMessage;


@Service
public class JournalEntryService {

	@Autowired
	private Jaxb2Marshaller marshaller;

	private WebServiceTemplate template;

	public JournalEntryCreateConfirmationBulkMessage postJournalEntryToSap(JournalEntryCreateRequestBulkMessage requestMessage) throws IOException, URISyntaxException {
		Map<String, String> properties = new HashMap<String, String>();
		String encoded = encodeUsernameAndPassword("Syuvraj",
				"Incture@12345");
		String jwtToken = getConectivityProxy();
//		properties.put("Authorization", encoded);
		properties.put("Proxy-Authorization", "Bearer " +jwtToken);
//		properties.put("SAP-Connectivity-SCC-Location_ID","incture");
		marshaller.setMarshallerProperties(properties);
		template = new WebServiceTemplate(marshaller);
		JournalEntryCreateConfirmationBulkMessage journalEntryCreateConfirmationMessage = (JournalEntryCreateConfirmationBulkMessage) template
				.marshalSendAndReceive(
						"" + "https://sd4.menabev.com:443"
								+ "/sap/bc/srt/xip/sap/journalentrycreaterequestconfi/100/journalcreateservice/journalcreatebinding",
						requestMessage);
		
			return journalEntryCreateConfirmationMessage;
	}
	
	public ResponseEntity<?> postNonPoItemsToSAP() throws IOException, URISyntaxException {
//		System.err.println("input "+inputDto.toString());
//		Map<String, Object> destinationInfo = getDestination("https://vhmeasd4ci.hec.menabev.com:44300/sap/bc/srt/xip/sap/journalentrycreaterequestconfi/100/journalcreateservice/journalcreatebinding");
		
//		System.err.println("destinationInfo "+destinationInfo);
		//set Url
		String url = "https://vhmeasd4ci.hec.menabev.com:44300"
		//String url = "https://sd4.menabev.com:443"
				+"/sap/bc/srt/xip/sap/journalentrycreaterequestconfi/100/journalcreateservice/journalcreatebinding";
		// form payload into a string entity 
		
		String  entity  = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sfin=\"http://sap.com/xi/SAPSCORE/SFIN\"> "
				+ "<soapenv:Header/> "
				+ "   <soapenv:Body>    "
				+ "   <sfin:JournalEntryBulkCreateRequest>  "
				+ "        <MessageHeader>     "
				+ "        <CreationDateTime>2021-05-25T12:37:00.1234567Z</CreationDateTime>     "
				+ "     </MessageHeader>      "
				+ "    <JournalEntryCreateRequest>   "
				+ "          <MessageHeader>      "
				+ "          <CreationDateTime>2021-05-25T12:37:00.1234567Z</CreationDateTime>    "
				+ "         </MessageHeader>      "
				+ "       <JournalEntry>        "
				+ "        <OriginalReferenceDocumentType>BKPFF</OriginalReferenceDocumentType>   "
				+ "             <OriginalReferenceDocument/>       "
				+ "         <OriginalReferenceDocumentLogicalSystem/>      "
				+ "          <BusinessTransactionType>RFBU</BusinessTransactionType>       "
				+ "         <AccountingDocumentType>KR</AccountingDocumentType>         "
				+ "       <DocumentReferenceID>INV12345</DocumentReferenceID>         "
				+ "       <CreatedByUser>SYUVRAJ</CreatedByUser>         "
				+ "       <CompanyCode>1010</CompanyCode>         "
				+ "       <DocumentDate>2021-05-25</DocumentDate>      "
				+ "          <PostingDate>2021-05-25</PostingDate>        "
				+ "        <Item>            "
				+ "       <GLAccount>0005500046</GLAccount>       "
				+ "            <CompanyCode>1010</CompanyCode>        "
				+ "           <AmountInTransactionCurrency currencyCode=\"SAR\">100.00</AmountInTransactionCurrency> 		"
				+ "		  <Tax>             "
				+ "         <TaxCode>I1</TaxCode>        "
				+ "           </Tax>          "
				+ "         <AccountAssignment>        "
				+ "              <CostCenter>0000521001</CostCenter>       "
				+ "            </AccountAssignment>        "
				+ "        </Item>      "
				+ "          <Item>      "
				+ "             <GLAccount>0006021003</GLAccount>      "
				+ "             <CompanyCode>1010</CompanyCode>      "
				+ "             <AmountInTransactionCurrency currencyCode=\"SAR\">100.00</AmountInTransactionCurrency> 		"
				+ "		  <Tax>            "
				+ ""
				+ "          <TaxCode>I1</TaxCode>    "
				+ "               </Tax>				   "
				+ "                  <AccountAssignment>          "
				+ "            <CostCenter>0000111001</CostCenter>         "
				+ "          </AccountAssignment>      "
				+ "          </Item>			      "
				+ ""
				+ "             <CreditorItem>      "
				+ "             <ReferenceDocumentItem>1</ReferenceDocumentItem>       "
				+ "            <Creditor>0001000030</Creditor>           "
				+ "        <AmountInTransactionCurrency currencyCode=\"SAR\">-230.00</AmountInTransactionCurrency>    "
				+ "            </CreditorItem> 		"
				+ "	   <ProductTaxItem>         "
				+ "          <TaxCode>I1</TaxCode>          "
				+ "         <AmountInTransactionCurrency currencyCode=\"SAR\">30.00</AmountInTransactionCurrency>  "
				+ ""
				+ "    <TaxBaseAmountInTransCrcy currencyCode=\"SAR\">200.00</TaxBaseAmountInTransCrcy> 	"
				+ "			  <ConditionType>MWVS</ConditionType> 		"
				+ "	   </ProductTaxItem>     		"
				+ "	                </JournalEntry>       "
				+ "   </JournalEntryCreateRequest>   "
				+ "    </sfin:JournalEntryBulkCreateRequest>  "
				+ "  </soapenv:Body> </soapenv:Envelope>";
		// call odata method 
//		stringToDom(entity);
		ResponseEntity<?> responseFromOdata = consumingOdataService(url, entity, "POST", null);
		System.err.println("odata output "+ responseFromOdata);
	 return responseFromOdata;

	}
//	public static File stringToDom(String xmlSource) 
//	        throws IOException {
//		File file = new File("file.xml");
//	    java.io.FileWriter fw = new java.io.FileWriter(file);
//	    fw.write(xmlSource);
//	    fw.close();
//	    return file;
//	}
	
	public static String getJwtTokenForAuthenticationForSapApi() throws URISyntaxException, IOException {
		System.err.println("77 destination");
		String jwtToken = null;
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost("https://menabevdev.authentication.eu20.hana.ondemand.com");
		httpPost.addHeader("Content-Type", "application/json");
		// Encoding username and password
		String auth = encodeUsernameAndPassword("sb-cloneb41bf10568ca4499840711bb8a0f2de4!b3189|connectivity!b5",
				"cf792fe9-32f6-496c-aeb6-aec065a33512$WhwgyCaocXG__utqLrg1NJjS3mRwCEGW9VxWDTTniK4=");
		httpPost.addHeader("Authorization", auth);
		HttpResponse res = client.execute(httpPost);
		System.err.println( " 92 rest" + res);
		String data = getDataFromStream(res.getEntity().getContent());
		if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			jwtToken = new JSONObject(data).getString("access_token");
			System.err.println("jwtProxyToken "+jwtToken);
				return jwtToken;
			}
		return jwtToken;
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

		HttpClient client = HttpClientBuilder.create().build();

		HttpPost httpPost = new HttpPost("https://sd4.menabev.com:443/sap/bc/srt/xip/sap/journalentrycreaterequestconfi/100/journalcreateservice/journalcreatebinding");
		httpPost.addHeader("Content-Type", "application/json");

		// Encoding username and password
		String auth = encodeUsernameAndPassword("Syuvraj",
				"Incture@12345");
		httpPost.addHeader("Authorization", auth);

		HttpResponse res = client.execute(httpPost);
		System.out.println("RESPONSE :::::::::::::" + res);

		String data = getDataFromStream(res.getEntity().getContent());
		if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String jwtToken = new JSONObject(data).getString("access_token");

			HttpGet httpGet = new HttpGet("/sap/bc/srt/xip/sap/journalentrycreaterequestconfi/100/journalcreateservice/journalcreatebinding"
					+ "destination-configuration/v1/destinations/" + destinationName);

			httpGet.addHeader("Content-Type", "application/json");

			httpGet.addHeader("Authorization", "Bearer " + jwtToken);

			HttpResponse response = client.execute(httpGet);
			String dataFromStream = getDataFromStream(response.getEntity().getContent());
			if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {

				JSONObject json = new JSONObject(dataFromStream);
				return json.getJSONObject("destinationConfiguration").toMap();

			}
		}

		return null;
	}
	
	public static ResponseEntity<?> consumingOdataService(String url, String entity, String method,
			Map<String, Object> destinationInfo) throws IOException, URISyntaxException {


		System.err.println("com.incture.utils.HelperClass  + Inside consumingOdataService==================");
		
//		String proxyHost = "connectivityproxy.internal.cf.eu20.hana.ondemand.com";
		String proxyHost = "10.0.4.5";
		System.err.println("proxyHost-- " + "10.0.4.5");
		int proxyPort = 20003;
		Header[] jsonResponse = null;
		String objresult = null;
		
//		JSONObject jsonObj = new JSONObject(System.getenv("VCAP_SERVICES"));
		
//		System.err.println("116 - jsonObj =" + jsonObj);
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort),
			    new UsernamePasswordCredentials( "Syuvraj", "Incture@12345")); 
		
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
		 String jwToken = getConectivityProxy();
		 System.out.println("741 jwt Token "+ jwToken);
		if (url != null) {
			if (method.equalsIgnoreCase("GET")) {
				httpRequestBase = new HttpGet(url);
			} else if (method.equalsIgnoreCase("POST")) {
				httpRequestBase = new HttpPost(url);
				File file = new File("file.xml");
			    java.io.FileWriter fw = new java.io.FileWriter(file);
			    fw.write(entity);
			    fw.close();
//				InputStream targetStream = new ByteArrayInputStream(entity.getBytes());
//				((HttpPost) httpRequestBase).setEntity(new InputStreamEntity(targetStream));
			    ((HttpPost) httpRequestBase).setEntity(new InputStreamEntity(new FileInputStream(file), file.length()));
			    httpRequestBase.addHeader("Content-type", "text/xml; charset=UTF-8");
				file.delete(); 

			}
//			if (destinationInfo.get("sap-client") != null) {
				httpRequestBase.addHeader("sap-client", "100");
//			}
		httpRequestBase.addHeader("Content-Type", "text/xml");
			/*
			Header[] headers = getAccessToken("https://sd4.menabev.com:443/sap/bc/srt/xip/sap/journalentrycreaterequestconfi/100/journalcreateservice/journalcreatebinding","Syuvraj",
					"Incture@12345",httpClient,  proxyHost, proxyPort,
					"100",jwToken);*/
			String token = null;
			List<String> cookies = new ArrayList<>();
			/*if(headers.length != 0){
			
			for (Header header : headers) {

				if (header.getName().equalsIgnoreCase("x-csrf-token")) {
					token = header.getValue();
					System.err.println("token --- " + token);
				}

				if (header.getName().equalsIgnoreCase("set-cookie")) {
					cookies.add(header.getValue());
				}

			}
			}*/

//			if (destinationInfo.get("User") != null && destinationInfo.get("Password") != null) {
				String encoded = encodeUsernameAndPassword("Syuvraj",
						"Incture@12345");
				httpRequestBase.addHeader("Authorization", encoded);
				httpRequestBase.setHeader("Proxy-Authorization","Bearer " +jwToken);
				httpRequestBase.addHeader("SAP-Connectivity-SCC-Location_ID","incture");
				
//			}
			/*if (token != null) {
				//httpRequestBase.addHeader("X-CSRF-Token", token);
			}
			if (!cookies.isEmpty()) {
				for (String cookie : cookies) {
					String tmp = cookie.split(";", 2)[0];
					httpRequestBase.addHeader("Cookie", tmp);
				}
			}*/
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
					String responseFromECC = httpResponse.getEntity().getContent().toString();
					
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
	
	public static String getConectivityProxy() throws URISyntaxException, IOException {

		System.err.println("77 destination");
		HttpClient client = HttpClientBuilder.create().build();

		HttpPost httpPost = new HttpPost("https://menabevdev.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials");
		httpPost.addHeader("Content-Type", "application/json");

		// Encoding username and password
		String auth = encodeUsernameAndPassword("sb-cloneb41bf10568ca4499840711bb8a0f2de4!b3189|connectivity!b5",
				"cf792fe9-32f6-496c-aeb6-aec065a33512$WhwgyCaocXG__utqLrg1NJjS3mRwCEGW9VxWDTTniK4=");
		
		
		httpPost.addHeader("Authorization", auth);

		HttpResponse res = client.execute(httpPost);
		
		System.err.println( " 92 rest" + res);

		String data = getDataFromStream(res.getEntity().getContent());
		if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String jwtToken = new JSONObject(data).getString("access_token");
			
			System.err.println("jwtProxyToken "+jwtToken);

			
				return jwtToken;

			}
		
		

		return null;
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
}
