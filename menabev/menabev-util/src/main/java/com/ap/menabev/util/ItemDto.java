package com.ap.menabev.util;

import lombok.Data;

@Data
public class ItemDto {
	
	private String   GLAccount;
    private String   CompanyCode;
    private   AmountInTransactionCurrencyDto AmountInTransactionCurrency;
    private TaxDto Tax;
    private AccountAssignmentDto AccountAssignment;

}