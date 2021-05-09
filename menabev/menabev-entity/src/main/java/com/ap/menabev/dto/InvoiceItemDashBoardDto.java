package com.ap.menabev.dto;



import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class InvoiceItemDashBoardDto {
	
	private String id;
	private String requestId;
	private String itemId;
	private String itemLifeCycleStatus;
	private String itemLifeCycleStatusText;
	private String itemCode;
	private String itemText;
	private String shortText;
	private String extItemId;
	private String customerItemID;
	@JsonProperty("invoiceUPCCode")
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
	private String matchDocItem;
	private String matchParam;
	private String unusedField1;
	private String unusedField2;
	private BigDecimal poQty;
	private String poUom;
	//Added fields
	private String createdByInDb;
	private Long createdAtInDB;
	private String updatedBy;
	private Long updatedAt;
	private Boolean isSelected;
	private String refDocCat;
	private String refDocNum;
	//private Long createdAtInDb;
	
	//missing fileds.
	private String threeWayMessae;//ItemComment
	private String isThreewayQtyIssue;
	private String isThreewayPriceIssue;
	private String poAvlQtyOPU;
	private BigDecimal poAvlQtyOU;
	private String poAvlQtySKU;
	private BigDecimal unitPriceOPU;
	
	private String unitPriceOU;
	private String unitPriceSKU;
	private BigDecimal poNetPrice;
	private String poTaxCode;
	private String poTaxPer;
	private String poTaxValue;
	private String poMaterialNum;
	private String poVendMat;
	private String poUPC;
	private String matchedBy;
	private String amountDifference;
	private Boolean isDeleted;
    private String isAccAssigned;

	private List<InvoiceItemAcctAssignmentDto>invItemAcctDtoList;
	
	//Added for showing messages for each line item to UI.
	private List<MessageDto> excpetionMessage;
	private String oldInvQty;
	private String oldItemText;
	private String oldPrice;
	private String oldNetWorth;
	
	
}
