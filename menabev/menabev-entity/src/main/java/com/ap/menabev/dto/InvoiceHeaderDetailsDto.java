package com.ap.menabev.dto;



import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceHeaderDetailsDto {

	private String Id;

	private String requestId;
	
	private String docStatus;

	private String vendorName;

	private String ocrBatchId;

	private String headerText;

	private String compCode;

	private Long refDocNum;

	private String extInvNum;
	// added for the tag in xml _InvoiceDate as discussed
	private String invoiceDate;
	private LocalDateTime createdAt;
	
	private String dueDate;

	private String vendorId;

	private Integer clerkId;

	private String clerkEmail;

	private String channelType;

	private String refDocCat;

	private String invoiceType;

	private double invoiceTotal;

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

	private String grossAmount;

	private String balance;

	private String reasonForRejection;
	private String rejectionText;//Duplicate Invoice,Vendor Id mismatch

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
	private String clearingAccountingDocument;
	private String clearingDate;
	private String paymentStatus;
	private String paymentBlock;
	private String paymentBlockDesc;
	private String accountingDoc;
	private Boolean balanceCheck;
	private String manualpaymentBlock;

	private List<InvoiceItemDto> invoiceItems;
}
