package com.ap.menabev.dto;

import lombok.Data;

@Data
public class ItemInvoiceAcctAssign {
	
	private String  accountAssgnGuid;
    private String  requestId;
    private String  itemId;
    private String  serialNo;
    private String  isDeleted;
    private String  isUnplanned;
    private String  qty;
    private String  qtyUnit;
    private String  distPerc;
    private String  netValue;
    private String  glAccount;
    private String  costCenter;
    private String  debitOrCredit;
    private String text;
    private String  taxValue;
    private String  taxPercentage;
    private String  taxCode;

}
