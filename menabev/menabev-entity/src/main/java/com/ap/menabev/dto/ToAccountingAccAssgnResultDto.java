package com.ap.menabev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ToAccountingAccAssgnResultDto {
	@JsonProperty("InvoiceReferenceNumber")
	private String invoiceReferenceNumber;
	@JsonProperty("InvoiceDocItem")
	private String invoiceDocItem;
	@JsonProperty("Xunpl")
	private Boolean xunpl;
	@JsonProperty("SerialNo")
	private String serialNo;
	@JsonProperty("TaxCode")
	private String taxCode;
	@JsonProperty("ItemAmount")
	private String itemAmount;
	@JsonProperty("")
	private String Quantity;
	@JsonProperty("")
	private String PoUnit;
	@JsonProperty("")
	private String PoUnitIso;
	@JsonProperty("")
	private String PoPrQnt;
	@JsonProperty("")
	private String PoPrUom;
	@JsonProperty("")
	private String PoPrUomIso;
	@JsonProperty("")
	private String GlAccount;
	@JsonProperty("")
	private String Costcenter;
	@JsonProperty("")
	private String ProfitCtr;
	@JsonProperty("")
	private String CoArea;
}
