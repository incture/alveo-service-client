package com.ap.menabev.dto;

import lombok.Data;

@Data
public class FilterHeaderDto {
	
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
	private String channelType;
	private String invoiceType;
	private String createdAtFrom;
	private String createdAtTo;
	private Long dueDateFrom;
	private String dueDateTo;
	private String assignedTo;
	private String invoiceDateFrom;
	private String invoiceDateTo;
	private String invoiceValueFrom;
	private String invoiceValueTo;
	private String docStatus;
	private  String validationStatus; /// Exception Status 
	private String taskOwner;
	private String forwaredTaskOwner;
	private boolean isNonPoOrPo;
	private String userId;
	private boolean isBuyerTask;
	private boolean isGrnTask;
	private boolean isClaimed;
	private boolean iscompleted;
	private boolean myTask; //true to get my task 
	private Long indexNum;
	private Long count;
	

}
