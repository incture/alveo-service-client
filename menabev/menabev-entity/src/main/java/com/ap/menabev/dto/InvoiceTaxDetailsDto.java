package com.ap.menabev.dto;



import java.util.UUID;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public @Data class  InvoiceTaxDetailsDto 
{
	private String id= UUID.randomUUID().toString();
	private String requestId;
	private String invoiceId;
	private String taxCode;
	private String taxJurisdiction;
	private String taxPer;
	private String taxValue;
	private String currencyOfCode;
	private String shipTo;
	private String shipFrom;
	private String purClassification;
	private String vatAmount;
	private String vatRate;
}