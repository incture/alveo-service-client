package com.ap.menabev.dto;



import java.util.UUID;

import lombok.Data;

public @Data class InvoiceItemAcctAssignmentDto {
	private String invAccAssId;
	private String requestId;
	private String itemId;
	private String documentNumber;
	private String documentItem;
	private String serialNo;
	private Boolean isDeleted;
	private Long createdOn;
	private Boolean isChanged;
	private String quantity;
	private double disPer;
	private String netValue;
	private String glAccount;
	private String businessArea;
	private String costCenter;
	private String sdDoc;
	private String itemNumber;
	private String schedLine;
	private String subNumber;
	private String assetNo;
	private String orderId;
	private String grRcpt;
	private String unloadPt;
	
	private String coArea;
	
	private String costObject;
	
	private String profitCtr;
	
	private String wbsElement;
	
	private String network;
	
	private String rlEstKey;
	
	private String partAcct;
	
	private String cmmtItem;
	
	private String recInd;
	
	private String fundsCtr;
	
	private String fund;
	
	private String funcArea;
	
	private String refDate;
	
	private String taxCode;
	
	private String taxjurcode;
	
	private String nondItax;
	
	private String acttype;

	
	private String coBusproc;

	
	private String resDoc;

	
	private String resItem;

	
	private String activity;
	
	private String grantNbr;
	
	private String cmmtItemLong;
	
	private String funcAreaLong;
	
	private String budgetPeriod;
	
	private Boolean finalInd;
	
	private String finalReason;

}
