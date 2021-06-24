package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TrackInvoiceOutputDto {
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
