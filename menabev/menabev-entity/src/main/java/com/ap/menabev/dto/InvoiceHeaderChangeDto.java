package com.ap.menabev.dto;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class InvoiceHeaderChangeDto {
	
	   private String  requestId;
		private String   invoiceType;
		private String   transactionType;
		private String   refPurcahseDocument;
		private String  vendorId;
		private String  vendorName;
		private String    vatRegistrationId;
		private String    companyCode;
		private String    currency;
		private boolean   rejected;
		private String   reasonForRejection;
		private String     invoiceRefNum;
		private String    documentDate;
		private String     baselineDate;
		private String     dueDate;
		private String    postingDate;
		private String     paymentTerms;
		private String     paymentMethod;
		private String    paymentBlock;
		private String  unplannedCost;
		private double  invoiceAmount ;
		private String  headerTaxcode;
		private double  taxAmount;
		private String  systemSuggestedtaxAmount;
		private double  grossAmount;
	    private double  balanceAmount;
		private String  invoiceStatus;
		private String   approvalStatus;
		private String   documentId;
		private String   workflowId;
		private String   taskId;
		private String   taskOwner;
		private boolean  isheaderchanged;
		private boolean   isItemChanged;
	    private boolean   isCommentsChanged;
	    private boolean   isCostAllocationChanged;
	    private String ocrBatchId;
	    private String headerText;
	    private String compCode;
	    private Long refDocNum;
	    private String extInvNum;
	    private Long invoiceDate; // added for the tag in xml _InvoiceDate as discussed
	    private LocalDateTime createdAt;
	    private Integer clerkId;
	    private String clerkEmail;
	    private String channelType;
	    private String refDocCat;
	   private String invoiceTotal;
	   private Long sapInvoiceNumber;
	   private String fiscalYear;
	   private Double shippingCost;
	   private String lifecycleStatus;
	   private String lifecycleStatusText;
	   private String taskStatus;
	   private Integer version;
	   private String emailFrom;
	   private String invoiceTotalFrom;
	private String invoiceTotalTo;
	private String createdAtFrom;
	private String createdAtTo;
	private BigInteger createdOn;
	private Long dueDateFrom;
	private String dueDateTo;
	private String balance;
	private String rejectionText;// Duplicate Invoice,Vendor Id mismatch
	private String createdByInDb;
	private Long createdAtInDB;
	private Long updatedBy;
	private Long updatedAt;
	private String assignedTo;
	private String accountNumber;
	private String disAmt;
	private String deposit;
	private String subTotal;
	private String clearingAccountingDocument;
	private Long clearingDate;
	private String paymentStatus;
	private String paymentBlockDesc;
	private String accountingDoc;
	private String invoiceDateFrom;
	private String invoiceDateTo;
	private Long postingDateFrom;
	private Long postingDateTo;
	private String docStatus;
	private String filterFor;
	private Boolean balanceCheck;
	private String validationStatus; /// Exception Status
	private String forwaredTaskOwner;
	private boolean isnonPoOrPo;
	private boolean isclaimed;
	private String claimedUser;
	private String manualpaymentBlock;
	private String taskOwnerId; // loggedin userid
	private String deliveryNote;
	private String amountBeforeTax;
	private String taxCode;
	private String taxRate;
	private String surcharge;
	private String discount;
	private String vatRegNum;
	private String plannedCost;
	private String baseLine;
	private String invoicePdfId;
	
	private List<InvoiceItemsDto> invoiceItems;
	private List<CostAllocationsDto> costAllocation;
	private List<ActivityLogDto> activityLog;
	private List<CommentDto> comment;    
	private List<AttachmentDto> attachment; 
	private  List<RemediationUser> remediationUserList; 
    private List<TaxDataDto> taxData;
	private List<HeaderMessageDto>  headerMessages;
	 
	  


}
