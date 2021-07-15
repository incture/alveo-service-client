package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class CostAllocationDto {
    private String costAllocationId;
    private String requestId; // (FK)
	private String itemId; // (FK)
	private Integer serialNo;
	private Boolean deleteInd;
	private String quantity;
	private String quantityUnit;
	private String distrPerc;
	private String orderId;
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
	private String taxCode;
	private String accountNum;
	private String lineText;
	private String taxValue; // sum of all the items taxValue based on taxcode
	private String taxPer;
	private String baseRate;   
	private String materialDesc;
	private String subNum;
	private String glAccountText;
	private String costCenterText;
	private String currency;
	private String conditionType;
	
}
