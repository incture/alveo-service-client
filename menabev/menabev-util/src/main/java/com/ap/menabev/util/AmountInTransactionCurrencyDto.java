package com.ap.menabev.util;

import lombok.Data;

@Data

public class AmountInTransactionCurrencyDto {
	private String  currencyCode;
   private String   text;
@Override
public String toString() {
	return "AmountInTransactionCurrencyDto [@currencyCode=" + currencyCode + ", #text=" + text + "]";
}




}
