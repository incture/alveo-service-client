package com.ap.menabev.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainTestClass {
	

	public static void main(String[] args) {
		
		StringBuilder url = new StringBuilder();
		List<String> value = new  ArrayList<String>();
		value.add("DKASYAP");
		value.add("ARUN");
		value.add("SMEGHANA");
	
		
		
		               appendValuesInOdataUrl(url,"PurchaseOrderCreator",value);
		               System.err.println("StringUrl "+url.toString());
	}

	public static void appendValuesInOdataUrl(StringBuilder url,String key, List<String> value){
		for(int i = 0; i<value.size();i++){
			if(value.size()==1){
			url.append(key+"%20eq%20"+"%27"+value.get(i)+"%27");
			System.out.println("26 ");
			}
			else if(i==value.size()-1){
				url.append(key+"%20eq%20"+"%27"+value.get(i)+"%27");
				System.out.println("30 ");
			}else{
				url.append(key+"%20eq%20"+"%27"+value.get(i)+"%27"+"%20or%20");
				System.out.println("29 ");
				
			}
		}
		
	}
	/*
	public static JAXBContext init() throws JAXBException {
        return jaxbContext = JAXBContext.newInstance(JournalEntryCreateRequest.class);
   
	}
	
	
	
	public static Object nonPoToPO (){
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
	
	return null;
	
	}
	
	// test for date 
	
	public static void data(){
		
		 String SEQ_DATE_FORMAT = "MMddyyyy";
			
			LocalDate todaysDate = LocalDate.now();
			
			String seq = "APA-" + todaysDate.format(DateTimeFormatter.ofPattern(SEQ_DATE_FORMAT))
			+"-"+String.format("%08d", 1);
			
			System.err.println("SEQ "+seq);
			
	}*/
}


