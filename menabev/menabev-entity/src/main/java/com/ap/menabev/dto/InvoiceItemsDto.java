package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

@Data
public class InvoiceItemsDto {
	
	private String  itemGuid;
    private String  requestId;
    private String  itemId;
    private String  refDocNum;
    private String  refDocCategory;
    private String  extItemId;
    private String  articleNumber;
    private String  customerItemIdVmn;
    private String  invText;
    private String  upcCode;
    private String  invQty;
    private String  uom;
    private String  unitPrice;
    private String  currency;
    private String  pricingUnit;
    private String orderPriceUnit;
    private String  grossPrice;
    private String  discountValue;
    private String  discousntPercentage;
    private  String  taxCode;
    private String  taxPercentage;
    private String  taxValue;
    private String  netWorth;
    private String  avlQtyUoM;
    private String poUnitPriceUoM;
    private boolean istwoWayMatched;
    private boolean  isthreewayMatched;
    private String  matchDocNum;
    private String  matchDocItem;
    private String  matchParam;
    private String matchServiceNumber;
    private String  matchPackageNumber;
    private String  requisitionNum;
    private String  requisitionItm;
    private String contractNum;
    private String  contractItm;
    private String  AccountAssignmentCat;
    private boolean isDeleted;
    private List<ItemMessageDto> itemMessages;
    private List<InvoiceItemAcctAssignmentDto>  invoiceItemAccAssgn;
     

}