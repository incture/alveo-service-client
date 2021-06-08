package com.ap.menabev.dto;

import lombok.Data;

@Data
public class CostAllocationsDto {

	private String   costAllocationGuid;
     private String  requestId;
     private String  itemId;
     private String  qty;
     private String  qtyUnit;
     private String   distPerc;
     private String  netValue;
     private String  accountNo;
     private String templateName;
     private String  templateId;
     private String  glAccount;
     private String  costCenter;
     private String  debitOrCredit;
     private String  text;
     private String gtaxValue;
     private String  taxPercentage;
     private String  taxCode;
	
}
