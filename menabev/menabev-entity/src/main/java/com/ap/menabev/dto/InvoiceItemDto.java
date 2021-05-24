package com.ap.menabev.dto;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.ap.menabev.entity.InvoiceItemAcctAssignmentDo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InvoiceItemDto {

	private String Id = UUID.randomUUID().toString();
	private String requestId;
	private String itemId;
	private String itemCode;
	private String itemLifeCycleStatus;
	private String itemLifeCycleStatusText;
	private String itemText;
	private String refDocCat;
	private Long refDocNum;
	private String extItemId;
	private Integer customerItemId;
	private String upcCode;
	private String invQty;
	private String qtyUom;
	private String price;
	private String currency;
	private Integer pricingUnit;
	private String unit;
	private String disAmt;
	private String disPer;
	private String deposit;
	private Integer shippingAmt;
	private String shippingPer;
	private Integer taxAmt;
	private Integer taxPer;
	private String netWorth;
	private String itemComment;
	private Boolean isTwowayMatched;
	private Boolean isThreewayMatched;
	private Long matchDocNum;
	private String matchedBy;
	private String matchDocItem;
	private String matchParam;
	private String unusedField1;
	private String unusedField2;

	private String createdByInDb;
	private Long createdAtInDB;
	private String updatedBy;
	private Long updatedAt;
	private Boolean isSelected;
	private String isAccAssigned;
	//private List<InvoiceItemAcctAssignmentDo> invItemAcctDtoList;

	/* PO Calculations */

	private BigDecimal poAvlQtyOU;

	private BigDecimal unitPriceOPU;

	private BigDecimal poNetPrice;

	private String poTaxCode;
	private String poMaterialNum;
	private String poVendMat;
	private String poUPC;
	private BigDecimal poQty;
	private String poUom;
	private String amountDifference;
	private Boolean isDeleted;
	// private String price2;
	//
	// private String invQty2;

}
