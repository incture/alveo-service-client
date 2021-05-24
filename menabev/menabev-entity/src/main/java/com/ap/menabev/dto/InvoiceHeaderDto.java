package com.ap.menabev.dto;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class InvoiceHeaderDto {


	private String requestId;

	private String vendorName;

	private String ocrBatchId;

	private String headerText;

	private String compCode;

	private Long refDocNum;

	private String extInvNum;
	// added for the tag in xml _InvoiceDate as discussed
	private Long invoiceDate;
	
	private Long dueDate;

	private String createdAt;

	private String VendorId;

	private Integer clerkId;

	private String clerkEmail;

	private String channelType;

	private String refDocCat;

	private String invoiceType;

	private String invoiceTotal;

	private Long sapInvoiceNumber;

	private String fiscalYear;

	private String currency;

	private String paymentTerms;

	private String taxAmount;

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
	
	private String grossAmount;
	private String balance;
	private String reasonForRejection;//R1,R2,R3
	private String rejectionText;//Duplicate Invoice,Vendor Id mismatch
	private String createdByInDb;
	private Long createdAtInDB;
	private Long updatedBy;
	private Long updatedAt;
	private Long postingDate;
	private String assignedTo;
	private String accountNumber;
	private String disAmt;
	private String deposit;
	private String subTotal;
	private String clearingAccountingDocument;
	private Long clearingDate;
	private String paymentStatus;
	private String paymentBlock;
	private String paymentBlockDesc;
	private String accountingDoc;
	private String invoiceDateFrom;
	private String invoiceDateTo;
	private Long postingDateFrom;
	private Long postingDateTo;
	private String docStatus;
	private String filterFor;
	private Boolean balanceCheck;
	private  String validationStatus; /// Exception Status 
	private String taskOwner;
	private String forwaredTaskOwner;
	private boolean isNonPoOrPo;
	private boolean isClaimed;
	private String claimedUser;
	private List<InvoiceItemDto> invoiceItems;
	private List<AttachmentDto> attachments;
	private List<CommentDto> comments;
	private String manualpaymentBlock;

}
