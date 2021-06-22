package com.ap.menabev.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class InvoiceHeaderCheckDto {
	private String requestID;
	private String vendorID;
	private String companyCode;
	private String invoiceReference;
	private Double invoiceAmount;
	private Double grossAmount;
	private Double taxAmount;
	private Double balanceAmount;
	private String taxCode;
	private String currency;
	private Long invoiceDate;
	private Long postingDate;
	private Long baselineDate;
	private String dueDate;
	private String paymentTerms;
	private String invoiceStatus;
	private String invoiceType;
	private Long discountedDueDate1;
	private Long discountedDueDate2;
	private Double systemSuggestedtaxAmount;
	private ChangeIndicator changeIndicator;
	private List<HeaderMessageDto> messages;
	
}
