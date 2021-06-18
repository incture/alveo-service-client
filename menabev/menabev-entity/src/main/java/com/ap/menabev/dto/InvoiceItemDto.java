package com.ap.menabev.dto;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;

import com.ap.menabev.entity.InvoiceItemAcctAssignmentDo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InvoiceItemDto {

	private String guid;
	private String itemCode;
	private String requestId;
	private String itemText;
	private String refDocCat;//ref_purchasedoc_category
	private Long refDocNum;//ref_purchase_num
	private String extItemId;
	private String articleNum;
	private Integer customerItemId;
	private String upcCode;
	private double invQty;
	private String uom;
	private double unitPrice;
	private String currency;
	private Integer pricingUnit;
	private String orderPriceUnit;
	private double grossPrice;
	private double discountValue;
	private double disPerentage;
	private String taxCode;
	private double taxValue;
	private double taxPercentage;
	private double netWorth;
	private Boolean isTwowayMatched;
	private Boolean isThreewayMatched;
	private Long matchDocNum;
	private String matchDocItem;
	private String matchParam;
	private String matchserviceNumber;
	private String matchpackageNumber;
	private String matchType;// manuall or Auto posting
	private String updatedBy;
	private long updatedAt;
	private Boolean isSelected;
	private String matchedBy;
    private boolean isAccAssigned;
    private String itemRequisationNum;
    private String requisationNum;
    private String contractNum;
    private String contractItem;
    private boolean isDeleted;
    private String setPoMaterialNum;
	private List<InvoiceItemAcctAssignmentDto> invItemAcctDtoList;
	private List<ItemMessageDto> invoiceItemMessages;

}
