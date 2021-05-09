package com.ap.menabev.dto;



import lombok.Data;

public @Data class NonPoCostAllocationDto {

	private String amount;
	private String materialDescription;
	private String debitCreditInd;
	private String glAccount;
	private String costCenter;
	private String orderId;
	private String profitCenter;
	private String asset;
	private String text;

}
