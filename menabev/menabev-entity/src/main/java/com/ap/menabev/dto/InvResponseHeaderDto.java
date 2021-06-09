package com.ap.menabev.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;


@Data
public class InvResponseHeaderDto {
	
	
	private String requestId;
	private String vendorName;
	private String ocrBatchId;
	private String headerTaxcode;
	private String companyCode;//7
	private String invoiceRefNum;
	private String documentDate;
	private Long dueDate;//22
	private LocalDateTime createdAt;//6
	private String vendorId;//4
	private String channelType;//10
	private String refDocCat;//11
	private String invoiceType;//12
	private double invoiceAmount;//2
	private String sapInvoiceNumber;//13
	private String fiscalYear;//14
	private String currency;//15
	private String paymentTerms;//16
	private String taxAmount;//17
	private String sysSusgestedTaxAmount;
	private String taskStatus;//5
	private Integer version;//21
	private String emailFrom;
	private String grossAmount;
	private String balanceAmount;
	private String reasonForRejection;
	private String deposit;
	private String accountNumber;
	private String accountingDoc;
	private Long postingDate;
	private String assignedTo;//25
	private String paymentStatus;
	private String paymentBlock;
	private String paymentBlockDesc;
	private Boolean balanceCheck;
	private String manualpaymentBlock;
	private String workflowId;
	private String taskId;
	private String taskOwner;
	private String taskOwnerId; // loggedin userid 
	private String docStatus;
	private String transactionType;
	private String vatRegNum;
	private String unplannedCost;
	private String plannedCost;
	private String baseLineDate;
	private String documentID; // add the document id of the invoice pdf to show in oepn pdf .
	private String invoiceStatus;
	private String approvalStatus;
	private boolean nonPoOrPo;
	private boolean isrejected;
	private boolean isheaderChanged;
	private boolean isitemChanged;
	private boolean iscommentsChanged;
	private boolean iscostAllocationChanged;
	private List<InvoiceItemsDto> invoiceItems;
	private List<CostAllocationsDto> costAllocation;
	private List<ActivityLogDto> activityLog;
	private List<CommentDto> comment;    
	private List<AttachmentDto> attachment; 
	private  List<RemediationUser> remediationUserList; 
    private List<TaxDataDto> taxData;
	private List<HeaderMessageDto>  headerMessages;


}
