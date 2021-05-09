package com.ap.menabev.entity;



import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.ap.menabev.sequences.InvoiceItemSequenceGenerator;

@ToString
@Entity
@Table(name = "INVOICE_HEADER")
@Getter
@Setter
public class InvoiceHeaderDo {

	@Id
	@Column(name = "ID")
	private String id = UUID.randomUUID().toString();

	// @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
	// "InvoiceHeader")
	// @GenericGenerator(name = "InvoiceHeader", strategy =
	// "com.incture.ap.sequences.InvoiceHeaderSequenceGenerator", parameters = {
	// @Parameter(name = InvoiceHeaderSequenceGenerator.INCREMENT_PARAM, value =
	// "1"),
	// @Parameter(name = InvoiceHeaderSequenceGenerator.VALUE_PREFIX_PARAMETER,
	// value = "APA-"),
	// @Parameter(name = InvoiceHeaderSequenceGenerator.NUMBER_FORMAT_PARAMETER,
	// value = "%06d"),
	// @Parameter(name = InvoiceHeaderSequenceGenerator.SEQUENCE_PARAM, value =
	// "INVOICE_HEADER_SEQ") })
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
	private Long refDocNum;//8
	@Column(name = "EXT_INV_NUM")
	private String extInvNum;//1

	@Column(name = "DUE_DATE")
	private String dueDate;//22
	
	// added for the tag in xml _InvoiceDate as discussed.
	@Column(name = "INVOICE_DATE")
	private String invoiceDate;//23
	@Column(name = "CREATED_AT")
	private String createdAt;//6
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
	private String updatedBy;

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
	private String clearingDate;
	
	@Column(name="PAYMENT_STATUS")
	private String paymentStatus;
	
	@Column(name="PAYMENT_BLOCK")
	private String paymentBlock;

	@Column(name="PAYMENT_BLOCK_DESC")
	private String paymentBlockDesc;
	
	@Column(name="BALANCE_CHECK")
	private Boolean balanceCheck;

	@Column(name="DOC_STATUS")
	private String docStatus;
	
	@Column(name="MANUAL_PAYMENT_BLOCK")
	private String manualpaymentBlock;

	
}
