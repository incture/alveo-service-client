package com.ap.menabev.dto;



import lombok.Data;

public @Data class InvoiceItemAcctAssignmentDto {
	private String accountAssgnGuid;
	private String requestId;
	private String itemId;
	private String serialNo;
	private Boolean isDeleted;
	private Boolean isUnplanned;
	private String qty;
	private String qtyUnit;
	private double distPerc;
	private String netValue;
	private String glAccount;
	private String costCenter;
	 private String  debitOrCredit;
	private String text;
	private String  taxValue;
     private String taxPercentage;

}
