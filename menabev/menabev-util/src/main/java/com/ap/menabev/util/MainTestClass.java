package com.ap.menabev.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.core4j.xml.XAttribute;
import org.core4j.xml.XDocument;
import org.core4j.xml.XElement;
import org.core4j.xml.XNamespace;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class MainTestClass {
	
	
	private static JAXBContext jaxbContext;

	public static void main(String[] args) throws JsonProcessingException, JAXBException {

	
		String xmlPayload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sfin=\"http://sap.com/xi/SAPSCORE/SFIN\"> "
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
		
	System.err.println("xmlPayload "+xmlPayload);
	
		JournalEntryCreateRequest  journEntry = new JournalEntryCreateRequest();
		MessageHeaderDto MessageHeader = new MessageHeaderDto();
		MessageHeader.setCreationDateTime("2021-05-25T12:37:00.1234567Z");
		JournalEntryDto JournalEntry = new JournalEntryDto();
		ProductTaxItemDto ProductTaxItem = new ProductTaxItemDto();
		JournalEntry.setProductTaxItem(ProductTaxItem);
		List<ItemDto> items =  new ArrayList<ItemDto>();
		ItemDto item = new ItemDto();
	
		item.setCompanyCode("INV");
		items.add(item);
		
		JournalEntry.setAccountingDocumentType("jasdj");
		JournalEntry.setItem(items);
                 
                  journEntry.setJournalEntry(JournalEntry);
                  journEntry.setMessageHeader(MessageHeader);
		
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(journEntry);
		System.err.println("jsonInput "+ json);
	//JSONObject json = new JSONObject(journEntry.toString());
		
	//System.err.println("json =" + json);
		
		
	String retulst = init().toString();
	
	System.err.println("result " + retulst);
	}

	
	
	public static JAXBContext init() throws JAXBException {
        return jaxbContext = JAXBContext.newInstance(JournalEntryCreateRequest.class);
   
	}
	
	
	
	
	// test for date 
	
	public static void data(){
		
		 String SEQ_DATE_FORMAT = "MMddyyyy";
			
			LocalDate todaysDate = LocalDate.now();
			
			String seq = "APA-" + todaysDate.format(DateTimeFormatter.ofPattern(SEQ_DATE_FORMAT))
			+"-"+String.format("%08d", 1);
			
			System.err.println("SEQ "+seq);
			
	}
}



