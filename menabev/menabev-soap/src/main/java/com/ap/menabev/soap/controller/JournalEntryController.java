package com.ap.menabev.soap.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.soap.SOAPException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.soap.journalcreatebinding.ChartOfAccountsItemCode;
import com.ap.menabev.soap.journalcreatebinding.JournalEntryCreateRequestBulkMessage;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@RestController
@RequestMapping("/soap")
public class JournalEntryController {
	//@Autowired
	//JournalEntryService journalEntryService;
	
	
/*
	@PostMapping("/postJournalEntryNonPo")
	public ResponseEntity<?> postJournalEntryToSapHttpClient(@RequestBody JournalEntryCreateRequestBulkMessage requestMessage) throws URISyntaxException, IOException, JAXBException, SOAPException{
		System.out.println("ReQUEST PAyloaD sOAP::::::"+requestMessage.getMessageHeader().getCreationDateTime());
		return journalEntryService.postNonPoItemsToSAP(requestMessage);
	}
	*/
	@PostMapping("/postJournalEntryString")
	public ResponseEntity<Resource> postJournalEntryStringEntity(@RequestBody JournalEntryCreateRequestBulkMessage requestMessage) throws URISyntaxException, IOException, JAXBException, SOAPException{
		System.out.println("ReQUEST PAyloaD sOAP::::::"+requestMessage.getMessageHeader().getCreationDateTime());
		
		String  entityTest  = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sfin=\"http://sap.com/xi/SAPSCORE/SFIN\"> "
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
		
		File file = new File("file.xml");
	    java.io.FileWriter fw = new java.io.FileWriter(file);
	    fw.write(entityTest);
	    fw.close();
		
	    InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

	    return ResponseEntity.ok()
	            .contentLength(file.length())
	            .contentType(MediaType.APPLICATION_OCTET_STREAM)
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
	            .body(resource);
		//return entityTest;
	}
	
	@PostMapping("/postJsonToXml")
	public  ResponseEntity<Resource>  postJsonToXml(@RequestBody JournalEntryCreateRequestBulkMessage requestMessage) throws Exception{
		System.out.println("ReQUEST PAyloaD sOAP::::::"+requestMessage.getMessageHeader().getCreationDateTime());
		XmlMapper xmlMapper = new XmlMapper();
		 String xml = xmlMapper.writeValueAsString(requestMessage);
		    System.err.println("java to xml"+xml);
		  // jaxbContext 
		    ChartOfAccountsItemCode chartVale1 =  new ChartOfAccountsItemCode();
		    chartVale1.setValue("0005500046");
		 requestMessage.getJournalEntryCreateRequest().get(0).getJournalEntry().getItem().get(0).setGLAccount(chartVale1);
		    ChartOfAccountsItemCode chartVale2 =  new ChartOfAccountsItemCode();
		    chartVale2.setValue("0006021003");
		 requestMessage.getJournalEntryCreateRequest().get(0).getJournalEntry().getItem().get(1).setGLAccount(chartVale2);
		  System.err.println("chartVale1 "+chartVale1.getValue());
		  System.err.println("chartVale2 "+chartVale1.getValue());
		  
		  JAXBContext contextObj = JAXBContext.newInstance(JournalEntryCreateRequestBulkMessage.class); 
		    
		    Marshaller marshallerObj = contextObj.createMarshaller(); 
		    
		    marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); 
		    marshallerObj.setProperty(Marshaller.JAXB_FRAGMENT, true);
		   // marshallerObj.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		   // soap enevlope 
		   // MessageFactory messageFactory = MessageFactory.newInstance();
           // SOAPMessage soapMessage = messageFactory.createMessage();
            //soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION,"true");
           /* SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
            SOAPBody soapBody = soapEnvelope.getBody();*/
		    //
		    StringWriter sw = new StringWriter();
		    marshallerObj.marshal(requestMessage,sw);  
		    String xmlContent = sw.toString();
		    System.err.println("xmlContent "+xmlContent);
		    int  indexOfHeader = xmlContent.indexOf("<MessageHeader>");
		   int indexOfEnd =  xmlContent.indexOf("</JournalEntryCreateRequest>");
		    System.err.println("indexOfHeaderTag "+indexOfHeader);
		    System.err.println("indexOfEnd "+indexOfEnd);
		    String croppedContent = xmlContent.substring(indexOfHeader, indexOfEnd);
		    
		  croppedContent = croppedContent.replaceAll("\"", "\\\\\"");
		    
		    System.err.println("escapeString"+ croppedContent);

		    
		    System.err.println("croppedContent "+croppedContent);
		    String  entity  = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sfin=\"http://sap.com/xi/SAPSCORE/SFIN\"> "
					+ "<soapenv:Header/> "
					+ "   <soapenv:Body>    "
					+ "   <sfin:JournalEntryBulkCreateRequest>  "
					+croppedContent
					+ "   </JournalEntryCreateRequest>   "
					+ "    </sfin:JournalEntryBulkCreateRequest>  "
					+ "  </soapenv:Body> </soapenv:Envelope>";
		    
		    
		    
		   /* soapBody.setEncodingStyle("UTF-8");
		    soapBody.setValue(xmlContent);
		    soapMessage.saveChanges();
		    ByteArrayOutputStream  fs =  new ByteArrayOutputStream ();
		    soapMessage.writeTo(fs);
		    String resultXml  = new String(fs.toByteArray());
		    
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    Document doc = builder.parse(new InputSource(new StringReader(resultXml)));

		    // Write the parsed document to an xml file
		    TransformerFactory transformerFactory = TransformerFactory.newInstance();
		    Transformer transformer = transformerFactory.newTransformer();
		    DOMSource source = new DOMSource(doc);

		File    file  = new File("my-file.xml");
		    StreamResult resultConverted =  new StreamResult(file);
		    transformer.transform(source, resultConverted);
		    
		 
		  
		    
		    String str = FileUtils.readFileToString(file, "UTF-8");
		    
		    System.err.println("soapMessage "+ str);
		    
*/		   File file = new File("file.xml");
java.io.FileWriter fw = new java.io.FileWriter(file);
fw.write(entity);
fw.close();

InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

return ResponseEntity.ok()
        .contentLength(file.length())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        .body(resource);
		    
		   // return entity;
		//return file;

	  
	}
	
	

}
