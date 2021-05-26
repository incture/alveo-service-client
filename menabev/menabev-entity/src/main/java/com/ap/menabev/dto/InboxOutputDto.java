package com.ap.menabev.dto;

import lombok.Data;

@Data
public class InboxOutputDto {
	
	private String requestId;

	private String vendorName;
	
	private String compCode;

	private Long refDocNum;

	private String extInvNum;
	// added for the tag in xml _InvoiceDate as discussed
	private Long invoiceDate;
	
	private Long dueDate;

	private String createdAt;

	private String VendorId;
	private String invoiceType;
	private Long sapInvoiceNumber;
	
	private String taskStatus;
	
	private String invoiceTotalFrom;

	private String invoiceTotalTo;

	private String createdAtFrom;

	private String createdAtTo;
	
	private Long dueDateFrom;
	
	private String dueDateTo;
	
	private  String validationStatus; 
	
	private String invoiceTotal;
	
	private boolean isClaimed;
	
	private String claimedUser;
	
	private String taskId;
	
	private int totalCount;

}
