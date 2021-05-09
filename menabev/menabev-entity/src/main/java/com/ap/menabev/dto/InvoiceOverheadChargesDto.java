package com.ap.menabev.dto;



import java.util.UUID;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public @Data class InvoiceOverheadChargesDto 
{
	private String iocId= UUID.randomUUID().toString();
	private String invoiceId;
	private String overheadCostCategoryCode;
	private String overheadCostCategoryText;
	private String quantity;
	private String currencyCode;
	private String requestId;
	private String description;
	private String amount;
	private String spendCategory;
	private String purchaseClass;
	private String shipTo;
	private String shipFrom;
}
