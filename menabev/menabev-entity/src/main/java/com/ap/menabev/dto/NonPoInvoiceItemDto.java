package com.ap.menabev.dto;



import lombok.Data;

public @Data class NonPoInvoiceItemDto {

	private String requestId;
	private String itemId;
	private String itemCode;
	private String itemText;
	private String refDocCat;
	private String refDocNum;
	private String extItemId;
	private String customerItemId;
	private String upcCode;
	private String invQty;
	private String qtyUom;
	private String price;
	private String currency;
	private String pricingUnit;
	private String unit;
	private String disAmt;
	private String disPer;
	private String shippingAmt;
	private String shippingPer;
	private String taxAmt;
	private String taxPer;
	private String netWorth;
	private String itemComment;
	private String isTwowayMatched;
	private String isThreewayMatched;
	private String matchDocNum;
	private String matchDocItem;
	private String matchParam;
	private String unusedField1;
	private String unusedField2;
	private String createdByInDb;
	private String createdAtInDB;
	private String updatedBy;
	private String updatedAt;
	private String isSelected;
	private String poAvlQtyOU;
	private String unitPriceOPU;
	private String poNetPrice;
	private String poTaxCode;
	private String id;

}
