package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class CostAllocationDto {
	private String costAllocationId;
	private String requestId;
	private Integer itemId;
	private Integer serialNo;
	private Boolean deleteInd;
	private String quantity;
	private String distrPerc;
	private String netValue;
	private String glAccount;
	private String costCenter;
	private String assetNo;
	private String subNumber;
	private String internalOrderId;
	private String profitCenter;
	private String wbsElement;
	private String crDbIndicator;
	private String itemText;
	private String allocationPercent;
	private String orderId;
	private String taxCode;
	private String accountNum;
	private String lineText;
	private String taxValue;
	private String taxPer;
	private String taxRate;
}
