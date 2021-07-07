package com.ap.menabev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemThreeWayMatchPaylod {
	@JsonProperty("Vendor")
	private String vendor;
	@JsonProperty("InvoiceItem")
	private String invoiceItem;
	@JsonProperty("PurchasingDocumentNumber")
	private String purchasingDocumentNumber;
	@JsonProperty("PurchasingDocumentItem")
	private String purchasingDocumentItem;
	@JsonProperty("BaseUnit")
	private String baseUnit;
	@JsonProperty("DntrConvOpuOu")
	private String dntrConvOpuOu;
	@JsonProperty("NmtrConvOpuOu")
	private String nmtrConvOpuOu;
	@JsonProperty("QtyOrdPurReq")
	private String qtyOrdPurReq;
	@JsonProperty("PostingDate")
	private String postingDate;
	@JsonProperty("CompanyCode")
	private String companyCode;
	@JsonProperty("ReferenceDocument")
	private String referenceDocument;
	@JsonProperty("Quantity")
	private String quantity;
	@JsonProperty("NetValDC")
	private String netValDC;
	@JsonProperty("NoQuantityLogic")
	private String noQuantityLogic;
	@JsonProperty("ItemCategory")
	private String itemCategory;
	@JsonProperty("QtyInvoiced")
	private String qtyInvoiced;
	@JsonProperty("ReturnsItem")
	private String returnsItem;
	@JsonProperty("DrCrInd")
	private String drCrInd;
	@JsonProperty("SubDrCrInd")
	private String subDrCrInd;
	@JsonProperty("CurrencyKey")
	private String currencyKey;
	@JsonProperty("GrBasedIvInd")
	private String grBasedIvInd;
	@JsonProperty("QtyGoodsReceived")
	private String qtyGoodsReceived;
	@JsonProperty("GoodsReceiptInd")
	private String goodsReceiptInd;
	
	//block this TranslationDate
//	@JsonProperty("TranslationDate")
//	private String translationDate;
	@JsonProperty("UpdatePODelCosts")
	private String updatePODelCosts;
	@JsonProperty("PostInvInd")
	private String postInvInd;
	@JsonProperty("DelItemAllocInd")
	private String delItemAllocInd;
	@JsonProperty("RetAllocInd")
	private String retAllocInd;
	@JsonProperty("IvOrigin")
	private String ivOrigin;
	@JsonProperty("QtyOpu")
	private String qtyOpu;
	@JsonProperty("InvValFC")
	private String invValFC;
	@JsonProperty("EstPriceInd")
	private String estPriceInd;
	@JsonProperty("GrNonValInd")
	private String grNonValInd;
	@JsonProperty("NetValSrvFC")
	private String netValSrvFC;
	@JsonProperty("AmtDC")
	private String amtDC;
	@JsonProperty("ValGrFC")
	private String valGrFC;
	@JsonProperty("NewInputVal")
	private String newInputVal;
	@JsonProperty("RequestId")
	private String requestId;
	@JsonProperty("ReferenceInvoiceItem")
	private String referenceInvoiceItem;
}
