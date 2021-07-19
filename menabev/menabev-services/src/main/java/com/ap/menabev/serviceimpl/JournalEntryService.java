package com.ap.menabev.serviceimpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.soap.SOAPException;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.ap.menabev.soap.journalcreatebinding.JournalEntryCreateRequestBulkMessage;
import com.ap.menabev.soap.journalcreatebinding.Root;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class JournalEntryService extends WebServiceGatewaySupport {

	
	
	
	public  HttpClient createHttpClient() throws URISyntaxException, IOException {
		
		String jwtToken = getConectivityProxy();
	        HttpHost proxy = new HttpHost("127.0.0.1", 8080);
	    List<Header> headers = new ArrayList<>();
	    List<BasicHeader> basicHeader = new ArrayList<>();
	    basicHeader.add(new BasicHeader("Authorization", "Basic " +"U3l1dnJhajpJbmN0dXJlQDEyMzQ1"));
	    basicHeader.add(new BasicHeader("Content-type", "text/xml; charset=UTF-8"));
	    basicHeader.add(new BasicHeader("sap-client", "100"));
	    basicHeader.add(new BasicHeader("Proxy-Authorization","Bearer " +jwtToken));
	    basicHeader.add(new BasicHeader("SAP-Connectivity-SCC-Location_ID","DEVHEC"));
	    headers.addAll(basicHeader);
	    // add more header as more as needed
	    RequestDefaultHeaders reqHeader = new RequestDefaultHeaders(headers);
	    CloseableHttpClient httpClient = 
	        HttpClients.custom().setProxy(proxy)
	            .addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor())
	            .addInterceptorLast(reqHeader)
	            .build();
	    
	    System.err.println("SOAP httpClient" + httpClient);
	    return httpClient;
	}
	
	public ResponseEntity<?> postNonPoItemsToSAP(JournalEntryCreateRequestBulkMessage requestMessage) throws IOException, URISyntaxException, JAXBException, SOAPException {
	
		String entity = formXmlPayload(requestMessage);
		   
		String url = "http://sq4.menabev.com:443"
				+"/sap/bc/srt/xip/sap/journalentrycreaterequestconfi/300/journalcreateservice/journalcreatebinding";
		   // String entity  = new String(fs.toByteArray());
		// call odata method 
		ResponseEntity<?> responseFromOdata = consumingOdataService(url, entity, "POST", null);
		System.err.println("odata output "+ responseFromOdata);
	 return responseFromOdata;
	}

	
	
public String formXmlPayload(JournalEntryCreateRequestBulkMessage requestMessage){
		
	System.err.println("requestMessage for xml payload "+requestMessage);
		try {
			String entity ="";
		    JAXBContext contextObj = JAXBContext.newInstance(JournalEntryCreateRequestBulkMessage.class); 
		    Marshaller marshallerObj = contextObj.createMarshaller(); 
		    marshallerObj.setProperty(Marshaller.JAXB_FRAGMENT, true);
		    StringWriter sw = new StringWriter();
		    marshallerObj.marshal(requestMessage,sw);  
		    String xmlContent = sw.toString();
		    int  indexOfHeader = xmlContent.indexOf("<MessageHeader>");
		    int indexOfEnd =  xmlContent.indexOf("</JournalEntryCreateRequest>");
		    System.err.println("indexOfHeaderTag "+indexOfHeader);
		    System.err.println("indexOfEnd "+indexOfEnd);
		    String croppedContent = xmlContent.substring(indexOfHeader, indexOfEnd);
		      entity  = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sfin=\"http://sap.com/xi/SAPSCORE/SFIN\"> "
					+ "<soapenv:Header/> "
					+ "   <soapenv:Body>    "
					+ "   <sfin:JournalEntryBulkCreateRequest>  "
					+croppedContent
					+ "   </JournalEntryCreateRequest>   "
					+ "    </sfin:JournalEntryBulkCreateRequest>  "
					+ "  </soapenv:Body> </soapenv:Envelope>";
		   System.err.println("entity "+entity);
		   
			return entity;
		}catch(Exception e){
			System.err.println("Exception in formXmlPayload "+ e);
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
	
	
	
	public static ResponseEntity<?> consumingOdataService(String url, String entity, String method,
			Map<String, Object> destinationInfo) throws IOException, URISyntaxException {


		System.err.println("com.incture.utils.HelperClass  + Inside consumingOdataService==================");
		String proxyHost = "10.0.4.5";
		System.err.println("proxyHost-- " + "10.0.4.5");
		int proxyPort = 20003;
//		JSONObject jsonObj = new JSONObject(System.getenv("VCAP_SERVICES"));
		
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort),
			    new UsernamePasswordCredentials( "Syuvraj", "Incture@123")); 
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
			    StringEntity stringEntity = new StringEntity(entity, "UTF-8");
			    stringEntity.setChunked(true);
			    ((HttpPost) httpRequestBase).setEntity(stringEntity);
				
			}
				httpRequestBase.addHeader("sap-client", "300");
		        httpRequestBase.addHeader("Content-Type", "text/xml");
		        httpRequestBase.addHeader("SOAPAction","http://sap.com/xi/SAPSCORE/SFIN/JournalEntryCreateRequestConfirmation_In/JournalEntryCreateRequestConfirmation_InRequest");
				String encoded = encodeUsernameAndPassword("Syuvraj","Incture@123");
				httpRequestBase.addHeader("Authorization", encoded);
				httpRequestBase.setHeader("Proxy-Authorization","Bearer " +jwToken);
				httpRequestBase.addHeader("SAP-Connectivity-SCC-Location_ID","DEVHEC");
			try {
				System.err.println("this is requestBase ============" + Arrays.asList(httpRequestBase));
				httpResponse = httpClient.execute(httpRequestBase);
				System.err.println(
						"com.incture.utils.HelperClass ============" + Arrays.asList(httpResponse.getAllHeaders()));
				System.err.println(
						"com.incture.utils.HelperClass ============" + httpResponse);
				System.err.println("STEP 4 com.incture.utils.HelperClass ============StatusCode from odata hit="
						+ httpResponse.getStatusLine().getStatusCode());
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					
					String responseFromECC =getDataFromStream( httpResponse.getEntity().getContent());
					System.err.println("responseFromEcc"+responseFromECC);
					return new ResponseEntity<String>(responseFromECC,HttpStatus.OK);
				} else {
					String responseFromECC =getDataFromStream( httpResponse.getEntity().getContent());
					System.err.println("responseFromEcc"+responseFromECC);
					return new ResponseEntity<String>(responseFromECC,HttpStatus.BAD_REQUEST);
				}
			} catch (IOException e) {
				System.err.print("IOException : " + e);
				throw new IOException(
						"Please Check VPN Connection ......." + e.getMessage() + " on " + e.getStackTrace()[4]);
			}
			

		}
		return new ResponseEntity<String>(jsonMessage,HttpStatus.OK);
	}
	
	public static String getConectivityProxy() throws URISyntaxException, IOException {
		System.err.println("77 destination");
		HttpClient client = HttpClientBuilder.create().build();
		//HttpPost httpPost = new HttpPost("https://menabevdev.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials");
		HttpPost httpPost = new HttpPost("https://menabev-p2pautomation-test.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials");
		httpPost.addHeader("Content-Type", "application/json");
		// Encoding username and password
		//Dev
		/*String auth = encodeUsernameAndPassword("sb-cloneb41bf10568ca4499840711bb8a0f2de4!b3189|connectivity!b5",
				"d56e99cf-76a5-4751-b16b-5e912f1483dc$iVWHjYhERnR-9oYc_ffRYWShcnGbdSdLQ4DOnPcpc5I=");
	*/	//qa
		String auth = encodeUsernameAndPassword("sb-clone38f786be563c4447b1ac03fe5831a53f!b3073|connectivity!b5",
				"9c5a2d59-abb3-4c8f-bdba-c3b0222ceb25$iBxUjgTsDHnBRBuByPBR7qfSnY77pLPYV-_QZkhzC5I=");
		
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
	
	public  Root outPutResponseFromOdataResponse(String responsePayload ) throws JAXBException, IOException, SOAPException{
        JSONObject json      =   XML.toJSONObject(responsePayload); 
        System.err.println("parsedJson "+json);
		      ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
          Root  soapRootResponse = objectMapper.readValue(json.toString(), Root.class);
        System.err.println("soapRootResponse "+soapRootResponse);
        // convert it into a JounralEntry 
       
        return soapRootResponse;
	 }
}
