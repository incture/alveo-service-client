package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

@Data
public class InboxOutputDto {
	private String requestId;
	private String vendorName;
	private String extInvNum;// added for the tag in xml _InvoiceDate as discussed
	private Long invoiceDate;
	private Long dueDate;
	private String VendorId;
	private String invoiceType;
	private Long sapInvoiceNumber;
	private  String validationStatus; 
	private String invoiceTotal;
	private String taskId;
	private String processor;
	private String status;
	private String claimedAt;
	private String completedAt;
	private String createdAt;
	private List<String> recipientUsers;
	private List<String> recipientGroups;
}
