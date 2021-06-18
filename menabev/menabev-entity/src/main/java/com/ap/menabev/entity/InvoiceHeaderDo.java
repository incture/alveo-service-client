package com.ap.menabev.entity;



import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@ToString
@Entity
@Table(name = "INVOICE_HEADER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(InvoiceHeaderPk.class)
public class InvoiceHeaderDo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "GUID")
	private String guid;
	@Id
	@Column(name = "REQUEST_ID", nullable = false)
	private String requestId; //0
	@Column(name = "REQUEST_CREATED_AT",length = 50 )
	private long request_created_at;
	@Column(name = "REQUEST_CREATED_BY")
	private String request_created_by;
	@Column(name = "REQUEST_UPDATED_AT" , length = 50)
	private long request_updated_at;
	@Column(name = "REQUEST_UPDATED_BY")
	private String request_updated_by;
	@Column(name = "INVOICE_REF_NUMBER",length = 16)
	private String invoice_ref_number;
	@Column(name = "VENDOR_NAME",length = 60)
	private String vendorName;//24
	@Column(name = "OCR_BATCH_ID",length = 64)
	private String ocrBatchId;
	@Column(name = "COMPANY_CODE",length = 6)
	private String compCode;//7
	@Column(name = "EXT_INV_NUM",length = 20)
	private String extInvNum;//  Invoice Number for NON po, Invoice Reference Number
	@Column(name = "INVOICE_TOTAL")
	private double invoiceTotal;//2
	@Column(name = "SAP_INVOICE_NUMBER",length = 15)
	private String sapInvoiceNumber;//13
	@Column(name = "INVOICE_PDF_ID",length = 64)
	private String invoicePdfId; // add the document id of the invoice pdf to show in oepn pdf .
	@Column(name = "STREET" ,length = 10)
	private String street;
	@Column(name = "ZIP_CODE", length = 10)
	private String zipCode;
	@Column(name = "CITY", length = 10)
	private String city;
	@Column(name = "COUNTRY_CODE",length = 40)
	private String countryCode;
	@Column(name = "DOCUMENT_DATE")
	private long documentDate;
	@Column(name = "DUE_DATE")
	private long dueDate;//22
	@Column(name = "INVOICE_DATE" )
	private long invoiceDate;// added for the tag in xml _InvoiceDate as discussed.
	@Column(name = "VENDOR_ID",length = 10)
	private String vendorId;//4
	@Column(name = "CHANNEL_TYPE",length = 10)
	private String channelType;//
	@Column(name = "REF_PURCHASE_DOC", length = 10)
	private String refpurchaseDoc;
	@Column(name = "REF_PURCHASE_DOC_CAT" , length = 10)
	private String refpurchaseDocCat;
	@Column(name = "INVOICE_TYPE" ,length = 10)
	private String invoiceType;//12
	@Column(name = "INVOICE_AMOUNT")
	private double invoiceAmount;//2
	@Column(name = "FISCAL_YEAR",length = 8)
	private String fiscalYear;//14
	@Column(name = "CURRENCY" , length = 6)
	private String currency;//15
	@Column(name = "PAYMENT_TERMS" ,length = 10)
	private String paymentTerms;//16
	@Column(name = "PAYMENT_METHOD" ,length = 20)
	private String paymentMethod;//16
	@Column(name = "TAX_AMOUNT")
	private double taxAmount;//17
	@Column(name = "SYS_SUGGESRTED_TAX_AMOUNT")
	private double sysSusgestedTaxAmount;
	@Column(name = "SHIPPING_COST")
	private double shippingCost;//18
	@Column(name = "TASK_STATUS",length = 20)
	private String taskStatus;//5
	@Column(name = "VERSION",length = 10)
	private Integer version;//21
	@Column(name = "GROSS_AMOUNT")
	private double grossAmount;
	@Column(name = "BALANCE_AMOUNT")
	private double balanceAmount;
	@Column(name = "REASON_FOR_REJECTION" , length = 10)
	private String reasonForRejection;
	@Column(name = "UPDATED_BY")
	private long updatedBy;
	@Column(name = "UPDATED_AT")
	private long updatedAt;
	@Column(name = "ACCOUNT_NUMBER" ,length = 15)
	private String accountNumber;
	@Column(name = "POSTING_DATE")
	private long postingDate;
	@Column(name="PAYMENT_BLOCK",length = 10)
	private String paymentBlock;
	@Column(name="PAYMENT_BLOCK_DESC",length = 20)
	private String paymentBlockDesc;
	@Column(name = "WORkFLOW_ID" ,length = 65)
	private String workflowId;
	@Column(name = "TASK_ID")
	private String taskId;
	@Column(name ="TASK_OWNER",length = 50)
	private String taskOwner;
	@Column(name = "TASK_GROUP",length = 50)
	private String taskGroup;
	@Column(name = "IS_NON-PO_OR_PO")
	private boolean isnonPoOrPo;
	@Column(name="TRANSACTION_TYPE" ,length = 8)
	private String transactionType;
	@Column(name="DELIVERY_NOTE",length = 50)
	private String deliveryNote;
	@Column(name = "AMOUNT_BEFORE_TAX")
	private double amountBeforeTax;
	@Column(name = "TAX_CODE" ,length = 4)
	private String taxCode;
	@Column(name = "TAX_RATE",length = 10)
	private String taxRate;
	@Column(name = "TAX_PERCENTAGE")
	private double taxPercentage;
	@Column(name="SURCHARGE")
	private double surcharge;
	@Column(name = "DISCOUNT")
	private double discount;
	@Column(name = "INVOCIE_GROSS")
	private double invoiceGross;
	@Column(name = "VAT_REG_NUM",length = 20)
	private String vatRegNum;
	@Column(name = "UNPLANNED_COST")
	private double unplannedCost;
	@Column(name = "PLANNED_COST")
	private double plannedCost;
	@Column(name = "BASE_LINE_DATE")
	private long baseLineDate;
	@Column(name = "IS_REJECTED")
	private boolean isRejected;
	@Column(name = "INVOICE_STATUS",length = 5)
	private String invoiceStatus;
	@Column(name = "INVOICE_STATUS_TEXT",length = 30)
	private String invoiceStatusText;
	@Column(name = "APPROVAL_STATUS",length = 10)
	private String approvalStatus;
	
	
}
