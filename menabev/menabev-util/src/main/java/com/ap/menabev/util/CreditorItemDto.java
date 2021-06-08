package com.ap.menabev.util;

import lombok.Data;

@Data
public class CreditorItemDto {
	
	private String ReferenceDocumentItem;
	private String Creditor;
	private  AmountInTransactionCurrencyDto AmountInTransactionCurrency;

}
