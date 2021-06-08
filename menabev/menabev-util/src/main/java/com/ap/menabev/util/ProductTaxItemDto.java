package com.ap.menabev.util;

import lombok.Data;

@Data
public class ProductTaxItemDto {
	
	private String TaxCode;
	private AmountInTransactionCurrencyDto AmountInTransactionCurrency;
	private TaxBaseAmountInTransCrcyDto TaxBaseAmountInTransCrcy;
	private String ConditionType;

}
