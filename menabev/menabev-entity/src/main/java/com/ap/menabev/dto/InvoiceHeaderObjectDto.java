package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InvoiceHeaderObjectDto {

	private String requestId;

	private String vendorId;

	private String companyCode;

	private Long invoiceDate;

	private String invoiceReference;

	private String invoiceAmount;

	private String invoiceStatus;
	
	private Boolean isDuplicate;
	
	private HeaderMessageDto messages;
}