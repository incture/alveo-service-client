package com.ap.menabev.serviceimpl;

import java.util.List;

import lombok.Data;

@Data
public class FilterMultipleHeaderSearchDto {
	
	private String requestId;
	private String extInvNum;// invoiceNumber
	private Long dueDateFrom;
	private Long dueDateTo;
	private Long invoiceDateFrom;
	private Long invoiceDateTo;
	private Double invoiceValueFrom;
	private Double  invoiceValueTo;
	private List<String> taskStatus; // add value only when to see ready or reserved task.
	private long top;
	private long skip;
	private  String roleOfUser; 
	private List<String> invoiceType;//f
	private List<String> VendorId;//f
	private List<String> assignedTo;//f
	private  List<String> validationStatus;//f
	
	
	
	

}
