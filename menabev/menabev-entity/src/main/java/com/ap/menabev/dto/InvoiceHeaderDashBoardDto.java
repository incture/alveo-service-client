package com.ap.menabev.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InvoiceHeaderDashBoardDto {

	private String requestId;
	private String vendorName;
	
	private String docStatus;
	
	private String ocrBatchId;
	private String headerText;
	private String id;
	private String extInvNum;
	// added for the tag in xml _InvoiceDate as discussed
	private String invoiceDate;
	private String dueDate;
	private String createdAt;
	private String vendorId;
	private Integer clerkId;
	private String compCode;
	private Long refDocNum;
	private String clerkEmail;
	private String channelType;
	private String refDocCat;
	private String invoiceType;
	private String grossAmount;
	private String discount;
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
	private String balance;
	private String reasonForRejection;//R1,R2,R3
	private String rejectionText;//Duplicate Invoice,Vendor Id Mismatch
	private MessageDto exceptionMessage;

	private String createdByInDb;
	private Long createdAtInDB;
	private String updatedBy;
	private Long updatedAt;
	private String accountNumber;
	private Long postingDate;
	private String assignedTo;
	
    private String disAmt;
	
	private String deposit;
	private String subTotal;
	private String accountingDoc;
	private String clearingAccountingDocument;
	private String clearingDate;
	private String paymentStatus;
	private String paymentBlock;
	private String paymentBlockDesc;
	private Boolean balanceCheck;
	private String manualpaymentBlock;
	

}
