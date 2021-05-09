package com.ap.menabev.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class CostAllocationDto {
	private String costAllocationId= UUID.randomUUID().toString();
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
}
