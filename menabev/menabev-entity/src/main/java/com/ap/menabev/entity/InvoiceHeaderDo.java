package com.ap.menabev.entity;



import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.ap.menabev.sequences.InvoiceHeaderSequenceGenerator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Entity
@Table(name = "INVOICE_HEADER")
@Getter
@Setter
public class InvoiceHeaderDo {
	
	 /*@GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
        "InvoiceHeader")
	 @GenericGenerator(name = "InvoiceHeader", strategy =
	 "com.ap.menabev.sequences.InvoiceHeaderSequenceGenerator", parameters = {
	 @Parameter(name = InvoiceHeaderSequenceGenerator.INCREMENT_PARAM, value =
	  "1"),
	 @Parameter(name = InvoiceHeaderSequenceGenerator.VALUE_PREFIX_PARAMETER,
     value = "APA-"),
	 @Parameter(name = InvoiceHeaderSequenceGenerator.NUMBER_FORMAT_PARAMETER,
	 value = "%06d"),
	 @Parameter(name = InvoiceHeaderSequenceGenerator.SEQUENCE_PARAM, value =
	 "INVOICE_HEADER_SEQ") })*/
	@Id
	@Column(name = "REQUEST_ID", nullable = false)
	private String requestId; //0

	@Column(name = "VENDOR_NAME")
	private String vendorName;//24

	@Column(name = "OCR_BATCH_ID")
	private String ocrBatchId;

	@Column(name = "HEADER_TEXT")
	private String headerText;

	@Column(name = "COMP_CODE")
	private String compCode;//7
	@Column(name = "REF_DOC_NUM")
	private Long refDocNum;//invoice ref number
	@Column(name = "EXT_INV_NUM")
	private String extInvNum;//  Invoice Number for NON po, Invoice Reference Number

	@Column(name = "DUE_DATE")
	private Long dueDate;//22
	
	// added for the tag in xml _InvoiceDate as discussed.
	@Column(name = "INVOICE_DATE")
	private Long invoiceDate;//23
	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt;//6
	@Column(name = "VENDOR_ID")
	private String vendorId;//4
	@Column(name = "CLERK_ID")
	private Integer clerkId;//9
	@Column(name = "CLERK_EMAIL")
	private String clerkEmail;//3
	@Column(name = "CHANNEL_TYPE")
	private String channelType;//10
	@Column(name = "REF_DOC_CAT")
	private String refDocCat;//11
	@Column(name = "INVOICE_TYPE")
	private String invoiceType;//12
	@Column(name = "INVOICE_TOTAL")
	private String invoiceTotal;//2
	@Column(name = "SUB_TOTAL")
	private String subTotal;                                           //26
	@Column(name = "SAP_INVOICE_NUMBER")
	private Long sapInvoiceNumber;//13
	@Column(name = "FISCAL_YEAR")
	private String fiscalYear;//14
	@Column(name = "CURRENCY")
	private String currency;//15
	@Column(name = "PAYMENT_TERMS")
	private String paymentTerms;//16
	@Column(name = "TAX_AMOUNT")
	private String taxAmount;//17
	@Column(name = "SHIPPING_COST")
	private Double shippingCost;//18
	@Column(name = "LIFECYCLE_STATUS")
	private String lifecycleStatus;//20
	@Column(name = "TASK_STATUS")
	private String taskStatus;//5
	@Column(name = "VERSION")
	private Integer version;//21
	@Column(name = "EMAIL_FROM")//19
	private String emailFrom;
	@Column(name = "GROSS_AMOUNT")
	private String grossAmount;
	@Column(name = "BALANCE")
	private String balance;
	@Column(name = "REASON_FOR_REJECTION")
	private String reasonForRejection;
	
	@Column(name = "DIS_AMT")
	private String disAmt;
	
	@Column(name = "DEPOSIT")
	private String deposit;

	@Column(name = "CREATED_BY_IN_DB")
	private String createdByInDb;

	@Column(name = "CREATED_AT_IN_DB")
	private Long createdAtInDB;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	@Column(name = "UPDATED_AT")
	private Long updatedAt;

	@Column(name = "ACCOUNT_NUMBER")
	private String accountNumber;
	
	@Column(name="ACCOUNTING_DOC")
	private String accountingDoc;
	
	@Column(name = "POSTING_DATE")
	private Long postingDate;
	@Column(name = "ASSIGNED_TO")
	private String assignedTo;//25
	@Column(name="CLEARING_ACCOUNTING_DOCUMENT")
	private String clearingAccountingDocument;
	@Column(name="CLEARING_DATE")
	private Long clearingDate;
	@Column(name="PAYMENT_STATUS")
	private String paymentStatus;
	@Column(name="PAYMENT_BLOCK")
	private String paymentBlock;
	@Column(name="PAYMENT_BLOCK_DESC")
	private String paymentBlockDesc;
	@Column(name="BALANCE_CHECK")
	private Boolean balanceCheck;
	@Column(name="MANUAL_PAYMENT_BLOCK")
	private String manualpaymentBlock;
	@Column(name = "WORkFLOW_ID")
	private String workflowId;
	@Column(name = "VALIDATION_STATUS")
	private  String validationStatus; /// Exception Status 
	@Column(name ="TASK_OWNER")
	private String taskOwner;
	@Column(name = "TASK_OWNER_ID")
	private String taskOwnerId; // loggedin userid 
	@Column(name = "FORWARDED_TASK_OWNER")
	private String forwaredTaskOwner;
	@Column(name = "IS_NON-PO_OR_PO")
	private boolean isNonPoOrPo;
	@Column(name="DOC_STATUS") // is Draft or Created  or Opened.
	private String docStatus;
	@Column(name="TRANSACTION_TYPE")
	private String transactionType;
	@Column(name="DELIVERY_NOTE")
	private String deliveryNote;
	@Column(name = "AMOUNT_BEFORE_TAX")
	private String amountBeforeTax;
	@Column(name = "TAX_CODE")
	private String taxCode;
	@Column(name = "TAX_RATE")
	private String taxRate;
	@Column(name = "TOTAL_BASE_RATE")
	private String totalBaseRate;
	@Column(name = "TAX_VALUE")
	private String taxValue;
	@Column(name="SURCHARGE")
	private String surcharge;
	@Column(name = "DISCOUNT")
	private String discount;
	@Column(name = "VAT_REG_NUM")
	private String vatRegNum;
	@Column(name = "UNPLANNED_COST")
	private String unplannedCost;
	@Column(name = "PLANNED_COST")
	private String plannedCost;
	@Column(name = "BASE_LINE")
	private String baseLine;
	@Column(name = "INVOICE_PDF_ID")
	private String invoicePdfId; // add the document id of the invoice pdf to show in oepn pdf .
	
	
	
	
	
	
	

	
}
