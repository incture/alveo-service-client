package com.ap.menabev.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Entity
@Table(name = "INVOICE_ITEM_ACCOUNT_ASSIGNMENT")
public @Data class InvoiceItemAcctAssignmentDo {
	@Id
	@Column(name="INV_ACC_ASS_ID")
	private String invAccAssId;
	
	@Column(name="REQUEST_ID", nullable = false)
	private String requestId;
	
	@Column(name = "ITEM_ID", nullable = false)
	private String itemId;
	
	@Column(name = "DOCUMENT_NUMBER",nullable=false)
	private String documentNumber;
	
	@Column(name = "DOCUMENT_ITEM",nullable=false)
	private String documentItem;
	
	@Column(name="SERIAL_NO",nullable=false)
	private String serialNo;
	
	@Column(name="IS_DELETED",nullable=false)
	private Boolean isDeleted;
	
	@Column(name="CREATED_ON")
	private Long createdOn;
	
	@Column(name="IS_CHANGED")
	private Boolean isChanged;
	
	@Column(name = "QUANTITY")
	private String quantity;
	
	@Column(name="DIS_PER")
	private double disPer;
	
	
	@Column(name = "NET_VALUE")
	private String netValue;
	
	@Column(name = "GL_ACCOUNT")
	private String glAccount;
	
	@Column(name = "BUSINESS_AREA")
	private String businessArea;
	
	@Column(name = "COST_CENTER")
	private String costCenter;
	
	@Column(name="SD_DOC")
	private String sdDoc;
	
	@Column(name="ITEM_NUMBER")
	private String itemNumber;
	
	@Column(name="SCHED_LINE")
	private String schedLine;
	
	@Column(name="ASSET_NO")
	private String assetNo;
	@Column(name="SUB_NUMBER")
	private String subNumber;
	@Column(name="ORDER_ID")
	private String orderId;
	@Column(name="GR_RCPT")
	private String grRcpt;
	@Column(name="UNLOAD_PT")
	private String unloadPt;
	@Column(name="CO_AREA")
	private String coArea;
	@Column(name="COST_OBJECT")
	private String costObject;
	@Column(name="PROFIT_CTR")
	private String profitCtr;
	@Column(name="WBS_ELEMENT")
	private String wbsElement;
	@Column(name="NETWORK")
	private String network;
	@Column(name="RLESTKEY")
	private String rlEstKey;
	@Column(name="PART_ACCT")
	private String partAcct;
	@Column(name="CMMT_ITEM")
	private String cmmtItem;
	@Column(name="REC_IND")
	private String recInd;
	@Column(name="FUNDS_CTR")
	private String fundsCtr;
	@Column(name="FUND")
	private String fund;
	@Column(name="FUNC_AREA")
	private String funcArea;
	@Column(name="REF_DATE")
	private String refDate;
	@Column(name="TAX_CODE")
	private String taxCode;
	@Column(name="TAXJURCODE")
	private String taxjurcode;
	@Column(name="NONDITAX")
	private String nondItax;
	@Column(name="ACT_TYPE")
	private String acttype;

	@Column(name="CO_BUS_PROC")
	private String coBusproc;

	@Column(name="RES_DOC")
	private String resDoc;

	@Column(name="RES_ITEM")
	private String resItem;

	@Column(name="ACTIVITY")
	private String activity;
	@Column(name="GRANTNBR")
	private String grantNbr;
	@Column(name="CMMT_ITEM_LONG")
	private String cmmtItemLong;
	@Column(name="FUNC_AREA_LONG")
	private String funcAreaLong;
	@Column(name="BUDGET_PERIOD")
	private String budgetPeriod;
	@Column(name="FINAL_IND")
	private String finalInd;
	@Column(name="FINAL_REASON")
	private String finalReason;
	

}

