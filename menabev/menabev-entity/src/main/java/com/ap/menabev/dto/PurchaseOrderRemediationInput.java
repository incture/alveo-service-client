package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

@Data
public class PurchaseOrderRemediationInput {
	
	  private String  guid;
	   private String  documentNumber;
	   private String  compCode;
	   private String  purOrg;
	   private String  purchGroup;
	   private String  createdBy;
	  private List<PoDocumentItem>  poDocumentItem;

}
