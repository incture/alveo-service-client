package com.ap.menabev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostToERPDto {
	@JsonProperty("InvoiceReferenceNumber")
	private String invoiceReferenceNumber;
	@JsonProperty("InvoiceInd")
	private String invoiceInd;
	@JsonProperty("DocDate")
	private String docDate;
	@JsonProperty("PstngDate")
	private String pstngDate;
	@JsonProperty("CompCode")
	private String compCode;
	@JsonProperty("Currency")
	private String currency;
	@JsonProperty("GrossAmount")
	private String grossAmount;
	@JsonProperty("pmnttrms")
	private String Pmnttrms;
	@JsonProperty("BlineDate")
	private String blineDate;
	@JsonProperty("ToItem")
	private ToItem toItem;

	@JsonProperty("ToGlAccount")
	private ToGlAccount toGlAccount;
	@JsonProperty("ToTax")
	private ToTax toTax;
	@JsonProperty("ToWithholdingTax")
	private ToWithholdingTax toWithholdingTax;
	@JsonProperty("ToReturn")
	private ToReturn toReturn;
	@JsonProperty("ToResult")
	private ToResult toResult;

	@JsonProperty("PmntBlock")
	private String pmntBlock;
	@JsonProperty("DelCosts")
	private String delCosts;
	@JsonProperty("PymtMeth")
	private String pymtMeth;
}
