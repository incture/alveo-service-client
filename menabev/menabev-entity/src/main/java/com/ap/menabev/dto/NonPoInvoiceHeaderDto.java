package com.ap.menabev.dto;



import java.util.List;

import lombok.Data;

public @Data class NonPoInvoiceHeaderDto {

	private String requestId;
	private String compCode;
	private String refDocNum;
	private String extInvNum;
	private String createdAt;
	private String vendorId;
	private String clerkId;
	private String clerkEmail;
	private String channelType;
	private String refDocCat;
	private String invoiceType;
	private String invoiceTotal;
	private String sapInvoiceNumber;
	private String fiscalYear;
	private String currency;
	private String paymentTerms;
	private String taxAmount;
	private String shippingCost;
	private String lifecycleStatus;
	private String taskStatus;
	private String version;
	private String emailFrom;
	private String grossAmount;
	private String balance;
	private String reasonForRejection;
	private String createdByInDb;
	private String createdAtInDB;
	private String updatedBy;
	private String updatedAt;
	private String accountNumber;
	private String id;
	private String invoiceLineTotal;
	private String vendorName;
	private String ocrBatchId;
	private String headerText;
	private List<NonPoInvoiceHeaderDto> invoiceItems;
	private List<NonPoCostAllocationDto> costAllocation;

}
