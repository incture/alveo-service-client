package com.ap.menabev.serviceimpl;

import java.util.List;

import lombok.Data;

@Data
public class FilterMultipleHeaderSearchDto {
	
	private String requestId;
	private String extInvNum;// invoiceNumber
	private Long invoiceDate;
	private Long dueDate;
	private String createdAt;
	private Long createdAtFrom;
	private Long createdAtTo;
	private Long dueDateFrom;
	private Long dueDateTo;
	private Long invoiceDateFrom;
	private Long invoiceDateTo;
	private double invoiceValueFrom;
	private double  invoiceValueTo;
	private String docStatus; /// Exception Status 
	private String userId;
	private String myTask; //true to get my task 
	private Long indexNum;
	private Long count;
	private String invoiceTotal;
	private  String roleOfUser; 
	private List<String> invoiceType;
	private List<String> vendorName;
	private List<String> VendorId;
	private List<String> assignedTo;
	private  List<String> validationStatus;
	
	
	
	

}
