package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
 
@Getter
@Setter
@ToString
public class TrackInvoiceOutputDto {
	
	/*private String referenceInvoiceNumber;
	private String sapInvoiceReceiptNumber;
	private long invoiceDate;
	private long postingDate;
	private String paymentTerms;
	private long netDuaDate;
	private long clearingDate;
	private String clearingDocument;
	private String paymentStatus;
	private String blockKey;
	private String blockDescription;*/
	
   /* "ReferenceInvoiceNumber" : "INV-NONPO2",		
    "SapInvoiceReceiptNumber" : "1900000083",		
    "InvoiceDate" : "20210524",		
    "PostingDate" : "20210524",		
    "PaymentTerms" : "V007",		
    "NetDueDate" : "20210723",		
    "ClearingDate" : "00000000",		
    "ClearingDocument" : "",		
    "PaymentStatus" : "BLOCKED",		
    "BlockKey" : "A",		
    "BlockDescription" : "Blocked for payment"		*/
	private String requestId; //0
private String invoiceNumber;
private long inoviceDate;
private long requestCreatedOn;
private String vendorId;
private String vendorName;
private String companyCode;
private String grossAmount;
private String taxAmount;
private String invoiceTotal;
private String invoiceStatus;
private String invoiceStatusText;
private long dueDate;
private long postingDate;
private long clearingDate;
private String paymentRef;
private String reasonOfRejectionCode;
private String reasonofRejectionText;
private String paymentTerms;
private String channel;
}
