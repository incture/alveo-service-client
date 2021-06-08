package com.ap.menabev.util;

import lombok.Data;

@Data
public class TaxBaseAmountInTransCrcyDto {
	
	private String currencyCode;
	private String text;
	@Override
	public String toString() {
		return "TaxBaseAmountInTransCrcyDto [@currencyCode=" + currencyCode + ", #text=" + text + "]";
	}
	
	

}
