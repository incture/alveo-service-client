package com.ap.menabev.dto;



import java.math.BigDecimal;


import lombok.Data;



public @Data class NonPoTemplateItemsDto {
	private String itemId;
	private String templateId;
	private String glAccount;
	private String costCenter;
	private String internalOrderId;
	private String assetNo;
	private String subNumber;
	private String wbsElement;
	private String materialDescription;
	private String crDbIndicator;
	private String profitCenter;
	private String itemText;
	private String companyCode;
	private Boolean isNonPo;
    private String accountNo;
	private String allocationPercent;
	
}
