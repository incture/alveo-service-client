package com.ap.menabev.dto;

import lombok.Data;

public @Data class InvoiceItemAcctAssignmentDto {
	
	
	private String accountAssgnGuid;
	private String requestId;
	private String itemId; // invoiceItemId
	private String serialNo;
	private Boolean isDeleted;
	private Boolean  isPlanned;
	private Double qty;
	private String qtyUnit;
	private Double distPerc;
	private Double netValue;
	private String glAccount;
	private String costCenter;
	private String costCenterText;
	private String  taxValue;
     private String taxPercentage;
	private String subNumber;
	private String orderId;
	private String profitCtr;
    private String wbsElement;
    private String crDbIndicator;

}
