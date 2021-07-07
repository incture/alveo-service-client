package com.ap.menabev.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemThreeWayAccAssgnPaylod {
	@JsonProperty("InvoiceReferenceNumber")
	private String invoiceReferenceNumber;
	@JsonProperty("InvoiceDocItem")
	private String invoiceDocItem;
	@JsonProperty("PoNumber")
	private String poNumber;
	@JsonProperty("PoItem")
	private String poItem;
	@JsonProperty("RefDoc")
	private String refDoc;
	@JsonProperty("RefDocYear")
	private String refDocYear;
	@JsonProperty("RefDocIt")
	private String refDocIt;
	@JsonProperty("TaxCode")
	private String taxCode;
	@JsonProperty("ItemAmount")
	private String itemAmount;
	@JsonProperty("Quantity")
	private String quantity;
	@JsonProperty("PoUnit")
	private String poUnit;
	@JsonProperty("PoUnitIso")
	private String poUnitIso;
	@JsonProperty("PoPrQnt")
	private String poPrQnt;
	@JsonProperty("PoPrUom")
	private String poPrUom;
	@JsonProperty("PoPrUomIso")
	private String poPrUomIso;
	@JsonProperty("SheetNo")
	private String sheetNo;
	@JsonProperty("SheetItem")
	private String sheetItem;
	@JsonProperty("ToAccounting")
	private ToAccountingAccAssgn toAccounting;
}
