package com.ap.menabev.serviceimpl;

import java.util.List;

import lombok.Data;

@Data
public class FilterMultipleHeaderSearchDto {
	
	private String requestId;
	private String compCode;
	private Long refDocNum;
	private String extInvNum;// added for the tag in xml _InvoiceDate as discussed
	private Long invoiceDate;
	private Long dueDate;
	private String createdAt;
	private String channelType;
	private String invoiceType;
	private Long createdAtFrom;
	private Long createdAtTo;
	private Long dueDateFrom;
	private Long dueDateTo;
	private Long invoiceDateFrom;
	private Long invoiceDateTo;
	private double invoiceValueFrom;
	private double  invoiceValueTo;
	private String docStatus; /// Exception Status 
	private String taskOwner;
	private String forwaredTaskOwner;
	private boolean isNonPoOrPo;
	private String userId;
	private boolean isBuyerTask;
	private boolean isGrnTask;
	private boolean isClaimed;
	private boolean iscompleted;
	private String myTask; //true to get my task 
	private Long indexNum;
	private Long count;
	private String invoiceTotal;
	private  String roleOfUser; 
	private List<String> vendorName;
	private List<String> VendorId;
	private List<String> assignedTo;
	private  List<String> validationStatus;
	
	
	
	

}
