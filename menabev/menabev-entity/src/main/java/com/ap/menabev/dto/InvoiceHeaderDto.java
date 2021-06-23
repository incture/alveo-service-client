package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

@Data
public class InvoiceHeaderDto {
	
	private String guid;
	private String requestId; //0
	private Long request_created_at;
	private String request_created_by;
	private Long request_updated_at;
	private String request_updated_by;
	private String invoice_ref_number;
	private String vendorName;//24
	private String ocrBatchId;
	private String compCode;//7
	private String extInvNum;//  Invoice Number for NON po, Invoice Reference Number
	private Double invoiceTotal;//2
	private Long sapInvoiceNumber;//13
	private String invoicePdfId; // add the document id of the invoice pdf to show in oepn pdf .
	private String street;
	private String zipCode;
	private String city;
	private String countryCode;
	private String companyCode;//7
	private Long documentDate;
	private Long dueDate;//22
	private Long invoiceDate;// added for the tag in xml _InvoiceDate as discussed.
	private String vendorId;//4
	private String channelType;//
	private String refpurchaseDoc;
	private String refpurchaseDocCat;
	private String invoiceType;//12
	private Double invoiceAmount;//2
	private String fiscalYear;//14
	private String currency;//15
	private String paymentTerms;//16
	private String paymentMethod;//16
	private Double taxAmount;//17
	private Double sysSusgestedTaxAmount;
	private Double shippingCost;//18
	private String taskStatus;//5
	private Integer version;//21
	private Double grossAmount;
	private Double balanceAmount;
	private String reasonForRejection;
	private Long updatedBy;
	private Long updatedAt;
	private String accountNumber;
	private Long postingDate;
	private String paymentBlock;
	private String paymentBlockDesc;
	private String workflowId;
	private String taskId;
	private String taskOwner;
	private String taskGroup;
	private Boolean isnonPoOrPo;
	private String transactionType;
	private String deliveryNote;
	private Double amountBeforeTax;
	private String taxCode;
	private String taxRate;
	private Double taxPercentage;
	private Double surcharge;
	private Double discount;
	private Double invoiceGross;
	private String vatRegNum;
	private Double unplannedCost;
	private Double plannedCost;
	private Long baseLineDate;
	private String totalBaseRate;
	private String taxValue;
	private Boolean isRejected;
	private String invoiceStatus;
	private String invoiceStatusText;
	private String approvalStatus;
	private String sapInvocieNumber;
	// not in DB
	private String processor;
	private Long emailReceivedAt;
	private List<InvoiceItemDto> invoiceItems;
	private List<CostAllocationDto> costAllocation;
	private List<ActivityLogDto> activityLog;
	private List<CommentDto> comment;    
	private List<AttachmentDto> attachment; 
	private  List<RemediationUser> remediationUserList; 
    private List<TaxDataDto> taxData;
	private List<HeaderMessageDto>  headerMessages;
	//Added by Dipanjan on 21/06/2021 from Menabev AP DB Tables sheet shared by Prashant Kumar
	private InvoiceChangeIndicator changeIndicators;
	

	  


}
