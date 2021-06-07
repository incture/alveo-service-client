package com.ap.menabev.soap.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.commons.io.IOUtils;
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
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.ap.menabev.soap.journalcreatebinding.ChartOfAccountsItemCode;
import com.ap.menabev.soap.journalcreatebinding.JournalEntryCreateConfirmationBulkMessage;
import com.ap.menabev.soap.journalcreatebinding.JournalEntryCreateRequestBulkMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;


@Service
public class JournalEntryService extends WebServiceGatewaySupport {

	/*@Autowired
	private Jaxb2Marshaller marshaller;
	
	private JAXBContext jaxbContext;*/
    
	/*public void init() throws JAXBException {
        jaxbContext = JAXBContext.newInstance("com.ap.menabev.soap.journalcreatebinding",JournalEntryCreateRequestBulkMessage.class.getClassLoader());
   System.err.println("jaxbContext" + jaxbContext);
	}*/
	
	public JournalEntryCreateConfirmationBulkMessage postJournalEntryToSap(JournalEntryCreateRequestBulkMessage requestMessage) throws IOException, URISyntaxException, JAXBException {
		WebServiceTemplate template = getWebServiceTemplate();
		//marshaller.setContextPath("com.ap.menabev.soap.journalcreatebinding");
		String url = "https://sd4.menabev.com:443"
				+"/sap/bc/srt/xip/sap/journalentrycreaterequestconfi/100/journalcreateservice/journalcreatebinding";
		HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender(createHttpClient());
		
		//template.setMarshaller(marshaller);
		template.setMessageSender(messageSender);
		    System.err.println("soap template"+ template);
		    final SoapActionCallback soapActionCallback = new SoapActionCallback("http://sap.com/xi/SAPSCORE/SFIN/JournalEntryCreateRequestConfirmation_In/JournalEntryCreateRequestConfirmation_InRequest");
		JournalEntryCreateConfirmationBulkMessage journalEntryCreateConfirmationMessage = (JournalEntryCreateConfirmationBulkMessage) template
				.marshalSendAndReceive(url,
						requestMessage,soapActionCallback );
		System.err.println("Soap "+journalEntryCreateConfirmationMessage.getLog());
			return journalEntryCreateConfirmationMessage;
	}
	
	
	
	
/*	private String marshall(final Object jaxbObject) throws JAXBException {
		  init();
	     final Marshaller marshaller = jaxbContext.createMarshaller();
	     marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	     final StringWriter stringWriter = new StringWriter();
	     marshaller.marshal(jaxbObject, stringWriter);
	     final String xmlString = stringWriter.toString();
	     System.err.println("Serialised JAXB entity to XML:\n" + xmlString);
	     return xmlString;
	} */
	
	
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
		
		 ChartOfAccountsItemCode chartVale1 =  new ChartOfAccountsItemCode();
		    chartVale1.setValue("0005500046");
		 requestMessage.getJournalEntryCreateRequest().get(0).getJournalEntry().getItem().get(0).setGLAccount(chartVale1);
		    ChartOfAccountsItemCode chartVale2 =  new ChartOfAccountsItemCode();
		    chartVale2.setValue("0006021003");
		 requestMessage.getJournalEntryCreateRequest().get(0).getJournalEntry().getItem().get(1).setGLAccount(chartVale2);
		
		String url = "http://sd4.menabev.com:443"
				+"/sap/bc/srt/xip/sap/journalentrycreaterequestconfi/100/journalcreateservice/journalcreatebinding";
		String  entityTest  = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sfin=\"http://sap.com/xi/SAPSCORE/SFIN\"> "
				+ "<soapenv:Header/> "
				+ "   <soapenv:Body>    "
				+ "<sfin:JournalEntryBulkCreateRequest>"
				+ "<MessageHeader>"
				+ "<CreationDateTime>2021-05-25T12:37:00.123Z</CreationDateTime>"
				+ "</MessageHeader>"
				+ "<JournalEntryCreateRequest>"
				+ "<MessageHeader>"
				+ "<CreationDateTime>2021-05-25T12:37:00.123Z</CreationDateTime>"
				+ "</MessageHeader>"
				+ "<JournalEntry>"
				+ "<OriginalReferenceDocumentType>BKPFF</OriginalReferenceDocumentType>"
				//+ "             <OriginalReferenceDocument/>       "
				//+ "         <OriginalReferenceDocumentLogicalSystem/>      "
				+ "<OriginalReferenceDocument></OriginalReferenceDocument>"
				+ "<OriginalReferenceDocumentLogicalSystem></OriginalReferenceDocumentLogicalSystem>"
				+ "<BusinessTransactionType>RFBU</BusinessTransactionType>"
				+ "<AccountingDocumentType>KR</AccountingDocumentType>"
				+ "<DocumentReferenceID>NONPOINV1</DocumentReferenceID>"
				+ "<CreatedByUser>SYUVRAJ</CreatedByUser>"
				+ "<CompanyCode>1010</CompanyCode>"
				+ "<DocumentDate>2021-05-25Z</DocumentDate>"
				+ "<PostingDate>2021-05-25Z</PostingDate>"
				+ "<Item>"
				+ "<CompanyCode>1010</CompanyCode>        "
				+ "<GLAccount>0005500046</GLAccount>       "
				+ "<AmountInTransactionCurrency currencyCode=\"SAR\">100.00</AmountInTransactionCurrency> 		"
				+ "<Tax>             "
				+ "<TaxCode>I1</TaxCode>        "
				+ "</Tax>          "
				+ "<AccountAssignment>        "
				+ "<CostCenter>0000521001</CostCenter>       "
				+ "</AccountAssignment>        "
				+ "</Item>      "
				+ "<Item>      "
				+ "<CompanyCode>1010</CompanyCode>      "
				+ "<GLAccount>0006021003</GLAccount>      "
				+ "<AmountInTransactionCurrency currencyCode=\"SAR\">100.00</AmountInTransactionCurrency> 		"
				+ "<Tax>"
				
				+ "<TaxCode>I1</TaxCode>    "
				+ "</Tax>				   "
				+ "<AccountAssignment>          "
				+ "<CostCenter>0000111001</CostCenter>         "
				+ "</AccountAssignment>      "
				+ "</Item>			      "
				
				+ "<CreditorItem>      "
				+ "<ReferenceDocumentItem>1</ReferenceDocumentItem>       "
				+ "<Creditor>0001000030</Creditor>           "
				+ "<AmountInTransactionCurrency currencyCode=\"SAR\">-230.00</AmountInTransactionCurrency>    "
				+ "</CreditorItem> 		"
				+ "<ProductTaxItem>         "
				+ "<TaxCode>I1</TaxCode>          "
				+ "<AmountInTransactionCurrency currencyCode=\"SAR\">30.00</AmountInTransactionCurrency>  "
				
				+ "<TaxBaseAmountInTransCrcy currencyCode=\"SAR\">200.00</TaxBaseAmountInTransCrcy> 	"
				+ "<ConditionType>MWVS</ConditionType> 		"
				+ "</ProductTaxItem>     		"
				+ "</JournalEntry>       "
				+ "</JournalEntryCreateRequest>   "
				+ "    </sfin:JournalEntryBulkCreateRequest>  "
				+ "  </soapenv:Body> </soapenv:Envelope>";
		System.err.println("entityTest "+entityTest);
		 JAXBContext contextObj = JAXBContext.newInstance(JournalEntryCreateRequestBulkMessage.class); 
		    Marshaller marshallerObj = contextObj.createMarshaller(); 
		    //marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false); 
		    marshallerObj.setProperty(Marshaller.JAXB_FRAGMENT, true);
		    StringWriter sw = new StringWriter();
		    marshallerObj.marshal(requestMessage,sw);  
		   
		    String xmlContent = sw.toString();
		    
		   // System.err.println("xmlContent "+xmlContent);
		    int  indexOfHeader = xmlContent.indexOf("<MessageHeader>");
		    int indexOfEnd =  xmlContent.indexOf("</JournalEntryCreateRequest>");
		    System.err.println("indexOfHeaderTag "+indexOfHeader);
		    System.err.println("indexOfEnd "+indexOfEnd);
		    String croppedContent = xmlContent.substring(indexOfHeader, indexOfEnd);
		   // System.err.println("croppedContent "+croppedContent);
		  // croppedContent = croppedContent.replaceAll("\"", "\\\\\"");
		    String  entity  = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sfin=\"http://sap.com/xi/SAPSCORE/SFIN\"> "
					+ "<soapenv:Header/> "
					+ "   <soapenv:Body>    "
					+ "   <sfin:JournalEntryBulkCreateRequest>  "
					+croppedContent
					+ "   </JournalEntryCreateRequest>   "
					+ "    </sfin:JournalEntryBulkCreateRequest>  "
					+ "  </soapenv:Body> </soapenv:Envelope>";
		   System.err.println("entity "+entity);
		   
		   
		   // String entity  = new String(fs.toByteArray());
		// call odata method 
		ResponseEntity<?> responseFromOdata = consumingOdataService(url, entity, "POST", null);
		System.err.println("odata output "+ responseFromOdata);
	 return responseFromOdata;
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
			    //((HttpPost) httpRequestBase).setEntity(new InputStreamEntity(new FileInputStream(file), file.length()));
			  
			    StringEntity stringEntity = new StringEntity(entity, "UTF-8");
			    stringEntity.setChunked(true);
			    ((HttpPost) httpRequestBase).setEntity(stringEntity);
			    /* InputStream ins =   ((HttpPost) httpRequestBase).getEntity().getContent();
			    String result = IOUtils.toString(ins, StandardCharsets.UTF_8);
			    System.err.println("result ="+result);*/
			   // httpRequestBase.addHeader("Content-type", "text/xml; charset=UTF-8");
				file.delete(); 
			}
				httpRequestBase.addHeader("sap-client", "100");
		httpRequestBase.addHeader("Content-Type", "text/xml");
		httpRequestBase.addHeader("SOAPAction","http://sap.com/xi/SAPSCORE/SFIN/JournalEntryCreateRequestConfirmation_In/JournalEntryCreateRequestConfirmation_InRequest");
				String encoded = encodeUsernameAndPassword("Syuvraj",
						"Incture@12345");
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
					return new ResponseEntity<String>("Response from odata call "+httpResponse,HttpStatus.BAD_REQUEST);
				} else {
					String responseFromECC =getDataFromStream( httpResponse.getEntity().getContent());
					System.err.println("responseFromEcc"+responseFromECC);
					return new ResponseEntity<String>("Response from odata call "+httpResponse,HttpStatus.BAD_REQUEST);
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

		HttpPost httpPost = new HttpPost("https://menabevdev.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials");
		/*HttpPost httpPost = new HttpPost("https://menabev-p2pautomation-test.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials");
		httpPost.addHeader("Content-Type", "application/json");*/

		// Encoding username and password
		String auth = encodeUsernameAndPassword("sb-cloneb41bf10568ca4499840711bb8a0f2de4!b3189|connectivity!b5",
				"cf792fe9-32f6-496c-aeb6-aec065a33512$WhwgyCaocXG__utqLrg1NJjS3mRwCEGW9VxWDTTniK4=");
		
		/*String auth = encodeUsernameAndPassword("sb-clone38f786be563c4447b1ac03fe5831a53f!b3073|connectivity!b5",
				"f761e7fd-0dac-4614-b5f5-752d3513b71a$Obl12NQPIxXrGa9-OZiB_wIjmQaB-bb7Dyfq_ccKWfM=");
		*/
		
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
	
}
