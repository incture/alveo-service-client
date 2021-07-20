package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString 
public class OdataTrackInvoiceObject {

	private String referenceInvoiceNumber;
	private String sapInvoiceReceiptNumber;
	private long invoiceDate;
	private long postingDate;
	private String paymentTerms;
	private long netDuaDate;
	private long clearingDate;
	private String clearingDocument;
	private String paymentStatus;
	private String blockKey;
	private String blockDescription;
	private String paymentReference;
}
